package pt.ulisboa.tecnico.cnv.foxrabbit;

public class Worlds {

    private int n_worlds;
    private int n_scenarios;
    private World[] worlds;

    private class World {

        private int M;
        private int N;

        public World(int M, int N) {
            this.M = M;
            this.N = N;
        }

        public int getM() {
            return M;
        }
        public int getN() {
            return N;
        }
    }

    public Worlds() {
        this.n_worlds = 4;
        this.n_scenarios = 3;
        this.worlds = new World[this.n_worlds];
        this.worlds[0] = new World(5, 5);
        this.worlds[1] = new World(10, 10);
        this.worlds[2] = new World(20, 20);
        this.worlds[3] = new World(30, 30);
    }

    public int Nscenarios() {
        return this.n_scenarios;
    }
    public int Nworlds() {
        return this.n_worlds;
    }
    public int getM(int world) {
        return this.worlds[world-1].getM();
    }
    public int getN(int world) {
        return this.worlds[world-1].getN();
    }
}