package com.brianandjim.fourddata.entity.models;

import com.brianandjim.fourddata.entity.dtos.LongValueDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LongValue extends NodeValue {

    private Long value;

    @ManyToOne
    @JoinColumn(name = "nodeValueSpaceId", nullable = false)
    private NodeValueSpace nodeValueSpace;

    public LongValue(LongValueDTO longValueDTO){
        super(longValueDTO.getNodeValueId());
        this.value = longValueDTO.getValue();
        this.nodeValueSpace = longValueDTO.getNodeValueSpace();
    }
}
