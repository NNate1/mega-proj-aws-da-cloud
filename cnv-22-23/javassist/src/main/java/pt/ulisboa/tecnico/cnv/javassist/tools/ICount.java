package pt.ulisboa.tecnico.cnv.javassist.tools;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
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

    private static final Map<Long, Statistic> statisticsMap = new HashMap<>();

    public ICount(List<String> packageNameList, String writeDestination) {
        super(packageNameList, writeDestination);
    }

    public static void incBasicBlock(int position, int length) {
        long tid = Thread.currentThread().getId();
        statisticsMap.computeIfPresent(tid, (banana, statistic) -> statistic.incBasicBlock(position, length));
    }

    public static void incBehavior(String name) {
        long tid = Thread.currentThread().getId();
        statisticsMap.computeIfPresent(tid, (banana, statistic) -> statistic.incBehaviour(name));
    }

    public static void setupStatistics() {
        Statistic st = new Statistic();
        long tid = Thread.currentThread().getId();
        statisticsMap.put(tid, st);
    }


    public static Statistic getStatistic(long tid) {
        return statisticsMap.get(tid);
    }

    @Override
    protected void transform(CtBehavior behavior) throws Exception {

        // int generation = ecosystem.runSimulation(n_generations);
        // String response = insect_wars.war(max, army1, army2);

        if (behavior.getName().equals("incBehaviour") || behavior.getName().equals("incBasicBlock")) return;

        super.transform(behavior);

        behavior.insertAfter(String.format("%s.incBehavior(\"%s\");", ICount.class.getName(), behavior.getLongName()));

        //behavior.insertAfter(String.format("%s.startStatistics(\"%s\", $$);", ICount.class.getName(), behavior.getLongName()));

        /*if (behavior.getName().equals("main")) {
            behavior.insertAfter(String.format("%s.printStatistics();", ICount.class.getName()));
        }*/


        /*String method = behavior.getClass().getName() + "." + behavior.getName();*/
        //System.out.println(behavior.getLongName());
        //System.out.println(behavior.getClass().getName());

        if (behavior.getName().contains("handle")){
            System.out.printf("SUIIIIIIIIIIII NAME: %s%n", behavior.getLongName());
            //behavior.insertBefore(String.format("%s.parse($1);", ICount.class.getName()));

            behavior.insertBefore(String.format("%s.setupStatistics();", ICount.class.getName()));
            //behavior.insertAfter(String.format("%s.printStatistics();", ICount.class.getName()));
        } else {
            System.out.printf("NOOOOOOOOOOOOOOO NAME: %s%n", behavior.getLongName());
        }
    }


    @Override
    protected void transform(BasicBlock block) throws CannotCompileException {
        super.transform(block);
        block.behavior.insertAt(block.line, String.format("%s.incBasicBlock(%s, %s);", ICount.class.getName(), block.getPosition(), block.getLength()));
    }
}