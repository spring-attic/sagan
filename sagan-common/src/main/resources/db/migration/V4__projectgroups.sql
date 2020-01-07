ALTER TABLE project ADD tag_line CHARACTER VARYING(255) DEFAULT '';
ALTER TABLE project ADD featured BOOLEAN;

-- create a new groups reference table
CREATE TABLE project_groups
(
    id   SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(255) UNIQUE,
    label VARCHAR(255)
);

-- relation table between project and project_groups tables
create table project_groups_rel
(
    project_id VARCHAR(255) REFERENCES project (id),
    group_id   INT REFERENCES project_groups (id),
    CONSTRAINT id PRIMARY KEY (project_id, group_id)
);