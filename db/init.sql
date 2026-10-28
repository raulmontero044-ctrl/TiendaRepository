CREATE TABLE IF NOT EXISTS product (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_catalogo BIGINT NOT NULL,
  nombre VARCHAR(255) NOT NULL,
  descripcion VARCHAR(1000),
  precio DECIMAL(10,2) NOT NULL,
  stock INT NOT NULL,
  INDEX idx_product_id_catalogo (id_catalogo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO product (id_catalogo, nombre, descripcion, precio, stock) VALUES
(1, 'Producto 1', 'Descripcion 1', 10.50, 100),
(1, 'Producto 2', 'Descripcion 2', 20.00, 50),
(2, 'Producto 3', 'Descripcion 3', 15.75, 30);
