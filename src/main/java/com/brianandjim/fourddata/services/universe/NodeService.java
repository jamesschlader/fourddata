package com.brianandjim.fourddata.services.universe;

import com.brianandjim.fourddata.entity.dao.NodeValueSpaceDao;
import com.brianandjim.fourddata.entity.dtos.NodeValueSpaceDTO;
import com.brianandjim.fourddata.entity.dtos.WorldDTO;
import com.brianandjim.fourddata.entity.models.NodeValueSpace;
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
public class NodeService {

    private final NodeValueSpaceDao nodeValueSpaceDao;

    public NodeService(NodeValueSpaceDao nodeValueSpaceDao) {
        this.nodeValueSpaceDao = nodeValueSpaceDao;
    }

    @GraphQLQuery(name="nodes")
    public List<NodeValueSpace> getAllNodes(){
        return nodeValueSpaceDao.findAll();
    }

    @GraphQLQuery(name="nodeById")
    public NodeValueSpace getOneNodeById(@GraphQLArgument(name="nodeId")Long id){
        return nodeValueSpaceDao.getOne(id);
    }

    @GraphQLMutation(name="saveNode")
    public NodeValueSpace saveNode(@GraphQLArgument(name="newNode") NodeValueSpaceDTO nodeValueSpaceDTO){
        return nodeValueSpaceDao.saveAndFlush(new NodeValueSpace(nodeValueSpaceDTO));
    }
}
