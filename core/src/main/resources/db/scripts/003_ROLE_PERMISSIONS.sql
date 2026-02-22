INSERT INTO role_permissions (permission_access, permission_id, role_id)
VALUES (15, 1, (SELECT id FROM roles WHERE name = 'admin')),
       (15, 2, (SELECT id FROM roles WHERE name = 'admin')),
       (15, 3, (SELECT id FROM roles WHERE name = 'admin')),
       (4, 1, (SELECT id FROM roles WHERE name = 'manager')),
       (4, 2, (SELECT id FROM roles WHERE name = 'manager')),
       (4, 3, (SELECT id FROM roles WHERE name = 'manager'));
