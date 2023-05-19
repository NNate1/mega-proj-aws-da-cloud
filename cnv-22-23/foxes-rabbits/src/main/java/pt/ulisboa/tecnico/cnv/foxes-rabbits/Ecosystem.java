package pt.ulisboa.tecnico.cnv.foxrabbit;

import java.util.Random;
import java.util.Scanner;
import java.io.InputStream;

public class Ecosystem {

    private int M;
    private int N;
    private int rabbit_breeding;
    private int fox_breeding;
    private int fox_starvation;
    
    private static Worlds worlds = new Worlds();
    
    private Cell[] board;
    private Cell[] aux_board;

    private static final int DOMINANCE = 50;

    public Ecosystem(int world, int n_scenario) {
        this.M = worlds.getM(world);
        this.N = worlds.getN(world);
        this.rabbit_breeding = 3;
        this.fox_breeding = 6;
        this.fox_starvation = 10;

        this.board = new Cell[this.M*this.N];
        this.aux_board = new Cell[this.M*this.N];
        for(int i = 0; i < this.M; i++) {
            for(int j = 0; j < this.N; j++) {
                this.board[i*this.N+j] = new Cell();
                this.aux_board[i*this.N+j] = new Cell();
            }
        }
        populate(world, n_scenario);
    }

    private void insertAnimal(int i, int j, Type type) {
        this.board[i*this.N+j].setType(type);
        this.aux_board[i*this.N+j].setType(type);
    }

    public void populate(int world, int n_scenario) {
        //File file = new File("resources/" + world + "-" + n_scenario + ".txt");
        InputStream in = getClass().getResourceAsStream("/" + world + "-" + n_scenario + ".txt");
        Scanner scanner = new Scanner(in);

        String line = scanner.nextLine();
        String[] pos;
        Type type;
        for(int i = 0; i < this.M; i++) {
            pos = scanner.nextLine().substring(3).split("\\|");
            for(int j = 0; j < this.N; j++) {
                switch(pos[j]) {
                    case " *":
                        type = Type.ROCK;
                        break;
                    case " R":
                        type = Type.RABBIT;
                        break;
                    case " F":
                        type = Type.FOX;
                        break;
                    default:
                        type = Type.EMPTY;
                        break;
                }
                if(type != Type.EMPTY) {
                    insertAnimal(i, j, type);
                }
            }
        }
    }

    private int moveTo(int posI, int posJ, Type destin) {
        int p = 0;     //possible moves
        int[] pos = new int[4]; //possible positions moves

        if(posI > 0 && this.aux_board[(posI-1)*this.N+posJ].getType() == destin) { //try up
            pos[p] = (posI-1)*this.N+posJ;
            p++;
        }
        if(posJ < this.N-1 && this.aux_board[posI*this.N+(posJ+1)].getType() == destin) { //try right
            pos[p] = posI*this.N+(posJ+1);
            p++;
        }
        if(posI < this.M-1 && this.aux_board[(posI+1)*this.N+posJ].getType() == destin) { //try down
            pos[p] = (posI+1)*this.N+posJ;
            p++;
        }
        if(posJ > 0 && this.aux_board[(posI*this.N)+(posJ-1)].getType() == destin) { //try left
            pos[p] = (posI*this.N)+(posJ-1);
            p++;
        }
        if(p > 0){
            int move = ((posI*this.N)+posJ)%p;
            return pos[move];
        }
        return -1;
    }

    private void rabbitMove(int posI, int posJ, int generation, int parity) {
        int pos = posI*this.N+posJ;
        this.board[pos].incrBreedingAge();
        int new_pos;

        //Check for empty spaces around
        new_pos = moveTo(posI, posJ, Type.EMPTY);
        if(new_pos != -1) {
            //Conflict: rabbit/fox
            if(this.board[new_pos].getType() == Type.FOX) {
                //Set fox starving_age to 0
                //Fox no longer dies at the end of the generation (if moved=-1)
                this.board[new_pos].setStarvingAge(0);
                this.board[new_pos].setMoved(generation);
            }
            //Conflict: rabbit/rabbit
            else if(this.board[new_pos].getType() == Type.RABBIT) {
                //If a rabbit is going to have a child, it competes with breeding_age at 0 and never beats another rabbit
                if(this.rabbit_breeding > this.board[pos].getBreedingAge() && this.board[new_pos].getBreedingAge() < this.board[pos].getBreedingAge()){
                    this.board[new_pos].setBreedingAge(this.board[pos].getBreedingAge());
                }
            }
            //No conflict
            else {
                this.board[new_pos].copy(this.board[pos]);
                this.board[new_pos].setMoved(generation);

                if(this.rabbit_breeding <= this.board[pos].getBreedingAge()){
                    this.board[new_pos].setBreedingAge(0);
                }
            }

            if(this.rabbit_breeding > this.board[pos].getBreedingAge()){
                this.board[pos].setType(Type.EMPTY);
            }

            this.board[pos].setBreedingAge(0);
            this.board[pos].setStarvingAge(0);
        }
    }

