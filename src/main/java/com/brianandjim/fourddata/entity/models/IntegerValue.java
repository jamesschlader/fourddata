package com.brianandjim.fourddata.entity.models;

import com.brianandjim.fourddata.entity.dtos.IntegerValueDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IntegerValue extends NodeValue {

    private Integer value;

    @ManyToOne
    @JoinColumn(name = "nodeValueSpaceId", nullable = false)
    private NodeValueSpace nodeValueSpace;

    public IntegerValue(IntegerValueDTO integerValueDTO){
        super(integerValueDTO.getNodeValueId());
        this.value = integerValueDTO.getValue();
        this.nodeValueSpace = integerValueDTO.getNodeValueSpace();
    }
}
