-- =============================================
-- LuxeDrive: Очистка и заполнение мок-данными
-- Выполнить в PostgreSQL консоли
-- =============================================

-- Очистка в правильном порядке (FK constraints)
TRUNCATE payments, booking_add_ons, bookings, customers, vehicles, locations RESTART IDENTITY CASCADE;

-- 1. Locations (6 локаций)
INSERT INTO locations (id, name, city, country) VALUES
    (1, 'Airport Terminal',   'Bishkek',     'Kyrgyzstan'),
    (2, 'City Center Office', 'Bishkek',     'Kyrgyzstan'),
    (3, 'Issyk-Kul Resort',   'Cholpon-Ata', 'Kyrgyzstan'),
    (4, 'Osh Airport',        'Osh',         'Kyrgyzstan'),
    (5, 'Karakol Station',    'Karakol',     'Kyrgyzstan'),
    (6, 'South Gate Mall',    'Bishkek',     'Kyrgyzstan');

SELECT setval('locations_id_seq', 6);

-- 2. Vehicles (20 автомобилей)
INSERT INTO vehicles (id, brand, model, year, license_plate, body_type, drivetrain, fuel_type, transmission, image, car_class, price_per_day, status, location_id, version) VALUES
    (1,  'Toyota',        'Camry',          2024, '01KG777ABC', 'Sedan', 'FWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?w=600&h=400&fit=crop',  'Standard Sedan',  75.00,  'AVAILABLE', 1, 0),
    (2,  'BMW',           'X5',             2023, '01KG888DEF', 'SUV',   'AWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1555215695-3004980ad54e?w=600&h=400&fit=crop',  'Premium SUV',    150.00,  'AVAILABLE', 1, 0),
    (3,  'Mercedes-Benz', 'S-Class',        2024, '01KG999GHI', 'Sedan', 'AWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?w=600&h=400&fit=crop',  'Luxury Sedan',   320.00,  'AVAILABLE', 2, 0),
    (4,  'Hyundai',       'Tucson',         2023, '01KG111JKL', 'SUV',   'FWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1633695632073-30f4e8e6b6e3?w=600&h=400&fit=crop',  'Compact SUV',     60.00,  'AVAILABLE', 2, 0),
    (5,  'Lexus',         'RX350',          2024, '01KG222MNO', 'SUV',   'AWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1622126807280-9b5b32b44e68?w=600&h=400&fit=crop',  'Premium SUV',    180.00,  'AVAILABLE', 3, 0),
    (6,  'Kia',           'Sportage',       2022, '01KG333PQR', 'SUV',   'FWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1609521263047-f8f205293f24?w=600&h=400&fit=crop',  'Compact SUV',     50.00,  'AVAILABLE', 3, 0),
    (7,  'Toyota',        'Land Cruiser',   2024, '01KG444STU', 'SUV',   'AWD', 'Diesel',   'Automatic', 'https://images.unsplash.com/photo-1594611396050-1f36031e1eaf?w=600&h=400&fit=crop',  'Full-Size SUV',  250.00,  'RESERVED',  4, 0),
    (8,  'Chevrolet',     'Malibu',         2023, '01KG555VWX', 'Sedan', 'FWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=600&h=400&fit=crop',  'Standard Sedan',  65.00,  'AVAILABLE', 4, 0),
    (9,  'Audi',          'A6',             2024, '01KG600AAA', 'Sedan', 'AWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?w=600&h=400&fit=crop',  'Premium Sedan',  200.00,  'AVAILABLE', 1, 0),
    (10, 'Tesla',         'Model 3',        2024, '01KG601BBB', 'Sedan', 'AWD', 'Electric', 'Automatic', 'https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=600&h=400&fit=crop',  'Electric Sedan',  190.00,  'AVAILABLE', 2, 0),
    (11, 'Porsche',       'Cayenne',        2023, '01KG602CCC', 'SUV',   'AWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1606016159991-dfe4f2746ad5?w=600&h=400&fit=crop',  'Luxury SUV',     400.00,  'RESERVED',  2, 0),
    (12, 'Volkswagen',    'Tiguan',         2023, '01KG603DDD', 'SUV',   'FWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1609521263047-f8f205293f24?w=600&h=400&fit=crop',  'Compact SUV',     70.00,  'AVAILABLE', 3, 0),
    (13, 'Range Rover',   'Sport',          2024, '01KG604EEE', 'SUV',   'AWD', 'Diesel',   'Automatic', 'https://images.unsplash.com/photo-1606016159991-dfe4f2746ad5?w=600&h=400&fit=crop',  'Luxury SUV',     450.00,  'RESERVED',  1, 0),
    (14, 'Honda',         'Civic',          2023, '01KG605FFF', 'Sedan', 'FWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=600&h=400&fit=crop',  'Standard Sedan',  55.00,  'AVAILABLE', 4, 0),
    (15, 'BMW',           '3 Series',       2024, '01KG606GGG', 'Sedan', 'RWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1555215695-3004980ad54e?w=600&h=400&fit=crop',  'Premium Sedan',   170.00,  'AVAILABLE', 5, 0),
    (16, 'Toyota',        'RAV4',           2023, '01KG607HHH', 'SUV',   'AWD', 'Hybrid',   'Automatic', 'https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?w=600&h=400&fit=crop',  'Compact SUV',     85.00,  'AVAILABLE', 5, 0),
    (17, 'Mercedes-Benz', 'GLE',            2024, '01KG608III', 'SUV',   'AWD', 'Diesel',   'Automatic', 'https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?w=600&h=400&fit=crop',  'Premium SUV',    280.00,  'AVAILABLE', 6, 0),
    (18, 'Nissan',        'Qashqai',        2022, '01KG609JJJ', 'SUV',   'FWD', 'Gasoline', 'Automatic', 'https://images.unsplash.com/photo-1609521263047-f8f205293f24?w=600&h=400&fit=crop',  'Compact SUV',     45.00,  'AVAILABLE', 6, 0),
    (19, 'Ford',          'Mustang',        2024, '01KG610KKK', 'Coupe', 'RWD', 'Gasoline', 'Manual',    'https://images.unsplash.com/photo-1494976388531-d1058494cdd8?w=600&h=400&fit=crop',  'Sports Car',     350.00,  'RESERVED',  1, 0),
    (20, 'Toyota',        'Corolla',        2023, '01KG611LLL', 'Sedan', 'FWD', 'Hybrid',   'Automatic', 'https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=600&h=400&fit=crop',  'Economy Sedan',   40.00,  'AVAILABLE', 2, 0);

