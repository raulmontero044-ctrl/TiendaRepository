package es.shop.domain;

import java.util.List;
import java.util.Optional;

public interface ProductDao {
    List<Product> findAll();

    List<Product> findByCatalogo(Long idCatalogo);

    Optional<Product> findById(Long id);

    Product save(Product product);

    Product update(Product product);

    void deleteById(Long id);
}
