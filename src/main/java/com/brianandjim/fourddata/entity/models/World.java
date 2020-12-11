package com.brianandjim.fourddata.entity.models;

import com.brianandjim.fourddata.entity.dtos.WorldDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties("spaces")
public class World {
    @Id
    @GeneratedValue
    @GraphQLQuery
    private Long worldId;
    @GraphQLQuery
    @Column(unique = true)
    private String name;
    @GraphQLQuery
    private String description;
    @ManyToOne
    @JoinColumn(name = "universe_id", nullable = false)
    @GraphQLQuery
    private Universe universe;

    @GraphQLQuery(name = "nodes")
    @OneToMany(mappedBy = "world", cascade = CascadeType.ALL)
    private Set<NodeValueSpace> nodes;

    public World(WorldDTO worldDTO) {
        this.name = worldDTO.getName();
        this.description = worldDTO.getDescription();
        this.universe = worldDTO.getUniverse();
        this.nodes = new HashSet<>();
    }

    public World updateWorld(WorldDTO worldDTO) {
        this.name = Objects.nonNull(worldDTO.getName()) ? worldDTO.getName() : this.name;
        this.description = Objects.nonNull(worldDTO.getDescription()) ? worldDTO.getDescription() : this.description;
        return this;
    }

    public void addNode(NodeValueSpace node) {
        this.nodes.add(node);
    }

    public void removeNode(NodeValueSpace node) {
        this.nodes.remove(node);
    }

    @Override
    public String toString() {
        StringBuilder nodeString = new StringBuilder();
        if (nodes.size() > 0) {
            this.nodes.forEach(node -> nodeString.append(node.toString() + ", "));
            nodeString.deleteCharAt(nodeString.lastIndexOf(", "));
        }
        return "worldId: " + this.worldId + ", name: " + this.name + ", description: " + this.description
                + ", nodes: [ " + nodeString.toString() + " ]";
    }
}
