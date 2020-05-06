package com.brianandjim.fourddata.entity.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
abstract class NodeValue implements ValueFunction{
    @Id
    @GeneratedValue
    private Long nodeValueId;
    private Timestamp createDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private NodeValueSpace nodeValueSpace;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeValue)) return false;
        return nodeValueId != null && nodeValueId.equals(((NodeValue) o).getNodeValueId());
    }
}