    private void foxMove(int posI, int posJ, int generation, int parity) {
        int pos = posI*this.N+posJ;
        this.board[pos].incrBreedingAge();
        this.board[pos].incrStarvingAge();
        int new_pos;

        //Check for rabbits around
        new_pos = moveTo(posI, posJ, Type.RABBIT);
        if(new_pos == -1){ //if there are no rabbits
            //Check for empty spaces around
            new_pos = moveTo(posI, posJ, Type.EMPTY);
        }

        if(new_pos != -1) {
            
            //Make sure it hasn't been eaten by another fox
            if(this.board[new_pos].getType() == Type.RABBIT){
                this.board[pos].setStarvingAge(0);
            }
            //Conflict: fox/fox
            if(this.board[new_pos].getType() == Type.FOX) {
                //Wins fox with higher breeding_age (if fox will have a child competes with breeding_age 0)
                //In case of a tie, the fox has the lowest starving_age

                if(this.fox_breeding > this.board[pos].getBreedingAge() && this.board[new_pos].getBreedingAge() < this.board[pos].getBreedingAge()){
                    this.board[new_pos].setBreedingAge(this.board[pos].getBreedingAge());
                    //Conflict: fox/fox/rabbit
                    if(this.board[new_pos].getStarvingAge() != 0){
                        this.board[new_pos].setStarvingAge(this.board[pos].getStarvingAge());
                    }
                } 
                else if(this.fox_breeding > this.board[pos].getBreedingAge() && this.board[new_pos].getBreedingAge() == this.board[pos].getBreedingAge()) {
                    if(this.board[new_pos].getStarvingAge() > this.board[pos].getStarvingAge()){
                        this.board[new_pos].setStarvingAge(this.board[pos].getStarvingAge());
                    }
                }
                //Case in which the two foxes have a child
                else if(this.fox_breeding <= this.board[pos].getBreedingAge() && this.board[new_pos].getBreedingAge() == 0){
                    if(this.board[new_pos].getStarvingAge() > this.board[pos].getStarvingAge()){
                        this.board[new_pos].setStarvingAge(this.board[pos].getStarvingAge());
                    }
                }
                this.board[new_pos].setMoved(generation);
            }
            else {
                //Move fox to new position
                this.board[new_pos].copy(this.board[pos]);
                this.board[new_pos].setMoved(generation);

                if(this.fox_breeding <= this.board[pos].getBreedingAge()){
                    this.board[new_pos].setBreedingAge(0);
                }
            }

            if(this.fox_breeding > this.board[pos].getBreedingAge()){
                this.board[pos].setType(Type.EMPTY);
            }

            this.board[pos].setBreedingAge(0);
            this.board[pos].setStarvingAge(0);

            //If it reached the starvation age, put moved=-1 to kill at the end of the generation
            if(this.board[new_pos].getStarvingAge() == this.fox_starvation) {
                this.board[new_pos].setMoved(-1);
            }
        }

        else if(new_pos == -1 && this.board[pos].getStarvingAge() == this.fox_starvation) {
            this.board[pos].setMoved(-1);
        }
    }

    private void runPos(int j, int k, int parity, int generation, int pos) {
        //Move animals that haven't moved in this generation
        if(this.aux_board[pos].getMoved() != -1 && this.aux_board[pos].getMoved() < generation) {
            if(this.aux_board[pos].getType() == Type.RABBIT){
                rabbitMove(j, k, generation, parity);
            }
            else{
                foxMove(j, k, generation, parity);
            }
        }
    }

