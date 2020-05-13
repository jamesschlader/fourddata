package com.brianandjim.fourddata.entity.models;

import com.brianandjim.fourddata.entity.dtos.UniverseDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties("worlds")
public class Universe {
    @Id
    @GeneratedValue
    @GraphQLQuery(name="universeId")
    private Long universeId;
    @GraphQLQuery(name="name")
    private String name;
    @GraphQLQuery(name="description")
    private String description;
    @OneToMany
    @JoinColumn(name = "worldId")
    @GraphQLQuery(name="worlds")
    private Set<World> worlds;

    public Universe(UniverseDTO universeDTO) {
        this.universeId = universeDTO.getUniverseId();
        this.name = universeDTO.getName();
        this.description = universeDTO.getDescription();
        this.worlds = new HashSet<>(universeDTO.getWorlds());
    }
}
