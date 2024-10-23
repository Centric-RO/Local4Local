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

import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(schema = "l4l_eu_security", name = "otp_attempts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpAttempts extends BaseEntity {

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
