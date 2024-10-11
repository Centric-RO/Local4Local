package nl.centric.innovation.local4localEU.repository;

import nl.centric.innovation.local4localEU.entity.Merchant;
import nl.centric.innovation.local4localEU.enums.MerchantStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MerchantRepository extends JpaRepository<Merchant, UUID> {
    Optional<Merchant> findByKvk(String kvk);
    List<Merchant> findByStatus(MerchantStatusEnum merchantStatusEnum);
    List<Merchant> findByCategoryId(Integer categoryId);
}
