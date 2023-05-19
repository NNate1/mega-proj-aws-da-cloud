package pt.ulisboa.tecnico.cnv.insectwar;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;


public class InsectWars {

	public final static int COLONY_SIZE=5000; // times two for different insects in each army

    public static void main(String[] args) {
        if (args.length < 3 ){
            System.out.println("InsectWars  <max-rounds> <num-colonies--army-1> <num-colonies-army-2>");
            System.exit(-1);
        }        

        int max=Integer.parseInt(args[0]), sz1 = Integer.parseInt(args[1]), sz2 = Integer.parseInt(args[2]);

        InsectWars insect_wars = new InsectWars();
        System.out.println(insect_wars.war(max, sz1, sz2));
    }

    public String war(int max, int sz1, int sz2) {
        sz1 = sz1*COLONY_SIZE;
        sz2 = sz2*COLONY_SIZE;

        // Create two insect armies
        InsectArmy army1 = new InsectArmy("Army 1");
        InsectArmy army2 = new InsectArmy("Army 2");
        Insect attacker, defender;

        int i, j;
        // Populate the armies with insects
        for (i=0;i<sz1;i++){
            army1.addInsect(new Insect("Ant"+i, 150, 1));
            army1.addInsect(new Insect("Bee"+i, 100, 2));
        }

        for (i=0;i<sz2;i++){
            army2.addInsect(new Insect("Spider"+i, 120, 3));
            army2.addInsect(new Insect("Beetle"+i, 100, 1));
        }

        // Simulate the war
        i= 0;
        
        // max iters
        while (!army1.isEmpty() && !army2.isEmpty() && ++i<max) {
            //	System.out.println("Iteration " + i );
            for (j=0; j < sz1 /10; j++){
                // Randomly select an insect from each army
                if (i % 2 == 0) {
                    attacker = army1.getRandomInsect();
                    defender = army2.getRandomInsect();
                } else {
                    attacker = army2.getRandomInsect();
                    defender = army1.getRandomInsect();
                }
                // Calculate the damage inflicted by the attacker
                int damage = attacker.attack();

                // Reduce defender's health by the damage
                defender.reduceHealth(damage);
            

                // Print the battle result
                if  (i % 2 == 0){
                    //System.out.println(attacker.getName() + " (Army 1) attacks " + defender.getName() + " (Army 2) and deals " + damage + " damage.");
                } else {
                    //System.out.println(attacker.getName() + " (Army 2) attacks " + defender.getName() + " (Army 1) and deals " + damage + " damage.");
                }
            }

            // Remove the dead insects from their armies
            army1.removeDeadInsects();
            army2.removeDeadInsects();

            // Print the current army status
            //System.out.println("Army 1: " + army1.getInsectCount() + " insects remaining.");
            //System.out.println("Army 2: " + army2.getInsectCount() + " insects remaining.");
            //System.out.println();
        }

        // Determine the winner
        army1.removeDeadInsects();
        army2.removeDeadInsects();

        String response = "";
        response += "<br>Final iteration: " + i + " <br>Army1 size: " + army1.getInsectCount() + " <br>Army2 size: " + army2.getInsectCount() + "\n";
        if (army1.getInsectCount() == army2.getInsectCount()) {
            response += "<br>The war ends in a draw!\n";
        } else if (army1.getInsectCount() < army2.getInsectCount()) {
            response += "<br>Army 2 wins the war!\n";
        } else {
            response += "<br>Army 1 wins the war!\n";
        }
        
        return response;
    }
}


class Insect {
    private String name;
    private int health;
    private int attackPower;

    public Insect(String name, int health, int attackPower) {
        this.name = name;
        this.health = health;
        this.attackPower = attackPower;
    }

    public String getName() {
        return name;
    }

    public int attack() {
        return attackPower;
    }

    public void reduceHealth(int damage) {
        health -= damage;
    }

    public boolean isDead() {
        return health <= 0;
    }
}

class InsectArmy {
    private String name;
    private List<Insect> insects;

    public InsectArmy(String name) {
        this.name = name;
        this.insects = new ArrayList<>();
    }

    public void addInsect(Insect insect) {
        insects.add(insect);
    }

    public Insect getRandomInsect() {
        Random rand = new Random();
        int index = rand.nextInt(insects.size());
        return insects.get(index);
    }

    public void removeDeadInsects() {
        insects.removeIf(Insect::isDead);
    }

    public int getInsectCount() {
        return insects.size();
    }

    public boolean isEmpty() {
        return insects.isEmpty();
    }
}
