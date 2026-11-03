-- Módulo carrito: un carrito por usuario autenticado (usuario_id viene de auth.api, sin FK entre módulos)
CREATE TABLE IF NOT EXISTS carrito_lineas (
    usuario_id  BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad    INT    NOT NULL,
    PRIMARY KEY (usuario_id, producto_id)
);
