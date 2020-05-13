package com.brianandjim.fourddata.entity.models;

import com.brianandjim.fourddata.entity.dtos.StringValueDTO;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StringValue extends NodeValue {

    @GraphQLQuery
    private String value;

    @ManyToOne
    @JoinColumn(name = "nodeValueSpaceId", nullable = false)
    @GraphQLQuery
    private NodeValueSpace nodeValueSpace;

    public StringValue(StringValueDTO stringValueDTO){
        super(stringValueDTO.getNodeValueId());
        this.value = stringValueDTO.getValue();
        this.nodeValueSpace = stringValueDTO.getNodeValueSpace();
    }
}
