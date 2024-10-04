package nl.centric.innovation.local4localEU.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(schema = "l4l_eu_security", name = "login_attempt")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttempt extends BaseEntity {
    @Column(name = "remote_addr")
    private String remoteAddress;

    @Column(name = "failed_count")
    private Integer count;
}
