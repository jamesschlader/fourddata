package com.brianandjim.fourddata.entity.dtos;

import com.brianandjim.fourddata.entity.models.NodeValueSpace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NodeValueSpaceDTO {
    private Long nodeSpaceId;
    private Integer xId;
    private Integer yId;
    private Long worldId;
    private String name;
    private String description;
    private String dataType;
    private Set<NodeValueSpace> watchedSpaces;
    private String strategy;
    private Integer power;
}
