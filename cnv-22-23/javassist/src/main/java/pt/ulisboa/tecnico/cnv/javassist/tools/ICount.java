package pt.ulisboa.tecnico.cnv.javassist.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.*;

public class ICount extends CodeDumper {

    /**
     * Number of executed basic blocks.
     */
    private static long nblocks = 0;

    /**
     * Number of executed methods.
     */
    private static long nmethods = 0;

    /**
     * Number of executed instructions.
     */
    private static long ninsts = 0;

    //private static final Map<Integer, Statistic> statisticsMap = new HashMap<>();

    public ICount(List<String> packageNameList, String writeDestination) {
        super(packageNameList, writeDestination);
    }

    public static void incBasicBlock(int tId, int position, int length) {

        //statisticsMap.computeIfPresent(tId, (integer, statistic) -> statistic.incBasicBlock(position, length));
    }

    public static void incBehavior(int tId, String name) {

        //statisticsMap.computeIfPresent(tId, (integer, statistic) -> statistic.incBehavior(name));
    }

    public static void printStatistics() {
        System.out.printf("[%s] Number of executed methods: %s%n", ICount.class.getSimpleName(), nmethods);
        System.out.printf("[%s] Number of executed basic blocks: %s%n", ICount.class.getSimpleName(), nblocks);
        System.out.printf("[%s] Number of executed instructions: %s%n", ICount.class.getSimpleName(), ninsts);
    }

    @Override
    protected void transform(CtBehavior behavior) throws Exception {
        // int generation = ecosystem.runSimulation(n_generations);
        // String response = insect_wars.war(max, army1, army2);

        super.transform(behavior);
        behavior.insertAfter(String.format("%s.incBehavior(\"%s\");", ICount.class.getName(), behavior.getLongName()));
        //behavior.insertAfter(String.format("%s.startStatistics(\"%s\", $$);", ICount.class.getName(), behavior.getLongName()));


        if (behavior.getName().equals("main")) {
            behavior.insertAfter(String.format("%s.printStatistics();", ICount.class.getName()));
        }
    }
}

        /*if (behavior.getName().equals("war")) {
            System.out.printf("\n\n Long name of war: [%s] %s%n", behavior.getLongName(),ICount.class.getSimpleName());
            behavior.insertBefore(String.format("%s.inc(\"%s\");", ICount.class.getName(), behavior.getLongName()));
            behavior.insertAfter(String.format("%s.printStatistics();", ICount.class.getName()));
        }*
    }



    @Override
    protected void transform(BasicBlock block) throws CannotCompileException {
        super.transform(block);
        block.behavior.insertAt(block.line, String.format("%s.incBasicBlock(%s, %s);", ICount.class.getName(), block.getPosition(), block.getLength()));
    }

    public class Statistic {

        private long nBlocks, nMethods, nIntr;

        public Statistic() {
            nBlocks = 0L;
            nMethods = 0L;
            nIntr = 0L;

        }

        public Statistic incBasicBlock(int position, int length) {
            nblocks++;
            ninsts += length;

            return this;
        }

        public Statistic incBehavior(String name) {
            nMethods++;

            return this;
        }
    }
}
