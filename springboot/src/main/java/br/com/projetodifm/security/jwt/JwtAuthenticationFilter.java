package br.com.projetodifm.security.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.projetodifm.services.JwtServices;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtServices service;

    @Autowired
    private UserDetailsService userDetailsService;

    // filtro de autenticação JWT
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,  
            HttpServletResponse response, 
            FilterChain filterChain) 
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization"); 
        final String token;
        final String username;
        if(authHeader == null || !authHeader.startsWith("Bearer ")){ 
            filterChain.doFilter(request, response);
            return;
        }
        token = authHeader.substring("Bearer ".length());
        username = service.extractEmail(token);

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            
            var user = this.userDetailsService.loadUserByUsername(username); 
            
            if(service.isTokenValid(token, user)){
                
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

}
