package pt.ulisboa.tecnico.cnv.webserver;

import com.sun.net.httpserver.HttpServer;
import pt.ulisboa.tecnico.cnv.compression.CompressImageHandlerImpl;
import pt.ulisboa.tecnico.cnv.foxrabbit.SimulationHandler;
import pt.ulisboa.tecnico.cnv.insectwar.WarSimulationHandler;
import pt.ulisboa.tecnico.cnv.javassist.tools.MethodStatistic;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class WebServer {

    private static final String AWS_REGION = "us-east-1";
    private static final String filename = "statistics.txt";
    //private static final AmazonDynamoDB dynamoDB;
    private static final List<MethodStatistic> methodStatistics = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        /**
        dynamoDB = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new EnvironmentVariableCredentialsProvider())
                .withRegion(AWS_REGION)
                .build();

        String tableName = "my-favorite-intructions-table";

        // Create a table with a primary hash key named 'name', which holds a string
        CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
                .withKeySchema(new KeySchemaElement().withAttributeName("args").withKeyType(KeyType.HASH))
                .withAttributeDefinitions(new AttributeDefinition().withAttributeName("icount").withAttributeType(ScalarAttributeType.S))
                .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L));

        // Create table if it does not exist yet
        TableUtils.createTableIfNotExists(dynamoDB, createTableRequest);
        // wait for the table to move into ACTIVE state
        TableUtils.waitUntilActive(dynamoDB, tableName);

        // Describe our new table
        DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(tableName);
        TableDescription tableDescription = dynamoDB.describeTable(describeTableRequest).getTable();
        System.out.println("Table Description: " + tableDescription);
        */

        Timer timer = new Timer();
        TimerTask dynamite = new TimerTask() {
            @Override
            public void run() {
                storeStatistics();
            }
        };

        timer.schedule(dynamite, 0, 5000);


        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        //server.createContext("/", new RootHandler());
        server.createContext("/simulate", new SimulationHandler(methodStatistics));
        server.createContext("/compressimage", new CompressImageHandlerImpl(methodStatistics));
        server.createContext("/insectwar", new WarSimulationHandler(methodStatistics));
        server.createContext("/test", new RootHandler());
        server.start();
    }

    private static void storeStatistics() {

        synchronized (methodStatistics) {
            try {
                FileOutputStream fos = new FileOutputStream(filename, true);

                for (MethodStatistic ms : methodStatistics) {
                    String statistic = ms.toString()  + "\n";
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
