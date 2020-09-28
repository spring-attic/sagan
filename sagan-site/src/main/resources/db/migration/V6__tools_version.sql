--
-- Add version column to springtools
--
ALTER TABLE spring_tools_platform_downloads
    ADD COLUMN version CHARACTER VARYING(64) DEFAULT '';