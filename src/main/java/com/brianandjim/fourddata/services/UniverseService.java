package com.brianandjim.fourddata.services;

import com.brianandjim.fourddata.entity.dao.UniverseDao;
import com.brianandjim.fourddata.entity.dtos.UniverseDTO;
import com.brianandjim.fourddata.entity.models.*;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@GraphQLApi
@Slf4j
@PreAuthorize(value = "hasAnyAuthority('READ_PRIVILEGE')")
public class UniverseService {

    private final UniverseDao universeDao;
    private final UserService userService;

    public UniverseService(UniverseDao universeDao, UserService userService) {
        this.universeDao = universeDao;
        this.userService = userService;
    }

    public List<Universe> findAll() {
        return universeDao.findAll();
    }

    public Universe findById(Long universeId) {
        return universeDao.findByUniverseId(universeId);
    }

    public Universe findByName(String name) {
        return universeDao.findFirstByName(name);
    }

    public Universe saveUniverse(Universe targetUniverse) {
        Universe savedUniverse = null;
        try {
            savedUniverse = universeDao.saveAndFlush(targetUniverse);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("There is already a universe named " + targetUniverse.getName() + ". Choose another name and try again.");
        }
        return savedUniverse;
    }

    public Universe create(UniverseDTO universeDTO) {
        if (StringUtils.isEmpty(universeDTO.getName())) {
            throw new IllegalArgumentException("Cannot create a universe without a name.");
        }
        FourDDUser user = userService.getCurrentUser();
        universeDTO.setUser(user);
        Universe savedUniverse = this.saveUniverse(new Universe(universeDTO));
        user.addUniverse(savedUniverse);
        userService.saveUser(user);
        return savedUniverse;
    }
}