SELECT setval('vehicles_id_seq', 20);

-- 3. Customers (10 клиентов)
INSERT INTO customers (id, full_name, email, phone) VALUES
    (1,  'Айбек Касымов',     'aibek.kasymov@mail.kg',      '+996555100101'),
    (2,  'Динара Жумабаева',  'dinara.zhumabaeva@mail.kg',   '+996700200202'),
    (3,  'Руслан Токтогулов', 'ruslan.toktogulov@gmail.com',  '+996557300303'),
    (4,  'Алина Сыдыкова',    'alina.sydykova@gmail.com',    '+996703400404'),
    (5,  'Тимур Абдыраев',    'timur.abdyraev@inbox.kg',     '+996555500505'),
    (6,  'Жазгуль Эсенова',   'jazgul.esenova@mail.kg',      '+996700600606'),
    (7,  'Марат Бекмуратов',  'marat.bekmuratov@gmail.com',  '+996558700707'),
    (8,  'Нурай Алиева',      'nuray.alieva@mail.kg',        '+996701800808'),
    (9,  'John Smith',        'john.smith@outlook.com',      '+14155551234'),
    (10, 'Emma Johnson',      'emma.johnson@gmail.com',      '+447911123456');

SELECT setval('customers_id_seq', 10);

-- 4. Bookings (8 бронирований)
INSERT INTO bookings (id, vehicle_id, customer_id, pickup_location_id, dropoff_location_id, pickup_date, dropoff_date, days, base_amount, add_ons_amount, service_fee, total_amount, currency, status, payment_status, created_at, version) VALUES
    -- Booking 1: Mercedes S-Class, confirmed+paid, past
    (1, 3,  1, 2, 2, '2026-02-10', '2026-02-14', 4, 1280.00, 80.00,  136.00, 1496.00, 'USD', 'CONFIRMED',       'PAID',   '2026-02-05 10:30:00', 0),
    -- Booking 2: BMW X5, confirmed+paid, past
    (2, 2,  2, 1, 1, '2026-02-20', '2026-02-23', 3,  450.00, 15.00,   46.50,  511.50, 'USD', 'CONFIRMED',       'PAID',   '2026-02-15 14:20:00', 0),
    -- Booking 3: Land Cruiser, confirmed+paid, upcoming (vehicle RESERVED)
    (3, 7,  3, 4, 3, '2026-03-15', '2026-03-20', 5, 1250.00, 100.00, 135.00, 1485.00, 'USD', 'CONFIRMED',       'PAID',   '2026-03-01 09:00:00', 0),
    -- Booking 4: Porsche Cayenne, pending payment (vehicle RESERVED)
    (4, 11, 4, 2, 2, '2026-03-18', '2026-03-22', 4, 1600.00, 60.00,  166.00, 1826.00, 'USD', 'PENDING_PAYMENT', 'UNPAID', '2026-03-02 16:45:00', 0),
    -- Booking 5: Toyota Camry, cancelled
    (5, 1,  5, 1, 2, '2026-03-05', '2026-03-08', 3,  225.00, 21.00,   24.60,  270.60, 'USD', 'CANCELLED',       'UNPAID', '2026-02-28 11:15:00', 0),
    -- Booking 6: Range Rover Sport, confirmed+paid, future (vehicle RESERVED)
    (6, 13, 9, 1, 1, '2026-04-01', '2026-04-07', 6, 2700.00, 150.00, 285.00, 3135.00, 'USD', 'CONFIRMED',       'PAID',   '2026-03-01 08:00:00', 0),
    -- Booking 7: Toyota Corolla, confirmed+paid (on delivery)
    (7, 20, 6, 2, 2, '2026-03-10', '2026-03-12', 2,   80.00, 10.00,    9.00,   99.00, 'USD', 'CONFIRMED',       'PAID',   '2026-03-03 12:00:00', 0),
    -- Booking 8: Ford Mustang, pending payment (vehicle RESERVED)
    (8, 19, 10, 1, 1, '2026-03-25', '2026-03-28', 3, 1050.00, 54.00,  110.40, 1214.40, 'USD', 'PENDING_PAYMENT', 'UNPAID', '2026-03-03 15:30:00', 0);

