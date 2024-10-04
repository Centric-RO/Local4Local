package nl.centric.innovation.local4localEU.authentication;

import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nl.centric.innovation.local4localEU.config.GlobalExceptionHandler;
import nl.centric.innovation.local4localEU.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class L4LAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Value("${error.jwt.expired}")
    private String errorJWTExpired;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {

        var exception = request.getAttribute("exception");

        // Only for this exception we need a custom error.
        if (exception instanceof ExpiredJwtException) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            // This will set the error to the custom error code
            GlobalExceptionHandler.ErrorResponse errorResponse = new GlobalExceptionHandler.ErrorResponse(errorJWTExpired);
            Gson gson = new Gson();
            String json = gson.toJson(errorResponse);
            response.getWriter().println(json);
            response.getWriter().flush();
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }

    private HttpServletResponse asHTTP(ServletResponse response) {
        return (HttpServletResponse) response;
    }
}
