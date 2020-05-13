package com.brianandjim.fourddata.services.universe;

import com.brianandjim.fourddata.entity.dao.DoubleValueDao;
import com.brianandjim.fourddata.entity.dtos.DoubleValueDTO;
import com.brianandjim.fourddata.entity.dtos.NodeValueSpaceDTO;
import com.brianandjim.fourddata.entity.models.DoubleValue;
import com.brianandjim.fourddata.entity.models.NodeValueSpace;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@GraphQLApi
public class DoubleService {

    private final DoubleValueDao doubleValueDao;

    public DoubleService(DoubleValueDao doubleValueDao) {
        this.doubleValueDao = doubleValueDao;
    }


    @GraphQLQuery(name="doubles")
    public List<DoubleValue> getAllDoubles(){
        return doubleValueDao.findAll();
    }

    @GraphQLQuery(name="doubleById")
    public DoubleValue getOneDoubleById(@GraphQLArgument(name="doubleId")Long id){
        return doubleValueDao.getOne(id);
    }

    @GraphQLMutation(name="saveDouble")
    public DoubleValue saveDouble(@GraphQLArgument(name="newDouble") DoubleValueDTO value){
        return doubleValueDao.saveAndFlush(new DoubleValue(value));
    }
}
