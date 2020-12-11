package com.brianandjim.fourddata.services;

import com.brianandjim.fourddata.entity.dtos.NodeValueDTO;
import com.brianandjim.fourddata.entity.dtos.NodeValueSpaceDTO;
import com.brianandjim.fourddata.entity.dtos.UniverseDTO;
import com.brianandjim.fourddata.entity.dtos.WorldDTO;
import com.brianandjim.fourddata.entity.models.NodeValue;
import com.brianandjim.fourddata.entity.models.NodeValueSpace;
import com.brianandjim.fourddata.entity.models.Universe;
import com.brianandjim.fourddata.entity.models.World;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@GraphQLApi
@Slf4j
@PreAuthorize(value = "hasAnyAuthority('READ_PRIVILEGE')")
public class GraphQLService {

    private final UniverseService universeService;
    private final UserService userService;
    private final WorldService worldService;
    private final NodeService nodeService;

    public GraphQLService(UniverseService universeService, UserService userService, WorldService worldService, NodeService nodeService) {
        this.universeService = universeService;
        this.userService = userService;
        this.worldService = worldService;
        this.nodeService = nodeService;
    }

    @GraphQLQuery(name = "universes")
    public List<Universe> getAllUniverses() {
        return universeService.findAll();
    }

    @GraphQLQuery(name = "getUniversesForUser")
    public Set<Universe> getUniversesForUser() {
        return userService.getCurrentUser().getUniverses();
    }

    @GraphQLQuery(name = "universeById")
    public Universe getOneUniverse(@GraphQLArgument(name = "universeId") Long id) {
        return universeService.findById(id);
    }

    @GraphQLQuery(name = "getUniverseByName")
    public Universe getUniverseByName(@GraphQLArgument(name = "name") String name) {
        return universeService.findByName(name);
    }

    @GraphQLMutation(name = "createUniverse")
    public Universe createUniverse(@GraphQLArgument(name = "universe") UniverseDTO universeDTO) {
        return universeService.create(universeDTO);
    }

    @GraphQLMutation(name = "editUniverse")
    public Universe editUniverse(@GraphQLArgument(name = "universe") UniverseDTO universeDTO) {
        return universeService.editUniverse(universeDTO);
    }

    @GraphQLMutation(name = "universeDelete")
    public Universe deleteUniverse(@GraphQLArgument(name = "universeId") Long universeId) {
        return universeService.deleteUniverse(universeId);
    }

    @GraphQLMutation(name = "worldDelete")
    public Universe deleteWorld(@GraphQLArgument(name = "worldId") Long worldId) {
        return universeService.deleteWorld(worldId);
    }

    @GraphQLMutation(name = "addWorldToUniverse", description = "Add an existing world to an existing universe.")
    public Universe addWorldToUniverse(@GraphQLArgument(name = "universeId") Long universeId, @GraphQLArgument(name =
            "worldId") Long worldId) {
        Universe universe = universeService.findById(universeId);
        World existingWorld = worldService.findById(worldId);
        if (Objects.isNull(universe) || Objects.isNull(existingWorld)) {
            throw new IllegalArgumentException("Cannot add world: " + worldId + " to universe: " + universeId + " because the one of " +
                    "them doesn't exist.");
        }
        existingWorld.setUniverse(universe);
        universe.getWorlds().add(existingWorld);
        return universeService.saveUniverse(universe);
    }

    @GraphQLMutation(name = "addWorldToUniverse", description = "Add a new world to an existing universe.")
    public Universe addWorldToUniverse(@GraphQLArgument(name = "world") WorldDTO worldDTO) {
        Universe universe = universeService.findById(worldDTO.getUniverseId());
        if (Objects.isNull(universe)) {
            throw new IllegalArgumentException("There is no universe associated with universeId: " + worldDTO.getUniverseId());
        }
        World existingWorld = worldService.findById(worldDTO.getWorldId());
        if (Objects.isNull(existingWorld)) {
            existingWorld = worldService.createWorld(universe, worldDTO);
        } else if (Objects.nonNull(worldDTO.getNewNodes())) {
            worldService.saveNewNodesToWorld(worldDTO.getNewNodes(), existingWorld);
        }
        universe.addWorld(existingWorld);
        return universeService.saveUniverse(universe);
    }

