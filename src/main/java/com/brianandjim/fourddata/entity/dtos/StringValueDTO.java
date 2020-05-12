package com.brianandjim.fourddata.entity.dtos;

import com.brianandjim.fourddata.entity.models.NodeValueSpace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StringValueDTO {
    private Long nodeValueId;
    private String value;
    private NodeValueSpace nodeValueSpace;
}
