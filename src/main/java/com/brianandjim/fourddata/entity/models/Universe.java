package com.brianandjim.fourddata.entity.models;

import com.brianandjim.fourddata.entity.dtos.UniverseDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private Long universeId;
    private String name;
    private String description;
    @OneToMany
    @JoinColumn(name = "worldId")
    private Set<World> worlds;

    public Universe(UniverseDTO universeDTO) {
        this.universeId = universeDTO.getUniverseId();
        this.name = universeDTO.getName();
        this.description = universeDTO.getDescription();
        this.worlds = new HashSet<>(universeDTO.getWorlds());
    }
}
