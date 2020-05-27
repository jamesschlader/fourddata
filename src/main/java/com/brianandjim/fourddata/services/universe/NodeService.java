package com.brianandjim.fourddata.services.universe;

import com.brianandjim.fourddata.entity.dao.*;
import com.brianandjim.fourddata.entity.dtos.*;
import com.brianandjim.fourddata.entity.models.*;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@GraphQLApi
@Slf4j
public class NodeService {

    private final NodeValueSpaceDao nodeValueSpaceDao;
    private final NodeValueDao nodeValueDao;
    private final StringValueDao stringValueDao;
    private final LongValueDao longValueDao;
    private final IntegerValueDao integerValueDao;
    private final DoubleValueDao doubleValueDao;

    public NodeService(NodeValueSpaceDao nodeValueSpaceDao, NodeValueDao nodeValueDao, StringValueDao stringValueDao, LongValueDao longValueDao, IntegerValueDao integerValueDao, DoubleValueDao doubleValueDao) {
        this.nodeValueSpaceDao = nodeValueSpaceDao;
        this.nodeValueDao = nodeValueDao;
        this.stringValueDao = stringValueDao;
        this.longValueDao = longValueDao;
        this.integerValueDao = integerValueDao;
        this.doubleValueDao = doubleValueDao;
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

//    private NodeValue valueFactory(NodeValueDTO value) {
//        log.info("value's value is: " + value.getValue() + " and class of the value is: " + value.getValue().getClass());
//        if (String.class.equals(value.getValue().getClass())) {
//            StringValue item = new StringValue();
//            item.setValue((String) value.getValue());
//            item.setNodeValueId(value.getNodeValueId());
//            item.setNodeValueSpace(value.getNodeValueSpace());
//            return stringValueDao.saveAndFlush(item);
//        } else if (Long.class.equals(value.getValue().getClass())) {
//            LongValue item = new LongValue();
//            item.setValue((Long) value.getValue());
//            item.setNodeValueId(value.getNodeValueId());
//            item.setNodeValueSpace(value.getNodeValueSpace());
//            return longValueDao.saveAndFlush(item);
//        } else if (Integer.class.equals(value.getValue().getClass())) {
//            IntegerValue item = new IntegerValue();
//            item.setValue((Integer) value.getValue());
//            item.setNodeValueId(value.getNodeValueId());
//            item.setNodeValueSpace(value.getNodeValueSpace());
//            return integerValueDao.saveAndFlush(item);
//        } else if (Double.class.equals(value.getValue().getClass())) {
//            DoubleValue item = new DoubleValue();
//            item.setValue((Double) value.getValue());
//            item.setNodeValueId(value.getNodeValueId());
//            item.setNodeValueSpace(value.getNodeValueSpace());
//            return doubleValueDao.saveAndFlush(item);
//        }
//        return null;
//    }
}
