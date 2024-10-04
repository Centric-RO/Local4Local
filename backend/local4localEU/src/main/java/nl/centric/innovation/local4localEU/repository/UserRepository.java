package nl.centric.innovation.local4localEU.repository;

import nl.centric.innovation.local4localEU.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findById(UUID userId);

    List<User> findAll();
}
