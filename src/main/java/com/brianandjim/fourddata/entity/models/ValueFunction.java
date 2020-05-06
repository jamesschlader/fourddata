package com.brianandjim.fourddata.entity.models;

public interface ValueFunction {
    Object getValue(NodeValueSpace xSpace, NodeValueSpace ySpace, char operator);
}
