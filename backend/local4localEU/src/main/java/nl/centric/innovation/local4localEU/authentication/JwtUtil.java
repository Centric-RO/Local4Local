package nl.centric.innovation.local4localEU.authentication;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nl.centric.innovation.local4localEU.exception.L4LEUException;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final Environment env;

    public String extractTokenFromCookie(HttpServletRequest request, String token) {
        if (request.getCookies() == null) {
            return null;
        }

        for (var cookie : request.getCookies()) {
            if (token.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    public String extractUsername(String token) throws BadCredentialsException {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) throws BadCredentialsException {
        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public boolean validateToken(String token) throws ExpiredJwtException, BadCredentialsException {
        try {
            SecretKey secret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(env.getProperty("jwt.secret.key")));
            Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", ex);
        } catch (ExpiredJwtException ex) {
            throw ex;
        }
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Integer expirationTime = Integer.valueOf(env.getProperty("jwt.expiration.time")) * 1000;
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) throws L4LEUException {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && validateToken(token);
    }

    public Date extractExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractRole(String token) throws BadCredentialsException {
        return extractClaim(token, claims -> {
            Map roleClaim = claims.get("role", Map.class);

            return roleClaim != null ? (String) roleClaim.get("name") : null;
        });
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(env.getProperty("jwt.secret.key"));
        return Keys.hmacShaKeyFor(keyBytes);
    }
}