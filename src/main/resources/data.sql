-- Seed data: runs on every start, so every insert must be safe to repeat (ON CONFLICT DO NOTHING)

CREATE UNIQUE INDEX IF NOT EXISTS uq_categories_name_lower ON categories (lower(name));

ALTER TABLE role_permissions DROP CONSTRAINT IF EXISTS role_permissions_permission_check;

INSERT INTO roles (name, description, built_in) VALUES
    ('ADMIN',         'Full access',                            true),
    ('STOCK_MANAGER', 'Manages stock in assigned warehouses',   true),
    ('VIEWER',        'Read-only access to products and stock', true)
ON CONFLICT (name) DO NOTHING;

INSERT INTO role_permissions (role_id, permission)
SELECT r.id, p.permission
FROM roles r
JOIN (VALUES
    ('ADMIN', 'PRODUCT_READ'), ('ADMIN', 'PRODUCT_WRITE'),
    ('ADMIN', 'STOCK_READ'),   ('ADMIN', 'STOCK_ADJUST'),
    ('ADMIN', 'USER_READ'),    ('ADMIN', 'USER_MANAGE'),
    ('ADMIN', 'ROLE_MANAGE'),  ('ADMIN', 'AUDIT_READ'),
    ('ADMIN', 'SYSTEM_MONITOR'),

    ('STOCK_MANAGER', 'PRODUCT_READ'),
    ('STOCK_MANAGER', 'STOCK_READ'),
    ('STOCK_MANAGER', 'STOCK_ADJUST'),

    ('VIEWER', 'PRODUCT_READ'),
    ('VIEWER', 'STOCK_READ')
) AS p (role_name, permission) ON p.role_name = r.name
ON CONFLICT DO NOTHING;

INSERT INTO warehouses (code, name, created_at) VALUES ('MAIN', 'Main warehouse', now())
ON CONFLICT (code) DO NOTHING;

INSERT INTO stock_products (id, sku, name, archived)
SELECT id, sku, name, status = 'ARCHIVED' FROM products
ON CONFLICT (id) DO UPDATE SET sku = EXCLUDED.sku, name = EXCLUDED.name, archived = EXCLUDED.archived;
