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
    price NUMERIC(15,2) NOT NULL
    );

CREATE INDEX IF NOT EXISTS ix_car_brand_model ON public.car (brand, model);