-- Update existing statuses to mark completion statuses as final
UPDATE task_statuses 
SET is_final = true 
WHERE LOWER(name) IN ('done', 'completed', 'выполнено', 'завершено', 'готово', 'finished', 'complete');

-- Ensure all other statuses are explicitly marked as non-final
UPDATE task_statuses 
SET is_final = false 
WHERE LOWER(name) NOT IN ('done', 'completed', 'выполнено', 'завершено', 'готово', 'finished', 'complete');

-- Update existing tasks to match their status finality
UPDATE tasks 
SET is_completed = true, 
    completed_at = CASE 
        WHEN completed_at IS NULL THEN updated_at 
        ELSE completed_at 
    END
WHERE status_id IN (
    SELECT id FROM task_statuses WHERE is_final = true
) AND is_completed = false;

-- Update tasks that are marked as completed but have non-final status
UPDATE tasks 
SET is_completed = false, 
    completed_at = NULL
WHERE status_id IN (
    SELECT id FROM task_statuses WHERE is_final = false
) AND is_completed = true; 