package com.zerobase.accountbook.common.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // authenticationManager 를 Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic(HttpBasicConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(configurer ->
                configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize-> authorize
                                        .requestMatchers(new AntPathRequestMatcher("/index")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/signup")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/my-accountbook")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/v1/**")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/v2/api-docs")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/swagger-resources/**")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/swagger-ui.html/**")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/configuration/ui")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/configuration/security")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/webjars/**")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/ws-stomp/**")).permitAll()
                                        .anyRequest().authenticated())
                .logout(withDefaults())
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
