package com.brianandjim.fourddata.entity.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DoubleValue extends NodeValue {

    private Double value;

    @ManyToOne
    @JoinColumn(name = "nodeValueSpaceId", nullable = false)
    private NodeValueSpace nodeValueSpace;

    public void setValue(NodeValueSpace x, NodeValueSpace y, char operator){

    }
}
