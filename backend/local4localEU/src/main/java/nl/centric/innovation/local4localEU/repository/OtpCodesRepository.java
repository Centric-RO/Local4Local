package nl.centric.innovation.local4localEU.repository;

import nl.centric.innovation.local4localEU.entity.OtpCodes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface OtpCodesRepository extends JpaRepository<OtpCodes, UUID> {
    Optional<OtpCodes> findBySessionIdAndOtpCode(UUID sessionId, Integer otpCode);

    Optional<OtpCodes> findBySessionId(UUID sessionId);

    void deleteAllByCreatedDateBefore(LocalDateTime cutoff);

}
