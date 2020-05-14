package com.brianandjim.fourddata.entity.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UniverseDTO {
    private Long universeId;
    private String name;
    private String description;
}
