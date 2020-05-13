package com.brianandjim.fourddata.entity.models;

import com.brianandjim.fourddata.entity.dtos.DoubleValueDTO;
import io.leangen.graphql.annotations.GraphQLQuery;
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

    @GraphQLQuery
    private Double value;

    @ManyToOne
    @JoinColumn(name = "nodeValueSpaceId", nullable = false)
    @GraphQLQuery
    private NodeValueSpace nodeValueSpace;

    public DoubleValue(DoubleValueDTO doubleValueDTO){
        super(doubleValueDTO.getNodeValueId());
        this.value = doubleValueDTO.getValue();
        this.nodeValueSpace = doubleValueDTO.getNodeValueSpace();
    }

    public void setValue(NodeValueSpace x, NodeValueSpace y, char operator){

    }
}
