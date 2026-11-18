INSERT INTO time_deposits (id, plan_type, days, balance, created_on)
SELECT g, 'basic', 60, 12000.00, DATE '2026-03-15' - 60 FROM generate_series(1, 1203) AS g;
