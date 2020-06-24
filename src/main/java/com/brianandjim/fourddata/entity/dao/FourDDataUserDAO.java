package com.brianandjim.fourddata.entity.dao;

import com.brianandjim.fourddata.entity.models.FourDDUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FourDDataUserDAO extends JpaRepository<FourDDUser, Integer> {
    FourDDUser findFirstByUsername (String username);
}
