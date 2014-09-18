# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups
-- These views are used to see which pallets that are on which slots.
create view pallet_on_slot_helper1 as (
  select location_position as position, pallet_id, date, id
  from moved_pallet
  where put_down=true AND (select count(*) 
                           from moved_pallet t 
                           where moved_pallet.pallet_id=t.pallet_id AND 
                           moved_pallet.date<t.date AND 
                           t.put_down = 0) < 1 AND location_position <> 'floor'
  group by position, pallet_id, date 
  order by date DESC, id DESC
);
   
create view pallet_on_slot_helper2 as (
  select * 
  from pallet_on_slot_helper1 
  group by position 
  order by date DESC, id DESC
); 

create view pallet_on_slot as (
  select * 
  from pallet_on_slot_helper2
  group by pallet_id
  order by date DESC, id DESC
);

create or replace view pallets_on_move as (
select location_position as position, pallet_id, date, id
  from moved_pallet  
  where put_down=0 AND (select count(*) 
                           from moved_pallet t 
                           where moved_pallet.pallet_id=t.pallet_id AND 
                           moved_pallet.date<t.date AND 
                           t.put_down = 1) < 1
  group by location_position, pallet_id, date 
  order by date DESC, id DESC
);

create or replace view pallet_on_floor as (
select location_position as position, pallet_id, date, id
from moved_pallet
where put_down=1 AND (select count(*)
from moved_pallet t
where moved_pallet.pallet_id=t.pallet_id AND
moved_pallet.date<t.date) < 1 AND location_position = 'floor'
group by pallet_id, date
order by date DESC, id DESC
); 


#Triggers. 

# Tags used once a Time

# Pallet insertions
create trigger good_tags_pallet before insert on pallet
for each row begin
    select count(*) into @ptag1 from pallet where tag1_id = NEW.tag1_id or tag2_id = NEW.tag1_id;;
    IF @ptag1 > 0 THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Tag 1 is already in use on pallet!';;
    END IF;;
    select count(*) into @ptag2 from pallet where tag1_id = NEW.tag2_id or tag2_id = NEW.tag2_id;;
    IF @ptag2 > 0 THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Tag 2 is already in use on pallet!';;
    END IF;;
    select count(*) into @pstag from pallet_slot where tag_id = NEW.tag1_id or tag_id = NEW.tag2_id;;
    IF @ptag2 > 0 THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'One of the tags are used as a pallet_slot!';;
    END IF;;
    IF NEW.tag1_id = NEW.tag2_id THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Tag 1 & 2 are the same!';;
    END IF;;
end;

# PalletSlot insertions
create trigger good_tags_slot before insert on pallet_slot
for each row begin
  select count(*) into @ptag from pallet where tag1_id = NEW.tag_id or tag2_id = NEW.tag_id;;
  IF @ptag > 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Tag is already in use on pallet!';;
  END IF;;
  select count(*) into @pstag from pallet_slot where tag_id = NEW.tag_id;;
  IF @pstag > 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Tag is already in use on pallet_slot!';;
  END IF;;    
end;


# Logging Moves
create trigger log_move after insert on moved_pallet
for each row begin 
  IF NEW.put_down THEN 
    insert into log (entity, identifier, change_type, event, date) values('move', NEW.pallet_id, 1, CONCAT('Pallet was placed on ',NEW.location_position,'.'), NEW.date);;
  ELSE 
    insert into log (entity, identifier, change_type, event, date) values('move', NEW.pallet_id, 2, CONCAT('Pallet was taken from ',NEW.location_position,'.'), NEW.date);;
  END IF;;
end;


# Article logging
create trigger log_article_add after insert on article
for each row begin 
    insert into log (entity, identifier, change_type, event, date) values('article', NEW.id, 3, CONCAT('Created as "',NEW.name,'".'), NOW());;
end;

create trigger log_article_remove before delete on article
for each row begin 
    insert into log (entity, identifier, change_type, event, date) values('article', OLD.id, 4, CONCAT('Removed "',OLD.name,'" from DB.'), NOW());;
end;

create trigger log_article_update before update on article
for each row begin 
    IF OLD.id = NEW.id THEN
      IF OLD.name <> NEW.name THEN
        insert into log (entity, identifier, change_type, event, date) values('article', OLD.id, 5, CONCAT('Was changed from "',OLD.name,'" to "',NEW.name,'".'), NOW());;
      END IF;;
    ELSE
      insert into log (entity, identifier, change_type, event, date) values('article', OLD.id, 5, CONCAT('Was changed from "',OLD.id,'" to "',NEW.id,'" and "',OLD.name,'" to "',NEW.name,'".'), NOW());;
    END IF;;
    
end;


# Pallet logging
create trigger log_pallet_add after insert on pallet
for each row begin 
    insert into log (entity, identifier, change_type, event, date) values('pallet', NEW.id, 3, CONCAT('Added pallet to system with tags "',NEW.tag1_id,'" and "',NEW.tag2_id,'".'), NOW());;
