package com.brianandjim.fourddata.entity.models;

import com.brianandjim.fourddata.entity.dtos.NodeValueSpaceDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties("values")
public class NodeValueSpace {
    @Id
    @GeneratedValue
    @GraphQLQuery
    private Long nodeSpaceId;

    @Column(nullable = false)
    @GraphQLQuery
    private Long xId;
    @Column(nullable = false)
    @GraphQLQuery
    private Long yId;

    @ManyToOne
    @JoinColumn(name = "world_id", nullable = false)
    @GraphQLQuery
    private World world;

    @OneToMany(mappedBy = "nodeValueSpace")
    @GraphQLQuery(name = "values")
    private Set<NodeValue> values;

    public NodeValueSpace(NodeValueSpaceDTO nodeValueSpaceDTO){
        this.nodeSpaceId = nodeValueSpaceDTO.getNodeSpaceId();
        this.xId = nodeValueSpaceDTO.getXId();
        this.yId = nodeValueSpaceDTO.getYId();
        this.world = nodeValueSpaceDTO.getWorld();
    }

    public NodeValue getLastValue(){
        return (NodeValue)this.values.stream()
                .sorted((nodeValue1, nodeValue2 ) -> nodeValue1.getCreateDate().compareTo(nodeValue2.getCreateDate()))
                .toArray()[0];
    }
}
