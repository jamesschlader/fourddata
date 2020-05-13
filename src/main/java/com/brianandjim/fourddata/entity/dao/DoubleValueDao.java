package com.brianandjim.fourddata.entity.dao;

import com.brianandjim.fourddata.entity.models.DoubleValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoubleValueDao extends JpaRepository<DoubleValue, Long> {
}
