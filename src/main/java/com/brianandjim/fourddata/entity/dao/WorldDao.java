package com.brianandjim.fourddata.entity.dao;

import com.brianandjim.fourddata.entity.models.World;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorldDao extends JpaRepository<World, Long> {
    World findFirstByWorldId(Long worldId);
}
