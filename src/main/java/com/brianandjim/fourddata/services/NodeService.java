package com.brianandjim.fourddata.services;

import com.brianandjim.fourddata.entity.dao.*;
import com.brianandjim.fourddata.entity.dtos.*;
import com.brianandjim.fourddata.entity.models.*;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@GraphQLApi
@Slf4j
//@PreAuthorize(value = "hasAnyAuthority('READ_PRIVILEGE')")
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

    @GraphQLQuery(name = "getLatestValueForNode")
    @Cacheable(value = "latestValues", key = "#nodeId")
    public NodeValue getLatestValueForNode(@GraphQLArgument(name = "nodeId") Long nodeId) {
        NodeValueSpace nodeValueSpace = nodeValueSpaceDao.findByNodeSpaceId(nodeId);
        if (Objects.nonNull(nodeValueSpace)) {
            return getLatestValue(nodeValueSpace.getValues());
        }
        return null;
    }

    @GraphQLMutation(name = "saveNode")
    public NodeValueSpace saveNode(@GraphQLArgument(name = "newNode") NodeValueSpaceDTO nodeValueSpaceDTO) {
        return nodeValueSpaceDao.saveAndFlush(new NodeValueSpace(nodeValueSpaceDTO));
    }

    @GraphQLMutation(name = "addValueToNode")
    @CachePut(value = "latestValues", key = "#nodeId")
    public NodeValue addValueToNode(@GraphQLArgument(name = "nodeId") Long nodeId, @GraphQLArgument(name =
            "value") NodeValueDTO value) {
        NodeValueSpace space = nodeValueSpaceDao.findByNodeSpaceId(nodeId);
        value.setNodeValueSpace(space);
        NodeValue savedValue = nodeValueDao.saveAndFlush(new NodeValue(processValue(value, value.getOperator())));
        space.getValues().add(savedValue);
        notifyDependentNodeOfChange(space);
        nodeValueSpaceDao.saveAndFlush(space);
        return savedValue;
    }

    private NodeValueDTO processValue(NodeValueDTO nodeValueToProcess, String operator) {
        if (StringUtils.isEmpty(nodeValueToProcess.getValue())) {
            List<NodeValueSpace> spaces =
                    nodeValueSpaceDao.findAllByNodeSpaceIdIn(nodeValueToProcess.getNodeValuesSpacesToReduce());
            Double reducedValue = reduceValuesBasedOnOperator(operator, getLatestDoublesToReduce(spaces));
            return new NodeValueDTO(nodeValueToProcess.getNodeValueId(), nodeValueToProcess.getNodeValueSpace(),
                    reducedValue.toString(), nodeValueToProcess.getOperator(),
                    nodeValueToProcess.getNodeValuesSpacesToReduce());
        }
        return new NodeValueDTO(nodeValueToProcess.getNodeValueId(), nodeValueToProcess.getNodeValueSpace(),
                nodeValueToProcess.getValue(), nodeValueToProcess.getOperator(),
                nodeValueToProcess.getNodeValuesSpacesToReduce());
    }

    private Double reduceValuesBasedOnOperator(String operator, List<Double> valuesToReduce) {
        if ("sum".equalsIgnoreCase(operator)) {
            return valuesToReduce.stream().reduce(Double::sum).get();
        }
        if ("min".equalsIgnoreCase(operator)) {
            return valuesToReduce.stream().min(Double::compareTo).get();
        }
        if ("avg".equalsIgnoreCase(operator)) {
            return valuesToReduce.stream().reduce((total, current) -> total + current).get() / valuesToReduce.size();
        }
        if ("max".equalsIgnoreCase(operator)) {
            return valuesToReduce.stream().max(Double::compareTo).get();
        }
        return 0.0;
    }

    private List<Double> getLatestDoublesToReduce(List<NodeValueSpace> spaces) {
        return spaces
                .stream()
                .map(NodeValueSpace::getValues)
                .map(this::getLatestValue)
                .filter(nodeValue -> Objects.nonNull(nodeValue.getDoubleValue()))
                .map(NodeValue::getDoubleValue)
                .collect(Collectors.toList());
    }

    private void notifyDependentNodeOfChange(NodeValueSpace space){

    }

    private NodeValue getLatestValue(Set<NodeValue> values){
        return values.stream().sorted(Comparator.comparing(NodeValue::getCreateDate).reversed())
                        .collect(Collectors.toList()).get(0);
    }
}
