package com.ssafy.backend.global.config;

import com.ssafy.backend.global.filter.JwtAuthenticationFilter;
import com.ssafy.backend.global.util.JwtProvider;
import com.ssafy.backend.global.util.RedisDao;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebMvcConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter(JwtProvider jwtProvider, RedisDao redisDao) {
        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtAuthenticationFilter(jwtProvider, redisDao));
        registrationBean.addUrlPatterns(
                "/api/member/logout",
                "/api/member/mypage",
                "/api/member/mystreak",
                "/api/member/modify-info",
                "/api/member/modify-password",
                "/api/member/delete",
                "/api/walk/*",
                "/api/club/*",
                "/api/club-member/*",
                "/api/share-board/*",
                "/api/scrap-record/*",
                "/api/plan/*",
                "/api/plan-club-member/*",
                "/api/chat/*"
        );
        return registrationBean;
    }

}
