package ru.netology.cloudservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableWebMvc
@EnableWebSecurity
@RequiredArgsConstructor
class WebConfig implements WebMvcConfigurer {
    private final DataSource dataSource;
    private static final String DEF_USERS_BY_USERNAME_QUERY = "SELECT username, CONCAT('{noop}', password), enabled FROM public.users WHERE username = ?";
    private static final String DEF_AUTHORITIES_BY_USERNAME_QUERY = "SELECT username, authority FROM public.authorities WHERE username = ?";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedOrigins("http://localhost:8080", "http://localhost:8088")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowPrivateNetwork(true);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery(DEF_USERS_BY_USERNAME_QUERY);
        manager.setAuthoritiesByUsernameQuery(DEF_AUTHORITIES_BY_USERNAME_QUERY);
        return manager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, final AuthenticationManager authManager) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .addFilterAfter(
                        // public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                        (servletRequest, servletResponse, filterChain) -> {
                            // (!!!) attempt to investigate CORS filter since App refuse CORS
                        },
                        CorsFilter.class)
                // (!!!) attempt to assign Security Context to HTTP Session while authenticate via Custom Login Form / URL
                .addFilterAfter((servletRequest, servletResponse, filterChain) -> {
                            if (isAuthenticated())
                                return; // user is logged in and SecurityContext should be available from the first call
                            HttpServletRequest req = (HttpServletRequest) servletRequest;
                            HttpServletResponse res = (HttpServletResponse) servletResponse;
                            byte[] inputStreamBytes = StreamUtils.copyToByteArray(req.getInputStream());
                            Map<?, ?> jsonRequest;
                            UsernamePasswordAuthenticationToken token;
                            if (inputStreamBytes.length != 0) {
                                jsonRequest = new ObjectMapper().readValue(inputStreamBytes, Map.class);
                                token = UsernamePasswordAuthenticationToken
                                        .unauthenticated(jsonRequest.get("login"), jsonRequest.get("password"));
                            } else {
                                token = UsernamePasswordAuthenticationToken
                                        .unauthenticated("m@m.ru", "pwd");
                            }
                            //AuthenticationManager authManager = authenticationManager(userDetailsService());
                            Authentication auth = authManager.authenticate(token);
                            SecurityContextHolder.getContext().setAuthentication(auth);
                            SecurityContextHolderStrategy strategy = SecurityContextHolder.getContextHolderStrategy();
                            SecurityContext context = strategy.createEmptyContext(); //strategy.getContext();
                            context.setAuthentication(auth);
                            strategy.setContext(context);
                            securityContextRepository().saveContext(context, req, res);
                            System.out.println(token);
                            System.out.println(auth);
                            System.out.println(authManager);
                            System.out.println(context);
                            filterChain.doFilter(req, res);
                        },
                        UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((request) -> request
                        .requestMatchers("/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "list").hasAuthority("AUTHORITY_VIEW")
                        .requestMatchers(HttpMethod.GET, "file").hasAuthority("AUTHORITY_READ")
                        .requestMatchers(HttpMethod.POST, "file").hasAuthority("AUTHORITY_WRITE")
                        .requestMatchers(HttpMethod.PUT, "file").hasAuthority("AUTHORITY_WRITE")
                        .anyRequest()
                        .authenticated())
                .formLogin((form) -> form
                        .loginPage("http://localhost:8080")
                        .usernameParameter("login")
                        .passwordParameter("password"))
                .logout((logout) -> logout
                        .logoutUrl("http://localhost:8080/logout")
                        .logoutSuccessHandler(logoutSuccessHandler())
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .clearAuthentication(true))
                .build();
    }

//    @Bean
//    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
//        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
//        authenticationProvider.setUserDetailsService(userDetailsService());
//        return authenticationProvider::authenticate;
//    }

    @Bean
    public AuthenticationManager authenticationManager(
            final AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        // new LogoutSuccessHandler()
        return (request, response, authentication) -> System.out.println("--> logoutSuccessHandler.onLogoutSuccess()");
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    private boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || AnonymousAuthenticationToken.class.isAssignableFrom(auth.getClass()))
            return false;
        return auth.isAuthenticated();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginPageInterceptor());
    }

    @Bean
    public HandlerInterceptor loginPageInterceptor() {
        return new HandlerInterceptor() {
            @Override
            public boolean preHandle(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Object handler) {
                System.out.println("--> loginPageInterceptor.preHandle()");
                return true;
            }
        };
    }
}