insert into user(email, password, salt, name, create_date, update_date, last_login_date)
values ('aaa', 'bbb', 'ccc', 'ddd', now(), '2018-10-10 00:00:00', '2018-10-10 00:00:00');

insert into product(name, category, description, price, create_date, update_date)
values
('name', 'category', 'description', 10, '1970-01-01 00:00:00', now()),
('name', 'category1', 'description', 10, '1970-01-01 00:00:00', now());

insert into authority(user_no, role, create_date, update_date)
values
(1, 'role_user', now(), now());
