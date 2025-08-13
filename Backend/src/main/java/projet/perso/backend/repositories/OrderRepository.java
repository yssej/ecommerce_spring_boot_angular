package projet.perso.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projet.perso.backend.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
