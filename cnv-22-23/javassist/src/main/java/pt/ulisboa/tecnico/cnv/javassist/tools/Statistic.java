package pt.ulisboa.tecnico.cnv.javassist.tools;

public class Statistic {

    private long nBlocks, nMethods, nIntr;
    private String method;
    private Object[] args;
    public Statistic(String method, Object[] args) {
        this.nBlocks = 0L;
        this.nMethods = 0L;
        this.nIntr = 0L;
        this.method = method;
        this.args = args;
    }

    public Statistic incBasicBlock(int position, int length) {
        this.nBlocks++;
        this.nIntr += length;
        return this;
    }

    public Statistic incBehaviour(String name) {
        this.nMethods++;
        return this;
    }

    @Override
    public String toString(){
        return String.format("[%s] Method: %s args: %s\n", ICount.class.getSimpleName(), args.toString()) +
                String.format("[%s] Number of executed methods: %s\n", ICount.class.getSimpleName(), nMethods) +
                String.format("[%s] Number of executed basic blocks: %s\n", ICount.class.getSimpleName(), nBlocks) +
                String.format("[%s] Number of executed instructions: %s", ICount.class.getSimpleName(), nIntr);
    }
}