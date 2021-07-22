--
-- Update project support policy
--
ALTER TABLE project ADD support_policy VARCHAR(255);

UPDATE project SET support_policy='UPSTREAM' where support_policy IS NULL;
UPDATE project SET support_policy='SPRING_BOOT' where id='spring-boot';
