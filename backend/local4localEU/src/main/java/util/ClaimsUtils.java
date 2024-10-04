package util;

import nl.centric.innovation.local4localEU.entity.User;

import java.util.HashMap;
import java.util.Map;

public class ClaimsUtils {

    public static Map<String, Object> setClaims(User userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();

        extraClaims.put("id", userDetails.getId());
        extraClaims.put("email", userDetails.getUsername());
        extraClaims.put("role", userDetails.getRole());

        return extraClaims;
    }
}
