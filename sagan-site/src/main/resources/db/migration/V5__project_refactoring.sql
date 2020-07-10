--
-- Update project schema
--
ALTER TABLE project
    RENAME COLUMN category TO status;

UPDATE project SET status='INCUBATING' where status='incubator';
UPDATE project SET status='ACTIVE' where status='active';
UPDATE project SET status='COMMUNITY' where status='community';
UPDATE project SET status='END_OF_LIFE' where status='attic';

ALTER TABLE project
    RENAME COLUMN raw_boot_config TO bootconfig_source;
ALTER TABLE project
    RENAME COLUMN rendered_boot_config TO bootconfig_html;
ALTER TABLE project
    RENAME COLUMN raw_overview TO overview_source;
ALTER TABLE project
    RENAME COLUMN rendered_overview TO overview_html;
ALTER TABLE project
    RENAME COLUMN display_order TO sort_order;
ALTER TABLE project
    ADD COLUMN project_generations_lastmodified TIMESTAMP DEFAULT Now();

CREATE INDEX idx_project_name ON project(name);

--
-- Update project_release_list
--
ALTER TABLE project_release_list
    RENAME TO project_release;
ALTER TABLE project_release
    DROP CONSTRAINT IF EXISTS project_release_list_pkey;
ALTER TABLE project_release
    RENAME COLUMN version_name TO version;
ALTER TABLE project_release
    ADD COLUMN id SERIAL;
ALTER TABLE project_release
    DROP COLUMN group_id;
ALTER TABLE project_release
    DROP COLUMN artifact_id;
CREATE UNIQUE INDEX project_version
    ON project_release(project_id, version);

-- Update release_status to String enum type
ALTER TABLE project_release
    RENAME COLUMN release_status TO release_status_old;
ALTER TABLE project_release
    ADD COLUMN release_status CHARACTER VARYING(255);
UPDATE project_release
    SET release_status='SNAPSHOT' where release_status_old=0;
UPDATE project_release
    SET release_status='PRERELEASE' where release_status_old=1;
UPDATE project_release
    SET release_status='GENERAL_AVAILABILITY' where release_status_old=2;
ALTER TABLE project_release
    DROP COLUMN release_status_old;

-- Update repository_id
ALTER TABLE project_release
    DROP COLUMN repository_id;
ALTER TABLE project_release
    ADD COLUMN repository CHARACTER VARYING(255);
UPDATE project_release
    SET repository='SNAPSHOT' where release_status='SNAPSHOT';
UPDATE project_release
    SET repository='MILESTONE' where release_status='PRERELEASE';
UPDATE project_release
    SET repository='RELEASE' where release_status='GENERAL_AVAILABILITY';

--
-- Update project_repository
--

-- This is now an enum
DROP TABLE project_repository;

--
-- Update project_sample_list
--
ALTER TABLE project_sample_list
    RENAME TO project_sample;
ALTER TABLE project_sample
    DROP CONSTRAINT IF EXISTS project_sample_list_pkey;
ALTER TABLE project_sample
    ADD COLUMN id SERIAL;
ALTER TABLE project_sample
    RENAME COLUMN display_order TO sort_order;
ALTER TABLE project_sample
    ALTER COLUMN title SET NOT NULL;
ALTER TABLE project_sample
    ALTER COLUMN description SET NOT NULL;
ALTER TABLE project_sample
    ALTER COLUMN url SET NOT NULL;


--
-- Create new Project Generations table
--
CREATE TABLE project_generation
(
    id                                      SERIAL PRIMARY KEY,
    project_id                              CHARACTER VARYING(255) NOT NULL,
    name                                    CHARACTER VARYING(255) NOT NULL,
    initial_release_date                    DATE NOT NULL,
    oss_support_enforced_end_date           DATE,
    oss_support_policy_end_date             DATE,
    commercial_support_enforced_end_date    DATE,
    commercial_support_policy_end_date      DATE,
    UNIQUE (project_id, name)
);

--
-- Update member_profile
--
CREATE INDEX idx_memberprofile_githubid ON member_profile(github_id);
CREATE INDEX idx_memberprofile_username ON member_profile(username);
