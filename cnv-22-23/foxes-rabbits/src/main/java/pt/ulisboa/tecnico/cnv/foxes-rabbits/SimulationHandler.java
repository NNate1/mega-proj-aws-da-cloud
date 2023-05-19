package pt.ulisboa.tecnico.cnv.foxrabbit;

import java.io.IOException;
import java.io.OutputStream;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URI;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class SimulationHandler implements HttpHandler, RequestHandler<Map<String, String>, String> {

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

        int n_generations = Integer.parseInt(parameters.get("generations"));
        int world = Integer.parseInt(parameters.get("world"));
        int n_scenario = Integer.parseInt(parameters.get("scenario"));

        Ecosystem ecosystem = new Ecosystem(world, n_scenario);
        int generation = ecosystem.runSimulation(n_generations);

        String response = "";
        response += "<p>Simulation finish at generation: " + generation + "</p>";
        response += "<p>Number of rocks: " + ecosystem.countType(Type.ROCK) + "</p>";
        response += "<p>Number of rabbits: " + ecosystem.countType(Type.RABBIT) + "</p>";
        response += "<p>Number of foxes: " + ecosystem.countType(Type.FOX) + "</p>";
        response += ecosystem.getCurrentWorldHtmlTable();

        //System.out.println(response);

        he.sendResponseHeaders(200, response.toString().length());
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());

        os.close();
    }

    public Map<String, String> queryToMap(String query) {
        if(query == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        for(String param : query.split("&")) {
            String[] entry = param.split("=");
            if(entry.length > 1) {
                result.put(entry[0], entry[1]);
            }else{
                result.put(entry[0], "");
            }
        }
        return result;
    }

    private String handleRequest(int generations, int world, int scenario) {
        Ecosystem ecosystem = new Ecosystem(world, scenario);
        int generation = ecosystem.runSimulation(generations);

        String response = "";
        response += "<p>Simulation finish at generation: " + generation + "</p>";
        response += "<p>Number of rocks: " + ecosystem.countType(Type.ROCK) + "</p>";
        response += "<p>Number of rabbits: " + ecosystem.countType(Type.RABBIT) + "</p>";
        response += "<p>Number of foxes: " + ecosystem.countType(Type.FOX) + "</p>";
        response += ecosystem.getCurrentWorldHtmlTable();

        return response;
    }

    @Override
    public String handleRequest(Map<String,String> event, Context context) {
        return handleRequest(Integer.parseInt(event.get("generations")), Integer.parseInt(event.get("world")), Integer.parseInt(event.get("scenario")));
    }
}