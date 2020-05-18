package com.brianandjim.fourddata.entity.dtos;

import com.brianandjim.fourddata.entity.models.World;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NodeValueSpaceDTO {
    private Long nodeSpaceId;
    private Long xId;
    private Long yId;
    private World world;
}
