package com.brianandjim.fourddata.entity.dao;

import com.brianandjim.fourddata.entity.models.Universe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UniverseDao extends JpaRepository<Universe, Long> {
}
