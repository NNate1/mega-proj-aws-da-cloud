package pt.ulisboa.tecnico.cnv.compression;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class WebServer {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/compressimage", new CompressImageHandlerImpl());
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        server.start();
    }
}
