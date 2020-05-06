package com.brianandjim.fourddata.entity.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NodeValueSpace {
    @Id
    @GeneratedValue
    private Long nodeSpaceId;
    private Timestamp createDate;

    @Column(nullable = false, unique = true)
    private Long xId;
    @Column(nullable = false, unique = true)
    private Long yId;

    @ManyToOne(fetch = FetchType.LAZY)
    private World world;

    @OneToMany
    private Set<NodeValue> values;
}
