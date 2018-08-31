package com.unistack.tamboo.mgt.service.sys;

import com.google.common.collect.Lists;
import com.unistack.tamboo.mgt.config.security.CustomUserDetails;
import com.unistack.tamboo.mgt.model.sys.SysUser;
import com.unistack.tamboo.mgt.utils.SecUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author Gyges Zean
 * @date 2018/5/18
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = SecUtils.getSysUserByUsername(username);

        List<GrantedAuthority> authorities = Lists.newArrayList();
        user.getRole().forEach(sysRole -> {
            GrantedAuthority authority = new SimpleGrantedAuthority(sysRole.getRoleName());
            authorities.add(authority);
        });
        return new CurrentUserDetails(user.getId(), user.getUsername(), user.getPassword(),
                authorities, user.getUserGroup());
    }

    public class CurrentUserDetails extends CustomUserDetails<Long, SysUser> {

        CurrentUserDetails(Long id, String username, String password,
                           Collection<? extends GrantedAuthority> authorities, String userGroup) {
            super(id, username, password, true, true, true, true,
                    authorities, userGroup);
        }
    }
}
