package com.example.demo.config;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // 인증 정보 제공
    @Autowired
    private AuthenticationProvider authenticationProvider;

    // 인증 성공 시 처리를 위한 Handler
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    // 인증 실패 시 처리를 위한 Handler
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

    // 구성 환경 설정에 관한 설정을 하기 위한 메소드
    // ex) resource, reject request 등
    @Override
    public void configure(WebSecurity web) throws Exception
    {
        web.ignoring().antMatchers("/css/**", "/script/**", "image/**", "/fonts/**", "lib/**");
    }

    // Http 요청에 대해 resource level에서의 권한이나 정보를 제공하기 위한 메소드
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.headers().frameOptions().sameOrigin();

        http.authorizeRequests()
                .antMatchers("/", "/signup", "/test", "/test1", "/aws/**", "/chat/**", "/chating").permitAll()
                .antMatchers("/board/**").hasRole("MEMBER")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            .and()
                .formLogin()
                .loginPage("/login1").permitAll()
                .usernameParameter("id")
                .passwordParameter("pw")
                .defaultSuccessUrl("/board")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
            .and()
                .logout()
                .permitAll()
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login1")
            .and()
                .csrf()
                    .disable();

        // 세션 설정
        http.sessionManagement()
                .maximumSessions(1)
                .expiredUrl("/login1")
                .maxSessionsPreventsLogin(false)
        ;
//        http.cors().and();
//        http.csrf().disable();
    }

    // 인증 정보를 설정하기 위한 메소드
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider);
    }
}