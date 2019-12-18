-- drop the groups_tag column
ALTER TABLE project DROP COLUMN groups_tag;

-- create a new groups reference table
CREATE TABLE project_groups
(
	id  SERIAL NOT NULL PRIMARY KEY,
	name  VARCHAR(255)
);

-- relation table between project and project_groups tables
create table project_groups_rel
(
	project_id varchar(255) references project(id),
	group_id int references project_groups(id),
	constraint id primary key (project_id, group_id)
);