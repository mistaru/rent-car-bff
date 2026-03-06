-- =============================================
-- AUTH SEED: Пользователи, роли, разрешения
-- Запускать ПОСЛЕ первого старта приложения
-- (Hibernate с ddl-auto:update создаст таблицы)
-- =============================================

-- 1. Пользователи (admin / manager)
--    пароль admin:   qwerty123
--    пароль manager: Qwerty123!
INSERT INTO auth (id, cdt, created_by_id, created_by_table, mdt, modified_by_id, modified_by_table, rdt,
                  blocked, last_activity, password, password_expire_date, username)
VALUES (nextval('auth_SEQ'), '2025-02-15 07:47:49.439297', null, null, '2025-02-15 07:47:49.439297', null,
        null, null, null, '2025-02-15 07:47:49.439297',
        '$s0$41010$Feo6IYP0vwGh98tKc0PuVw==$caiCX8dvVgFlRBL/vEw0zj6rEN+2bA5u9hp2yvtbmB4=', null, 'admin'),
       (nextval('auth_SEQ'), '2025-03-16 23:43:51.423239', 9999, 'AUTH', '2025-03-23 02:04:13.501261', 9999,
        'AUTH', null, null, '2025-03-23 02:04:13.501261',
        '$s0$41010$IaU30kq0z56/7VzDPzOgWg==$xo2l2qHa3V+0Wf4qlT98OdqI50bus1GcHpIMd7JfTRI=', '2025-04-22 02:04:13.479580',
        'manager')
ON CONFLICT DO NOTHING;

-- 2. Роли
INSERT INTO roles (id, cdt, created_by_id, created_by_table, mdt, modified_by_id, modified_by_table, rdt,
                   description, name)
VALUES (nextval('roles_SEQ'), null, null, null, '2025-03-17 22:03:58.845724', 9999, 'AUTH', null,
        'Отвечает за управление системой, настройку доступа и обеспечение ее стабильной работы', 'admin'),
       (nextval('roles_SEQ'), '2025-03-16 22:04:15.447190', 9999, 'AUTH', '2025-03-23 02:09:36.165717', 9999,
        'AUTH', null, 'Рядовый сотрудник', 'manager')
ON CONFLICT DO NOTHING;

-- 3. Привязка ролей к пользователям
INSERT INTO auth_roles (active, auth_id, role_id)
VALUES (true, (SELECT id FROM auth WHERE username = 'admin'),
        (SELECT id FROM roles WHERE name = 'admin')),
       (true, (SELECT id FROM auth WHERE username = 'manager'),
        (SELECT id FROM roles WHERE name = 'manager'))
ON CONFLICT DO NOTHING;

-- 4. Разрешения
INSERT INTO permissions (id, cdt, created_by_id, created_by_table, mdt, modified_by_id,
                         modified_by_table, rdt, description, name)
VALUES (nextval('PERMISSIONS_SEQ'), '2025-02-15 08:03:55.018933', null, null, '2025-02-15 08:03:55.018933',
        null, null, null, 'Доступы', 'PERMISSION'),
       (nextval('PERMISSIONS_SEQ'), '2025-02-15 08:03:55.018933', null, null, '2025-02-15 08:03:55.018933',
        null, null, null, 'Роли', 'ROLE'),
       (nextval('PERMISSIONS_SEQ'), '2025-02-15 08:03:55.018933', null, null, '2025-02-15 08:03:55.018933',
        null, null, null, 'Пользователи', 'AUTH')
ON CONFLICT DO NOTHING;

-- 5. Привязка разрешений к ролям
--    admin: полный доступ (15), manager: только чтение (4)
INSERT INTO role_permissions (permission_access, permission_id, role_id)
VALUES (15, 1, (SELECT id FROM roles WHERE name = 'admin')),
       (15, 2, (SELECT id FROM roles WHERE name = 'admin')),
       (15, 3, (SELECT id FROM roles WHERE name = 'admin')),
       (4, 1, (SELECT id FROM roles WHERE name = 'manager')),
       (4, 2, (SELECT id FROM roles WHERE name = 'manager')),
       (4, 3, (SELECT id FROM roles WHERE name = 'manager'))
ON CONFLICT DO NOTHING;

