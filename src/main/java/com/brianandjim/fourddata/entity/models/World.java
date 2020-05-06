package com.brianandjim.fourddata.entity.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class World {
    @Id
    @GeneratedValue
    private Long worldId;
    private Timestamp createDate;
    private String name;
    private String description;
    @ManyToOne
    private Universe universe;
}
