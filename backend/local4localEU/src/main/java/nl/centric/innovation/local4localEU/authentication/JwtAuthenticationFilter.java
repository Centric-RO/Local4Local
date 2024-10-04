package nl.centric.innovation.local4localEU.authentication;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        WebApplicationContext webApplicationContext =
                WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());

        if (null == jwtService) {
            jwtService = webApplicationContext.getBean(JwtUtil.class);
        }

        if (null == userDetailsService) {
            userDetailsService = webApplicationContext.getBean(UserDetailsService.class);
        }

       String jwt = jwtService.extractTokenFromCookie(request,"jwtToken");

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtService.validateToken(jwt)) {
                String username = jwtService.extractUsername(jwt);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    manageAuthentication(request, username);
                }
            }
        } catch (BadCredentialsException | ExpiredJwtException ex) {
            request.setAttribute("exception", ex);
        }

        filterChain.doFilter(request, response);
    }

    private void manageAuthentication(HttpServletRequest request, String username) {
        UserDetails user = this.userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user,
                null, user.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
