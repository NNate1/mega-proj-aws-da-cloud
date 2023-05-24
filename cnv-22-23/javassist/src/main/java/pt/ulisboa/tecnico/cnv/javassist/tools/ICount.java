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

    private static final Map<Long, Statistic> statisticsMap = new HashMap<>();

    public ICount(List<String> packageNameList, String writeDestination) {
        super(packageNameList, writeDestination);
    }

    public static void incBasicBlock(int position, int length) {
        //statisticsMap.get(Thread.currentThread().getId()).incBasicBlock(position, length);

        //nao devia fazer diferença isso a linha de cima funciona
        //Statistic st = statisticsMap.get(Thread.currentThread().getId());

        long tid = Thread.currentThread().getId();
        Statistic st = statisticsMap.get(tid);

        if (st != null) {
            st.incBasicBlock(position, length);
            /*nblocks++;
            ninsts += length;*/
        }

        //statisticsMap.computeIfPresent(Thread.currentThread().getId(), (banana, statistic) -> statistic.incBasicBlock(position, length));
    }

    public static void incBehavior(String name) {

        long tid = Thread.currentThread().getId();
        Statistic st = statisticsMap.get(tid);
        if (st != null) {
            st.incBehaviour(name);
            //nmethods++;
        }
            //st.incB(name);
        //statisticsMap.computeIfPresent(tid, (banana, statistic) -> statistic.incBehavior(name));
    }

    public static void setupSimulation(String method, Object[] args) {
        Statistic st = new Statistic(method, args);
        long tid = Thread.currentThread().getId();
        statisticsMap.put(tid, st);
    }
    /*public static void setupInsectWar(int max, int sz1, int sz2) {
        Statistic st = new Statistic();
        long tid = Thread.currentThread().getId();
        statisticsMap.put(tid, st);
    }*/

    public static void printAntStatistics() {
        System.out.println(statisticsMap.get(Thread.currentThread().getId()));
    }

    public static void printStatistics() {
       System.out.println(String.format("[%s] Number of executed methods: %s", ICount.class.getSimpleName(), nmethods));
        System.out.println(String.format("[%s] Number of executed basic blocks: %s", ICount.class.getSimpleName(), nblocks));
        System.out.println(String.format("[%s] Number of executed instructions: %s", ICount.class.getSimpleName(), ninsts));
    }


    @Override
        protected void transform(CtBehavior behavior) throws Exception {
        // int generation = ecosystem.runSimulation(n_generations);
        // String response = insect_wars.war(max, army1, army2);

        if (behavior.getName().equals("incBehaviour") || behavior.getName().equals("incBasicBlock"))
            return;

        super.transform(behavior);

        behavior.insertAfter(String.format("%s.incBehavior(\"%s\");", ICount.class.getName(), behavior.getLongName()));

        //behavior.insertAfter(String.format("%s.startStatistics(\"%s\", $$);", ICount.class.getName(), behavior.getLongName()));

        /*if (behavior.getName().equals("main")) {
            behavior.insertAfter(String.format("%s.printStatistics();", ICount.class.getName()));
        }*/


        if (behavior.getName().equals("war")) {
            System.out.println(String.format("LONG NAME OF WAR: %s", behavior.getLongName()));

            behavior.insertBefore(String.format("%s.setupSimulation(\"%s\", $args);", ICount.class.getName(), behavior.getName()));
            //behavior.insertBefore(String.format("%s.setupInsectWar($1, $2, $3);", ICount.class.getName()));
            behavior.insertAfter(String.format("%s.printAntStatistics();", ICount.class.getName()));
        }
    }

    @Override
    protected void transform(BasicBlock block) throws CannotCompileException {
        super.transform(block);
        block.behavior.insertAt(block.line, String.format("%s.incBasicBlock(%s, %s);", ICount.class.getName(), block.getPosition(), block.getLength()));
    }
}