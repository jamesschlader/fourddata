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
@Table(indexes = @Index(
        name = "idx_xid_yid",
        columnList = "x_id, y_id",
        unique = true
))
public class NodeValueSpace {
    @Id
    @GeneratedValue
    @GraphQLQuery
    private Long nodeSpaceId;

    @Column(nullable = false, name = "x_id")
    @GraphQLQuery
    private Integer xId;
    @Column(nullable = false, name = "y_id")
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

    public NodeValueSpace updateNode(NodeValueSpaceDTO nodeValueSpaceDTO) {
        this.xId = Objects.nonNull(nodeValueSpaceDTO.getXId()) ? nodeValueSpaceDTO.getXId() : this.xId;
        this.yId = Objects.nonNull(nodeValueSpaceDTO.getYId()) ? nodeValueSpaceDTO.getYId() : this.yId;
        this.name = Objects.nonNull(nodeValueSpaceDTO.getName()) ? nodeValueSpaceDTO.getName() : this.name;
        this.description = Objects.nonNull(nodeValueSpaceDTO.getDescription()) ? nodeValueSpaceDTO.getDescription() :
                this.description;
        this.dataType = Objects.nonNull(nodeValueSpaceDTO.getDataType()) ? nodeValueSpaceDTO.getDataType() :
                this.dataType;
        this.strategy = Objects.nonNull(nodeValueSpaceDTO.getStrategy()) ? nodeValueSpaceDTO.getStrategy() :
                this.strategy;
        this.power = Objects.nonNull(nodeValueSpaceDTO.getPower()) ? nodeValueSpaceDTO.getPower() : this.power;
        this.watchedSpaces = Objects.nonNull(nodeValueSpaceDTO.getWatchedSpaces()) ?
                nodeValueSpaceDTO.getWatchedSpaces() : this.watchedSpaces;
        return this;
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
