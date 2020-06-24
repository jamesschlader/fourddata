package com.brianandjim.fourddata.services;

import com.brianandjim.fourddata.entity.dao.FourDDataUserDAO;
import com.brianandjim.fourddata.entity.models.FourDDUser;
import com.brianandjim.fourddata.entity.models.FourDDataUserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class FourDDataUserDetailsService implements UserDetailsService {

    private FourDDataUserDAO fourDDataUserDAO;

    public FourDDataUserDetailsService(FourDDataUserDAO fourDDataUserDAO) {
        this.fourDDataUserDAO = fourDDataUserDAO;
    }

    @Override
    public FourDDataUserPrincipal loadUserByUsername(String username) {
        FourDDataUserPrincipal userPrincipal = new FourDDataUserPrincipal();
        FourDDUser user = fourDDataUserDAO.findFirstByUsername(username);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException(username);
        }
        try {
            userPrincipal.setUser(user);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return userPrincipal;
    }
}
