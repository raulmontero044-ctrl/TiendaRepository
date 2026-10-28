package es.shop.controller;

import es.shop.domain.Product;
import es.shop.domain.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalogos")
public class CatalogoController {
    private final ProductService productService;

    public CatalogoController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAll() {
        return productService.obtenerCatalogo();
    }

    @GetMapping("/{idCatalogo}")
    public List<Product> getByCatalogo(@PathVariable Long idCatalogo) {
        return productService.obtenerPorCatalogo(idCatalogo);
    }
}