    public int runSimulation(int n_generations) {
        int last_live_rabbits = 0;
        int last_live_foxes = 0;
        int i;
        for(i = 1; i <= n_generations; i++) {
            //Red sub-generation
            int parity = 0;
            int generation = i;
            int pos = 0;

            for(int j = 0; j < this.M; j++){
                for(int k = (j+parity)%2; k < this.N; k+=2){
                    pos = j*this.N+k;
                    if(this.aux_board[pos].getType() == Type.EMPTY || this.aux_board[pos].getType() == Type.ROCK){
                        continue;
                    }
                    runPos(j, k, parity, generation, pos);
                }
            }

            //printWorld(i, 0);

            for (int j = 0; j < this.M*this.N; j++) {
                this.aux_board[j].copy(this.board[j]);
            }

            //Black sub-generation
            parity = 1;
            for(int j = 0; j < this.M; j++){
                for(int k = (j+parity)%2; k < this.N; k+=2){
                    pos = j*this.N+k;
                    if(this.aux_board[pos].getType() == Type.EMPTY || this.aux_board[pos].getType() == Type.ROCK){
                        continue;
                    }
                    runPos(j, k, parity, generation, pos);
                }
            }

            for (int j = 0; j < this.M*this.N; j++) {
                //Killing foxes with starvation age on the limit (moved=-1)
                if (this.board[j].getMoved() == -1) {
                    this.board[j].resetCell();
                }
                this.aux_board[j].copy(this.board[j]);
            }
            
            //printWorld(i, 1);

            int live_rabbits = countType(Type.RABBIT);
            int live_foxes = countType(Type.FOX);
            
            //everybody died
            if(live_rabbits == 0 || live_foxes == 0) {
                return i;
            }
            //The configuration of the animals is maintained for two consecutive generations and there is dominance of one species
            else if((last_live_rabbits == live_rabbits && last_live_foxes == live_foxes) 
                        && (live_rabbits / live_foxes > DOMINANCE || live_foxes / live_rabbits > DOMINANCE)) {
                return i;
            }

            last_live_rabbits = live_rabbits;
            last_live_foxes = live_foxes;
        }
        return n_generations;
    }

    public void printWorld(int generation, int parity) {
        String color = ((parity == 0) ? "red" : "black");

        System.out.println("Generation " + generation + ", " + color);
        for(int i = 0; i <= this.N; i++){
            System.out.print("---");
        }
        System.out.print("\n   ");
        for(int i = 0; i < N; i++){
            System.out.printf("%02d|", i);
        }
        System.out.println();
        for(int i = 0; i < M; i++){
            System.out.printf("%02d:", i);
            for(int j = 0; j < N; j++){
                switch(this.board[i*N+j].getType()){
                    case EMPTY:
                        System.out.print("  |");
                        break;
                    case ROCK:
                        System.out.print(" *|");
                        break;
                    case RABBIT:
                        System.out.printf(" R|");
                        break;
                    case FOX:
                        System.out.printf(" F|");
                        break;
                }
            }
            System.out.println();
        }
    }

    public String getCurrentWorldHtmlTable() {
        String world = "<style>table, th, td {border:1px solid black;} td { width:40px; height:40px; }</style>";

        world += "<table><tr><td></td>";
        for(int i = 0; i < N; i++){
            world += "<td>" + String.format("%02d", i) + "</td>";
        }
        world += "</tr>";
        for(int i = 0; i < M; i++){
            world += "<tr><td>" + String.format("%02d:", i) + "</td>";
            for(int j = 0; j < N; j++){
                world += "<td>";
                switch(this.board[i*N+j].getType()){
                    case EMPTY:
                        //world += "&nbsp;&nbsp;|";
                        break;
                    case ROCK:
                        world += "<img src=\"https://grupos.ist.utl.pt/~meic-cnv.daemon/project/rock.png\" width=\"40\" height=\"40\">";
                        break;
                    case RABBIT:
                        world += "<img src=\"https://grupos.ist.utl.pt/~meic-cnv.daemon/project/rabbit.png\" width=\"40\" height=\"40\">";
                        break;
                    case FOX:
                        world += "<img src=\"https://grupos.ist.utl.pt/~meic-cnv.daemon/project/fox.png\" width=\"40\" height=\"40\">";
                        break;
                }
                world += "</td>";
            }
            world += "</tr>";
        }
        return world + "</table>";
    }

    public int countType(Type type) {
        int count = 0;

        for(int i = 0; i < this.M*this.N; i++){
            if(this.board[i].getType() == type){
                count++;
            }
        }

        return count;
    }

}