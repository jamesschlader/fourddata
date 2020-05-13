package com.brianandjim.fourddata.entity.models;

import com.brianandjim.fourddata.entity.dtos.WorldDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
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
    @JoinColumn(name = "universeId")
    @GraphQLQuery
    private Universe universe;
    @OneToMany
    @JoinColumn(name = "nodeValueId")
    @GraphQLQuery
    private Set<NodeValueSpace> spaces;

    public World(WorldDTO worldDTO){
        this.worldId = worldDTO.getWorldId();
        this.description = worldDTO.getDescription();
        this.universe = worldDTO.getUniverse();
        this.spaces = new HashSet<>(worldDTO.getSpaces());
    }
}
