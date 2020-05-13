package com.brianandjim.fourddata.services.universe;

import com.brianandjim.fourddata.entity.dao.StringValueDao;
import com.brianandjim.fourddata.entity.dtos.DoubleValueDTO;
import com.brianandjim.fourddata.entity.dtos.StringValueDTO;
import com.brianandjim.fourddata.entity.models.DoubleValue;
import com.brianandjim.fourddata.entity.models.StringValue;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@GraphQLApi
public class StringService {

    private final StringValueDao stringValueDao;

    public StringService(StringValueDao stringValueDao) {
        this.stringValueDao = stringValueDao;
    }

    @GraphQLQuery(name="strings")
    public List<StringValue> getAllStrings(){
        return stringValueDao.findAll();
    }

    @GraphQLQuery(name="stringById")
    public StringValue getOneStringById(@GraphQLArgument(name="stringId")Long id){
        return stringValueDao.getOne(id);
    }

    @GraphQLMutation(name="saveString")
    public StringValue saveString(@GraphQLArgument(name="newString") StringValueDTO value){
        return stringValueDao.saveAndFlush(new StringValue(value));
    }
}
