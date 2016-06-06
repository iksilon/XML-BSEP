insert into Role (name)
values ('Predsednik');
insert into Role (name)
values ('Odbornik');
insert into Role (name)
values ('Web admin');

--dodavanje admina
--mail je admin@admin.com
--password je admin
insert into Users (username, password, PASSWORD_SALT, role_id)
values ('admin@admin.com', 'KwDYa0xpn3Od66TYcK2UlB7Bizs=', '+D8W0xIbY00=', '3');