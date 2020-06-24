package com.brianandjim.fourddata.entity.dao;

import com.brianandjim.fourddata.entity.models.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeDAO extends JpaRepository<Privilege, Integer> {
    Privilege findByNameEquals(GrantedAuthority name);
}