    @GraphQLMutation(name = "addWorldToUniverse", description = "Add an existing world to an existing universe or " +
            "create a universe and add an existing world to it.")
    public Universe addWorldToUniverse(@GraphQLArgument(name = "universeDTO") UniverseDTO universeDTO,
                                       @GraphQLArgument(name = "worldId") Long worldId) {
        Universe existingUniverse = universeService.findById(universeDTO.getUniverseId());
        if (Objects.isNull(existingUniverse)) {
            existingUniverse = universeService.create(universeDTO);
        }
        World existingWorld = worldService.findById(worldId);
        if (Objects.isNull(existingWorld)) {
            throw new IllegalArgumentException("There is no world associated with worldId: " + worldId);
        }
        existingWorld.setUniverse(existingUniverse);
        existingUniverse.getWorlds().add(worldService.saveWorld(existingWorld));
        return universeService.saveUniverse(existingUniverse);
    }

    @GraphQLQuery(name = "worlds")
    public List<World> getAllWorlds() {
        return worldService.findAll();
    }

    @GraphQLQuery(name = "worldById")
    public World getOneWorldById(@GraphQLArgument(name = "worldId") Long id) {
        return worldService.findById(id);
    }

    @GraphQLMutation
    public World createWorld(@GraphQLArgument(name = "world", description = "Create a world and assign to a universe." +
            " universeId must be included in the WorldDTO!") WorldDTO worldDTO) {
        Universe universe = universeService.findById(worldDTO.getUniverseId());
        if (Objects.isNull(universe)) {
            throw new IllegalArgumentException("There is no universe associated with universeId: " + worldDTO.getUniverseId());
        }
        World newWorld = worldService.createWorld(universe, worldDTO);
        universe.addWorld(newWorld);
        universeService.saveUniverse(universe);
        return newWorld;
    }

    @GraphQLMutation(name = "addNodeToWorld", description = "Adds a new node to an existing world.")
    public World addNodeToWorld(@GraphQLArgument(name = "node") NodeValueSpaceDTO nodeValueSpaceDTO) {
        log.info("Adding node at XId: " + nodeValueSpaceDTO.getXId() + ", YId: " + nodeValueSpaceDTO.getYId() + " to " +
                "worldId: " + nodeValueSpaceDTO.getWorldId());
        World world = worldService.findById(nodeValueSpaceDTO.getWorldId());
        if (Objects.isNull(world)) {
            throw new IllegalStateException("No world exists associated with worldId: " + nodeValueSpaceDTO.getWorldId());
        }
        try {
            NodeValueSpace node = nodeService.saveNode(new NodeValueSpace(nodeValueSpaceDTO));
            node.setWorld(world);
            world.addNode(nodeService.saveNode(node));
        } catch (DataIntegrityViolationException e) {
            log.error("Apparently, cannot save node because " + e.getMessage());
        }
        return worldService.saveWorld(world);
    }

    @GraphQLMutation(name = "addManyNodesToWorld", description = "Adds a list of nodes to a single world in one go.")
    public World addManyNodesToWorld(@GraphQLArgument(name = "nodes") List<NodeValueSpaceDTO> nodeValueSpaceDTOS){
        log.info("Adding node at XId: " + nodeValueSpaceDTOS.get(0).getXId() + ", YId: " + nodeValueSpaceDTOS.get(0).getYId() +
                " to worldId: " + nodeValueSpaceDTOS.get(0).getWorldId());
        World world = worldService.findById(nodeValueSpaceDTOS.get(0).getWorldId());
        if (Objects.isNull(world)) {
            throw new IllegalStateException("No world exists associated with worldId: " + nodeValueSpaceDTOS.get(0).getWorldId());
        }
        try {
            worldService.saveNewNodesToWorld(nodeValueSpaceDTOS, world);
        } catch (DataIntegrityViolationException e){
            log.error("Apparently, cannot save node because " + e.getMessage());
        }
        return world;
    }

