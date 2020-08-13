package com.brianandjim.fourddata.entity.models;

import com.brianandjim.fourddata.entity.dtos.WorldDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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
    private String name;
    @GraphQLQuery
    private String description;
    @ManyToOne
    @JoinColumn(name = "universe_id", nullable = false)
    @GraphQLQuery
    private Universe universe;

    @GraphQLQuery(name = "nodes")
    @OneToMany(mappedBy = "world")
    private Set<NodeValueSpace> nodes;

    public World(WorldDTO worldDTO){
        this.worldId = worldDTO.getWorldId();
        this.description = worldDTO.getDescription();
        this.universe = worldDTO.getUniverse();
    }
}
