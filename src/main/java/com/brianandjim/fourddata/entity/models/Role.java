package com.brianandjim.fourddata.entity.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties("privileges")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer roleId;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    @JoinTable(
            name = "roles_privileges",
            joinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "roleId"),
            inverseJoinColumns = @JoinColumn(
                    name = "privilege_id", referencedColumnName = "privilegeId"))
    private Set<Privilege> privileges;

    public Role(String name){
        this(null, name, new HashSet<>());
    }
}

