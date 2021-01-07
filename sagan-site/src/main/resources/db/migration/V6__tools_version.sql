--
-- Add version column to springtools
--
ALTER TABLE spring_tools_platform_downloads
    ADD COLUMN version VARCHAR(64) DEFAULT '';