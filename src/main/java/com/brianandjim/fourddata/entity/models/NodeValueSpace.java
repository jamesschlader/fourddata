package com.brianandjim.fourddata.entity.models;

import com.brianandjim.fourddata.entity.dtos.NodeValueSpaceDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
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

    @Column(nullable = false, unique = true)
    @GraphQLQuery
    private Long xId;
    @Column(nullable = false, unique = true)
    @GraphQLQuery
    private Long yId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worldId", nullable = false)
    @GraphQLQuery
    private World world;

    public NodeValueSpace(NodeValueSpaceDTO nodeValueSpaceDTO){
        this.nodeSpaceId = nodeValueSpaceDTO.getNodeSpaceId();
        this.xId = nodeValueSpaceDTO.getXId();
        this.yId = nodeValueSpaceDTO.getYId();
        this.world = nodeValueSpaceDTO.getWorld();
    }
}
