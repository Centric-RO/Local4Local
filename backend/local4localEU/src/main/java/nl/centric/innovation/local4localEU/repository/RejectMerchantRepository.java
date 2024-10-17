package nl.centric.innovation.local4localEU.repository;

import nl.centric.innovation.local4localEU.entity.RejectMerchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RejectMerchantRepository extends JpaRepository<RejectMerchant, UUID> {
    Optional<RejectMerchant> findByMerchantId(UUID merchantId);
}
