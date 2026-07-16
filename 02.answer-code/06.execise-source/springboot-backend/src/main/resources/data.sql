INSERT INTO users (name, email, version) VALUES ('홍길동', 'hong@sk.com', 0);
INSERT INTO users (name, email, version) VALUES ('김철수', 'kim@sk.com', 0);
INSERT INTO users (name, email, version) VALUES ('이영희', 'lee@sk.com', 0);

INSERT INTO categories (name, description) VALUES ('컴퓨터/노트북', '노트북, 데스크탑 등 컴퓨팅 기기');
INSERT INTO categories (name, description) VALUES ('주변기기', '마우스, 키보드, 모니터 등 PC 주변기기');
INSERT INTO categories (name, description) VALUES ('저장장치', 'SSD, HDD, USB 등 저장 매체');

INSERT INTO products (product_name, price, stock_quantity, status, description, user_id, category_id, version) VALUES ('노트북', 1500000, 10, 'ON_SALE', '고성능 개발용 노트북입니다.', 1, 1, 0);
INSERT INTO products (product_name, price, stock_quantity, status, description, user_id, category_id, version) VALUES ('무선 마우스', 35000, 50, 'ON_SALE', '2.4GHz 무선 마우스입니다.', 1, 2, 0);
INSERT INTO products (product_name, price, stock_quantity, status, description, user_id, category_id, version) VALUES ('기계식 키보드', 120000, 0, 'SOLD_OUT', '청축 기계식 키보드입니다.', 2, 2, 0);
INSERT INTO products (product_name, price, stock_quantity, status, description, user_id, category_id, version) VALUES ('27인치 모니터', 350000, 5, 'ON_SALE', 'QHD 해상도 모니터입니다.', 2, 2, 0);
INSERT INTO products (product_name, price, stock_quantity, status, description, user_id, category_id, version) VALUES ('USB 허브', 25000, 0, 'DISCONTINUED', '단종된 4포트 USB 허브입니다.', 3, 3, 0);
