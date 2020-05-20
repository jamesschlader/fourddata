package com.brianandjim.fourddata.entity.dtos;

import com.brianandjim.fourddata.entity.models.NodeValueSpace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NodeValueDTO {
    private Long nodeValueId;
    private NodeValueSpace nodeValueSpace;
    private String value;
}
