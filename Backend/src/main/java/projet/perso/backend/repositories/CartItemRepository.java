package projet.perso.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projet.perso.backend.entities.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
}
