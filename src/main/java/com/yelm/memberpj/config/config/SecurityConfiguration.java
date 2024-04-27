package com.yelm.memberpj.config.config;


import com.yelm.memberpj.config.filter.UserJwtAuthenticationFilter;
import com.yelm.memberpj.config.filter.UserJwtVerificationFilter;
import com.yelm.memberpj.config.handler.MemberAccessDeniedHandler;
import com.yelm.memberpj.config.handler.MemberAuthenticationEntryPoint;
import com.yelm.memberpj.config.handler.MemberAuthenticationFailureHandler;
import com.yelm.memberpj.config.handler.MemberAuthenticationSuccessHandler;
import com.yelm.memberpj.config.jwt.JwtTokenizer;
import com.yelm.memberpj.config.refreshtoken.RefreshTokenRepository;
import com.yelm.memberpj.config.refreshtoken.RefreshTokenService;
import com.yelm.memberpj.config.util.UserCustomAuthorityUtils;
import com.yelm.memberpj.user.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfiguration implements WebMvcConfigurer{
    private final JwtTokenizer jwtTokenizer;
    private final UserCustomAuthorityUtils userauthorityUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final UserService memberService;

    public SecurityConfiguration(JwtTokenizer jwtTokenizer,UserCustomAuthorityUtils userauthorityUtils,
                                 RefreshTokenService refreshTokenService, RefreshTokenRepository refreshTokenRepository,
                                 @Lazy UserService memberService){
        this.jwtTokenizer = jwtTokenizer;
        this.userauthorityUtils = userauthorityUtils;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.memberService = memberService;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .headers().frameOptions().sameOrigin()
                .and()
                .csrf().disable()
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new MemberAuthenticationEntryPoint())
                .accessDeniedHandler(new MemberAccessDeniedHandler())
                .and()
                .authorizeHttpRequests(authorize -> authorize
                        .antMatchers(HttpMethod.POST, "/api/user/**").permitAll()
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowCredentials(true);
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Refresh");
        configuration.addExposedHeader("roles");
        configuration.addExposedHeader("memberId");
        configuration.addExposedHeader("adminId");


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }

    public class UserCustomFilterConfigurer extends AbstractHttpConfigurer<UserCustomFilterConfigurer, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

            UserJwtAuthenticationFilter jwtAuthenticationFilter = new UserJwtAuthenticationFilter(authenticationManager, jwtTokenizer, refreshTokenService, refreshTokenRepository, memberService);
            jwtAuthenticationFilter.setFilterProcessesUrl("/api/user/login");
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new MemberAuthenticationSuccessHandler());
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new MemberAuthenticationFailureHandler());

            UserJwtVerificationFilter jwtVerificationFilter = new UserJwtVerificationFilter(jwtTokenizer, userauthorityUtils, refreshTokenRepository);
            builder
                    .addFilter(jwtAuthenticationFilter)
                    .addFilterAfter(jwtVerificationFilter, UserJwtAuthenticationFilter.class);
        }

    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .exposedHeaders("Authorization")
                .exposedHeaders("Refresh")
                .exposedHeaders("roles")
                .exposedHeaders("memberId")
                .maxAge(3000);
    }

}
