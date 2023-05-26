package pt.ulisboa.tecnico.cnv.javassist.tools;

import java.util.List;

public class MethodStatistic{
    List<String> arguments;
    Statistic statistic;

    public MethodStatistic(List<String> args, Statistic statistic){
        this.args = args;
        this.statistic = statistic;
    }

    @Override
    public String toString() {
        return "Args: " + Arrays.toString(args.toArray()) + ":\n" + statistic.toString();
    }
}

