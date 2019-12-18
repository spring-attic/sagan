CREATE TABLE project_groups
(
	id  SERIAL NOT NULL PRIMARY KEY,
	name  VARCHAR(255)
);

INSERT INTO project_groups (name) values('Microservices');
INSERT INTO project_groups (name) values('Event Driven');
INSERT INTO project_groups (name) values('Cloud');
INSERT INTO project_groups (name) values('Reactive');
INSERT INTO project_groups (name) values('Web Apps');
INSERT INTO project_groups (name) values('Serverless');
INSERT INTO project_groups (name) values('Streams');
INSERT INTO project_groups (name) values('Batch');

create table project_groups_rel
(
	project_id varchar(255) references project(id),
	group_id int references project_groups(id),
	constraint id primary key (project_id, group_id)
);