INSERT INTO public.tenants (id, schema_name, type, name, vertical) VALUES
  ('7b83736f-45ba-43dd-8d12-f6c8e01dd0b1', '_7b83736f45ba43dd8d12f6c8e01dd0b1', 'PERSONAL', 'Dênia Ingrid França Santos',   'HEALTH'),
  ('d9e4e8b0-e7fd-451c-a514-a69087057f1f', '_d9e4e8b0e7fd451ca514a69087057f1f', 'PERSONAL', 'Kenia Kristina do Nascimento', 'HEALTH');

INSERT INTO public.users (id, email, name, password_hash, personal_tenant_id, email_verified_at) VALUES
  ('64de8485-e3ff-425e-8619-5bb9c69878d8', 'deniapsicologa@hotmail.com',           'Dênia Ingrid França Santos',
     crypt('Dox@2026', gen_salt('bf', 10)), '7b83736f-45ba-43dd-8d12-f6c8e01dd0b1', NOW()),
  ('8bf6fa21-f31b-4c97-a8c2-278b2f2b59cb', 'psicologa.kenianascimento@hotmail.com', 'Kenia Kristina do Nascimento',
     crypt('Dox@2026', gen_salt('bf', 10)), 'd9e4e8b0-e7fd-451c-a514-a69087057f1f', NOW());
