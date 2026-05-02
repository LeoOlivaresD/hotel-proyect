-- ============================================================
-- Hotel Pacific Reef – Schema + Datos de prueba
-- Se ejecuta una vez al arrancar (spring.sql.init.mode=always)
-- ============================================================

CREATE TABLE IF NOT EXISTS habitacion (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero      VARCHAR(10)  NOT NULL UNIQUE,
    tipo        VARCHAR(30)  NOT NULL,
    piso        INT          NOT NULL,
    capacidad   INT          NOT NULL,
    precio      DECIMAL(10,2) NOT NULL,
    estado      VARCHAR(20)  NOT NULL DEFAULT 'disponible',
    amenidades  VARCHAR(255),
    m2          INT
);

CREATE TABLE IF NOT EXISTS cliente (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    apellido    VARCHAR(100) NOT NULL,
    rut         VARCHAR(20)  NOT NULL UNIQUE,
    correo      VARCHAR(150) NOT NULL,
    telefono    VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS reserva (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id      BIGINT NOT NULL,
    habitacion_id   BIGINT NOT NULL,
    fecha_entrada   DATE   NOT NULL,
    fecha_salida    DATE   NOT NULL,
    huespedes       INT    NOT NULL DEFAULT 1,
    metodo_pago     VARCHAR(30),
    estado_pago     VARCHAR(20) DEFAULT 'pendiente',
    estado          VARCHAR(20) NOT NULL DEFAULT 'pendiente',
    total           DECIMAL(10,2),
    observaciones   VARCHAR(500),
    CONSTRAINT fk_reserva_cliente    FOREIGN KEY (cliente_id)    REFERENCES cliente(id),
    CONSTRAINT fk_reserva_habitacion FOREIGN KEY (habitacion_id) REFERENCES habitacion(id)
);

-- ── Datos de prueba: habitaciones ────────────────────────────────────────────
INSERT INTO habitacion (numero, tipo, piso, capacidad, precio, estado, amenidades, m2)
SELECT v_numero, v_tipo, v_piso, v_capacidad, v_precio, v_estado, v_amenidades, v_m2
FROM (
    SELECT '101' AS v_numero, 'Simple'       AS v_tipo, 1 AS v_piso, 1 AS v_capacidad, 65.00  AS v_precio, 'disponible'    AS v_estado, 'WiFi,TV,Aire acondicionado'  AS v_amenidades, 20 AS v_m2 UNION ALL
    SELECT '102', 'Simple',       1, 1,  65.00, 'mantenimiento', 'WiFi,TV',                          20 UNION ALL
    SELECT '201', 'Doble',        2, 2,  95.00, 'ocupada',       'WiFi,TV,Minibar,Balcon',            30 UNION ALL
    SELECT '204', 'Doble',        2, 2,  95.00, 'disponible',    'WiFi,TV,Minibar',                   30 UNION ALL
    SELECT '301', 'Suite',        3, 3, 180.00, 'disponible',    'WiFi,TV,Jacuzzi,Vista al mar',      55 UNION ALL
    SELECT '302', 'Suite',        3, 3, 180.00, 'ocupada',       'WiFi,TV,Jacuzzi',                   55 UNION ALL
    SELECT '401', 'Junior Suite', 4, 2, 130.00, 'disponible',    'WiFi,TV,Kitchenette',               42 UNION ALL
    SELECT '402', 'Junior Suite', 4, 2, 130.00, 'disponible',    'WiFi,TV,Kitchenette,Balcon',        42
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM habitacion WHERE numero = tmp.v_numero);

-- ── Datos de prueba: clientes ────────────────────────────────────────────────
INSERT INTO cliente (nombre, apellido, rut, correo, telefono)
SELECT * FROM (
    SELECT 'María','González','12.345.678-9','maria.gonzalez@email.com','+56912345678' UNION ALL
    SELECT 'Pedro','Ramírez','13.456.789-0','pedro.ramirez@email.com','+56923456789' UNION ALL
    SELECT 'Claudia','Soto','14.567.890-1','claudia.soto@email.com','+56934567890'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM cliente WHERE rut = tmp.rut);

-- ── Datos de prueba: reservas ────────────────────────────────────────────────
INSERT INTO reserva (cliente_id, habitacion_id, fecha_entrada, fecha_salida, huespedes, metodo_pago, estado_pago, estado, total)
SELECT * FROM (
    SELECT 1,4,'2025-04-15','2025-04-18',2,'credito','pagado','confirmada',643.00 UNION ALL
    SELECT 2,5,'2025-04-16','2025-04-20',3,'transferencia','pendiente','pendiente',1020.00 UNION ALL
    SELECT 3,1,'2025-04-14','2025-04-17',1,'efectivo','pagado','confirmada',260.00
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM reserva LIMIT 1);
