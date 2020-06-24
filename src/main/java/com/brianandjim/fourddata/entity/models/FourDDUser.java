package com.brianandjim.fourddata.entity.models;

import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FourDDUser {

    @Id
    @GeneratedValue
    @GraphQLQuery(name = "userId")
    private Integer userId;

    @Column(nullable = false, unique = true)
    @GraphQLQuery(name = "username")
    private String username;
    @GraphQLQuery(name = "password")
    private String password;
    @GraphQLQuery(name = "isAccountNonExpired")
    private boolean isAccountNonExpired;
    @GraphQLQuery(name = "isAccountNonLocked")
    private boolean isAccountNonLocked;
    @GraphQLQuery(name = "isCredentialsNonExpired")
    private boolean isCredentialsNonExpired;
    @GraphQLQuery(name = "isEnabled")
    private boolean isEnabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "userId"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName =
                    "roleId"))
    @GraphQLQuery(name = "roles")
    private Collection<Role> roles;

    @GraphQLQuery(name = "universes")
    @OneToMany(mappedBy = "user")
    private Set<Universe> universes;

    public FourDDUser(String username, String password) {
        this(null, username, password, true, true, true, true, new HashSet<>(), new HashSet<>());
    }

    public void addRole(Role role){
        roles.add(role);
    }
}
