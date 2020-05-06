package com.brianandjim.fourddata.entity.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StringValue extends NodeValue {

    private String value;

    @Override
    public String getValue(NodeValueSpace xSpace, NodeValueSpace ySpace, char operator) {
        return null;
    }
}
