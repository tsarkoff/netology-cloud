-- liquibase formatted sql
--changeset tsarkoff:003 failOnError:true

-- fill up roles
insert into public.roles (role)
values ('ROLE_GUEST'),
       ('ROLE_VIEWER'),
       ('ROLE_READER'),
       ('ROLE_WRITER'),
       ('ROLE_MASTER');

-- fill up permissions
insert into public.permissions (permission)
values ('AUTHORITY_NONE'),
       ('AUTHORITY_LOGIN'),
       ('AUTHORITY_VIEW'),
       ('AUTHORITY_READ'),
       ('AUTHORITY_WRITE'),
       ('AUTHORITY_DELETE');

-- fill up roles_permissions
insert into public.roles_permissions (role_id, permission_id)
values ((select r.id from roles as r where r.role = 'ROLE_GUEST'),
        (select p.id from permissions as p where p.permission = 'AUTHORITY_LOGIN')),

       ((select r.id from roles as r where r.role = 'ROLE_VIEWER'),
        (select p.id from permissions as p where p.permission = 'AUTHORITY_LOGIN')),
       ((select r.id from roles as r where r.role = 'ROLE_VIEWER'),
        (select p.id from permissions as p where p.permission = 'AUTHORITY_VIEW')),

       ((select r.id from roles as r where r.role = 'ROLE_READER'),
        (select p.id from permissions as p where p.permission = 'AUTHORITY_LOGIN')),
       ((select r.id from roles as r where r.role = 'ROLE_READER'),
        (select p.id from permissions as p where p.permission = 'AUTHORITY_VIEW')),
       ((select r.id from roles as r where r.role = 'ROLE_READER'),
        (select p.id from permissions as p where p.permission = 'AUTHORITY_READ')),

       ((select r.id from roles as r where r.role = 'ROLE_WRITER'),
        (select p.id from permissions as p where p.permission = 'AUTHORITY_LOGIN')),
       ((select r.id from roles as r where r.role = 'ROLE_WRITER'),
        (select p.id from permissions as p where p.permission = 'AUTHORITY_WRITE')),

       ((select r.id from roles as r where r.role = 'ROLE_MASTER'),
        (select p.id from permissions as p where p.permission = 'AUTHORITY_LOGIN')),
       ((select r.id from roles as r where r.role = 'ROLE_MASTER'),
        (select p.id from permissions as p where p.permission = 'AUTHORITY_VIEW')),
       ((select r.id from roles as r where r.role = 'ROLE_MASTER'),
        (select p.id from permissions as p where p.permission = 'AUTHORITY_READ')),
       ((select r.id from roles as r where r.role = 'ROLE_MASTER'),
        (select p.id from permissions as p where p.permission = 'AUTHORITY_WRITE')),
       ((select r.id from roles as r where r.role = 'ROLE_MASTER'),
        (select p.id from permissions as p where p.permission = 'AUTHORITY_DELETE'));

-- fill up users
insert into public.users (username, password, role_id)
values ('g@m.ru', 'pwd', (select r.id from roles as r where r.role = 'ROLE_GUEST')),
       ('v@m.ru', 'pwd', (select r.id from roles as r where r.role = 'ROLE_VIEWER')),
       ('r@m.ru', 'pwd', (select r.id from roles as r where r.role = 'ROLE_READER')),
       ('w@m.ru', 'pwd', (select r.id from roles as r where r.role = 'ROLE_WRITER')),
       ('m@m.ru', 'pwd', (select r.id from roles as r where r.role = 'ROLE_MASTER'));

-- fill up authorities
insert into public.authorities (username_id, username, authority)
values ((select u.id from users as u where u.username = 'g@m.ru'), 'g@m.ru', 'AUTHORITY_LOGIN'),

       ((select u.id from users as u where u.username = 'v@m.ru'), 'v@m.ru', 'AUTHORITY_LOGIN'),
       ((select u.id from users as u where u.username = 'v@m.ru'), 'v@m.ru', 'AUTHORITY_VIEW'),

       ((select u.id from users as u where u.username = 'r@m.ru'), 'r@m.ru', 'AUTHORITY_LOGIN'),
       ((select u.id from users as u where u.username = 'r@m.ru'), 'r@m.ru', 'AUTHORITY_VIEW'),
       ((select u.id from users as u where u.username = 'r@m.ru'), 'r@m.ru', 'AUTHORITY_READ'),

       ((select u.id from users as u where u.username = 'w@m.ru'), 'w@m.ru', 'AUTHORITY_LOGIN'),
       ((select u.id from users as u where u.username = 'w@m.ru'), 'w@m.ru', 'AUTHORITY_VIEW'),
       ((select u.id from users as u where u.username = 'w@m.ru'), 'w@m.ru', 'AUTHORITY_READ'),
       ((select u.id from users as u where u.username = 'w@m.ru'), 'w@m.ru', 'AUTHORITY_WRITE'),

       ((select u.id from users as u where u.username = 'm@m.ru'), 'm@m.ru', 'AUTHORITY_LOGIN'),
       ((select u.id from users as u where u.username = 'm@m.ru'), 'm@m.ru', 'AUTHORITY_VIEW'),
       ((select u.id from users as u where u.username = 'm@m.ru'), 'm@m.ru', 'AUTHORITY_READ'),
       ((select u.id from users as u where u.username = 'm@m.ru'), 'm@m.ru', 'AUTHORITY_WRITE'),
       ((select u.id from users as u where u.username = 'm@m.ru'), 'm@m.ru', 'AUTHORITY_DELETE');