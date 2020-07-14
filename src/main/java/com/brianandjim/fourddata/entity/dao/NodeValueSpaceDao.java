package com.brianandjim.fourddata.entity.dao;

import com.brianandjim.fourddata.entity.models.NodeValueSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface NodeValueSpaceDao extends JpaRepository<NodeValueSpace, Long> {
    NodeValueSpace findByNodeSpaceId(Long id);
    List<NodeValueSpace> findAllByNodeSpaceIdIn(List<Long> nodeSpaceIds);
}
