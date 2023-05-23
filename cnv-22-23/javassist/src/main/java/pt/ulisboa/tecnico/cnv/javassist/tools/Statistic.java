package pt.ulisboa.tecnico.cnv.javassist.tools;

public class Statistic {

    private long nBlocks, nMethods, nIntr;

    public Statistic() {
        this.nBlocks = 0L;
        this.nMethods = 0L;
        this.nIntr = 0L;
    }

    public Statistic incBasicBlock(int position, int length) {
        this.nBlocks++;
        this.nIntr += length;
        return this;
    }

    public Statistic incBehavior(String name) {
        this.nMethods++;
        return this;
    }

    @Override
    public String toString(){
        return String.format("[%s] Number of executed methods: %s\n", ICount.class.getSimpleName(), nmethods) +
                String.format("[%s] Number of executed basic blocks: %s\n", ICount.class.getSimpleName(), nblocks) +
                String.format("[%s] Number of executed instructions: %s", ICount.class.getSimpleName(), ninsts);
    }
}