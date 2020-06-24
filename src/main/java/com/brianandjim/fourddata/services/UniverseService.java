package com.brianandjim.fourddata.services;

import com.brianandjim.fourddata.entity.dao.UniverseDao;
import com.brianandjim.fourddata.entity.dao.WorldDao;
import com.brianandjim.fourddata.entity.dtos.UniverseDTO;
import com.brianandjim.fourddata.entity.dtos.WorldDTO;
import com.brianandjim.fourddata.entity.models.Universe;
import com.brianandjim.fourddata.entity.models.World;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@GraphQLApi
@Slf4j
@PreAuthorize(value = "hasAnyAuthority('READ_PRIVILEGE')")
public class UniverseService {

    private final UniverseDao universeDao;
    private final WorldDao worldDao;

    public UniverseService(UniverseDao universeDao, WorldDao worldDao) {
        this.universeDao = universeDao;
        this.worldDao = worldDao;
    }

    @GraphQLQuery(name = "universes")
    public List<Universe> getAllUniverses() {
        return universeDao.findAll();
    }

    @GraphQLQuery(name = "universeById")
    public Universe getOneUniverse(@GraphQLArgument(name = "universeId") Long id) {
        return universeDao.getOne(id);
    }

    @GraphQLQuery(name = "getUniverseByName")
    public Universe getUniverseByName(@GraphQLArgument(name = "name") String name){
        return universeDao.findFirstByName(name);
    }

    @GraphQLMutation(name = "createUniverse")
    public Universe createUniverse(@GraphQLArgument(name = "universe") UniverseDTO universeDTO) {
        return universeDao.saveAndFlush(new Universe(universeDTO));
    }

    @GraphQLMutation(name = "addWorldToUniverse")
    public Universe addWorldToUniverse(@GraphQLArgument(name = "universeId") Long universeId, @GraphQLArgument(name =
            "worldId") Long worldId) {
        Optional<Universe> universe = universeDao.findById(universeId);
        Optional<World> existingWorld = worldDao.findById(worldId);
        if (universe.isPresent() && existingWorld.isPresent()) {
            existingWorld.get().setUniverse(universe.get());
            universe.get().getWorlds().add(existingWorld.get());
        }
        return universeDao.saveAndFlush(universe.get());
    }

    @GraphQLMutation(name = "addWorldToUniverse")
    public Universe addWorldToUniverse(@GraphQLArgument(name = "universeDTO") UniverseDTO universeDTO,
                                       @GraphQLArgument(name = "worldId") Long worldId){
        Optional<World> existingWorld = worldDao.findById(worldId);
        Universe universe = new Universe(universeDTO);
        if (existingWorld.isPresent()){
            existingWorld.get().setUniverse(universe);
            universe.getWorlds().add(existingWorld.get());
        }
        return universeDao.saveAndFlush(universe);
    }

    @GraphQLMutation(name = "addWorldToUniverse")
    public Universe addWorldToUniverse(@GraphQLArgument(name = "universeId") Long universeId, @GraphQLArgument(name =
            "worldDTO") WorldDTO worldDTO){
        Optional<Universe> universe = universeDao.findById(universeId);
        World world = new World();
        world.setDescription(worldDTO.getDescription());
        world.setName(worldDTO.getName());
        world = worldDao.saveAndFlush(world);
        if(universe.isPresent()){
            universe.get().getWorlds().add(world);
            world.setUniverse(universe.get());
        }
        return universeDao.saveAndFlush(universe.get());
    }

    @GraphQLMutation(name = "addWorldToUniverse")
    public Universe addWorldToUniverse(@GraphQLArgument(name = "universe") UniverseDTO universeDTO,
                                          @GraphQLArgument(name = "world") WorldDTO worldDTO){
        Universe universe = new Universe(universeDTO);
        universe = universeDao.saveAndFlush(universe);
        World world = new World();
        world.setUniverse(universe);
        world.setName(worldDTO.getName());
        world.setDescription(worldDTO.getDescription());
        world = worldDao.saveAndFlush(world);
        universe.getWorlds().add(world);
        return universeDao.saveAndFlush(universe);
    }
}
