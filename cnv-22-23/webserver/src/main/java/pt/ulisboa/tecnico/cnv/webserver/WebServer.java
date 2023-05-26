package pt.ulisboa.tecnico.cnv.webserver;

import java.net.InetSocketAddress;
import java.util.*;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.sun.net.httpserver.HttpServer;

import pt.ulisboa.tecnico.cnv.foxrabbit.SimulationHandler;
import pt.ulisboa.tecnico.cnv.compression.CompressImageHandlerImpl;
import pt.ulisboa.tecnico.cnv.insectwar.WarSimulationHandler;
import pt.ulisboa.tecnico.cnv.javassist.tools.MethodStatistic;

public class WebServer {

    private static final List<MethodStatistic> methodStatistics = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        TimerTask dynamite = new TimerTask() {
            @Override
            public void run() {
               storeStatistics();
            }
        };

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        //server.createContext("/", new RootHandler());
        server.createContext("/simulate", new SimulationHandler(methodStatistics));
        server.createContext("/compressimage", new CompressImageHandlerImpl(methodStatistics));
        server.createContext("/insectwar", new WarSimulationHandler(methodStatistics));
        server.start();
    }

    private static void storeStatistics() {
        synchronized (methodStatistics) {

        }
    }
}

/*
Script para criar imagem
Por Threads
Load Balancer e AutoScaling Group da aws
*/
