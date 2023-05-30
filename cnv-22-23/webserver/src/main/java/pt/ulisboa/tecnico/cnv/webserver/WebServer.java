package pt.ulisboa.tecnico.cnv.webserver;


import com.sun.net.httpserver.HttpServer;
import pt.ulisboa.tecnico.cnv.compression.CompressImageHandlerImpl;
import pt.ulisboa.tecnico.cnv.foxrabbit.SimulationHandler;
import pt.ulisboa.tecnico.cnv.insectwar.WarSimulationHandler;
import pt.ulisboa.tecnico.cnv.javassist.tools.MethodStatistic;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;


public class WebServer {

    private static final String AWS_REGION = "us-east-1";
    private static final String filename = "statistics.txt";

    private static final String TABLE_NAME = "CNV-statistics";

    private static final List<MethodStatistic> methodStatistics = new ArrayList<>();

    private static DynamoDbClient dynamoClient;

    public static void main(String[] args) throws Exception {

        Timer timer = new Timer();
        TimerTask dynamite = new TimerTask() {
            @Override
            public void run() {
                storeStatistics();
            }
        };

        timer.schedule(dynamite, 0, 5000);

        setupDB();

        executePutRequest();

        executeGetRequest();

        System.exit(1);

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        //server.createContext("/", new RootHandler());
        server.createContext("/simulate", new SimulationHandler(methodStatistics));
        server.createContext("/compressimage", new CompressImageHandlerImpl(methodStatistics));
        server.createContext("/insectwar", new WarSimulationHandler(methodStatistics));
        server.createContext("/test", new RootHandler());
        server.start();

    }

    private static void setupDB() {

        dynamoClient = DynamoDbClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(Region.US_EAST_1)
                .build();

        try {

            CreateTableRequest request = CreateTableRequest.builder()
                    .tableName(TABLE_NAME)
                    .attributeDefinitions(
                            AttributeDefinition.builder()
                                    .attributeName("args")
                                    .attributeType(ScalarAttributeType.S)
                                    .build())
                    .keySchema(KeySchemaElement.builder()
                            .attributeName("args")
                            .keyType(KeyType.HASH)
                            .build())
                    .provisionedThroughput(ProvisionedThroughput.builder()
                            .readCapacityUnits(1L)
                            .writeCapacityUnits(1L)
                            .build())
                    .build();

            CreateTableResponse response = dynamoClient.createTable(request);

            TableDescription tableId = response.tableDescription();


            System.out.println("DEU CARALHOOOOOOO: " + tableId.toString());

            dynamoClient.waiter().waitUntilTableExists(DescribeTableRequest.builder().tableName(TABLE_NAME).build());

            System.out.println("SAÍ DO WAIT!!");
        } catch (Exception e) {
            System.err.println("Error setting up Dynamo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void executePutRequest() {

        HashMap<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("args", AttributeValue.builder().s("war-1-2-3").build());
        itemValues.put("iCount", AttributeValue.builder().n("4573498").build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(itemValues)
                .build();

        try {
            PutItemResponse response = dynamoClient.putItem(request);

            System.out.println(TABLE_NAME + " was successfully updated. The request id is " + response.responseMetadata().requestId());
        } catch (Exception e) {
            System.err.println("Deu merda a por itens " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void executeGetRequest() {

        HashMap<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put("args", AttributeValue.builder().s("war-1-2-3").build());

        GetItemRequest request = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(keyToGet)
                .build();

        try {
            Map<String, AttributeValue> returnedItem = dynamoClient.getItem(request).item();

            if (returnedItem != null) {
                Set<String> keys = returnedItem.keySet();
                System.out.println("Attributes: \n");

                for (String key : keys) {
                    System.out.format("%s: %s\n", key, returnedItem.get(key).toString());
                }
            } else {
                System.out.println("Procura banana!");
            }

        } catch (Exception e) {
            System.err.println("Deu merda a encontrar itens " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void storeStatistics() {

        synchronized (methodStatistics) {
            try {
                FileOutputStream fos = new FileOutputStream(filename, true);

                for (MethodStatistic ms : methodStatistics) {
                    String statistic = ms.toString() + "\n";
                    fos.write(statistic.getBytes());
                }

                fos.close();

                methodStatistics.clear();

            } catch (IOException e) {
                System.err.println("Error opening file: " + e.getMessage());
            }
        }
    }
}
