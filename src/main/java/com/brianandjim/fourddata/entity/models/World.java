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
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties("spaces")
public class World {
    @Id
    @GeneratedValue
    private Long worldId;
    private Timestamp createDate;
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "universeId")
    private Universe universe;
    @OneToMany
    @JoinColumn(name = "nodeValueId")
    private Set<NodeValueSpace> spaces;
}
