package com.brianandjim.fourddata.entity.models;

import com.brianandjim.fourddata.entity.dtos.NodeValueSpaceDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
    private Integer xId;
    @Column(nullable = false)
    @GraphQLQuery
    private Integer yId;
    @GraphQLQuery
    private String name;
    @GraphQLQuery
    private String description;

    @ManyToOne
    @JoinColumn(name = "world_id")
    @GraphQLQuery
    private World world;

    @OneToMany(mappedBy = "nodeValueSpace")
    @GraphQLQuery(name = "values")
    private Set<NodeValue> values;

    @ManyToMany
    @GraphQLQuery(name = "listeners")
    private Set<NodeValueSpace> listeners;

    @ManyToMany
    @GraphQLQuery(name = "watchedSpaces")
    private Set<NodeValueSpace> watchedSpaces;

    @GraphQLQuery(name = "strategy")
    private String strategy;

    @GraphQLQuery(name = "power")
    private Integer power;

    @GraphQLQuery(name = "dataType")
    private String dataType;

    public NodeValueSpace(NodeValueSpaceDTO nodeValueSpaceDTO) {
        this.nodeSpaceId = nodeValueSpaceDTO.getNodeSpaceId();
        this.xId = nodeValueSpaceDTO.getXId();
        this.yId = nodeValueSpaceDTO.getYId();
        this.listeners = new HashSet<>();
        this.watchedSpaces = Objects.nonNull(nodeValueSpaceDTO.getWatchedSpaces()) ? nodeValueSpaceDTO.getWatchedSpaces() :
                new HashSet<>();
        this.name = nodeValueSpaceDTO.getName();
        this.description = nodeValueSpaceDTO.getDescription();
        this.strategy = nodeValueSpaceDTO.getStrategy();
        this.power = nodeValueSpaceDTO.getPower();
        this.dataType = nodeValueSpaceDTO.getDataType();
        this.values = new HashSet<>();
    }

    public void addNodeValueSpaceToListeners(NodeValueSpace space) {
        this.listeners.add(space);
    }

    public NodeValue getLatestValue() {
        return this.values.stream().sorted(Comparator.comparing(NodeValue::getCreateDate).reversed())
                .collect(Collectors.toList()).get(0);
    }

    public void addValue(NodeValue nodeValue) {
        this.dataType = Objects.nonNull(nodeValue.getDoubleValue()) ? "number" : "text";
        this.values.add(nodeValue);
    }

}
