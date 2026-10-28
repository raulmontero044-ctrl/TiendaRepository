package es.shop.domain;

import java.util.List;
import java.util.Optional;

public class ProductService {
    private final ProductDao productDao;

    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public List<Product> obtenerCatalogo() {
        return productDao.findAll();
    }

    public List<Product> obtenerPorCatalogo(Long idCatalogo) {
        return productDao.findByCatalogo(idCatalogo);
    }

    public List<Product> listarProductos() {
        return productDao.findAll();
    }

    public Optional<Product> obtenerProducto(Long id) {
        return productDao.findById(id);
    }

    public Product crearProducto(Product product) {
        return productDao.save(product);
    }

    public Product actualizarProducto(Product product) {
        return productDao.update(product);
    }

    public void eliminarProducto(Long id) {
        productDao.deleteById(id);
    }
}
