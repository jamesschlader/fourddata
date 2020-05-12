package com.brianandjim.fourddata.entity.models;

import com.brianandjim.fourddata.services.utils.Operator;

public interface ValueFunction {
    Object getValue(NodeValueSpace xSpace, NodeValueSpace ySpace, Operator operator);
}
