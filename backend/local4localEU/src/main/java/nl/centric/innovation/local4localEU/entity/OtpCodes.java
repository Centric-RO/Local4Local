package nl.centric.innovation.local4localEU.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.UUID;


@EqualsAndHashCode(callSuper = true)
@Entity
@Table(schema = "l4l_eu_security", name = "otp_codes")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpCodes extends BaseEntity {

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(name = "otp_code", nullable = false)
    private Integer otpCode;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
