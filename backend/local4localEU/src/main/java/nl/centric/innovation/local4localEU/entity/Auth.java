package nl.centric.innovation.local4localEU.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Auth {
    private String method;
    private String token;

    public Auth(String token) {
        this.method = "token";
        this.token = token;
    }
}
