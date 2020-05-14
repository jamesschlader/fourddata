package com.brianandjim.fourddata.entity.dtos;

import com.brianandjim.fourddata.entity.models.Universe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorldDTO {
    private Long worldId;
    private String name;
    private String description;
    private Universe universe;
}
