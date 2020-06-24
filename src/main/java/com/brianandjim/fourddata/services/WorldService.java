package com.brianandjim.fourddata.services;

import com.brianandjim.fourddata.entity.dao.NodeValueSpaceDao;
import com.brianandjim.fourddata.entity.dao.UniverseDao;
import com.brianandjim.fourddata.entity.dao.WorldDao;
import com.brianandjim.fourddata.entity.dtos.NodeValueSpaceDTO;
import com.brianandjim.fourddata.entity.dtos.WorldDTO;
import com.brianandjim.fourddata.entity.models.NodeValueSpace;
import com.brianandjim.fourddata.entity.models.Universe;
import com.brianandjim.fourddata.entity.models.World;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@GraphQLApi
@Slf4j
@PreAuthorize(value = "hasAnyAuthority('READ_PRIVILEGE')")
public class WorldService {

    private final WorldDao worldDao;
    private final UniverseDao universeDao;
    private final NodeValueSpaceDao nodeValueSpaceDao;


    public WorldService(WorldDao worldDao, UniverseDao universeDao, NodeValueSpaceDao nodeValueSpaceDao) {
        this.worldDao = worldDao;
        this.universeDao = universeDao;
        this.nodeValueSpaceDao = nodeValueSpaceDao;
    }

    @GraphQLQuery(name = "worlds")
    public List<World> getAllWorlds() {
        var context = SecurityContextHolder.getContext();
        log.info("some context: " + context.getAuthentication().getPrincipal().toString());
        return worldDao.findAll();
    }

    @GraphQLQuery(name = "worldById")
    public World getOneWorldById(@GraphQLArgument(name = "worldId") Long id) {
        return worldDao.getOne(id);
    }

    @GraphQLMutation(name = "saveWorld")
    public World saveWorld(@GraphQLArgument(name = "newWorld") WorldDTO world) {
        return worldDao.saveAndFlush(new World(world));
    }

    @GraphQLMutation
    public World createWorld(@GraphQLArgument(name = "world") WorldDTO worldDTO) {
        World newWorld = new World();
        newWorld.setDescription(worldDTO.getDescription());
        newWorld.setName(worldDTO.getName());
        Optional<Universe> targetUniverse = universeDao.findById(worldDTO.getUniverse().getUniverseId());
        targetUniverse.ifPresent(newWorld::setUniverse);
        return worldDao.saveAndFlush(newWorld);
    }

    @GraphQLMutation
    public World createWorld(@GraphQLArgument(name = "name") String name,
                             @GraphQLArgument(name = "description") String description) {
        World newWorld = new World();
        newWorld.setName(name);
        newWorld.setDescription(description);
        return worldDao.saveAndFlush(newWorld);
    }

    @GraphQLMutation(name = "addNodeToWorld")
    public World addNodeToWorld(@GraphQLArgument(name = "worldId") Long worldId,
                                @GraphQLArgument(name = "node") NodeValueSpaceDTO nodeValueSpaceDTO) {
        Optional<World> world = worldDao.findById(worldId);
        NodeValueSpace node = new NodeValueSpace(nodeValueSpaceDTO);
        world.ifPresent(node::setWorld);
        NodeValueSpace savedNode = nodeValueSpaceDao.saveAndFlush(node);
        world.ifPresent(world1 -> world1.getNodes().add(savedNode));
        return worldDao.saveAndFlush(world.get());
    }
}
