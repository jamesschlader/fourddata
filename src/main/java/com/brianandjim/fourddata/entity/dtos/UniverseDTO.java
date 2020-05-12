package com.brianandjim.fourddata.entity.dtos;

import com.brianandjim.fourddata.entity.models.World;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UniverseDTO {
    private Long universeId;
    private String name;
    private String description;
    private Set<World> worlds;
}
