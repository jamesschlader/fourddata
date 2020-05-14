package com.brianandjim.fourddata.services.universe;

import com.brianandjim.fourddata.entity.dao.UniverseDao;
import com.brianandjim.fourddata.entity.dao.WorldDao;
import com.brianandjim.fourddata.entity.dtos.UniverseDTO;
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
public class UniverseService {

    private final UniverseDao universeDao;

    public UniverseService(UniverseDao universeDao) {
        this.universeDao = universeDao;
    }

    @GraphQLQuery(name="universes")
    public List<Universe> getAllUniverses(){
        return universeDao.findAll();
    }

    @GraphQLQuery(name="universeById")
    public Universe getOneUniverse(@GraphQLArgument(name="universeId")Long id){
        return universeDao.getOne(id);
    }

    @GraphQLMutation(name="createUniverse")
    public Universe createUniverse(@GraphQLArgument(name="universe") UniverseDTO universeDTO){
        return universeDao.saveAndFlush(new Universe(universeDTO));
    }

}
