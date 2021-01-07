ALTER TABLE project ADD raw_boot_config MEDIUMTEXT;
ALTER TABLE project ADD rendered_boot_config MEDIUMTEXT;

ALTER TABLE project ADD raw_overview MEDIUMTEXT;
ALTER TABLE project ADD rendered_overview MEDIUMTEXT;

ALTER TABLE project DROP COLUMN is_aggregator;
ALTER TABLE project ADD parent_project_id VARCHAR(255) DEFAULT NULL;

ALTER TABLE project ADD display_order INT NOT NULL DEFAULT 255;

CREATE TABLE project_sample_list (
  title          VARCHAR(255),
  description    VARCHAR(255),
  url            VARCHAR(255),
  display_order  INT NOT NULL,
  project_id     VARCHAR(128) NOT NULL,
  PRIMARY KEY (project_id, display_order)
);