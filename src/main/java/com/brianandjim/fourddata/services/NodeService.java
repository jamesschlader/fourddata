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
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        if (space.isPresent()) {
            value.setNodeValueSpace(space.get());
        }
        NodeValue savedValue = nodeValueDao.saveAndFlush(new NodeValue(processValue(value, value.getOperator())));
        space.ifPresent(s -> s.getValues().add(savedValue));
        return nodeValueSpaceDao.saveAndFlush(space.get());
    }

    private NodeValueDTO processValue(NodeValueDTO nodeValueToProcess, String operator) {
        if (StringUtils.isEmpty(nodeValueToProcess.getValue())) {
            List<NodeValueSpace> spaces =
                    nodeValueSpaceDao.findAllById(nodeValueToProcess.getNodeValuesSpacesToReduce());

            List<Double> doublesToReduce = spaces
                    .stream()
                    .map(NodeValueSpace::getValues)
                    .map(nodeValues -> nodeValues.stream().sorted(Comparator.comparing(NodeValue::getCreateDate).reversed())
                            .filter(nodeValue -> Objects.nonNull(nodeValue.getDoubleValue())).collect(Collectors.toList()).get(0))
                    .map(NodeValue::getDoubleValue)
                    .collect(Collectors.toList());

            Double reducedValue = reduceValuesBasedOnOperator(operator, doublesToReduce);

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
        if("max".equalsIgnoreCase(operator)){
            return valuesToReduce.stream().max(Double::compareTo).get();
        }
        return 0.0;
    }

}
