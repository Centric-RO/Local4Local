package nl.centric.innovation.local4localEU.entity.taler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TalerInstance {

    private String id;
    private String user_type;
    private boolean use_stefan;
    private Map<String, Long> default_pay_delay;
    private Map<String, Long> default_wire_transfer_delay;
    private String name;
    private Auth auth;
    private Map<String, Object> address;
    private Map<String, Object> jurisdiction;

    public TalerInstance(String name, String token) {
        this.id = name;
        this.user_type = "business";
        this.use_stefan = true;
        this.default_pay_delay = Map.of("d_us", 7200000000L);
        this.default_wire_transfer_delay = Map.of("d_us", 172800000000L);
        this.name = name;
        this.auth = new Auth(token);
        this.address = Map.of();
        this.jurisdiction = Map.of();
    }

}
