package com.brianandjim.fourddata.entity.models;

import com.brianandjim.fourddata.entity.dtos.NodeValueDTO;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

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

    @GraphQLQuery
    private Timestamp createDate;

    @ManyToOne
    @JoinColumn(name = "nodeValueSpaceId", nullable = false)
    @GraphQLQuery
    private NodeValueSpace nodeValueSpace;

    public NodeValue(NodeValueDTO nodeValueDTO){
        this.nodeValueId = nodeValueDTO.getNodeValueId();
        this.nodeValueSpace = nodeValueDTO.getNodeValueSpace();
        this.createDate = new Timestamp(System.currentTimeMillis());
        try{
            this.doubleValue = Double.parseDouble(nodeValueDTO.getValue());
            this.stringValue = nodeValueDTO.getValue();
        } catch (NumberFormatException e){
            this.stringValue = nodeValueDTO.getValue();
    }
    }
}
