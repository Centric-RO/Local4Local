package nl.centric.innovation.local4localEU.repository;

import nl.centric.innovation.local4localEU.entity.OtpCodes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OtpCodesRepository extends JpaRepository<OtpCodes, UUID> {

}
