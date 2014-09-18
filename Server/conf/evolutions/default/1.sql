# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table article (
  id                        varchar(30) not null,
  name                      varchar(50),
  constraint pk_article primary key (id))
;

create table log (
  id                        integer auto_increment not null,
  entity                    varchar(20),
  identifier                varchar(30),
  event                     varchar(255),
  change_type               integer,
  date                      datetime,
  constraint pk_log primary key (id))
;

create table moved_pallet (
  id                        bigint auto_increment not null,
  pallet_id                 integer not null,
  date                      datetime not null,
  location_position         varchar(30) not null,
  reader_id                 varchar(30) not null,
  put_down                  tinyint(1) default 0,
  constraint pk_moved_pallet primary key (id))
;

create table pallet (
  id                        integer auto_increment not null,
  tag1_id                   varchar(22) not null,
  tag2_id                   varchar(22) not null,
  time_entrance             datetime not null,
  constraint pk_pallet primary key (id))
;

create table pallet_slot (
  position                  varchar(30) not null,
  tag_id                    varchar(22),
  constraint pk_pallet_slot primary key (position))
;

create table reader (
  id                        varchar(30) not null,
  description               varchar(255),
  last_scanned_unoccupied_slot_position varchar(30),
  last_scanned_unoccupied_slot_date datetime,
  constraint pk_reader primary key (id))
;

create table set_of_article (
  id                        integer auto_increment not null,
  article_id                varchar(30) not null,
  amount                    integer not null,
  pallet_id                 integer not null,
  constraint pk_set_of_article primary key (id))
;

create table tag (
  id                        varchar(22) not null,
  constraint pk_tag primary key (id))
;

create table user (
  email                     varchar(255) not null,
  name                      varchar(255),
  password                  varchar(255),
  constraint pk_user primary key (email))
;

alter table moved_pallet add constraint fk_moved_pallet_pallet_1 foreign key (pallet_id) references pallet (id) on delete restrict on update restrict;
create index ix_moved_pallet_pallet_1 on moved_pallet (pallet_id);
alter table moved_pallet add constraint fk_moved_pallet_location_2 foreign key (location_position) references pallet_slot (position) on delete restrict on update restrict;
create index ix_moved_pallet_location_2 on moved_pallet (location_position);
alter table moved_pallet add constraint fk_moved_pallet_reader_3 foreign key (reader_id) references reader (id) on delete restrict on update restrict;
create index ix_moved_pallet_reader_3 on moved_pallet (reader_id);
alter table pallet add constraint fk_pallet_tag1_4 foreign key (tag1_id) references tag (id) on delete restrict on update restrict;
create index ix_pallet_tag1_4 on pallet (tag1_id);
alter table pallet add constraint fk_pallet_tag2_5 foreign key (tag2_id) references tag (id) on delete restrict on update restrict;
create index ix_pallet_tag2_5 on pallet (tag2_id);
alter table pallet_slot add constraint fk_pallet_slot_tag_6 foreign key (tag_id) references tag (id) on delete restrict on update restrict;
create index ix_pallet_slot_tag_6 on pallet_slot (tag_id);
alter table reader add constraint fk_reader_lastScannedUnoccupiedSlot_7 foreign key (last_scanned_unoccupied_slot_position) references pallet_slot (position) on delete restrict on update restrict;
create index ix_reader_lastScannedUnoccupiedSlot_7 on reader (last_scanned_unoccupied_slot_position);
alter table set_of_article add constraint fk_set_of_article_article_8 foreign key (article_id) references article (id) on delete restrict on update restrict;
create index ix_set_of_article_article_8 on set_of_article (article_id);
alter table set_of_article add constraint fk_set_of_article_pallet_9 foreign key (pallet_id) references pallet (id) on delete restrict on update restrict;
create index ix_set_of_article_pallet_9 on set_of_article (pallet_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table article;

drop table log;

drop table moved_pallet;

drop table pallet;

drop table pallet_slot;

drop table reader;

drop table set_of_article;

drop table tag;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

