package com.brianandjim.fourddata.services;

import com.brianandjim.fourddata.entity.dao.NodeValueDao;
import com.brianandjim.fourddata.entity.dao.NodeValueSpaceDao;
import com.brianandjim.fourddata.entity.dao.WorldDao;
import com.brianandjim.fourddata.entity.dtos.NodeValueDTO;
import com.brianandjim.fourddata.entity.dtos.NodeValueSpaceDTO;
import com.brianandjim.fourddata.entity.models.NodeValue;
import com.brianandjim.fourddata.entity.models.NodeValueSpace;
import com.brianandjim.fourddata.entity.models.World;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@GraphQLApi
@Slf4j
@PreAuthorize(value = "hasAnyAuthority('READ_PRIVILEGE')")
public class NodeService {

    private final NodeValueSpaceDao nodeValueSpaceDao;
    private final NodeValueDao nodeValueDao;
    private final WorldDao worldDao;

    public NodeService(NodeValueSpaceDao nodeValueSpaceDao, NodeValueDao nodeValueDao, WorldDao worldDao) {
        this.nodeValueSpaceDao = nodeValueSpaceDao;
        this.nodeValueDao = nodeValueDao;
        this.worldDao = worldDao;
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
            return nodeValueSpace.getLatestValue();
        }
        return null;
    }

    @GraphQLMutation(name = "saveNode")
    public NodeValueSpace saveNode(@GraphQLArgument(name = "newNode") NodeValueSpaceDTO nodeValueSpaceDTO) {
        World world = worldDao.findFirstByWorldId(nodeValueSpaceDTO.getWorldId());
        NodeValueSpace newSpace = new NodeValueSpace(nodeValueSpaceDTO);
        newSpace.setWorld(world);
        return nodeValueSpaceDao.saveAndFlush(newSpace);
    }

    @GraphQLMutation(name = "updateNode")
    public NodeValueSpace updateNode(@GraphQLArgument(name = "node") NodeValueSpaceDTO nodeValueSpaceDTO) {
        NodeValueSpace existingNode = nodeValueSpaceDao.findByNodeSpaceId(nodeValueSpaceDTO.getNodeSpaceId());
        if (Objects.nonNull(nodeValueSpaceDTO.getDescription())) {
            existingNode.setName(nodeValueSpaceDTO.getName());
        }
        if (Objects.nonNull(nodeValueSpaceDTO.getDescription())) {
            existingNode.setDescription(nodeValueSpaceDTO.getDescription());
        }
        return nodeValueSpaceDao.saveAndFlush(existingNode);
    }

    @GraphQLMutation(name = "addValueToNode")
    @CachePut(value = "latestValues", key = "#nodeId")
    public NodeValue addValueToNode(@GraphQLArgument(name = "nodeId") Long nodeId, @GraphQLArgument(name =
            "value") NodeValueDTO value) {
        NodeValueSpace space = nodeValueSpaceDao.findByNodeSpaceId(nodeId);
        value.setNodeValueSpace(space);
        NodeValue savedValue = nodeValueDao.saveAndFlush(new NodeValue(processValue(value)));
        space.addValue(savedValue);
        notifyDependentNodesOfChange(space);
        try {
            nodeValueSpaceDao.saveAndFlush(space);
        } catch (UnsupportedOperationException e) {
            log.error("Trouble saving to " + space.getNodeSpaceId());
            log.error(e.getMessage());
        }
        return savedValue;
    }

    private NodeValueDTO processValue(NodeValueDTO nodeValueToProcess) {
        if (StringUtils.isEmpty(nodeValueToProcess.getValue())) {
            List<NodeValueSpace> spaces =
                    nodeValueSpaceDao.findAllByNodeSpaceIdIn(nodeValueToProcess.getNodeValuesSpacesToReduce());
            Double reducedValue = reduceValuesBasedOnOperator(nodeValueToProcess, getLatestDoublesToReduce(spaces));
            spaces.forEach(space -> space.addNodeValueSpaceToListeners(nodeValueToProcess.getNodeValueSpace()));
            nodeValueToProcess.getNodeValueSpace().setWatchedSpaces(Set.copyOf(spaces));
            nodeValueToProcess.getNodeValueSpace().setStrategy(nodeValueToProcess.getOperator());
            return new NodeValueDTO(nodeValueToProcess.getNodeValueId(), nodeValueToProcess.getNodeValueSpace(),
                    reducedValue.toString(), nodeValueToProcess.getOperator(), nodeValueToProcess.getPower(),
                    nodeValueToProcess.getNodeValuesSpacesToReduce());
        }
        return new NodeValueDTO(nodeValueToProcess.getNodeValueId(), nodeValueToProcess.getNodeValueSpace(),
                nodeValueToProcess.getValue(), nodeValueToProcess.getOperator(), nodeValueToProcess.getPower(),
                nodeValueToProcess.getNodeValuesSpacesToReduce());
    }

    private Double reduceValuesBasedOnOperator(NodeValueDTO nodeValueToProcess, List<Double> valuesToReduce) {
        String operator = nodeValueToProcess.getOperator();
        if (ObjectUtils.isEmpty(valuesToReduce)) {
            return 0D;
        }
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
        if ("product".equalsIgnoreCase(operator) && Objects.isNull(nodeValueToProcess.getPower())) {
            return valuesToReduce.stream().reduce((total, current) -> total * current).get();
        }
        if ("product".equalsIgnoreCase(operator)) {
            for (int i = 1; i < nodeValueToProcess.getPower(); i++) {
                valuesToReduce.add(valuesToReduce.get(0));
            }
            return valuesToReduce.stream().reduce((total, current) -> total * current).get();
        }
        return 0.0;
    }

    private List<Double> getLatestDoublesToReduce(List<NodeValueSpace> spaces) {
        return spaces
                .stream()
                .map(NodeValueSpace::getLatestValue)
                .filter(nodeValue -> Objects.nonNull(nodeValue.getDoubleValue()))
                .map(NodeValue::getDoubleValue)
                .collect(Collectors.toList());
    }

    private void notifyDependentNodesOfChange(NodeValueSpace space) {
        int counter = 0;
        List<NodeValueSpace> listeners = new ArrayList<>(space.getListeners());
        while (counter < space.getListeners().size()) {
            NodeValueSpace currentSpace = listeners.get(counter);
            this.addValueToNode(currentSpace.getNodeSpaceId(), new NodeValueDTO(null, null, null,
                    currentSpace.getStrategy(), currentSpace.getPower(),
                    currentSpace.getWatchedSpaces().stream().map(NodeValueSpace::getNodeSpaceId).collect(Collectors.toList())));
            counter++;
        }
    }
}
