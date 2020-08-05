INSERT INTO user (email, password, name, create_date, update_date, last_login_date)
VALUES ('test@test.com', '$2a$10$C.Okl5Uo5eWn82/ZKsbWPOf82qox/pC6RzQ9fhhfK.f4MKwaSopbm', '홍길동',
'2020-05-20 14:01:11', '2020-05-20 14:01:11', '2020-08-04 17:53:38');
-- rawPassword: testpass

INSERT INTO authority (user_no, role, create_date, update_date)
VALUES (1, 'ROLE_USER', '2020-05-20 14:01:11', '2020-05-20 14:01:11');

--insert into product(name, category, description, price, create_date, update_date)
--values
--('name', 'category', 'description', 10, '1970-01-01 00:00:00', now()),
--('name', 'category1', 'description', 10, '1970-01-01 00:00:00', now());


INSERT INTO product (name, category, description, price, image, create_date, update_date)
VALUES ('Porsche 718 Boxster', '차량',
'박스터에 대한 설명',
896000000, 'images/products/car/boxster.jpg', '2020-03-20 09:17:13', '2020-03-20 09:17:13');

INSERT INTO product (name, category, description, price, image, create_date, update_date)
VALUES ('스프링 인 액션 : 스프링 5의 강력한 기능과 생산성을 활용한 웹 애플리케이션 개발[5판]', '도서',
'스프링 인 액션에 대한 설명',
31500, 'images/products/book/book01.jpg', '2020-03-20 09:17:13', '2020-03-20 09:17:13');

INSERT INTO product (name, category, description, price, image, create_date, update_date)
VALUES ('SN57 만사형통 스트레치 드레스셔츠', '의류',
'셔츠 설명',
37800, 'images/products/dress/dress01.png', '2020-03-20 09:17:13', '2020-03-20 09:17:13');

INSERT INTO product (name, category, description, price, image, create_date, update_date)
VALUES ('생초밥 2인', '식품',
'초밥에 대한 설명',
38000, 'images/products/food/food01.jpeg', '2020-03-20 09:17:13', '2020-03-20 09:17:13');
