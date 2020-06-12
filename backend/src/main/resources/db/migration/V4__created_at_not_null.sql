UPDATE multi_link
SET created_at = now()
WHERE created_at IS NULL;

ALTER TABLE multi_link
    ALTER COLUMN created_at SET NOT NULL;
