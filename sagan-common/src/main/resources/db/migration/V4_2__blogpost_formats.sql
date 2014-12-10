alter table post add format character varying(255);

UPDATE post set format = 'markdown' where format = null;