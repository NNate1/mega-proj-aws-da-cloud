package pt.ulisboa.tecnico.cnv.foxrabbit;

public class Simulation {
    public static void main(String[] args) throws Exception {
        int n_generations = Integer.parseInt(args[0]);
        int world = Integer.parseInt(args[1]);
        int n_scenario = Integer.parseInt(args[2]);
        
        Ecosystem ecosystem = new Ecosystem(world, n_scenario);

        //ecosystem.printWorld(0, 0);

        int generation = ecosystem.runSimulation(n_generations);

        int final_rocks = ecosystem.countType(Type.ROCK);
        int final_rabbits = ecosystem.countType(Type.RABBIT);
        int final_foxes = ecosystem.countType(Type.FOX);

        System.out.println("generation: " + generation + " | " + final_rocks + " " + final_rabbits + " " + final_foxes);
    }
}