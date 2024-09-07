CREATE DATABASE shopapp;
USE shopapp;

CREATE TABLE users(
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      fullname VARCHAR(100) DEFAULT '',
                      phone_number VARCHAR(10) NOT NULL,
                      address VARCHAR(200) DEFAULT '',
                      password VARCHAR(100) NOT NULL DEFAULT '',
                      created_at DATETIME,
                      updated_at DATETIME,
                      is_active TINYINT(1) DEFAULT 1,
                      date_of_birth DATE,
                      facebook_account_id INT DEFAULT 0,
                      google_account_id INT DEFAULT 0
);
ALTER TABLE users ADD COLUMN role_id INT;

CREATE TABLE roles(
                      id INT PRIMARY KEY,
                      name VARCHAR(20) NOT NULL
);
ALTER TABLE users ADD FOREIGN KEY (role_id) REFERENCES roles (id);

CREATE TABLE tokens(
                       id int PRIMARY KEY AUTO_INCREMENT,
                       token varchar(255) UNIQUE NOT NULL,
                       token_type varchar(50) NOT NULL,
                       expiration_date DATETIME,
                       revoked tinyint(1) NOT NULL,
                       expired tinyint(1) NOT NULL,
                       user_id int,
                       FOREIGN KEY (user_id) REFERENCES users(id)
);


CREATE TABLE social_accounts(
                                id INT PRIMARY KEY AUTO_INCREMENT,
                                provider VARCHAR(20) NOT NULL COMMENT 'Tên nhà social network',
                                provider_id VARCHAR(50) NOT NULL,
                                email VARCHAR(150) NOT NULL COMMENT 'Email tài khoản',
                                name VARCHAR(100) NOT NULL COMMENT 'Tên người dùng',
                                user_id int,
                                FOREIGN KEY (user_id) REFERENCES users(id)
);


CREATE TABLE categories(
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           name varchar(100) NOT NULL DEFAULT ''
);


