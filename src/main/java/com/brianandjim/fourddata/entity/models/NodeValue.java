package com.brianandjim.fourddata.entity.models;

import com.brianandjim.fourddata.entity.dtos.NodeValueDTO;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class NodeValue {
    @Id
    @GeneratedValue
    @GraphQLQuery
    private Long nodeValueId;

    @GraphQLQuery
    private String stringValue;

    @GraphQLQuery
    private Double doubleValue;

    @ManyToOne
    @JoinColumn(name = "nodeValueSpaceId", nullable = false)
    @GraphQLQuery
    private NodeValueSpace nodeValueSpace;

    public NodeValue(NodeValueDTO nodeValueDTO){
        this.nodeValueId = nodeValueDTO.getNodeValueId();
        this.nodeValueSpace = nodeValueDTO.getNodeValueSpace();
        try{
            this.doubleValue = Double.parseDouble(nodeValueDTO.getValue());
        } catch (Exception e){
            this.stringValue = nodeValueDTO.getValue();
        }
    }
}
