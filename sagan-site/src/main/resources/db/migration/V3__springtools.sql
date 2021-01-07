CREATE TABLE spring_tools_platform
(
  id   VARCHAR(255) NOT NULL PRIMARY KEY
);

CREATE TABLE spring_tools_platform_downloads
(
  spring_tools_platform_id VARCHAR(128) NOT NULL,
  download_url             VARCHAR(255) NOT NULL,
  variant                  VARCHAR(128) NOT NULL,
  label                    VARCHAR(128) NOT NULL,
  PRIMARY KEY (spring_tools_platform_id, variant)
);