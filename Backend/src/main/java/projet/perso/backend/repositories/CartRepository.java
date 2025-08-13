package projet.perso.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projet.perso.backend.entities.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {
}
