package com.brianandjim.fourddata.entity.dao;

import com.brianandjim.fourddata.entity.models.NodeValueSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface NodeValueSpaceDao extends JpaRepository<NodeValueSpace, Long> {
    NodeValueSpace findByNodeSpaceId(Long id);

    List<NodeValueSpace> findAllByNodeSpaceIdIn(List<Long> nodeSpaceIds);

    @Query(value = "SELECT * FROM NODE_VALUE_SPACE WHERE world_id = :worldId AND x_id = :xId", nativeQuery = true)
    Set<NodeValueSpace> findAllByWorldAndXId(@Param("worldId") Long worldId, @Param("xId") Integer xId);

    @Query(value = "SELECT * FROM NODE_VALUE_SPACE WHERE world_id = :worldId AND y_id = :yId", nativeQuery = true)
    Set<NodeValueSpace> findAllByWorldAndYId(@Param("worldId") Long worldId, @Param("yId") Integer yId);

    @Query(value = "SELECT * FROM NODE_VALUE_SPACE WHERE world_id = :worldId AND x_id = :xId AND y_id = :yId",
            nativeQuery = true)
    NodeValueSpace findFirstByWorldAndXIdAndYId(@Param("worldId") Long worldId, @Param("xId") Integer xId, @Param("yId") Integer yId);
}
