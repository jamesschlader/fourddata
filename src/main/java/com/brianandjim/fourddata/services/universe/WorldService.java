package com.brianandjim.fourddata.services.universe;

import com.brianandjim.fourddata.entity.dao.WorldDao;
import com.brianandjim.fourddata.entity.dtos.UniverseDTO;
import com.brianandjim.fourddata.entity.dtos.WorldDTO;
import com.brianandjim.fourddata.entity.models.Universe;
import com.brianandjim.fourddata.entity.models.World;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@GraphQLApi
public class WorldService {

    private final WorldDao worldDao;

    public WorldService(WorldDao worldDao) {
        this.worldDao = worldDao;
    }

    @GraphQLQuery(name="worlds")
    public List<World> getAllWorlds(){
        return worldDao.findAll();
    }

    @GraphQLQuery(name="worldById")
    public World getOneWorldById(@GraphQLArgument(name="worldId")Long id){
        return worldDao.getOne(id);
    }

    @GraphQLMutation(name="saveWorld")
    public World saveWorld(@GraphQLArgument(name="newWorld") WorldDTO world){
        return worldDao.saveAndFlush(new World(world));
    }

}
