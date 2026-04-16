-- =============================================
-- RENTAL SEED: Полное заполнение мок-данными
-- Car Rental
-- Запускать ПОСЛЕ первого старта приложения
-- (Hibernate с ddl-auto:update создаст таблицы)
-- =============================================

-- Очистка в правильном порядке (FK constraints)
TRUNCATE vehicle_attribute_values, vehicle_attributes, payments, booking_add_ons, bookings, customers, price_tiers, pricing_templates, vehicles, locations RESTART IDENTITY CASCADE;

-- =============================================
-- 1. Locations (6 локаций)
-- =============================================
INSERT INTO locations (id, name, city, country) VALUES
    (1, 'Airport Terminal',   'Bishkek',     'Kyrgyzstan'),
    (2, 'City Center Office', 'Bishkek',     'Kyrgyzstan'),
    (3, 'Issyk-Kul Resort',   'Cholpon-Ata', 'Kyrgyzstan'),
    (4, 'Osh Airport',        'Osh',         'Kyrgyzstan'),
    (5, 'Karakol Station',    'Karakol',     'Kyrgyzstan'),
    (6, 'South Gate Mall',    'Bishkek',     'Kyrgyzstan');

SELECT setval('locations_id_seq', 6);

-- =============================================
-- 2. Vehicles (20 автомобилей)
-- =============================================
INSERT INTO vehicles (id, brand, model, license_plate, image, car_class, price_per_day, status, location_id, version) VALUES
    (1,  'Toyota',        'Camry',          '01KG777ABC', 'https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?w=600&h=400&fit=crop',  'Standard Sedan',   75.00, 'AVAILABLE', 1, 0),
    (2,  'BMW',           'X5',             '01KG888DEF', 'https://images.unsplash.com/photo-1555215695-3004980ad54e?w=600&h=400&fit=crop',  'Premium SUV',     150.00, 'AVAILABLE', 1, 0),
    (3,  'Mercedes-Benz', 'S-Class',        '01KG999GHI', 'https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?w=600&h=400&fit=crop',  'Luxury Sedan',    320.00, 'AVAILABLE', 2, 0),
    (4,  'Hyundai',       'Tucson',         '01KG111JKL', 'https://images.unsplash.com/photo-1633695632073-30f4e8e6b6e3?w=600&h=400&fit=crop',  'Compact SUV',      60.00, 'AVAILABLE', 2, 0),
    (5,  'Lexus',         'RX350',          '01KG222MNO', 'https://images.unsplash.com/photo-1622126807280-9b5b32b44e68?w=600&h=400&fit=crop',  'Premium SUV',     180.00, 'AVAILABLE', 3, 0),
    (6,  'Kia',           'Sportage',       '01KG333PQR', 'https://images.unsplash.com/photo-1609521263047-f8f205293f24?w=600&h=400&fit=crop',  'Compact SUV',      50.00, 'AVAILABLE', 3, 0),
    (7,  'Toyota',        'Land Cruiser',   '01KG444STU', 'https://images.unsplash.com/photo-1594611396050-1f36031e1eaf?w=600&h=400&fit=crop',  'Full-Size SUV',   250.00, 'RESERVED',  4, 0),
    (8,  'Chevrolet',     'Malibu',         '01KG555VWX', 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=600&h=400&fit=crop',  'Standard Sedan',   65.00, 'AVAILABLE', 4, 0),
    (9,  'Audi',          'A6',             '01KG600AAA', 'https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?w=600&h=400&fit=crop',  'Premium Sedan',   200.00, 'AVAILABLE', 1, 0),
    (10, 'Tesla',         'Model 3',        '01KG601BBB', 'https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=600&h=400&fit=crop',  'Electric Sedan',  190.00, 'AVAILABLE', 2, 0),
    (11, 'Porsche',       'Cayenne',        '01KG602CCC', 'https://images.unsplash.com/photo-1606016159991-dfe4f2746ad5?w=600&h=400&fit=crop',  'Luxury SUV',      400.00, 'RESERVED',  2, 0),
    (12, 'Volkswagen',    'Tiguan',         '01KG603DDD', 'https://images.unsplash.com/photo-1609521263047-f8f205293f24?w=600&h=400&fit=crop',  'Compact SUV',      70.00, 'AVAILABLE', 3, 0),
    (13, 'Range Rover',   'Sport',          '01KG604EEE', 'https://images.unsplash.com/photo-1606016159991-dfe4f2746ad5?w=600&h=400&fit=crop',  'Luxury SUV',      450.00, 'RESERVED',  1, 0),
    (14, 'Honda',         'Civic',          '01KG605FFF', 'https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=600&h=400&fit=crop',  'Standard Sedan',   55.00, 'AVAILABLE', 4, 0),
    (15, 'BMW',           '3 Series',       '01KG606GGG', 'https://images.unsplash.com/photo-1555215695-3004980ad54e?w=600&h=400&fit=crop',  'Premium Sedan',   170.00, 'AVAILABLE', 5, 0),
    (16, 'Toyota',        'RAV4',           '01KG607HHH', 'https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?w=600&h=400&fit=crop',  'Compact SUV',      85.00, 'AVAILABLE', 5, 0),
    (17, 'Mercedes-Benz', 'GLE',            '01KG608III', 'https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?w=600&h=400&fit=crop',  'Premium SUV',     280.00, 'AVAILABLE', 6, 0),
    (18, 'Nissan',        'Qashqai',        '01KG609JJJ', 'https://images.unsplash.com/photo-1609521263047-f8f205293f24?w=600&h=400&fit=crop',  'Compact SUV',      45.00, 'AVAILABLE', 6, 0),
    (19, 'Ford',          'Mustang',        '01KG610KKK', 'https://images.unsplash.com/photo-1494976388531-d1058494cdd8?w=600&h=400&fit=crop',  'Sports Car',      350.00, 'RESERVED',  1, 0),
    (20, 'Toyota',        'Corolla',        '01KG611LLL', 'https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=600&h=400&fit=crop',  'Economy Sedan',    40.00, 'AVAILABLE', 2, 0);
SELECT setval('vehicles_id_seq', 20);

-- =============================================
-- 3. Pricing Templates (6 тарифных шаблонов)
-- =============================================
INSERT INTO pricing_templates (id, name, description, currency, active, created_at, updated_at, version) VALUES
    (1, 'Economy',     'Тариф для эконом-класса автомобилей',  'USD', TRUE, NOW(), NOW(), 0),
    (2, 'Standard',    'Тариф для стандартных автомобилей',     'USD', TRUE, NOW(), NOW(), 0),
    (3, 'Compact SUV', 'Тариф для компактных кроссоверов',     'USD', TRUE, NOW(), NOW(), 0),
    (4, 'Premium',     'Тариф для премиальных автомобилей',    'USD', TRUE, NOW(), NOW(), 0),
    (5, 'Luxury',      'Тариф для люксовых автомобилей',       'USD', TRUE, NOW(), NOW(), 0),
    (6, 'Sports',      'Тариф для спортивных автомобилей',     'USD', TRUE, NOW(), NOW(), 0);

SELECT setval('pricing_templates_id_seq', 6);

-- =============================================
-- 4. Price Tiers (тарифные диапазоны)
-- =============================================

-- Economy tiers (Corolla $40, Qashqai $45, Sportage $50)
INSERT INTO price_tiers (id, pricing_template_id, min_days, max_days, price_per_day) VALUES
    (1,  1, 1,  3,    45.00),
    (2,  1, 4,  7,    38.00),
    (3,  1, 8,  14,   32.00),
    (4,  1, 15, NULL,  25.00);

-- Standard tiers (Camry $75, Malibu $65, Civic $55)
INSERT INTO price_tiers (id, pricing_template_id, min_days, max_days, price_per_day) VALUES
    (5,  2, 1,  3,    75.00),
    (6,  2, 4,  7,    65.00),
    (7,  2, 8,  14,   55.00),
    (8,  2, 15, NULL,  45.00);

-- Compact SUV tiers (Tucson $60, Tiguan $70, RAV4 $85)
INSERT INTO price_tiers (id, pricing_template_id, min_days, max_days, price_per_day) VALUES
    (9,  3, 1,  3,    80.00),
    (10, 3, 4,  7,    68.00),
    (11, 3, 8,  14,   58.00),
    (12, 3, 15, NULL,  48.00);

-- Premium tiers (BMW X5 $150, Lexus $180, Audi A6 $200, BMW 3 $170, Tesla $190, MB GLE $280)
INSERT INTO price_tiers (id, pricing_template_id, min_days, max_days, price_per_day) VALUES
    (13, 4, 1,  3,   200.00),
    (14, 4, 4,  7,   170.00),
    (15, 4, 8,  14,  145.00),
    (16, 4, 15, NULL, 120.00);

-- Luxury tiers (Mercedes S-Class $320, Porsche $400, Range Rover $450, Land Cruiser $250)
INSERT INTO price_tiers (id, pricing_template_id, min_days, max_days, price_per_day) VALUES
    (17, 5, 1,  3,   380.00),
    (18, 5, 4,  7,   320.00),
    (19, 5, 8,  14,  270.00),
    (20, 5, 15, NULL, 220.00);

-- Sports tiers (Mustang $350)
INSERT INTO price_tiers (id, pricing_template_id, min_days, max_days, price_per_day) VALUES
    (21, 6, 1,  3,   350.00),
    (22, 6, 4,  7,   300.00),
    (23, 6, 8,  14,  260.00),
    (24, 6, 15, NULL, 210.00);

SELECT setval('price_tiers_id_seq', 24);

-- =============================================
-- 5. Привязка шаблонов к авто + min_price_per_day
-- =============================================

-- Economy: Corolla(20), Qashqai(18), Sportage(6)
UPDATE vehicles SET pricing_template_id = 1, min_price_per_day = 25.00 WHERE id IN (20, 18, 6);

-- Standard: Camry(1), Malibu(8), Civic(14)
UPDATE vehicles SET pricing_template_id = 2, min_price_per_day = 45.00 WHERE id IN (1, 8, 14);

-- Compact SUV: Tucson(4), Tiguan(12), RAV4(16)
UPDATE vehicles SET pricing_template_id = 3, min_price_per_day = 48.00 WHERE id IN (4, 12, 16);

-- Premium: BMW X5(2), Lexus RX350(5), Audi A6(9), Tesla Model 3(10), BMW 3-Series(15), MB GLE(17)
UPDATE vehicles SET pricing_template_id = 4, min_price_per_day = 120.00 WHERE id IN (2, 5, 9, 10, 15, 17);

-- Luxury: Mercedes S-Class(3), Porsche Cayenne(11), Range Rover(13), Land Cruiser(7)
UPDATE vehicles SET pricing_template_id = 5, min_price_per_day = 220.00 WHERE id IN (3, 11, 13, 7);

-- Sports: Ford Mustang(19)
UPDATE vehicles SET pricing_template_id = 6, min_price_per_day = 210.00 WHERE id = 19;

-- =============================================
-- 6. Customers (10 клиентов)
-- =============================================
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

-- =============================================
-- 7. Bookings (8 бронирований)
-- =============================================
INSERT INTO bookings (id, vehicle_id, customer_id, pickup_location_id, dropoff_location_id,
                      pickup_date, dropoff_date, days, price_per_day, price_tier_description,
                      base_amount, add_ons_amount, service_fee, total_amount,
                      prepayment_amount, prepayment_paid,
                      currency, status, payment_status, created_at, version)
VALUES
    -- 1: Mercedes S-Class, confirmed+paid, past
    (1, 3, 1, 2, 2, '2026-02-10', '2026-02-14', 4, 320.00, 'Тариф 1–3 дня',
     1280.00, 80.00, 136.00, 1496.00, 224.40, TRUE,
     'USD', 'CONFIRMED', 'PAID', '2026-02-05 10:30:00', 0),

    -- 2: BMW X5, confirmed+paid, past
    (2, 2, 2, 1, 1, '2026-02-20', '2026-02-23', 3, 150.00, 'Тариф 1–3 дня',
     450.00, 21.00, 47.10, 518.10, 77.72, TRUE,
     'USD', 'CONFIRMED', 'PAID', '2026-02-15 14:20:00', 0),

    -- 3: Land Cruiser, confirmed+paid, upcoming
    (3, 7, 3, 4, 3, '2026-03-15', '2026-03-20', 5, 320.00, 'Тариф 4–7 дней',
     1600.00, 80.00, 168.00, 1848.00, 277.20, TRUE,
     'USD', 'CONFIRMED', 'PAID', '2026-03-01 09:00:00', 0),

    -- 4: Porsche Cayenne, pending payment
    (4, 11, 4, 2, 2, '2026-03-18', '2026-03-22', 4, 380.00, 'Тариф 1–3 дня',
     1520.00, 80.00, 160.00, 1760.00, 264.00, FALSE,
     'USD', 'PENDING_PAYMENT', 'UNPAID', '2026-03-02 16:45:00', 0),

    -- 5: Toyota Camry, cancelled
    (5, 1, 5, 1, 2, '2026-03-05', '2026-03-08', 3, 75.00, 'Тариф 1–3 дня',
     225.00, 24.00, 24.90, 273.90, 41.09, FALSE,
     'USD', 'CANCELLED', 'UNPAID', '2026-02-28 11:15:00', 0),

    -- 6: Range Rover Sport, confirmed+paid, future
    (6, 13, 9, 1, 1, '2026-04-01', '2026-04-07', 6, 320.00, 'Тариф 4–7 дней',
     1920.00, 132.00, 205.20, 2257.20, 338.58, TRUE,
     'USD', 'CONFIRMED', 'PAID', '2026-03-01 08:00:00', 0),

    -- 7: Toyota Corolla, confirmed+paid (on delivery)
    (7, 20, 6, 2, 2, '2026-03-10', '2026-03-12', 2, 45.00, 'Тариф 1–3 дня',
     90.00, 50.00, 14.00, 154.00, 23.10, TRUE,
     'USD', 'CONFIRMED', 'PAID', '2026-03-03 12:00:00', 0),

    -- 8: Ford Mustang, pending payment
    (8, 19, 10, 1, 1, '2026-03-25', '2026-03-28', 3, 350.00, 'Тариф 1–3 дня',
     1050.00, 105.00, 115.50, 1270.50, 190.58, FALSE,
     'USD', 'PENDING_PAYMENT', 'UNPAID', '2026-03-03 15:30:00', 0);

SELECT setval('bookings_id_seq', 8);

-- =============================================
-- 8. Booking Add-ons (12 штук)
-- =============================================
INSERT INTO booking_add_ons (id, booking_id, add_on_type, price_per_day) VALUES
    -- Booking 1: ROOF_TENT($15) + SLEEPING_BAGS($5) × 4 дня = $80
    (1,  1, 'ROOF_TENT',           15.00),
    (2,  1, 'SLEEPING_BAGS',        5.00),
    -- Booking 2: KITCHEN_UTENSILS($7) × 3 дня = $21
    (3,  2, 'KITCHEN_UTENSILS',     7.00),
    -- Booking 3: GROUND_TENT($10) + TABLE_AND_CHAIRS($6) × 5 дней = $80
    (4,  3, 'GROUND_TENT',         10.00),
    (5,  3, 'TABLE_AND_CHAIRS',     6.00),
    -- Booking 4: BORDER_DOCUMENTS_KZ($20) × 4 дня = $80
    (6,  4, 'BORDER_DOCUMENTS_KZ', 20.00),
    -- Booking 5: REFRIGERATOR($8) × 3 дня = $24
    (7,  5, 'REFRIGERATOR',         8.00),
    -- Booking 6: ROOF_TENT($15) + KITCHEN_UTENSILS($7) × 6 дней = $132
    (8,  6, 'ROOF_TENT',           15.00),
    (9,  6, 'KITCHEN_UTENSILS',     7.00),
    -- Booking 7: DELIVERY_AIRPORT($25) × 2 дня = $50
    (10, 7, 'DELIVERY_AIRPORT',    25.00),
    -- Booking 8: BORDER_DOCUMENTS_UZ($20) + DELIVERY_CITY($15) × 3 дня = $105
    (11, 8, 'BORDER_DOCUMENTS_UZ', 20.00),
    (12, 8, 'DELIVERY_CITY',       15.00);

SELECT setval('booking_add_ons_id_seq', 12);

-- =============================================
-- 9. Payments (8 платежей)
-- =============================================
INSERT INTO payments (id, booking_id, method, status, amount, transaction_id, created_at) VALUES
    (1, 1, 'ONLINE',      'SUCCESS',   1496.00, 'txn_rental_20260205_001', '2026-02-05 10:35:00'),
    (2, 2, 'ONLINE',      'SUCCESS',    518.10, 'txn_rental_20260215_002', '2026-02-15 14:30:00'),
    (3, 3, 'ONLINE',      'SUCCESS',   1848.00, 'txn_rental_20260301_003', '2026-03-01 09:10:00'),
    (4, 4, 'ONLINE',      'INITIATED', 1760.00, NULL,                     '2026-03-02 16:50:00'),
    (5, 5, 'ONLINE',      'FAILED',     273.90, NULL,                     '2026-02-28 11:20:00'),
    (6, 6, 'ONLINE',      'SUCCESS',   2257.20, 'txn_rental_20260301_006', '2026-03-01 08:15:00'),
    (7, 7, 'ON_DELIVERY', 'SUCCESS',    154.00, NULL,                     '2026-03-03 12:05:00'),
    (8, 8, 'ONLINE',      'INITIATED', 1270.50, NULL,                     '2026-03-03 15:35:00');

SELECT setval('payments_id_seq', 8);

-- =============================================
-- 8. Service Options (доп. услуги из админки)
-- =============================================
TRUNCATE service_options RESTART IDENTITY CASCADE;

INSERT INTO service_options (id, code, name, description, category, icon, price_per_day, active, sort_order, total_quantity, created_at, updated_at) VALUES
    (1,  'ROOF_TENT',            'Roof Tent',             'Rooftop tent for comfortable camping on the go',            'EQUIPMENT',  'mdi-tent',                       15.00, TRUE,  1,  5,    NOW(), NOW()),
    (2,  'GROUND_TENT',          'Ground Tent',           'Spacious ground tent for 2–3 people',                       'EQUIPMENT',  'mdi-home-outline',               10.00, TRUE,  2,  8,    NOW(), NOW()),
    (3,  'SLEEPING_BAGS',        'Sleeping Bags',         'Warm sleeping bags (set of 2)',                              'EQUIPMENT',  'mdi-bed-outline',                 5.00, TRUE,  3,  15,   NOW(), NOW()),
    (4,  'KITCHEN_UTENSILS',     'Kitchen Utensils',      'Cooking set: stove, pots, pans, cutlery',                   'EQUIPMENT',  'mdi-silverware-fork-knife',       7.00, TRUE,  4,  10,   NOW(), NOW()),
    (5,  'REFRIGERATOR',         'Refrigerator',          'Portable car fridge to keep food & drinks cool',            'EQUIPMENT',  'mdi-fridge-outline',              8.00, TRUE,  5,  6,    NOW(), NOW()),
    (6,  'TABLE_AND_CHAIRS',     'Table & Chairs',        'Foldable camping table and 2 chairs',                       'EQUIPMENT',  'mdi-table-furniture',             6.00, TRUE,  6,  10,   NOW(), NOW()),
    (7,  'BORDER_DOCUMENTS_KZ',  'Border Docs (KZ)',      'Documents for crossing into Kazakhstan',                    'DOCUMENTS',  'mdi-file-document-outline',      20.00, TRUE,  7,  NULL, NOW(), NOW()),
    (8,  'BORDER_DOCUMENTS_UZ',  'Border Docs (UZ)',      'Documents for crossing into Uzbekistan',                    'DOCUMENTS',  'mdi-file-document-outline',      20.00, TRUE,  8,  NULL, NOW(), NOW()),
    (9,  'DELIVERY_OFFICE',      'Pick-up at Office',     'Collect the car from our office (free)',                     'DELIVERY',   'mdi-office-building-marker',      0.00, TRUE,  9,  NULL, NOW(), NOW()),
    (10, 'DELIVERY_CITY',        'City Delivery',         'We deliver the car to your address in the city',            'DELIVERY',   'mdi-truck-delivery-outline',     15.00, TRUE,  10, NULL, NOW(), NOW()),
    (11, 'DELIVERY_AIRPORT',     'Airport Delivery',      'We deliver the car to the airport terminal',                'DELIVERY',   'mdi-airplane-marker',            25.00, TRUE,  11, NULL, NOW(), NOW());

SELECT setval('service_options_id_seq', 11);

-- =============================================
-- 10. Vehicle Attributes (справочник характеристик)
-- =============================================
INSERT INTO vehicle_attributes (id, code, name, value_type, possible_values, filterable, sort_order, active, created_at, updated_at) VALUES
    (1, 'BODY_TYPE',      'Тип кузова',        'ENUM', 'Sedan,SUV,Coupe,Hatchback,Wagon,Minivan,Pickup', TRUE,  1, TRUE, NOW(), NOW()),
    (2, 'DRIVETRAIN',     'Привод',            'ENUM', 'FWD,RWD,AWD,4WD',                                 TRUE,  2, TRUE, NOW(), NOW()),
    (3, 'FUEL_TYPE',      'Тип топлива',       'ENUM', 'Gasoline,Diesel,Electric,Hybrid,LPG',             TRUE,  3, TRUE, NOW(), NOW()),
    (4, 'TRANSMISSION',   'Трансмиссия',       'ENUM', 'Automatic,Manual,CVT',                            TRUE,  4, TRUE, NOW(), NOW()),
    (5, 'SEATS',          'Кол-во мест',       'NUMBER', NULL,                                            TRUE,  5, TRUE, NOW(), NOW()),
    (6, 'ENGINE_VOLUME',  'Объём двигателя',   'TEXT',   NULL,                                            FALSE, 6, TRUE, NOW(), NOW()),
    (7, 'HAS_AC',         'Кондиционер',       'BOOLEAN', NULL,                                           TRUE,  7, TRUE, NOW(), NOW()),
    (8, 'COLOR',          'Цвет',              'ENUM', 'White,Black,Silver,Red,Blue,Grey,Green',           FALSE, 8, TRUE, NOW(), NOW());

SELECT setval('vehicle_attributes_id_seq', 8);

-- =============================================
-- 11. Vehicle Attribute Values (значения для каждого авто)
-- =============================================
-- Vehicle 1: Toyota Camry
INSERT INTO vehicle_attribute_values (vehicle_id, attribute_id, value) VALUES
    (1, 1, 'Sedan'), (1, 2, 'FWD'), (1, 3, 'Gasoline'), (1, 4, 'Automatic'), (1, 5, '5'), (1, 7, 'true'), (1, 8, 'Silver');
-- Vehicle 2: BMW X5
INSERT INTO vehicle_attribute_values (vehicle_id, attribute_id, value) VALUES
    (2, 1, 'SUV'), (2, 2, 'AWD'), (2, 3, 'Gasoline'), (2, 4, 'Automatic'), (2, 5, '5'), (2, 7, 'true'), (2, 8, 'Black');
-- Vehicle 3: Mercedes S-Class
INSERT INTO vehicle_attribute_values (vehicle_id, attribute_id, value) VALUES
    (3, 1, 'Sedan'), (3, 2, 'AWD'), (3, 3, 'Gasoline'), (3, 4, 'Automatic'), (3, 5, '5'), (3, 7, 'true'), (3, 8, 'Black');
-- Vehicle 4: Hyundai Tucson
INSERT INTO vehicle_attribute_values (vehicle_id, attribute_id, value) VALUES
    (4, 1, 'SUV'), (4, 2, 'FWD'), (4, 3, 'Gasoline'), (4, 4, 'Automatic'), (4, 5, '5'), (4, 7, 'true'), (4, 8, 'White');
-- Vehicle 5: Lexus RX350
INSERT INTO vehicle_attribute_values (vehicle_id, attribute_id, value) VALUES
    (5, 1, 'SUV'), (5, 2, 'AWD'), (5, 3, 'Gasoline'), (5, 4, 'Automatic'), (5, 5, '5'), (5, 7, 'true'), (5, 8, 'White');
-- Vehicle 6: Kia Sportage
INSERT INTO vehicle_attribute_values (vehicle_id, attribute_id, value) VALUES
    (6, 1, 'SUV'), (6, 2, 'FWD'), (6, 3, 'Gasoline'), (6, 4, 'Automatic'), (6, 5, '5'), (6, 7, 'true'), (6, 8, 'Grey');
-- Vehicle 7: Toyota Land Cruiser
INSERT INTO vehicle_attribute_values (vehicle_id, attribute_id, value) VALUES
    (7, 1, 'SUV'), (7, 2, 'AWD'), (7, 3, 'Diesel'), (7, 4, 'Automatic'), (7, 5, '7'), (7, 7, 'true'), (7, 8, 'White');
-- Vehicle 8: Chevrolet Malibu
INSERT INTO vehicle_attribute_values (vehicle_id, attribute_id, value) VALUES
    (8, 1, 'Sedan'), (8, 2, 'FWD'), (8, 3, 'Gasoline'), (8, 4, 'Automatic'), (8, 5, '5'), (8, 7, 'true'), (8, 8, 'Silver');
-- Vehicle 9: Audi A6
INSERT INTO vehicle_attribute_values (vehicle_id, attribute_id, value) VALUES
    (9, 1, 'Sedan'), (9, 2, 'AWD'), (9, 3, 'Gasoline'), (9, 4, 'Automatic'), (9, 5, '5'), (9, 7, 'true'), (9, 8, 'Black');
-- Vehicle 10: Tesla Model 3
INSERT INTO vehicle_attribute_values (vehicle_id, attribute_id, value) VALUES
    (10, 1, 'Sedan'), (10, 2, 'AWD'), (10, 3, 'Electric'), (10, 4, 'Automatic'), (10, 5, '5'), (10, 7, 'true'), (10, 8, 'White');
-- Vehicle 11: Porsche Cayenne
INSERT INTO vehicle_attribute_values (vehicle_id, attribute_id, value) VALUES
    (11, 1, 'SUV'), (11, 2, 'AWD'), (11, 3, 'Gasoline'), (11, 4, 'Automatic'), (11, 5, '5'), (11, 7, 'true'), (11, 8, 'Red');
-- Vehicle 12: VW Tiguan
INSERT INTO vehicle_attribute_values (vehicle_id, attribute_id, value) VALUES
    (12, 1, 'SUV'), (12, 2, 'FWD'), (12, 3, 'Gasoline'), (12, 4, 'Automatic'), (12, 5, '5'), (12, 7, 'true'), (12, 8, 'Grey');
-- Vehicle 13: Range Rover Sport
INSERT INTO vehicle_attribute_values (vehicle_id, attribute_id, value) VALUES
    (13, 1, 'SUV'), (13, 2, 'AWD'), (13, 3, 'Diesel'), (13, 4, 'Automatic'), (13, 5, '5'), (13, 7, 'true'), (13, 8, 'Black');
-- Vehicle 14: Honda Civic
INSERT INTO vehicle_attribute_values (vehicle_id, attribute_id, value) VALUES
    (14, 1, 'Sedan'), (14, 2, 'FWD'), (14, 3, 'Gasoline'), (14, 4, 'Automatic'), (14, 5, '5'), (14, 7, 'true'), (14, 8, 'Blue');
-- Vehicle 15: BMW 3 Series
INSERT INTO vehicle_attribute_values (vehicle_id, attribute_id, value) VALUES
    (15, 1, 'Sedan'), (15, 2, 'RWD'), (15, 3, 'Gasoline'), (15, 4, 'Automatic'), (15, 5, '5'), (15, 7, 'true'), (15, 8, 'Blue');
-- Vehicle 16: Toyota RAV4
INSERT INTO vehicle_attribute_values (vehicle_id, attribute_id, value) VALUES
    (16, 1, 'SUV'), (16, 2, 'AWD'), (16, 3, 'Hybrid'), (16, 4, 'Automatic'), (16, 5, '5'), (16, 7, 'true'), (16, 8, 'Green');
-- Vehicle 17: Mercedes GLE
INSERT INTO vehicle_attribute_values (vehicle_id, attribute_id, value) VALUES
    (17, 1, 'SUV'), (17, 2, 'AWD'), (17, 3, 'Diesel'), (17, 4, 'Automatic'), (17, 5, '5'), (17, 7, 'true'), (17, 8, 'Black');
-- Vehicle 18: Nissan Qashqai
INSERT INTO vehicle_attribute_values (vehicle_id, attribute_id, value) VALUES
    (18, 1, 'SUV'), (18, 2, 'FWD'), (18, 3, 'Gasoline'), (18, 4, 'Automatic'), (18, 5, '5'), (18, 7, 'true'), (18, 8, 'Silver');
-- Vehicle 19: Ford Mustang
INSERT INTO vehicle_attribute_values (vehicle_id, attribute_id, value) VALUES
    (19, 1, 'Coupe'), (19, 2, 'RWD'), (19, 3, 'Gasoline'), (19, 4, 'Manual'), (19, 5, '4'), (19, 7, 'true'), (19, 8, 'Red');
-- Vehicle 20: Toyota Corolla
INSERT INTO vehicle_attribute_values (vehicle_id, attribute_id, value) VALUES
    (20, 1, 'Sedan'), (20, 2, 'FWD'), (20, 3, 'Hybrid'), (20, 4, 'Automatic'), (20, 5, '5'), (20, 7, 'true'), (20, 8, 'White');

-- =============================================
-- Проверка
-- =============================================
SELECT 'locations'               AS tbl, count(*) FROM locations
UNION ALL SELECT 'vehicles',              count(*) FROM vehicles
UNION ALL SELECT 'pricing_templates',      count(*) FROM pricing_templates
UNION ALL SELECT 'price_tiers',           count(*) FROM price_tiers
UNION ALL SELECT 'customers',             count(*) FROM customers
UNION ALL SELECT 'bookings',              count(*) FROM bookings
UNION ALL SELECT 'booking_add_ons',       count(*) FROM booking_add_ons
UNION ALL SELECT 'payments',              count(*) FROM payments
UNION ALL SELECT 'service_options',       count(*) FROM service_options
UNION ALL SELECT 'vehicle_attributes',    count(*) FROM vehicle_attributes
UNION ALL SELECT 'vehicle_attr_values',   count(*) FROM vehicle_attribute_values;

