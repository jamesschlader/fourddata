package com.brianandjim.fourddata.entity.models;

import com.brianandjim.fourddata.entity.dtos.UniverseDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties("worlds")
public class Universe {
    @Id
    @GeneratedValue
    @GraphQLQuery(name = "universeId")
    private Long universeId;
    @GraphQLQuery(name = "name")
    @Column(unique = true)
    private String name;
    @GraphQLQuery(name = "description")
    private String description;
    @GraphQLQuery(name = "worlds")
    @OneToMany(mappedBy = "universe", cascade = CascadeType.ALL)
    private Set<World> worlds;
    @GraphQLQuery(name = "user")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private FourDDUser user;

    public Universe(UniverseDTO universeDTO) {
        this.universeId = universeDTO.getUniverseId();
        this.name = universeDTO.getName();
        this.description = universeDTO.getDescription();
        this.worlds = new HashSet<>();
        this.user = universeDTO.getUser();
    }

    public Universe editUniverse(UniverseDTO universeDTO){
        this.name = !StringUtils.isEmpty(universeDTO.getName()) ? universeDTO.getName() : this.name;
        this.description = !StringUtils.isEmpty(universeDTO.getDescription()) ? universeDTO.getDescription() :
                this.description;
        return this;
    }

    public void addWorld(World world){
        this.worlds.add(world);
    }

    public void removeWorld(World world){
        this.worlds.remove(world);
    }
}
