package com.pulsepay.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        //1. Look for the "Authorization" header in the incoming request
        String authHeader = request.getHeader("Authorization");

        //2. JWTs must look like : "Bearer eYJhbGci.."
        //if there is not header, move to the next filter
        //if the endpoint is private , Spring Security will reject it automatically

        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        //3. Extract the actual token (skip the word "Bearer" - 7 characters
        String jwtToken = authHeader.substring(7);

        //4 use JwtUtil to crack open the token and get the username
        String username = jwtUtil.extractUsername(jwtToken);

        //5. if we found the username and they are not authenticated in the current request
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // look into the database
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            //verify the token is not expired
            if(!jwtUtil.isTokenExpired(jwtToken)){
                //create an "authentication ticket" for Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                //Place the ticket in the Security Context
                //it tells Spring that this person is allowed in
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        //6. pass the request along to the next step
        filterChain.doFilter(request, response);


    }



}
