
-- =============================================
-- SEED DATA: Тарифные шаблоны
-- =============================================

-- Economy: дешёвые авто (Corolla, Civic, Qashqai, Sportage, Malibu)
INSERT INTO pricing_templates (id, name, description, currency, active) VALUES
    (1, 'Economy',   'Тариф для эконом-класса автомобилей',      'USD', TRUE),
    (2, 'Standard',  'Тариф для стандартных автомобилей',         'USD', TRUE),
    (3, 'Compact SUV', 'Тариф для компактных кроссоверов',       'USD', TRUE),
    (4, 'Premium',   'Тариф для премиальных автомобилей',        'USD', TRUE),
    (5, 'Luxury',    'Тариф для люксовых автомобилей',           'USD', TRUE),
    (6, 'Sports',    'Тариф для спортивных автомобилей',         'USD', TRUE)
ON CONFLICT (name) DO NOTHING;

SELECT setval('pricing_templates_id_seq', 6);

-- Economy tiers (Corolla $40, Qashqai $45, Sportage $50)
INSERT INTO price_tiers (id, pricing_template_id, min_days, max_days, price_per_day) VALUES
    (1,  1, 1,  3,  45.00),
    (2,  1, 4,  7,  38.00),
    (3,  1, 8,  14, 32.00),
    (4,  1, 15, NULL, 25.00);

-- Standard tiers (Camry $75, Malibu $65, Civic $55)
INSERT INTO price_tiers (id, pricing_template_id, min_days, max_days, price_per_day) VALUES
    (5,  2, 1,  3,  75.00),
    (6,  2, 4,  7,  65.00),
    (7,  2, 8,  14, 55.00),
    (8,  2, 15, NULL, 45.00);

-- Compact SUV tiers (Tucson $60, Tiguan $70, RAV4 $85)
INSERT INTO price_tiers (id, pricing_template_id, min_days, max_days, price_per_day) VALUES
    (9,  3, 1,  3,  80.00),
    (10, 3, 4,  7,  68.00),
    (11, 3, 8,  14, 58.00),
    (12, 3, 15, NULL, 48.00);

-- Premium tiers (BMW X5 $150, Lexus $180, Audi A6 $200, BMW 3 $170, Tesla $190, MB GLE $280)
INSERT INTO price_tiers (id, pricing_template_id, min_days, max_days, price_per_day) VALUES
    (13, 4, 1,  3,  200.00),
    (14, 4, 4,  7,  170.00),
    (15, 4, 8,  14, 145.00),
    (16, 4, 15, NULL, 120.00);

-- Luxury tiers (Mercedes S-Class $320, Porsche $400, Range Rover $450, Land Cruiser $250)
INSERT INTO price_tiers (id, pricing_template_id, min_days, max_days, price_per_day) VALUES
    (17, 5, 1,  3,  380.00),
    (18, 5, 4,  7,  320.00),
    (19, 5, 8,  14, 270.00),
    (20, 5, 15, NULL, 220.00);

-- Sports tiers (Mustang $350)
INSERT INTO price_tiers (id, pricing_template_id, min_days, max_days, price_per_day) VALUES
    (21, 6, 1,  3,  350.00),
    (22, 6, 4,  7,  300.00),
    (23, 6, 8,  14, 260.00),
    (24, 6, 15, NULL, 210.00);

SELECT setval('price_tiers_id_seq', 24);

-- =============================================
-- Привязка шаблонов к автомобилям и обновление minPricePerDay
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

-- Обновляем существующие бронирования (заполняем price_per_day из vehicles для обратной совместимости)
UPDATE bookings b SET
    price_per_day = (SELECT v.price_per_day FROM vehicles v WHERE v.id = b.vehicle_id),
    prepayment_amount = ROUND(b.total_amount * 0.15, 2),
    service_block_start = b.pickup_date - INTERVAL '1 day',
    service_block_end = b.dropoff_date + INTERVAL '1 day'
WHERE b.price_per_day IS NULL;

-- Готово! Проверяем:
SELECT 'pricing_templates' AS tbl, count(*) FROM pricing_templates
UNION ALL SELECT 'price_tiers', count(*) FROM price_tiers;

SELECT v.id, v.brand, v.model, pt.name AS template, v.min_price_per_day
FROM vehicles v
LEFT JOIN pricing_templates pt ON pt.id = v.pricing_template_id
ORDER BY v.id;

