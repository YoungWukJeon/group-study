create table user(
    no BIGINT primary key auto_increment,
    email VARCHAR,
    password VARCHAR,
    salt VARCHAR(50),
    name VARCHAR(20),
    create_date DATETIME,
    update_date DATETIME,
    last_login_date DATETIME
);