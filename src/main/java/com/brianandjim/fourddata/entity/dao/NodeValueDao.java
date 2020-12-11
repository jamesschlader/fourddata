package com.brianandjim.fourddata.entity.dao;

import com.brianandjim.fourddata.entity.models.NodeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeValueDao extends JpaRepository<NodeValue, Long> {

    @Query(value="SELECT TOP(:limit) * FROM NODE_VALUE WHERE node_value_space_id = :nodeSpaceId ORDER BY create_date " +
            "desc",
            nativeQuery = true)
    List<NodeValue> getValueHistoryForNode(@Param("nodeSpaceId") Long nodeSpaceId, @Param("limit") Integer limit);
}
