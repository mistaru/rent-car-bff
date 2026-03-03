-- LuxeDrive Rental Schema Migration
-- Creates all tables for the car rental service

-- 1. Locations
CREATE TABLE IF NOT EXISTS locations (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    city        VARCHAR(255) NOT NULL,
    country     VARCHAR(255) NOT NULL
);

-- 2. Vehicles
CREATE TABLE IF NOT EXISTS vehicles (
    id            BIGSERIAL PRIMARY KEY,
    brand         VARCHAR(255) NOT NULL,
    model         VARCHAR(255) NOT NULL,
    year          INTEGER,
    license_plate VARCHAR(255) NOT NULL,
    body_type     VARCHAR(50),
    drivetrain    VARCHAR(20),
    fuel_type     VARCHAR(30),
    transmission  VARCHAR(30),
    image         VARCHAR(1024),
    car_class     VARCHAR(100),
    price_per_day NUMERIC(10, 2) NOT NULL,
    status        VARCHAR(20)  NOT NULL DEFAULT 'AVAILABLE',
    location_id   BIGINT       NOT NULL REFERENCES locations(id),
    version       BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_vehicle_status ON vehicles(status);
CREATE INDEX IF NOT EXISTS idx_vehicle_location ON vehicles(location_id);

-- 3. Customers
CREATE TABLE IF NOT EXISTS customers (
    id        BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email     VARCHAR(255) NOT NULL UNIQUE,
    phone     VARCHAR(50)
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_customer_email ON customers(email);

-- 4. Bookings
CREATE TABLE IF NOT EXISTS bookings (
    id                  BIGSERIAL PRIMARY KEY,
    vehicle_id          BIGINT         NOT NULL REFERENCES vehicles(id),
    customer_id         BIGINT         NOT NULL REFERENCES customers(id),
    pickup_location_id  BIGINT         NOT NULL REFERENCES locations(id),
    dropoff_location_id BIGINT         NOT NULL REFERENCES locations(id),
    pickup_date         DATE           NOT NULL,
    dropoff_date        DATE           NOT NULL,
    days                INTEGER        NOT NULL,
    base_amount         NUMERIC(12, 2) NOT NULL,
    add_ons_amount      NUMERIC(12, 2) NOT NULL DEFAULT 0,
    service_fee         NUMERIC(12, 2) NOT NULL DEFAULT 0,
    total_amount        NUMERIC(12, 2) NOT NULL,
    currency            VARCHAR(3)     NOT NULL DEFAULT 'USD',
    status              VARCHAR(30)    NOT NULL DEFAULT 'DRAFT',
    payment_status      VARCHAR(20)    NOT NULL DEFAULT 'UNPAID',
    created_at          TIMESTAMP      NOT NULL DEFAULT NOW(),
    version             BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_booking_vehicle ON bookings(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_booking_dates ON bookings(pickup_date, dropoff_date);

-- 5. Booking Add-ons
CREATE TABLE IF NOT EXISTS booking_add_ons (
    id            BIGSERIAL PRIMARY KEY,
    booking_id    BIGINT         NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    add_on_type   VARCHAR(50)    NOT NULL,
    price_per_day NUMERIC(10, 2) NOT NULL
);

-- 6. Payments
CREATE TABLE IF NOT EXISTS payments (
    id             BIGSERIAL PRIMARY KEY,
    booking_id     BIGINT         NOT NULL REFERENCES bookings(id),
    method         VARCHAR(20)    NOT NULL,
    status         VARCHAR(20)    NOT NULL DEFAULT 'INITIATED',
    amount         NUMERIC(12, 2) NOT NULL,
    transaction_id VARCHAR(255),
    created_at     TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- =============================================
-- SEED DATA
-- =============================================

-- 7. Seed data: Locations
INSERT INTO locations (name, city, country) VALUES
    ('Airport Terminal', 'Bishkek', 'Kyrgyzstan'),
    ('City Center Office', 'Bishkek', 'Kyrgyzstan'),
    ('Issyk-Kul Resort', 'Cholpon-Ata', 'Kyrgyzstan'),
    ('Osh Airport', 'Osh', 'Kyrgyzstan'),
    ('Karakol Station', 'Karakol', 'Kyrgyzstan'),
    ('South Gate Mall', 'Bishkek', 'Kyrgyzstan')
ON CONFLICT DO NOTHING;

-- 8. Seed data: Vehicles (20 cars)
INSERT INTO vehicles (brand, model, year, license_plate, body_type, drivetrain, fuel_type, transmission, image, car_class, price_per_day, status, location_id, version) VALUES
    ('Toyota',        'Camry',          2024, '01KG777ABC', 'Sedan',      'FWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?w=600&h=400&fit=crop',  'Standard Sedan',    75.00,  'AVAILABLE', 1, 0),
    ('BMW',           'X5',             2023, '01KG888DEF', 'SUV',        'AWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1555215695-3004980ad54e?w=600&h=400&fit=crop',  'Premium SUV',      150.00,  'AVAILABLE', 1, 0),
    ('Mercedes-Benz', 'S-Class',        2024, '01KG999GHI', 'Sedan',      'AWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?w=600&h=400&fit=crop',  'Luxury Sedan',     320.00,  'AVAILABLE', 2, 0),
    ('Hyundai',       'Tucson',         2023, '01KG111JKL', 'SUV',        'FWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1633695632073-30f4e8e6b6e3?w=600&h=400&fit=crop',  'Compact SUV',       60.00,  'AVAILABLE', 2, 0),
    ('Lexus',         'RX350',          2024, '01KG222MNO', 'SUV',        'AWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1622126807280-9b5b32b44e68?w=600&h=400&fit=crop',  'Premium SUV',      180.00,  'AVAILABLE', 3, 0),
    ('Kia',           'Sportage',       2022, '01KG333PQR', 'SUV',        'FWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1609521263047-f8f205293f24?w=600&h=400&fit=crop',  'Compact SUV',       50.00,  'AVAILABLE', 3, 0),
    ('Toyota',        'Land Cruiser',   2024, '01KG444STU', 'SUV',        'AWD', 'Diesel',   'Automatic', 'https://images.unsplash.com/photo-1594611396050-1f36031e1eaf?w=600&h=400&fit=crop',  'Full-Size SUV',    250.00,  'AVAILABLE', 4, 0),
    ('Chevrolet',     'Malibu',         2023, '01KG555VWX', 'Sedan',      'FWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=600&h=400&fit=crop',  'Standard Sedan',    65.00,  'AVAILABLE', 4, 0),
    ('Audi',          'A6',             2024, '01KG600AAA', 'Sedan',      'AWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?w=600&h=400&fit=crop',  'Premium Sedan',    200.00,  'AVAILABLE', 1, 0),
    ('Tesla',         'Model 3',        2024, '01KG601BBB', 'Sedan',      'AWD', 'Electric', 'Automatic', 'https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=600&h=400&fit=crop',  'Electric Sedan',   190.00,  'AVAILABLE', 2, 0),
    ('Porsche',       'Cayenne',        2023, '01KG602CCC', 'SUV',        'AWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1606016159991-dfe4f2746ad5?w=600&h=400&fit=crop',  'Luxury SUV',       400.00,  'AVAILABLE', 2, 0),
    ('Volkswagen',    'Tiguan',         2023, '01KG603DDD', 'SUV',        'FWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1609521263047-f8f205293f24?w=600&h=400&fit=crop',  'Compact SUV',       70.00,  'AVAILABLE', 3, 0),
    ('Range Rover',   'Sport',          2024, '01KG604EEE', 'SUV',        'AWD', 'Diesel',   'Automatic', 'https://images.unsplash.com/photo-1606016159991-dfe4f2746ad5?w=600&h=400&fit=crop',  'Luxury SUV',       450.00,  'AVAILABLE', 1, 0),
    ('Honda',         'Civic',          2023, '01KG605FFF', 'Sedan',      'FWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=600&h=400&fit=crop',  'Standard Sedan',    55.00,  'AVAILABLE', 4, 0),
    ('BMW',           '3 Series',       2024, '01KG606GGG', 'Sedan',      'RWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1555215695-3004980ad54e?w=600&h=400&fit=crop',  'Premium Sedan',    170.00,  'AVAILABLE', 5, 0),
    ('Toyota',        'RAV4',           2023, '01KG607HHH', 'SUV',        'AWD', 'Hybrid',   'Automatic', 'https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?w=600&h=400&fit=crop',  'Compact SUV',       85.00,  'AVAILABLE', 5, 0),
    ('Mercedes-Benz', 'GLE',            2024, '01KG608III', 'SUV',        'AWD', 'Diesel',   'Automatic', 'https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?w=600&h=400&fit=crop',  'Premium SUV',      280.00,  'AVAILABLE', 6, 0),
    ('Nissan',        'Qashqai',        2022, '01KG609JJJ', 'SUV',        'FWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1609521263047-f8f205293f24?w=600&h=400&fit=crop',  'Compact SUV',       45.00,  'AVAILABLE', 6, 0),
    ('Ford',          'Mustang',        2024, '01KG610KKK', 'Coupe',      'RWD', 'Gasoline', 'Manual',    'https://images.unsplash.com/photo-1494976388531-d1058494cdd8?w=600&h=400&fit=crop',  'Sports Car',       350.00,  'AVAILABLE', 1, 0),
    ('Toyota',        'Corolla',        2023, '01KG611LLL', 'Sedan',      'FWD', 'Hybrid',   'Automatic', 'https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=600&h=400&fit=crop',  'Economy Sedan',     40.00,  'AVAILABLE', 2, 0)
ON CONFLICT DO NOTHING;

-- 9. Seed data: Customers (10 customers)
INSERT INTO customers (full_name, email, phone) VALUES
    ('Айбек Касымов',     'aibek.kasymov@mail.kg',     '+996555100101'),
    ('Динара Жумабаева',  'dinara.zhumabaeva@mail.kg',  '+996700200202'),
    ('Руслан Токтогулов', 'ruslan.toktogulov@gmail.com', '+996557300303'),
    ('Алина Сыдыкова',    'alina.sydykova@gmail.com',   '+996703400404'),
    ('Тимур Абдыраев',    'timur.abdyraev@inbox.kg',    '+996555500505'),
    ('Жазгуль Эсенова',   'jazgul.esenova@mail.kg',     '+996700600606'),
    ('Марат Бекмуратов',  'marat.bekmuratov@gmail.com', '+996558700707'),
    ('Нурай Алиева',      'nuray.alieva@mail.kg',       '+996701800808'),
    ('John Smith',        'john.smith@outlook.com',     '+14155551234'),
    ('Emma Johnson',      'emma.johnson@gmail.com',     '+447911123456')
ON CONFLICT (email) DO NOTHING;

-- 10. Seed data: Bookings (8 bookings with various statuses)
-- Booking 1: Confirmed, paid (past trip)
INSERT INTO bookings (vehicle_id, customer_id, pickup_location_id, dropoff_location_id, pickup_date, dropoff_date, days, base_amount, add_ons_amount, service_fee, total_amount, currency, status, payment_status, created_at, version) VALUES
    (3, 1, 2, 2, '2026-02-10', '2026-02-14', 4, 1280.00, 80.00, 136.00, 1496.00, 'USD', 'CONFIRMED', 'PAID', '2026-02-05 10:30:00', 0);

-- Booking 2: Confirmed, paid (past trip)
INSERT INTO bookings (vehicle_id, customer_id, pickup_location_id, dropoff_location_id, pickup_date, dropoff_date, days, base_amount, add_ons_amount, service_fee, total_amount, currency, status, payment_status, created_at, version) VALUES
    (2, 2, 1, 1, '2026-02-20', '2026-02-23', 3, 450.00, 15.00, 46.50, 511.50, 'USD', 'CONFIRMED', 'PAID', '2026-02-15 14:20:00', 0);

-- Booking 3: Confirmed, upcoming trip
INSERT INTO bookings (vehicle_id, customer_id, pickup_location_id, dropoff_location_id, pickup_date, dropoff_date, days, base_amount, add_ons_amount, service_fee, total_amount, currency, status, payment_status, created_at, version) VALUES
    (7, 3, 4, 3, '2026-03-15', '2026-03-20', 5, 1250.00, 100.00, 135.00, 1485.00, 'USD', 'CONFIRMED', 'PAID', '2026-03-01 09:00:00', 0);

-- Booking 4: Pending payment
INSERT INTO bookings (vehicle_id, customer_id, pickup_location_id, dropoff_location_id, pickup_date, dropoff_date, days, base_amount, add_ons_amount, service_fee, total_amount, currency, status, payment_status, created_at, version) VALUES
    (11, 4, 2, 2, '2026-03-18', '2026-03-22', 4, 1600.00, 60.00, 166.00, 1826.00, 'USD', 'PENDING_PAYMENT', 'UNPAID', '2026-03-02 16:45:00', 0);

-- Booking 5: Cancelled booking
INSERT INTO bookings (vehicle_id, customer_id, pickup_location_id, dropoff_location_id, pickup_date, dropoff_date, days, base_amount, add_ons_amount, service_fee, total_amount, currency, status, payment_status, created_at, version) VALUES
    (1, 5, 1, 2, '2026-03-05', '2026-03-08', 3, 225.00, 21.00, 24.60, 270.60, 'USD', 'CANCELLED', 'UNPAID', '2026-02-28 11:15:00', 0);

-- Booking 6: Confirmed, future luxury trip
INSERT INTO bookings (vehicle_id, customer_id, pickup_location_id, dropoff_location_id, pickup_date, dropoff_date, days, base_amount, add_ons_amount, service_fee, total_amount, currency, status, payment_status, created_at, version) VALUES
    (13, 9, 1, 1, '2026-04-01', '2026-04-07', 6, 2700.00, 120.00, 282.00, 3102.00, 'USD', 'CONFIRMED', 'PAID', '2026-03-01 08:00:00', 0);

-- Booking 7: Confirmed, economy
INSERT INTO bookings (vehicle_id, customer_id, pickup_location_id, dropoff_location_id, pickup_date, dropoff_date, days, base_amount, add_ons_amount, service_fee, total_amount, currency, status, payment_status, created_at, version) VALUES
    (20, 6, 2, 2, '2026-03-10', '2026-03-12', 2, 80.00, 10.00, 9.00, 99.00, 'USD', 'CONFIRMED', 'PAID', '2026-03-03 12:00:00', 0);

-- Booking 8: Pending payment, sports car
INSERT INTO bookings (vehicle_id, customer_id, pickup_location_id, dropoff_location_id, pickup_date, dropoff_date, days, base_amount, add_ons_amount, service_fee, total_amount, currency, status, payment_status, created_at, version) VALUES
    (19, 10, 1, 1, '2026-03-25', '2026-03-28', 3, 1050.00, 45.00, 109.50, 1204.50, 'USD', 'PENDING_PAYMENT', 'UNPAID', '2026-03-03 15:30:00', 0);

-- 11. Seed data: Booking Add-ons
-- Booking 1 add-ons: GPS + INSURANCE_PREMIUM (4 days)
INSERT INTO booking_add_ons (booking_id, add_on_type, price_per_day) VALUES
    (1, 'GPS',               5.00),
    (1, 'INSURANCE_PREMIUM', 15.00);

-- Booking 2 add-ons: GPS (3 days)
INSERT INTO booking_add_ons (booking_id, add_on_type, price_per_day) VALUES
    (2, 'GPS', 5.00);

-- Booking 3 add-ons: INSURANCE_PREMIUM + ADDITIONAL_DRIVER (5 days)
INSERT INTO booking_add_ons (booking_id, add_on_type, price_per_day) VALUES
    (3, 'INSURANCE_PREMIUM', 15.00),
    (3, 'ADDITIONAL_DRIVER', 10.00);

-- Booking 4 add-ons: INSURANCE_PREMIUM (4 days)
INSERT INTO booking_add_ons (booking_id, add_on_type, price_per_day) VALUES
    (4, 'INSURANCE_PREMIUM', 15.00);

-- Booking 5 add-ons: CHILD_SEAT (3 days)
INSERT INTO booking_add_ons (booking_id, add_on_type, price_per_day) VALUES
    (5, 'CHILD_SEAT', 7.00);

-- Booking 6 add-ons: INSURANCE_PREMIUM + ADDITIONAL_DRIVER (6 days)
INSERT INTO booking_add_ons (booking_id, add_on_type, price_per_day) VALUES
    (6, 'INSURANCE_PREMIUM', 15.00),
    (6, 'ADDITIONAL_DRIVER', 10.00);

-- Booking 7 add-ons: GPS (2 days)
INSERT INTO booking_add_ons (booking_id, add_on_type, price_per_day) VALUES
    (7, 'GPS', 5.00);

-- Booking 8 add-ons: INSURANCE_PREMIUM + WIFI_HOTSPOT (3 days)
INSERT INTO booking_add_ons (booking_id, add_on_type, price_per_day) VALUES
    (8, 'INSURANCE_PREMIUM', 15.00),
    (8, 'WIFI_HOTSPOT',      3.00);

-- 12. Seed data: Payments
-- Payment for Booking 1 (successful)
INSERT INTO payments (booking_id, method, status, amount, transaction_id, created_at) VALUES
    (1, 'ONLINE', 'SUCCESS', 1496.00, 'txn_luxe_20260205_001', '2026-02-05 10:35:00');

-- Payment for Booking 2 (successful)
INSERT INTO payments (booking_id, method, status, amount, transaction_id, created_at) VALUES
    (2, 'ONLINE', 'SUCCESS', 511.50, 'txn_luxe_20260215_002', '2026-02-15 14:30:00');

-- Payment for Booking 3 (successful)
INSERT INTO payments (booking_id, method, status, amount, transaction_id, created_at) VALUES
    (3, 'ONLINE', 'SUCCESS', 1485.00, 'txn_luxe_20260301_003', '2026-03-01 09:10:00');

-- Payment for Booking 4 (initiated, waiting)
INSERT INTO payments (booking_id, method, status, amount, transaction_id, created_at) VALUES
    (4, 'ONLINE', 'INITIATED', 1826.00, NULL, '2026-03-02 16:50:00');

-- Payment for Booking 5 (failed — then booking cancelled)
INSERT INTO payments (booking_id, method, status, amount, transaction_id, created_at) VALUES
    (5, 'ONLINE', 'FAILED', 270.60, NULL, '2026-02-28 11:20:00');

-- Payment for Booking 6 (successful)
INSERT INTO payments (booking_id, method, status, amount, transaction_id, created_at) VALUES
    (6, 'ONLINE', 'SUCCESS', 3102.00, 'txn_luxe_20260301_006', '2026-03-01 08:15:00');

-- Payment for Booking 7 (on delivery — no online payment)
INSERT INTO payments (booking_id, method, status, amount, transaction_id, created_at) VALUES
    (7, 'ON_DELIVERY', 'SUCCESS', 99.00, NULL, '2026-03-03 12:05:00');

-- Payment for Booking 8 (initiated, waiting)
INSERT INTO payments (booking_id, method, status, amount, transaction_id, created_at) VALUES
    (8, 'ONLINE', 'INITIATED', 1204.50, NULL, '2026-03-03 15:35:00');