CREATE TABLE products (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          name VARCHAR(350) COMMENT 'Tên sản phẩm',
                          price FLOAT NOT NULL CHECK (price >= 0),
                          thumbnailproducts VARCHAR(300) DEFAULT '',
                          description VARCHAR(350) DEFAULT '',
                          created_at DATETIME,
                          updated_at DATETIME,
                          category_id INT,
                          FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE product_images(
                               id INT PRIMARY KEY AUTO_INCREMENT,
                               product_id INT,
                               FOREIGN KEY (product_id) REFERENCES products (id),
                               CONSTRAINT fk_product_images_product_id
                                   FOREIGN KEY (product_id)
                                       REFERENCES products (id) ON DELETE CASCADE,
                               image_url VARCHAR(300)
);



CREATE TABLE orders(
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       FOREIGN KEY (user_id) REFERENCES users(id),
                       fullname VARCHAR(100) DEFAULT '',
                       email VARCHAR(100) DEFAULT '',
                       phone_number VARCHAR(20) NOT NULL,
                       address VARCHAR(200) NOT NULL,
                       note VARCHAR(100) DEFAULT '',
                       order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                       status VARCHAR(20),
                       total_money FLOAT CHECK(total_money >= 0)
);




ALTER TABLE orders ADD COLUMN `shipping_method` VARCHAR(100);
ALTER TABLE orders ADD COLUMN `shipping_address` VARCHAR(200);
ALTER TABLE orders ADD COLUMN `shipping_date` DATE;
ALTER TABLE orders ADD COLUMN `tracking_number` VARCHAR(100);
ALTER TABLE orders ADD COLUMN `payment_method` VARCHAR(100);

ALTER TABLE orders ADD COLUMN active TINYINT(1);

ALTER TABLE orders
    MODIFY COLUMN status ENUM('pending', 'processing', 'shipped', 'delivered', 'cancelled')
    COMMENT 'Trạng thái đơn hàng';

CREATE TABLE order_details(
                              id INT PRIMARY KEY AUTO_INCREMENT,
                              order_id INT,
                              FOREIGN KEY (order_id) REFERENCES orders (id),
                              product_id INT,
                              FOREIGN KEY (product_id) REFERENCES products (id),
                              price FLOAT CHECK(price >= 0),
                              number_of_products INT CHECK(number_of_products > 0),
                              total_money FLOAT CHECK(total_money >= 0),
                              color VARCHAR(20) DEFAULT ''
);

INSERT INTO categories(name) VALUES ('Balo');
INSERT INTO categories(name) VALUES ('Bút');
INSERT INTO categories(name) VALUES ('Hộp bút');
INSERT INTO categories(name) VALUES ('Thước kẻ');

INSERT INTO products(name,price,thumbnail,category_id) VALUES ('Balo vải Cute cat mắt tròn có tai 9x29x32',230.000,'/Balo/balo01.jpg',1);
INSERT INTO products(name,price,thumbnail,category_id) VALUES ('Balo vải Smile phối màu 20x30x46',300.000,'/Balo/balo02.jpg',1);
INSERT INTO products(name,price,thumbnail,category_id) VALUES ('Balo vải Tai mèo lucky phối màu 17x32x46',340.000,'/Balo/balo03.jpg',1);
INSERT INTO products(name,price,thumbnail,category_id) VALUES ('Balo vải Tai thỏ phối màu 10x25x30',240.000,'/Balo/balo04.jpg',1);
INSERT INTO products(name,price,thumbnail,category_id) VALUES ('Balo vải Little baby bear phối màu 17x27x43',320.000,'/Balo/balo05.jpg',1);

INSERT INTO products(name,price,thumbnail,category_id) VALUES ('Bút viết bóp đuýt Phía sau Sanrio family Pandabiz',20.000,'/But/but01.jpg',2);
INSERT INTO products(name,price,thumbnail,category_id) VALUES ('Bút viết unoff Labubu áo trắng ngồi',15.000,'/But/but02.jpg',2);
INSERT INTO products(name,price,thumbnail,category_id) VALUES ('Bút viết bấm nhiều màu Funny vibes frog fish ',25.000,'/But/but03.jpg',2);
INSERT INTO products(name,price,thumbnail,category_id) VALUES ('Bút viết bấm nhiều màu Unicorn dream sky star',20.000,'/But/but04.jpg',2);
INSERT INTO products(name,price,thumbnail,category_id) VALUES ('Bút viết set4 Hải ly Loopy cosplay Sanrio family',35.000,'/But/but05.jpg',2);

INSERT INTO products(name,price,thumbnail,category_id) VALUES ('Hộp bút lớn Sanrio family Hello Kitty face bow nơ',80.000,'/HopBut/hopbut01.jpg',3);
INSERT INTO products(name,price,thumbnail,category_id) VALUES ('Hộp bút lớn Shinchan and friends very fun 8x19',80.000,'/HopBut/hopbut02.jpg',3);
INSERT INTO products(name,price,thumbnail,category_id) VALUES ('Hộp bút lớn Sanrio family Kuromi khóa kéo lớn 8x16',80.000,'/HopBut/hopbut03.jpg',3);
INSERT INTO products(name,price,thumbnail,category_id) VALUES ('Hộp bút lớn Rabbit love carrot flower 9x12x21',100.000,'/HopBut/hopbut04.jpg',3);
INSERT INTO products(name,price,thumbnail,category_id) VALUES ('Hộp bút lớn Baby bear enjoy time 12x23',110.000,'/HopBut/hopbut05.jpg',3);

INSERT INTO products(name,price,thumbnail,category_id) VALUES ('Bộ thước kẻ Shinchan and friends heart star',30.000,'/ThuocKe/thuoc01.jpg',4);
INSERT INTO products(name,price,thumbnail,category_id) VALUES ('Thước kẻ Sanrio family Kuromi little friend',20.000,'/ThuocKe/thuoc02.jpg',4);
INSERT INTO products(name,price,thumbnail,category_id) VALUES ('Bộ thước kẻ Capybara cute music rich 15cm',30.000,'/ThuocKe/thuoc03.jpg',4);
INSERT INTO products(name,price,thumbnail,category_id) VALUES ('Bộ thước kẻ Sanrio family Sanrio lovers club 20cm',20.000,'/ThuocKe/thuoc04.jpg',4);

INSERT INTO users(phone_number,password) VALUES ('123456','123456')