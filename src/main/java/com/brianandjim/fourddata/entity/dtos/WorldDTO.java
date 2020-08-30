package com.brianandjim.fourddata.entity.dtos;

import com.brianandjim.fourddata.entity.models.Universe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorldDTO {
    private String worldId;
    private String name;
    private String description;
    private List<NodeValueSpaceDTO> nodes;
    private Universe universe;
    private Long universeId;
}
