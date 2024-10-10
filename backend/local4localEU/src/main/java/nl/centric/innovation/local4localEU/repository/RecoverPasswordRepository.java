package nl.centric.innovation.local4localEU.repository;


import nl.centric.innovation.local4localEU.entity.RecoverPassword;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecoverPasswordRepository extends CrudRepository<RecoverPassword, UUID> {

    static final String COUNT_ALL_BY_USER_ID_LAST_DAY =
            "SELECT COUNT(rp) FROM RecoverPassword rp WHERE rp.userId =:userId " +
                    "AND TIMESTAMPDIFF(SECOND, rp.tokenExpirationDate, current_timestamp) <= 86400";

    Optional<RecoverPassword> findByTokenAndIsActiveTrue(String token);

    @Query(COUNT_ALL_BY_USER_ID_LAST_DAY)
    Integer countRecoveryRequestsInLastDayByUser(@Param("userId") UUID userId);
}