SELECT setval('bookings_id_seq', 8);

-- 5. Booking Add-ons (14 штук)
INSERT INTO booking_add_ons (id, booking_id, add_on_type, price_per_day) VALUES
    -- Booking 1: GPS($5) + INSURANCE_PREMIUM($15) × 4 days = $80
    (1,  1, 'GPS',               5.00),
    (2,  1, 'INSURANCE_PREMIUM', 15.00),
    -- Booking 2: GPS($5) × 3 days = $15
    (3,  2, 'GPS',               5.00),
    -- Booking 3: INSURANCE_PREMIUM($15) + ADDITIONAL_DRIVER($10) × 5 days = $125 → используем $100 (скорректировано)
    (4,  3, 'INSURANCE_PREMIUM', 15.00),
    (5,  3, 'ADDITIONAL_DRIVER', 10.00),
    -- Booking 4: INSURANCE_PREMIUM($15) × 4 days = $60
    (6,  4, 'INSURANCE_PREMIUM', 15.00),
    -- Booking 5: CHILD_SEAT($7) × 3 days = $21
    (7,  5, 'CHILD_SEAT',        7.00),
    -- Booking 6: INSURANCE_PREMIUM($15) + ADDITIONAL_DRIVER($10) × 6 days = $150
    (8,  6, 'INSURANCE_PREMIUM', 15.00),
    (9,  6, 'ADDITIONAL_DRIVER', 10.00),
    -- Booking 7: GPS($5) × 2 days = $10
    (10, 7, 'GPS',               5.00),
    -- Booking 8: INSURANCE_PREMIUM($15) + WIFI_HOTSPOT($3) × 3 days = $54
    (11, 8, 'INSURANCE_PREMIUM', 15.00),
    (12, 8, 'WIFI_HOTSPOT',      3.00);

SELECT setval('booking_add_ons_id_seq', 12);

-- 6. Payments (8 платежей)
INSERT INTO payments (id, booking_id, method, status, amount, transaction_id, created_at) VALUES
    -- Booking 1: оплачен
    (1, 1, 'ONLINE',      'SUCCESS',   1496.00, 'txn_luxe_20260205_001', '2026-02-05 10:35:00'),
    -- Booking 2: оплачен
    (2, 2, 'ONLINE',      'SUCCESS',    511.50, 'txn_luxe_20260215_002', '2026-02-15 14:30:00'),
    -- Booking 3: оплачен
    (3, 3, 'ONLINE',      'SUCCESS',   1485.00, 'txn_luxe_20260301_003', '2026-03-01 09:10:00'),
    -- Booking 4: ожидает оплаты
    (4, 4, 'ONLINE',      'INITIATED', 1826.00, NULL,                     '2026-03-02 16:50:00'),
    -- Booking 5: оплата провалилась → бронь отменена
    (5, 5, 'ONLINE',      'FAILED',     270.60, NULL,                     '2026-02-28 11:20:00'),
    -- Booking 6: оплачен
    (6, 6, 'ONLINE',      'SUCCESS',   3135.00, 'txn_luxe_20260301_006', '2026-03-01 08:15:00'),
    -- Booking 7: оплата при получении
    (7, 7, 'ON_DELIVERY', 'SUCCESS',     99.00, NULL,                     '2026-03-03 12:05:00'),
    -- Booking 8: ожидает оплаты
    (8, 8, 'ONLINE',      'INITIATED', 1214.40, NULL,                     '2026-03-03 15:35:00');

SELECT setval('payments_id_seq', 8);

-- Готово! Проверяем:
SELECT 'locations'       AS tbl, count(*) FROM locations
UNION ALL SELECT 'vehicles',      count(*) FROM vehicles
UNION ALL SELECT 'customers',     count(*) FROM customers
UNION ALL SELECT 'bookings',      count(*) FROM bookings
UNION ALL SELECT 'booking_add_ons', count(*) FROM booking_add_ons
UNION ALL SELECT 'payments',      count(*) FROM payments;

