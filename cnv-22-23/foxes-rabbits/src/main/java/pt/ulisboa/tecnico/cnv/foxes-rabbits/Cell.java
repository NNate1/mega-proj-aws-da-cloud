package pt.ulisboa.tecnico.cnv.foxrabbit;

public class Cell {
    
    private int breeding_age;
    private int starving_age;
    private Type type;
    private int moved;

    public Cell() {
        this.breeding_age = 0;
        this.starving_age = 0;
        this.type = Type.EMPTY;
        this.moved = 0;
    }

    public void copy(Cell cell) {
        this.breeding_age = cell.getBreedingAge();
        this.starving_age = cell.getStarvingAge();
        this.type = cell.getType();
        this.moved = cell.getMoved();
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getMoved() {
        return this.moved;
    }

    public void setMoved(int moved) {
        this.moved = moved;
    }

    public void resetCell() {
        this.breeding_age = 0;
        this.starving_age = 0;
        this.type = Type.EMPTY;
        this.moved = 0;
    }

    public void incrBreedingAge() {
        this.breeding_age++;
    }

    public int getBreedingAge() {
        return this.breeding_age;
    }

    public void setBreedingAge(int breeding_age) {
        this.breeding_age = breeding_age;
    }

    public void incrStarvingAge() {
        this.starving_age++;
    }

    public int getStarvingAge() {
        return this.starving_age;
    }

    public void setStarvingAge(int age) {
        this.starving_age = age;
    }
}