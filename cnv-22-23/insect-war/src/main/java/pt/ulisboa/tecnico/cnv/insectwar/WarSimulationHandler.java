package pt.ulisboa.tecnico.cnv.insectwar;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javassist.tools.web.Webserver;
import pt.ulisboa.tecnico.cnv.javassist.tools.ICount;
import pt.ulisboa.tecnico.cnv.javassist.tools.MethodStatistic;
import pt.ulisboa.tecnico.cnv.javassist.tools.Statistic;
import pt.ulisboa.tecnico.cnv.webserver.WebServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarSimulationHandler implements HttpHandler, RequestHandler<Map<String, String>, String> {

    @Override
    public void handle(HttpExchange he) throws IOException {
        // Handling CORS
        he.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

        if (he.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            he.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
            he.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
            he.sendResponseHeaders(204, -1);
            return;
        }

        // parse request
        URI requestedUri = he.getRequestURI();
        String query = requestedUri.getRawQuery();
        Map<String, String> parameters = queryToMap(query);

        int max = Integer.parseInt(parameters.get("max"));
        int army1 = Integer.parseInt(parameters.get("army1"));
        int army2 = Integer.parseInt(parameters.get("army2"));

        pt.ulisboa.tecnico.cnv.insectwar.InsectWars insect_wars = new pt.ulisboa.tecnico.cnv.insectwar.InsectWars();
        String response = insect_wars.war(max, army1, army2);

        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());

        os.close();

        //------
        Statistic st = ICount.getStatistic(Thread.currentThread().getId());

        System.out.println("Image Compression: " + parameters + " -> " + st);

        WebServer.enrichMethodStatistic(new MethodStatistic(List.of("war", parameters.get("max"), parameters.get("army1"), parameters.get("army2")), st));
    }

    public Map<String, String> queryToMap(String query) {
        if (query == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }

    private String handleRequest(int max, int army1, int army2) {
        pt.ulisboa.tecnico.cnv.insectwar.InsectWars insect_wars = new pt.ulisboa.tecnico.cnv.insectwar.InsectWars();
        String response = insect_wars.war(max, army1, army2);

        return response;
    }

    @Override
    public String handleRequest(Map<String, String> event, Context context) {
        return handleRequest(Integer.parseInt(event.get("max")), Integer.parseInt(event.get("army1")), Integer.parseInt(event.get("army2")));
    }
}