package nl.centric.innovation.local4localEU.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import nl.centric.innovation.local4localEU.entity.MerchantInvitation;

public interface MerchantInvitationRepository extends JpaRepository<MerchantInvitation, UUID> {
    Integer countByIsActiveTrue();
}
