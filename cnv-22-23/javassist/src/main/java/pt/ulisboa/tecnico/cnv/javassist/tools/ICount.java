package pt.ulisboa.tecnico.cnv.javassist.tools;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javassist.CannotCompileException;
import javassist.CtBehavior;

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
        statisticsMap.computeIfPresent(Thread.currentThread().getId(), (banana, statistic) -> statistic.incBasicBlock(position, length));
    }

    public static void incBehavior(String name) {
        statisticsMap.computeIfPresent(Thread.currentThread().getId(), (banana, statistic) -> statistic.incBehavior(name));
    }

    public static void executeInsectWar(int max, int sz1, int sz2) {
        Statistic st = new Statistic();
        long tid = Thread.currentThread().getId();
        statisticsMap.put(tid, st);
    }

    public static void printAntStatistics() {
        System.out.println(statisticsMap.get(Thread.currentThread().getId()));
    }

    /*public static void printStatistics() {
        System.out.println(String.format("[%s] Number of executed methods: %s", ICount.class.getSimpleName(), nmethods));
        System.out.println(String.format("[%s] Number of executed basic blocks: %s", ICount.class.getSimpleName(), nblocks));
        System.out.println(String.format("[%s] Number of executed instructions: %s", ICount.class.getSimpleName(), ninsts));
    }*/

    @Override
    protected void transform(CtBehavior behavior) throws Exception {
        super.transform(behavior);
        behavior.insertAfter(String.format("%s.incBehavior(\"%s\");", ICount.class.getName(), behavior.getLongName()));

        /*if (behavior.getName().equals("main")) {
            behavior.insertAfter(String.format("%s.printStatistics();", ICount.class.getName()));
        }*/

        if (behavior.getName().equals("war")) {
            System.out.println(String.format("LONG NAME OF WAR: %s", behavior.getLongName()));
            behavior.insertBefore(String.format("%s.executeInsectWar($1, $2, $3);", ICount.class.getName()));
            behavior.insertAfter(String.format("%s.printAntStatistics();", ICount.class.getName()));
        }
    }

    @Override
    protected void transform(BasicBlock block) throws CannotCompileException {
        super.transform(block);
        block.behavior.insertAt(block.line, String.format("%s.incBasicBlock(%s, %s);", ICount.class.getName(), block.getPosition(), block.getLength()));
    }
}
