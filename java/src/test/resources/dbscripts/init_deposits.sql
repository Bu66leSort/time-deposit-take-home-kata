-- "today" in tests is 2026-03-15 (TestClock); days = today - created_on.
INSERT INTO time_deposits (id, plan_type, days, balance, created_on, last_accrual_month) VALUES
  (1, 'basic',   45,  12000.00, DATE '2026-03-15' - 45,  NULL),
  (2, 'basic',   30,  12000.00, DATE '2026-03-15' - 30,  NULL),
  (3, 'student', 100, 12000.00, DATE '2026-03-15' - 100, NULL),
  (4, 'student', 366, 12000.00, DATE '2026-03-15' - 366, NULL),
  (5, 'premium', 46,  12000.00, DATE '2026-03-15' - 46,  NULL),
  (6, 'gold',    100, 12000.00, DATE '2026-03-15' - 100, NULL),
  (7, 'basic',   45,  12000.00, DATE '2026-03-15' - 45,  DATE '2026-03-01');
INSERT INTO withdrawals (id, time_deposit_id, amount, date) VALUES
  (1, 1, 100.00, DATE '2026-03-01'),
  (2, 1, 250.50, DATE '2026-03-10'),
  (3, 5, 500.00, DATE '2026-03-12');
