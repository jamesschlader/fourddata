package com.brianandjim.fourddata.config;

import com.brianandjim.fourddata.entity.dao.FourDDataUserDAO;
import com.brianandjim.fourddata.entity.dao.PrivilegeDAO;
import com.brianandjim.fourddata.entity.dao.RoleDAO;
import com.brianandjim.fourddata.entity.models.FourDDUser;
import com.brianandjim.fourddata.entity.models.Privilege;
import com.brianandjim.fourddata.entity.models.Role;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    private FourDDataUserDAO userDAO;
    private RoleDAO roleDAO;
    private PrivilegeDAO privilegeDAO;
    private PasswordEncoder passwordEncoder;

    public InitialDataLoader(FourDDataUserDAO userDAO,
                             RoleDAO roleDAO,
                             PrivilegeDAO privilegeDAO,
                             PasswordEncoder passwordEncoder
    ) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.privilegeDAO = privilegeDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup)
            return;

        Privilege readPrivilege
                = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege
                = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        createRoleIfNotFound("ROLE_ADMIN", new HashSet<>(Arrays.asList(readPrivilege, writePrivilege)));
        createRoleIfNotFound("ROLE_User", new HashSet<>(Arrays.asList(readPrivilege)));

        FourDDUser user = userDAO.findFirstByUsername("Test");
        if (user == null) {
            Role adminRole = roleDAO.findByName("ROLE_ADMIN");
            FourDDUser newUser = new FourDDUser("Test", passwordEncoder.encode("test"));
            newUser.addRole(adminRole);
            userDAO.save(newUser);
        }

        FourDDUser user2 = userDAO.findFirstByUsername("User");
        if (user2 == null) {
            Role userRole = roleDAO.findByName("ROLE_User");
            FourDDUser newUser = new FourDDUser("User", passwordEncoder.encode("user"));
            newUser.addRole(userRole);
            userDAO.save(newUser);
        }

        alreadySetup = true;
    }

    @Transactional
    public Privilege createPrivilegeIfNotFound(String name) {
        GrantedAuthority test = new SimpleGrantedAuthority(name);
        Privilege privilege = privilegeDAO.findByNameEquals(test);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeDAO.save(privilege);
        }
        return privilege;
    }

    @Transactional
    public Role createRoleIfNotFound(
            String name, Set<Privilege> privileges) {

        Role role = roleDAO.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleDAO.save(role);
        }
        return role;
    }

}
