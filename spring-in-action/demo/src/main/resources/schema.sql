CREATE TABLE user (
    no BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR,
    password VARCHAR,
    salt VARCHAR(50),
    name VARCHAR(20),
    create_date DATETIME,
    update_date DATETIME,
    last_login_date DATETIME
);

CREATE TABLE product (
    no BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR,
    category VARCHAR(50),
    description TEXT,
    price BIGINT,
    image TEXT,
    create_date DATETIME,
    update_date DATETIME
);

CREATE TABLE authority (
    no BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_no BIGINT,
    role VARCHAR(20),
    create_date DATETIME,
    update_date DATETIME,
--    constraint fk_user_no foreign key(user_no) references user(no)
    FOREIGN KEY (user_no) REFERENCES user (no)
);