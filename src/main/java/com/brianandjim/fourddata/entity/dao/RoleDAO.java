package com.brianandjim.fourddata.entity.dao;

import com.brianandjim.fourddata.entity.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDAO extends JpaRepository<Role, Integer> {
    Role findByName (String name);
}
