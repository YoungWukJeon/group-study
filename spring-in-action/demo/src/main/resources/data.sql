insert into user(email, password, salt, name, create_date, update_date, last_login_date)
values ('zzz@a.com', 'bbb', 'ccc', 'ddd', now(), now(), now());

insert into authority(user_no, role, create_date, update_date)
values (1, 'ROLE_USER', now(), now())