package com.brianandjim.fourddata.entity.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties("values")
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
    @JoinColumn(name = "worldId", nullable = false)
    private World world;

    @OneToMany
    private Set<NodeValue> values;
}
