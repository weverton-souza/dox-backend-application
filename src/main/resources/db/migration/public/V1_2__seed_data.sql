INSERT INTO tenants (id, schema_name, type, name, vertical) VALUES
('a0000001-0000-0000-0000-000000000001', '_a0000001000000000000000000000001', 'PERSONAL', 'Dra. Ana Silva', 'HEALTH');

INSERT INTO users (id, email, name, password_hash, personal_tenant_id) VALUES
('b0000001-0000-0000-0000-000000000001', 'ana.silva@email.com', 'Ana Silva', '$2a$10$1GXVGBltuNF2RiWWn5v0/u4FUyIth1LEaASlWONu0zy.w.R3skNIO', 'a0000001-0000-0000-0000-000000000001');
