package com.brianandjim.fourddata.services;

import com.brianandjim.fourddata.entity.dao.NodeValueDao;
import com.brianandjim.fourddata.entity.dao.NodeValueSpaceDao;
import com.brianandjim.fourddata.entity.dtos.NodeValueDTO;
import com.brianandjim.fourddata.entity.models.NodeValue;
import com.brianandjim.fourddata.entity.models.NodeValueSpace;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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

    public NodeService(NodeValueSpaceDao nodeValueSpaceDao, NodeValueDao nodeValueDao) {
        this.nodeValueSpaceDao = nodeValueSpaceDao;
        this.nodeValueDao = nodeValueDao;
    }

    public List<NodeValueSpace> findAll() {
        return nodeValueSpaceDao.findAll();
    }

    public NodeValueSpace getById(Long id) {
        return nodeValueSpaceDao.findByNodeSpaceId(id);
    }

    public NodeValueDTO processValue(NodeValueDTO nodeValueToProcess) {
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
        return nodeValueToProcess;
    }

    public Double reduceValuesBasedOnOperator(NodeValueDTO nodeValueToProcess, List<Double> valuesToReduce) {
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

    public List<Double> getLatestDoublesToReduce(List<NodeValueSpace> spaces) {
        return spaces
                .stream()
                .map(NodeValueSpace::getLatestValue)
                .filter(nodeValue -> Objects.nonNull(nodeValue.getDoubleValue()))
                .map(NodeValue::getDoubleValue)
                .collect(Collectors.toList());
    }

    public void notifyDependentNodesOfChange(NodeValueSpace space) {
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

    public NodeValue addValueToNode(Long nodeId, NodeValueDTO value) {
        NodeValueSpace space = this.getById(nodeId);
        if (Objects.isNull(space)) {
            throw new IllegalArgumentException("The node value space for nodeId: " + nodeId + " doesn't exist.");
        }
        value.setNodeValueSpace(space);
        NodeValue savedValue = nodeValueDao.saveAndFlush(new NodeValue(this.processValue(value)));
        space.addValue(savedValue);
        this.notifyDependentNodesOfChange(space);
        try {
            this.saveNode(space);
        } catch (UnsupportedOperationException e) {
            log.error("Trouble saving to " + space.getNodeSpaceId());
            log.error(e.getMessage());
        }
        return savedValue;
    }

    public NodeValueSpace saveNode(NodeValueSpace nodeValueSpace) {
        NodeValueSpace savedSpace = null;
        try {
            savedSpace = nodeValueSpaceDao.saveAndFlush(nodeValueSpace);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("The combination of XId and YId you submitted is already in use" +
                    " by another node. The combination must be unique.");
        }
        return savedSpace;
    }

    public Set<NodeValueSpace> getNodesByYID(Long worldId, Integer xId) {
        return nodeValueSpaceDao.findAllByWorldAndXId(worldId, xId);
    }

    public Set<NodeValueSpace> getNodesByXID(Long worldId, Integer yId) {
        return nodeValueSpaceDao.findAllByWorldAndYId(worldId, yId);
    }

    public NodeValueSpace getNodeByCoordinates(Long worldId, Integer xId, Integer yId) {
        return nodeValueSpaceDao.findFirstByWorldAndXIdAndYId(worldId, xId, yId);
    }
}
