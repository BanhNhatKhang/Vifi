insert into users (
    id,
    username,
    email,
    password_hash,
    role,
    provider,
    is_enabled,
    created_at
) values (
    '11111111-1111-1111-1111-111111111111',
    'flowuser',
    'flow@test.com',
    '$2a$10$7QJ8s0Z1Yy0LJ3m1j1pZ6OeYkH6QeY8W9xg0fFv9F9vZKkYF2Zp2e', -- password = password
    'USER',
    'LOCAL',
    true,
    now()
);
