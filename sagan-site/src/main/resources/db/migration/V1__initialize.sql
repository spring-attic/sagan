CREATE TABLE member_profile (
  id                   INT AUTO_INCREMENT PRIMARY KEY,
  avatar_url           VARCHAR(255),
  bio                  VARCHAR(2048),
  latitude             FLOAT,
  longitude            FLOAT,
  github_id            BIGINT,
  github_username      VARCHAR(128),
  gravatar_email       VARCHAR(128),
  hidden               BOOLEAN,
  lanyrd_username      VARCHAR(128),
  location             VARCHAR(64),
  name                 VARCHAR(128),
  speakerdeck_username VARCHAR(128),
  twitter_username     VARCHAR(128),
  username             VARCHAR(128) NOT NULL,
  video_embeds         VARCHAR(255),
  job_title            VARCHAR(128)
);

CREATE TABLE post (
  id               INT AUTO_INCREMENT PRIMARY KEY,
  broadcast        BOOLEAN                NOT NULL,
  category         VARCHAR(128) NOT NULL,
  created_at       TIMESTAMP              NOT NULL,
  draft            BOOLEAN                NOT NULL,
  format           VARCHAR(128),
  public_slug      VARCHAR(255) UNIQUE,
  publish_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  raw_content      LONGTEXT                NOT NULL,
  rendered_content LONGTEXT                NOT NULL,
  rendered_summary LONGTEXT                NOT NULL,
  title            VARCHAR(255) NOT NULL,
  author_id        INTEGER                NOT NULL REFERENCES member_profile (id)
);

CREATE TABLE post_public_slug_aliases (
  post_id             INT                    NOT NULL,
  public_slug_aliases VARCHAR(255) NOT NULL UNIQUE,
  PRIMARY KEY (post_id, public_slug_aliases)
);

CREATE INDEX idx_category
  ON post(category);
CREATE INDEX idx_draft
  ON post(draft);
CREATE INDEX idx_publish_at
  ON post(publish_at);

CREATE TABLE project (
  id                  VARCHAR(128) NOT NULL PRIMARY KEY,
  name                VARCHAR(255),
  repo_url            VARCHAR(255),
  category            VARCHAR(255),
  site_url            VARCHAR(255),
  is_aggregator       BOOLEAN,
  stack_overflow_tags VARCHAR(255)
);

CREATE TABLE project_release_list (
  project_id     VARCHAR(128) NOT NULL,
  repository_id  VARCHAR(128),
  api_doc_url    VARCHAR(255),
  artifact_id    VARCHAR(255),
  group_id       VARCHAR(255),
  is_current     BOOLEAN,
  ref_doc_url    VARCHAR(255),
  release_status INT,
  version_name   VARCHAR(64),
  PRIMARY KEY (project_id, version_name)
);

CREATE TABLE project_repository (
  id                VARCHAR(255) NOT NULL PRIMARY KEY,
  name              VARCHAR(128),
  url               VARCHAR(255),
  snapshots_enabled BOOLEAN
);

