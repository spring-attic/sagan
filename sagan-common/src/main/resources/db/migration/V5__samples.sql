CREATE TABLE project_sample_list (
  title          VARCHAR,
  description    VARCHAR,
  url            VARCHAR,
  display_order  INT NOT NULL ,
  project_id     CHARACTER VARYING(255) NOT NULL,
  PRIMARY KEY (project_id, display_order)
);