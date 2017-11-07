ALTER TABLE project ADD raw_boot_config VARCHAR;
ALTER TABLE project ADD rendered_boot_config VARCHAR;

ALTER TABLE project ADD raw_overview VARCHAR DEFAULT '';
ALTER TABLE project ADD rendered_overview VARCHAR DEFAULT '';

ALTER TABLE project DROP COLUMN is_aggregator;
ALTER TABLE project ADD parent_project_id CHARACTER VARYING(255) DEFAULT NULL;

ALTER TABLE project ADD display_order INT NOT NULL DEFAULT 255;

CREATE TABLE project_sample_list (
  title          VARCHAR,
  description    VARCHAR,
  url            VARCHAR,
  display_order  INT NOT NULL,
  project_id     CHARACTER VARYING(255) NOT NULL,
  PRIMARY KEY (project_id, display_order)
);