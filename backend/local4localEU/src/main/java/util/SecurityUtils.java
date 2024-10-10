package util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;

import java.util.Optional;

public class SecurityUtils {
    public static String getClientIP(HttpServletRequest httpRequest) {
        Optional<String> xfHeader = Optional.ofNullable(httpRequest.getHeader("X-Forwarded-For"));
        if (xfHeader.isPresent()) {
            return xfHeader.get().split(",")[0];
        }
        return httpRequest.getRemoteAddr();
    }

    public static HttpCookie createCookie(String cookieName, String cookieValue, String durationProperty) {
        long expirationTimeSeconds = Integer.parseInt(durationProperty);

        return ResponseCookie.from(cookieName, cookieValue)
                .maxAge(expirationTimeSeconds)
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .path("/")
                .build();
    }

    public static ResponseCookie deleteCookie(String cookieName) {
        return ResponseCookie.from(cookieName, null)
                .maxAge(0)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .build();
    }
}
