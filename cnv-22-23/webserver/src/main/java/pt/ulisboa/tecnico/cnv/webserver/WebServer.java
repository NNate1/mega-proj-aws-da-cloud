package pt.ulisboa.tecnico.cnv.webserver;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sun.net.httpserver.HttpServer;

import pt.ulisboa.tecnico.cnv.foxrabbit.SimulationHandler;
import pt.ulisboa.tecnico.cnv.compression.CompressImageHandlerImpl;
import pt.ulisboa.tecnico.cnv.insectwar.WarSimulationHandler;
import pt.ulisboa.tecnico.cnv.javassist.tools.MethodStatistic;

public class WebServer {

    private static final List<MethodStatistic> methodStatistics = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        //server.createContext("/", new RootHandler());
        server.createContext("/simulate", new SimulationHandler());
        server.createContext("/compressimage", new CompressImageHandlerImpl());
        server.createContext("/insectwar", new WarSimulationHandler());
        server.start();
    }


    public static void enrichMethodStatistic (final MethodStatistic methodStatistic){
        synchronized (methodStatistic) {
            methodStatistics.add(methodStatistic);
        }
    }
}

/*
Script para criar imagem
Por Threads
Load Balancer e AutoScaling Group da aws
*/
