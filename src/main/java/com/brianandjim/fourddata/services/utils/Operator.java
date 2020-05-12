package com.brianandjim.fourddata.services.utils;

public interface Operator {
    default Double sum(Number x, Number y, Integer places) {
        if (places != null){
            String format = "%." + places + "f";
            return Double.parseDouble(String.format(format, Double.sum((Double) x, (Double) y)));
        }
        return Double.sum((Double) x, (Double) y);
    }

    default Double sum(Number x, Number y){
        return Double.sum((Double) x, (Double) y);
    }

}
