package com.brianandjim.fourddata.services;

import com.brianandjim.fourddata.entity.dao.FourDDataUserDAO;
import com.brianandjim.fourddata.entity.dao.UniverseDao;
import com.brianandjim.fourddata.entity.dao.WorldDao;
import com.brianandjim.fourddata.entity.dtos.UniverseDTO;
import com.brianandjim.fourddata.entity.dtos.WorldDTO;
import com.brianandjim.fourddata.entity.models.FourDDUser;
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
import java.util.Objects;
import java.util.Set;

@Service
@GraphQLApi
@Slf4j
@PreAuthorize(value = "hasAnyAuthority('READ_PRIVILEGE')")
public class UniverseService {

    private final UniverseDao universeDao;
    private final WorldDao worldDao;
    private final FourDDataUserDAO fourDDataUserDAO;

    public UniverseService(UniverseDao universeDao, WorldDao worldDao, FourDDataUserDAO fourDDataUserDAO) {
        this.universeDao = universeDao;
        this.worldDao = worldDao;
        this.fourDDataUserDAO = fourDDataUserDAO;
    }

    @GraphQLQuery(name = "universes")
    public List<Universe> getAllUniverses() {
        return universeDao.findAll();
    }

    @GraphQLQuery(name = "getUniversesForUser")
    public Set<Universe> getUniversesForUser(@GraphQLArgument(name = "username") String username) {
        FourDDUser user = fourDDataUserDAO.findFirstByUsername(username);
        return user.getUniverses();
    }

    @GraphQLQuery(name = "universeById")
    public Universe getOneUniverse(@GraphQLArgument(name = "universeId") Long id) {
        return universeDao.getOne(id);
    }

    @GraphQLQuery(name = "getUniverseByName")
    public Universe getUniverseByName(@GraphQLArgument(name = "name") String name) {
        return universeDao.findFirstByName(name);
    }

    @GraphQLMutation(name = "createUniverse")
    public Universe createUniverse(@GraphQLArgument(name = "universe") UniverseDTO universeDTO) {
        FourDDUser user = fourDDataUserDAO.findFirstByUsername(universeDTO.getUsername());
        universeDTO.setUser(user);
        Universe savedUniverse = universeDao.saveAndFlush(new Universe(universeDTO));
        user.addUniverse(savedUniverse);
        fourDDataUserDAO.saveAndFlush(user);
        return savedUniverse;
    }

    @GraphQLMutation(name = "addWorldToUniverse")
    public Universe addWorldToUniverse(@GraphQLArgument(name = "universeId") Long universeId, @GraphQLArgument(name =
            "worldId") Long worldId) {
        Universe universe = universeDao.findByUniverseId(universeId);
        World existingWorld = worldDao.findFirstByWorldId(worldId);
        if (Objects.nonNull(universe) && Objects.nonNull(existingWorld)) {
            existingWorld.setUniverse(universe);
            universe.getWorlds().add(existingWorld);
        }
        return universeDao.saveAndFlush(universe);
    }

    @GraphQLMutation(name = "addWorldToUniverse")
    public Universe addWorldToUniverse(@GraphQLArgument(name = "universeDTO") UniverseDTO universeDTO,
                                       @GraphQLArgument(name = "worldId") Long worldId) {
        Universe universe;
        Universe existingUniverse = universeDao.findByUniverseId(universeDTO.getUniverseId());
        FourDDUser user = fourDDataUserDAO.findFirstByUsername(universeDTO.getUsername());
        if(Objects.nonNull(existingUniverse)){
            universe = existingUniverse;
        } else {
            universe = new Universe(universeDTO);
            user.addUniverse(universe);
        }
        World existingWorld = worldDao.findFirstByWorldId(worldId);
        if (Objects.nonNull(existingWorld)) {
            existingWorld.setUniverse(universe);
            universe.getWorlds().add(existingWorld);
        }
        return universeDao.saveAndFlush(universe);
    }

    @GraphQLMutation(name = "addWorldToUniverse")
    public Universe addWorldToUniverse(@GraphQLArgument(name = "universeId") Long universeId, @GraphQLArgument(name =
            "worldDTO") WorldDTO worldDTO) {
        Universe universe = universeDao.findByUniverseId(universeId);
        World world = new World();
        world.setDescription(worldDTO.getDescription());
        world.setName(worldDTO.getName());
        world = worldDao.saveAndFlush(world);
        if (Objects.nonNull(universe)) {
            universe.getWorlds().add(world);
            world.setUniverse(universe);
        }
        return universeDao.saveAndFlush(universe);
    }

    @GraphQLMutation(name = "addWorldToUniverse")
    public Universe addWorldToUniverse(@GraphQLArgument(name = "universe") UniverseDTO universeDTO,
                                       @GraphQLArgument(name = "world") WorldDTO worldDTO) {
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
