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

create table authority(
    no BIGINT primary key auto_increment,
    user_no BIGINT,
    role VARCHAR(20),
    create_date DATETIME,
    update_date DATETIME,
    constraint fk_user_no foreign key(user_no) references user(no)
);