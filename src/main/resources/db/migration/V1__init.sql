-- =========================
-- USERS
-- =========================
CREATE TABLE IF NOT EXISTS public.app_users (
                                                id BIGSERIAL PRIMARY KEY,
                                                name VARCHAR(120) NOT NULL,
    email VARCHAR(180) NOT NULL,
    cpf VARCHAR(11) NOT NULL,
    password_hash VARCHAR(120) NOT NULL,
    role VARCHAR(50) NOT NULL,

    CONSTRAINT uk_app_users_email UNIQUE (email),
    CONSTRAINT uk_app_users_cpf UNIQUE (cpf)
    );

CREATE INDEX IF NOT EXISTS ix_app_users_role ON public.app_users (role);

-- =========================
-- CAR
-- =========================
CREATE TABLE IF NOT EXISTS public.car (
                                          id BIGSERIAL PRIMARY KEY,
                                          brand VARCHAR(60) NOT NULL,
    model VARCHAR(80) NOT NULL,
    year INT NOT NULL,
    color VARCHAR(30) NOT NULL,
    price NUMERIC(15,2) NOT NULL,
    sold BOOLEAN NOT NULL DEFAULT FALSE
    );

CREATE INDEX IF NOT EXISTS ix_car_brand_model ON public.car (brand, model);
CREATE INDEX IF NOT EXISTS ix_car_sold ON public.car (sold);

-- =========================
-- SALES (venda do carro)
-- =========================
CREATE TABLE IF NOT EXISTS public.sales (
                                            id BIGSERIAL PRIMARY KEY,
                                            car_id BIGINT NOT NULL UNIQUE,
                                            buyer_cpf VARCHAR(11) NOT NULL,
    sold_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_sales_car
    FOREIGN KEY (car_id) REFERENCES public.car(id)
    ON DELETE RESTRICT
    );

CREATE INDEX IF NOT EXISTS ix_sales_buyer_cpf ON public.sales (buyer_cpf);
