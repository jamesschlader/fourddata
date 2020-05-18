package com.brianandjim.fourddata.services.universe;

import com.brianandjim.fourddata.entity.dao.UniverseDao;
import com.brianandjim.fourddata.entity.dao.WorldDao;
import com.brianandjim.fourddata.entity.dtos.WorldDTO;
import com.brianandjim.fourddata.entity.models.Universe;
import com.brianandjim.fourddata.entity.models.World;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@GraphQLApi
public class WorldService {

    private final WorldDao worldDao;
    private final UniverseDao universeDao;

    public WorldService(WorldDao worldDao, UniverseDao universeDao) {
        this.worldDao = worldDao;
        this.universeDao = universeDao;
    }

    @GraphQLQuery(name = "worlds")
    public List<World> getAllWorlds() {
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
        Universe targetUniverse = universeDao.findById(worldDTO.getUniverse().getUniverseId()).get();
        newWorld.setUniverse(targetUniverse);
        return worldDao.saveAndFlush(newWorld);
    }
}