end;

create trigger log_pallet_remove before delete on pallet
for each row begin 
    insert into log (entity, identifier, change_type, event, date) values('pallet', OLD.id, 4, CONCAT('Removed pallet with tags "',OLD.tag1_id,'" and "',OLD.tag2_id,'" from DB.'), NOW());;
end;

# Pallet Slot Logging
create trigger log_slot_add after insert on pallet_slot
for each row begin 
    insert into log (entity, identifier, change_type, event, date) values('slot', NEW.position, 3, CONCAT('Added pallet slot to system with tag "',NEW.tag_id,'".'), NOW());;
end;

create trigger log_slot_remove before delete on pallet_slot
for each row begin 
    insert into log (entity, identifier, change_type, event, date) values('slot', OLD.position, 4, 'Removed pallet slot from DB.', NOW());;
end;
create trigger log_slot_update after update on pallet_slot
for each row begin 
    insert into log (entity, identifier, change_type, event, date) values('slot', OLD.position, 5, CONCAT('Switched tag from "',OLD.tag_id,'" to "',NEW.tag_id,'".'), NOW());;
end;


# Set of Articles logging
create trigger log_setOfA_add after insert on set_of_article
for each row begin 
    select name into @a_new_name from article where id=NEW.article_id;;
    insert into log (entity, identifier, change_type, event, date) values('pallet', NEW.pallet_id, 3, CONCAT('Added ',NEW.amount,' of "',@a_new_name, '" to pallet.'), NOW());;
end;

create trigger log_setOfA_remove after delete on set_of_article
for each row begin 
    select name into @a_old_name from article where id=OLD.article_id;;
    insert into log (entity, identifier, change_type, event, date) values('pallet', OLD.pallet_id, 4, CONCAT('Removed ',OLD.amount,' of "',@a_old_name, '" from pallet.'), NOW());;
end;

create trigger log_setOfA_update after update on set_of_article
for each row begin 
  select name into @a_old_name from article where id=OLD.article_id;; 
  select name into @a_new_name from article where id=NEW.article_id;;
  IF OLD.pallet_id = NEW.pallet_id THEN
    IF OLD.article_id = NEW.article_id THEN
      IF OLD.amount <> NEW.amount THEN
        insert into log (entity, identifier, change_type, event, date) values('pallet', OLD.pallet_id, 5, CONCAT('Altered ',OLD.amount,' of "',@a_old_name, '" to ',NEW.amount,'.'), NOW());;
      END IF;; 
    ELSE
      insert into log (entity, identifier, change_type, event, date) values('pallet', OLD.pallet_id, 5, CONCAT('Altered ',OLD.amount,' of "',@a_old_name,'" to ',NEW.amount,' of "',@a_new_name,'".'), NOW());;
    END IF;;
  ELSE
    IF OLD.article_id = NEW.article_id THEN
      IF OLD.amount = NEW.amount THEN
        insert into log (entity, identifier, change_type, event, date) values('pallet', OLD.pallet_id, 5, CONCAT('Changed pallet_id to ',NEW.pallet_id,'.'), NOW());;
      ELSE
        insert into log (entity, identifier, change_type, event, date) values('pallet', OLD.pallet_id, 5, CONCAT('Altered ',OLD.amount,' of "',@a_old_name, '" to ',NEW.amount,'.(Changed pallet_id to:',NEW.pallet_id,'.)'), NOW());;
      END IF;;
    ELSE
      insert into log (entity, identifier, change_type, event, date) values('pallet', OLD.pallet_id, 5, CONCAT('Altered ',OLD.amount,' of "',@a_old_name, '" to ',NEW.amount,'of "',@a_new_name,'.(Changed pallet_id to:',NEW.pallet_id,'.)'), NOW());;
    END IF;;
  END IF;;
end;



# --- !Downs
drop view if exists pallet_on_floor;
drop view if exists pallets_on_move;
drop view if exists pallets_on_slots;
drop view if exists p_o_s_h2;
drop view if exists p_o_s_h1;
drop view if exists pallet_on_slot;
drop view if exists pallet_on_slot_helper2;
drop view if exists pallet_on_slot_helper1;
drop trigger if exists good_tags_pallet;
drop trigger if exists good_tags_slot;
drop trigger if exists log_move;
drop trigger if exists log_article_add;
drop trigger if exists log_article_update;
drop trigger if exists log_article_remove;
drop trigger if exists log_pallet_add;
drop trigger if exists log_pallet_remove;
drop trigger if exists log_slot_remove;
drop trigger if exists log_slot_add;
drop trigger if exists log_slot_update;
drop trigger if exists log_setOfA_add;
drop trigger if exists log_setOfA_remove;
drop trigger if exists log_setOfA_update;
