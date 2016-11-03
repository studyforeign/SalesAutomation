CREATE TABLE PT_REP (
  ID          int(10) unsigned NOT NULL auto_increment,
  Group_id    varchar(32) NOT NULL,
  Script      varchar(32) NOT NULL,
  Start       datetime,
  End         datetime,
  Duration    int(10),
  Volume      int(10),
  Create_time datetime,
  User        varchar(32),
  PRIMARY KEY (ID)
);
