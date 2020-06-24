package com.brianandjim.fourddata.services;

import com.brianandjim.fourddata.entity.dao.*;
import com.brianandjim.fourddata.entity.dtos.*;
import com.brianandjim.fourddata.entity.models.*;
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
public class NodeService {

    private final NodeValueSpaceDao nodeValueSpaceDao;
    private final NodeValueDao nodeValueDao;

    public NodeService(NodeValueSpaceDao nodeValueSpaceDao, NodeValueDao nodeValueDao) {
        this.nodeValueSpaceDao = nodeValueSpaceDao;
        this.nodeValueDao = nodeValueDao;
    }

    @GraphQLQuery(name = "nodes")
    public List<NodeValueSpace> getAllNodes() {
        return nodeValueSpaceDao.findAll();
    }

    @GraphQLQuery(name = "nodeById")
    public NodeValueSpace getOneNodeById(@GraphQLArgument(name = "nodeId") Long id) {
        return nodeValueSpaceDao.getOne(id);
    }

    @GraphQLMutation(name = "saveNode")
    public NodeValueSpace saveNode(@GraphQLArgument(name = "newNode") NodeValueSpaceDTO nodeValueSpaceDTO) {
        return nodeValueSpaceDao.saveAndFlush(new NodeValueSpace(nodeValueSpaceDTO));
    }

    @GraphQLMutation(name = "addValueToNode")
    public NodeValueSpace addValueToNode(@GraphQLArgument(name = "nodeId") Long nodeId, @GraphQLArgument(name =
            "value") NodeValueDTO value) {
        Optional<NodeValueSpace> space = nodeValueSpaceDao.findById(nodeId);
       if(space.isPresent()){
           value.setNodeValueSpace(space.get());
       }
        NodeValue savedValue = nodeValueDao.saveAndFlush(new NodeValue(value));
        space.ifPresent(s -> s.getValues().add(savedValue));
        return nodeValueSpaceDao.saveAndFlush(space.get());
    }

}