    @GraphQLMutation(name = "editWorld")
    public World editWorld(@GraphQLArgument(name = "worldDTO") WorldDTO worldDTO) {
        World existingWorld = worldService.findById(worldDTO.getWorldId());
        if (Objects.isNull(existingWorld)) {
            throw new IllegalArgumentException("No world exists associated with worldId: " + worldDTO.getWorldId());
        }
        existingWorld.updateWorld(worldDTO);
        if (Objects.nonNull(worldDTO.getNewNodes())) {
            worldService.saveNewNodesToWorld(worldDTO.getNewNodes(), existingWorld);
        }
        return worldService.saveWorld(existingWorld);
    }

    @GraphQLQuery(name = "nodes")
    public List<NodeValueSpace> getAllNodes() {
        return nodeService.findAll();
    }

    @GraphQLQuery(name = "nodeById")
    public NodeValueSpace getOneNodeById(@GraphQLArgument(name = "nodeId") Long id) {
        return nodeService.getById(id);
    }

    @GraphQLQuery(name = "getAllNodesByWorldId")
    public Set<NodeValueSpace> getAllNodesByWorldId(@GraphQLArgument(name = "worldId") Long worldId) {
        return nodeService.getAllNodesByWorldId(worldId);
    }

    @GraphQLQuery(name = "getNodesByXID")
    public Set<NodeValueSpace> getNodesByXID(Long worldId, Integer xId) {
        return nodeService.getNodesByXID(worldId, xId);
    }

    @GraphQLQuery(name = "getNodesByYID")
    public Set<NodeValueSpace> getNodesByYID(Long worldId, Integer yId) {
        return nodeService.getNodesByYID(worldId, yId);
    }

    @GraphQLQuery(name = "getNodeByCoordinates")
    public NodeValueSpace getNodeByCoordinates(Long worldId, Integer xId, Integer yId) {
        return nodeService.getNodeByCoordinates(worldId, xId, yId);
    }

    @GraphQLQuery(name = "getLatestValueForNode")
    @Cacheable(value = "latestValues", key = "#nodeId")
    public NodeValue getLatestValueForNode(@GraphQLArgument(name = "nodeId") Long nodeId) {
        NodeValueSpace nodeValueSpace = nodeService.getById(nodeId);
        if (Objects.nonNull(nodeValueSpace)) {
            return nodeValueSpace.getLatestValue();
        }
        return null;
    }

    @GraphQLQuery(name = "getValueHistoryForNode")
    public List<NodeValue> getValueHistoryForNode(@GraphQLArgument(name = "nodeId") Long nodeId,
                                                  @GraphQLArgument(name = "limit") Integer limit){
        return nodeService.getValueHistoryForNode(nodeId, limit);
    }

    @GraphQLMutation(name = "updateNode", description = "Updates an already existing node.")
    public NodeValueSpace updateNode(@GraphQLArgument(name = "node") NodeValueSpaceDTO nodeValueSpaceDTO) {
        NodeValueSpace existingNode = this.getOneNodeById(nodeValueSpaceDTO.getNodeSpaceId());
        if (Objects.isNull(existingNode)) {
            throw new IllegalStateException("No node exists for nodeSpaceId: " + nodeValueSpaceDTO.getNodeSpaceId() +
                    ". Cannot update. Use addNodeToWorld method instead to add the node first.");
        }
        existingNode.updateNode(nodeValueSpaceDTO);
        return nodeService.saveNode(existingNode);
    }

    @GraphQLMutation(name = "addValueToNode")
    @CachePut(value = "latestValues", key = "#nodeId")
    public NodeValue addValueToNode(@GraphQLArgument(name = "nodeId") Long nodeId, @GraphQLArgument(name =
            "value") NodeValueDTO value) {
        return nodeService.addValueToNode(nodeId, value);
    }

    @GraphQLMutation(name = "nodeDelete")
    public World deleteNodeFromWorld(@GraphQLArgument(name = "node") NodeValueSpaceDTO nodeValueSpaceDTO) {
        return worldService.deleteNodeFromWorld(nodeValueSpaceDTO);
    }
}
