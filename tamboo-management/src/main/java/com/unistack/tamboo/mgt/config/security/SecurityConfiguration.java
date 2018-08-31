package com.unistack.tamboo.mgt.config.security;

import com.unistack.tamboo.mgt.handler.EntryPointUnauthorizedHandler;
import com.unistack.tamboo.mgt.handler.MyAccessDeniedHandler;
import com.unistack.tamboo.mgt.service.sys.UserDetailsServiceImpl;
import com.unistack.tamboo.mgt.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Gyges Zean
 * @date 2018/5/17
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;


    @Autowired
    private EntryPointUnauthorizedHandler pointUnauthorizedHandler;

    @Autowired
    private MyAccessDeniedHandler accessDeniedHandler;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/index")
                .permitAll()
//                .antMatchers("/system/*")
//                .authenticated()
                .and()
                .sessionManagement()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(this.accessDeniedHandler) //403
                .authenticationEntryPoint(this.pointUnauthorizedHandler)//401
                .and()
                .httpBasic();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return MD5Util.encode((String) rawPassword);
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return encodedPassword.equals(MD5Util.encode((String) rawPassword));
            }
        };
    }


    @Override
    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
}
