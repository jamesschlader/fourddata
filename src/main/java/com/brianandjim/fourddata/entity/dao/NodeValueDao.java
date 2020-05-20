package com.brianandjim.fourddata.entity.dao;

import com.brianandjim.fourddata.entity.models.NodeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeValueDao extends JpaRepository<NodeValue, Long> {
}
