CREATE TABLE user (
    no BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR UNIQUE NOT NULL,
    password VARCHAR NOT NULL,
    name VARCHAR(20) NOT NULL,
    create_date DATETIME,
    update_date DATETIME,
    last_login_date DATETIME
);

CREATE TABLE product (
    no BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR NOT NULL,
    category VARCHAR(50) NOT NULL,
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