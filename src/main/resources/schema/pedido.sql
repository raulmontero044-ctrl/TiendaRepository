-- Módulo pedido: las líneas guardan un snapshot del producto (nombre, precio unitario, cantidad, producto_id)
CREATE TABLE IF NOT EXISTS pedidos (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT        NOT NULL,
    fecha      DATETIME      NOT NULL,
    estado     VARCHAR(20)   NOT NULL,
    total      DECIMAL(10,2) NOT NULL,
    INDEX idx_pedidos_usuario (usuario_id)
);

CREATE TABLE IF NOT EXISTS pedido_lineas (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id       BIGINT        NOT NULL,
    producto_id     BIGINT        NOT NULL,
    nombre_producto VARCHAR(255)  NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    cantidad        INT           NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedidos (id)
);
