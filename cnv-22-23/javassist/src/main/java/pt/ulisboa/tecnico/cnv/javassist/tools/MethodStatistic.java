package pt.ulisboa.tecnico.cnv.javassist.tools;

import java.util.Arrays;
import java.util.List;

public class MethodStatistic{

    private final List<String> args;
    private final Statistic statistic;

    public MethodStatistic(List<String> args, Statistic statistic){
        this.args = args;
        this.statistic = statistic;
    }

    @Override
    public String toString() {
        return "Args: " + Arrays.toString(args.toArray()) + ":\n" + statistic.toString();
    }
}

