ALTER TABLE app_users
ALTER COLUMN role TYPE varchar(30)
    USING role::varchar;
