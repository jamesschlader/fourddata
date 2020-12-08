package com.brianandjim.fourddata.services;

import com.brianandjim.fourddata.entity.dao.WorldDao;
import com.brianandjim.fourddata.entity.dtos.NodeValueSpaceDTO;
import com.brianandjim.fourddata.entity.dtos.WorldDTO;
import com.brianandjim.fourddata.entity.models.NodeValueSpace;
import com.brianandjim.fourddata.entity.models.Universe;
import com.brianandjim.fourddata.entity.models.World;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@GraphQLApi
@Slf4j
@PreAuthorize(value = "hasAnyAuthority('READ_PRIVILEGE')")
public class WorldService {

    private final WorldDao worldDao;
    private final NodeService nodeService;

    public WorldService(WorldDao worldDao, NodeService nodeService) {
        this.worldDao = worldDao;
        this.nodeService = nodeService;
    }

    public List<World> findAll() {
        return worldDao.findAll();
    }

    public World findById(Long id) {
        return worldDao.findFirstByWorldId(id);
    }

    public void deleteWorld(World world){
        log.info("Deleting world: " + world.getName());
        worldDao.delete(world);
    }

    public World createWorld(Universe universe, WorldDTO worldDTO) {
        if (StringUtils.isEmpty(worldDTO.getName())) {
            throw new IllegalArgumentException("Cannot create world without a name.");
        }
        worldDTO.setUniverse(universe);
        World newWorld = this.saveWorld(new World(worldDTO));
        if (Objects.nonNull(worldDTO.getNewNodes())) {
            this.saveNewNodesToWorld(worldDTO.getNewNodes(), newWorld);
        }
        return newWorld;
    }

    public World saveWorld(World worldToSave) {
        World savedWorld = null;
        try {
            log.info("Saving world: " + worldToSave.toString());
            savedWorld = worldDao.saveAndFlush(worldToSave);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("There is already a world with the name: " + worldToSave.getName() + ". Choose another name and try again.");
        }
        return savedWorld;
    }

    public void saveNewNodesToWorld(List<NodeValueSpaceDTO> nodeValueSpaceDTOS, World world) {
        Set<NodeValueSpace> nodes = nodeValueSpaceDTOS
                .stream()
                .map(node -> {
                    NodeValueSpace newNode = new NodeValueSpace(node);
                    newNode.setWorld(world);
                    return nodeService.saveNode(newNode);
                })
                .collect(Collectors.toSet());
        nodes.forEach(world::addNode);
        this.saveWorld(world);
    }

    public World deleteNodeFromWorld(NodeValueSpaceDTO nodeValueSpaceDTO) {
        World world = findById(nodeValueSpaceDTO.getWorldId());
        if(Objects.nonNull(world)){
            NodeValueSpace nodeToDelete = nodeService.getById(nodeValueSpaceDTO.getNodeSpaceId());
            world.removeNode(nodeToDelete);
            world = this.saveWorld(world);
            nodeService.deleteNode(nodeToDelete);
        }
        return world;
    }
}
