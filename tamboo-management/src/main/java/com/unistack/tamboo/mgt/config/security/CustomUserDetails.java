package com.unistack.tamboo.mgt.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Gyges Zean
 * @date 2018/5/18
 */
public abstract class CustomUserDetails<I extends Serializable, U> extends User implements UserDetails {

    private static  long serialVersionUID = 8063484673226426535L;

    private  I id;

    private  String userGroup;

    public CustomUserDetails(I id, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
                             Collection<? extends GrantedAuthority> authorities, String userGroup) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
        this.userGroup = userGroup;
    }

    public I getId() {
        return id;
    }

    public String getUserGroup() {
        return userGroup;
    }


}
