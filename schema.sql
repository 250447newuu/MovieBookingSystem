DROP DATABASE IF EXISTS movie_booking;
CREATE DATABASE movie_booking;
USE movie_booking;

CREATE TABLE customers (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(100) NOT NULL,
                           email VARCHAR(100) NOT NULL UNIQUE,
                           phone VARCHAR(30),
                           password VARCHAR(100) NOT NULL
);

CREATE TABLE cities (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(100) NOT NULL
);

CREATE TABLE cinemas (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         city_id INT NOT NULL,
                         FOREIGN KEY (city_id) REFERENCES cities(id)
);

CREATE TABLE cinema_halls (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              name VARCHAR(100) NOT NULL,
                              cinema_id INT NOT NULL,
                              FOREIGN KEY (cinema_id) REFERENCES cinemas(id)
);

CREATE TABLE movies (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        title VARCHAR(100) NOT NULL,
                        language VARCHAR(50) NOT NULL,
                        genre VARCHAR(50) NOT NULL,
                        release_date DATE NOT NULL,
                        duration_minutes INT NOT NULL,
                        description TEXT
);

CREATE TABLE shows (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       movie_id INT NOT NULL,
                       hall_id INT NOT NULL,
                       show_time DATETIME NOT NULL,
                       price DECIMAL(10,2) NOT NULL,
                       FOREIGN KEY (movie_id) REFERENCES movies(id),
                       FOREIGN KEY (hall_id) REFERENCES cinema_halls(id)
);

CREATE TABLE show_seats (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            show_id INT NOT NULL,
                            seat_number VARCHAR(10) NOT NULL,
                            status VARCHAR(20) NOT NULL,
                            FOREIGN KEY (show_id) REFERENCES shows(id),
                            UNIQUE (show_id, seat_number)
);

CREATE TABLE bookings (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          customer_id INT NOT NULL,
                          show_id INT NOT NULL,
                          booking_time DATETIME NOT NULL,
                          total_amount DECIMAL(10,2) NOT NULL,
                          payment_method VARCHAR(30) NOT NULL,
                          coupon_code VARCHAR(30),
                          status VARCHAR(30) NOT NULL,
                          FOREIGN KEY (customer_id) REFERENCES customers(id),
                          FOREIGN KEY (show_id) REFERENCES shows(id)
);

CREATE TABLE booking_seats (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               booking_id INT NOT NULL,
                               show_seat_id INT NOT NULL,
                               FOREIGN KEY (booking_id) REFERENCES bookings(id),
                               FOREIGN KEY (show_seat_id) REFERENCES show_seats(id),
                               UNIQUE (show_seat_id)
);

CREATE TABLE coupons (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         code VARCHAR(30) NOT NULL UNIQUE,
                         discount_percent INT NOT NULL
);

INSERT INTO customers (name, email, phone, password) VALUES
                                                         ('Alice Johnson', 'alice@email.com', '1111111111', 'alice123'),
                                                         ('Bob Smith', 'bob@email.com', '2222222222', 'bob123');

INSERT INTO cities (name) VALUES
                              ('New York'),
                              ('Los Angeles'),
                              ('Chicago');

INSERT INTO cinemas (name, city_id) VALUES
                                        ('Grand Cineplex', 1),
                                        ('Sunset Cinema', 2),
                                        ('Downtown Movies', 3);

INSERT INTO cinema_halls (name, cinema_id) VALUES
                                               ('Hall A', 1),
                                               ('Hall B', 1),
                                               ('Hall A', 2),
                                               ('Hall A', 3);

INSERT INTO movies (title, language, genre, release_date, duration_minutes, description) VALUES
                                                                                             ('Inception', 'English', 'Sci-Fi', '2010-07-16', 148, 'A thief enters dreams to steal secrets.'),
                                                                                             ('The Dark Knight', 'English', 'Action', '2008-07-18', 152, 'Batman faces the Joker in Gotham City.'),
                                                                                             ('Interstellar', 'English', 'Sci-Fi', '2014-11-07', 169, 'Astronauts travel through space to save humanity.'),
                                                                                             ('Parasite', 'Korean', 'Thriller', '2019-05-30', 132, 'A poor family becomes involved with a rich family.');

INSERT INTO shows (movie_id, hall_id, show_time, price) VALUES
                                                            (1, 1, '2026-05-10 18:00:00', 12.00),
                                                            (1, 2, '2026-05-10 21:00:00', 14.00),
                                                            (2, 1, '2026-05-11 19:00:00', 13.00),
                                                            (3, 3, '2026-05-12 20:00:00', 15.00),
                                                            (4, 4, '2026-05-13 17:30:00', 11.00);

INSERT INTO coupons (code, discount_percent) VALUES
                                                 ('SAVE10', 10),
                                                 ('SAVE20', 20),
                                                 ('SAVE50',50),
                                                 ('SAVE70',70);

INSERT INTO show_seats (show_id, seat_number, status)
SELECT s.id, seat_list.seat_number, 'AVAILABLE'
FROM shows s
         CROSS JOIN (
    SELECT 'A1' AS seat_number UNION ALL
    SELECT 'A2' UNION ALL
    SELECT 'A3' UNION ALL
    SELECT 'A4' UNION ALL
    SELECT 'A5' UNION ALL
    SELECT 'B1' UNION ALL
    SELECT 'B2' UNION ALL
    SELECT 'B3' UNION ALL
    SELECT 'B4' UNION ALL
    SELECT 'B5' UNION ALL
    SELECT 'C1' UNION ALL
    SELECT 'C2' UNION ALL
    SELECT 'C3' UNION ALL
    SELECT 'C4' UNION ALL
    SELECT 'C5'
) seat_list;