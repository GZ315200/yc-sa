package com.unistack.tamboo.mgt.config;

import com.unistack.tamboo.mgt.handler.RequestTimeCostInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author Gyges Zean
 * @date 15/01/2018
 */
@Configuration
@EnableWebMvc
public class ManagerInterceptorConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private RequestTimeCostInterceptor requestTimeCostInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestTimeCostInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error");
        super.addInterceptors(registry);
    }
}
