package pt.ulisboa.tecnico.cnv.javassist.tools;

import java.util.List;

public class MethodStatistic{
    List<String> arguments;
    Statistic statistic;

    public MethodStatistic(List<String> arguments, Statistic statistic){
        this.arguments = arguments;
        this.statistic = statistic;
    }
}

