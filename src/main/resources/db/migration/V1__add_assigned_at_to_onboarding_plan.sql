-- Добавление колонки assigned_at в таблицу onboarding_plan
ALTER TABLE onboarding_plan 
ADD COLUMN assigned_at TIMESTAMP WITHOUT TIME ZONE;

-- Обновление существующих записей текущей датой
UPDATE onboarding_plan 
SET assigned_at = NOW() 
WHERE assigned_at IS NULL;

-- Изменение колонки на NOT NULL
ALTER TABLE onboarding_plan 
ALTER COLUMN assigned_at 
SET NOT NULL;
