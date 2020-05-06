package com.brianandjim.fourddata.entity.dao;

import com.brianandjim.fourddata.entity.models.NodeValueSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeValueSpaceDao extends JpaRepository<NodeValueSpace, Long> {
}
