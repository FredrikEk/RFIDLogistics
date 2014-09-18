-- Insert these if database goes clean to quickly add stuff to the system.

INSERT INTO `tag` (`id`) VALUES ('0100A3A560');
INSERT INTO `tag` (`id`) VALUES ('01010172C6');
INSERT INTO `tag` (`id`) VALUES ('010102B29E');
INSERT INTO `tag` (`id`) VALUES ('01012034F2');
INSERT INTO `tag` (`id`) VALUES ('010886B011');
INSERT INTO `tag` (`id`) VALUES ('1235SDF234');
INSERT INTO `tag` (`id`) VALUES ('ejfdlsa234312');
INSERT INTO `tag` (`id`) VALUES ('qwemlkasdmnkl');
INSERT INTO `tag` (`id`) VALUES ('sadmnklögwoi');
INSERT INTO `tag` (`id`) VALUES ('sfdnsdkldae22312');

INSERT INTO `article` (`id`, `name`) VALUES ('123456', 'RFID-test-Article');
INSERT INTO `article` (`id`, `name`) 
  VALUES 
  ('900000', 'Test-item 1'),
  ('900001', 'Test-item 2'),
  ('900002', 'Test-item 3'),
  ('900003', 'Test-item 4'),
  ('900004', 'Test-item 5'),
  ('900005', 'Test-item 6'),
  ('900007', 'Test-item 7'),
  ('900006', 'Test-item 8'),
  ('900008', 'Test-item 9');

INSERT INTO `tag` (`id`) 
VALUES
('testtag10000'),
('testtag10001'),
('testtag10002'),
('testtag10003'),
('testtag10004'),
('testtag10005'),
('testtag10006'),
('testtag10007'),
('testtag10008'),
('testtag10009'),
('testtag10010'),
('testtag10011'),
('testtag10012'),
('testtag10013'),
('testtag10014'),
('testtag10015'),
('testtag10016'),
('testtag10017'),
('testtag10018'),
('testtag10019'),
('testtag10020'),
('testtag10021'),
('testtag10022'),
('testtag10023'),
('testtag10024'),
('testtag10025'),
('testtag10026'),
('testtag10027'),
('testtag10028'),
('testtag10029'),
('testtag10030'),
('testtag10031');

INSERT INTO `pallet` (`id`, `tag1_id`, `tag2_id`, `time_entrance`) 
  VALUES 
  (95000, 'testtag10001', 'testtag10002', NOW()),
  (95001, 'testtag10003', 'testtag10004', NOW()),
  (95002, 'testtag10005', 'testtag10006', NOW()),
  (95003, 'testtag10007', 'testtag10008', NOW()),
  (95004, 'testtag10009', 'testtag10010', NOW()),
  (95005, 'testtag10011', 'testtag10012', NOW()),
  (95006, 'testtag10013', 'testtag10014', NOW()),
  (95007, 'testtag10015', 'testtag10016', NOW()),
  (95008, 'testtag10017', 'testtag10018', NOW()),
  (95009, 'testtag10019', 'testtag10020', NOW());

INSERT INTO `set_of_article` (`id`, `article_id`, `amount`, `pallet_id`) 
VALUES 
  (10000, '900000', 500, 95000),
  (10001, '900001', 500, 95001),
  (10002, '900002', 500, 95002),
  (10003, '900003', 500, 95003),
  (10004, '900004', 500, 95004),
  (10005, '900004', 500, 95005),
  (10006, '900008', 500, 95006),
  (10007, '900002', 500, 95007),
  (10008, '900002', 500, 95008),
  (10009, '900004', 500, 95009);

INSERT INTO `pallet_slot` (`position`, `tag_id`) 
  VALUES 
  ('Test-Slot 1000', 'testtag10021'),
  ('Test-Slot 1001', 'testtag10022'),
  ('Test-Slot 1002', 'testtag10023'),
  ('Test-Slot 1003', 'testtag10024'),
  ('Test-Slot 1004', 'testtag10025'),
  ('Test-Slot 1005', 'testtag10026'),
  ('Test-Slot 1006', 'testtag10027'),
  ('Test-Slot 1007', 'testtag10028'),
  ('Test-Slot 1008', 'testtag10029'),
  ('Test-Slot 1009', 'testtag10030'),
  ('Test-Slot 1010', 'testtag10031');


INSERT INTO `moved_pallet` (`id`, `pallet_id`, `date`, `location_position`, `reader_id`, `put_down`)
  VALUES 
  (1000, 95000, NOW(), 'Test-Slot 1000', '1\r\n\r\n',1),
  (1001, 95001, NOW(), 'Test-Slot 1001', '1\r\n\r\n',1),
  (1002, 95002, NOW(), 'Test-Slot 1002', '1\r\n\r\n',1),
  (1003, 95003, NOW(), 'Test-Slot 1003', '1\r\n\r\n',1),
  (1004, 95004, NOW(), 'Test-Slot 1004', '1\r\n\r\n',1),
  (1005, 95005, NOW(), 'Test-Slot 1005', '1\r\n\r\n',1),
  (1006, 95006, NOW(), 'Test-Slot 1006', '1\r\n\r\n',1),
  (1007, 95007, NOW(), 'Test-Slot 1007', '1\r\n\r\n',1),
  (1008, 95008, NOW(), 'Test-Slot 1008', '1\r\n\r\n',1); 
INSERT INTO `smartrfid`.`moved_pallet` (`id`, `pallet_id`, `date`, `location_position`, `reader_id`, `put_down`) VALUES (12, 1, '2014-04-14 16:27:24', 'Slot 1', '1\r\n\r\n', 1);


INSERT INTO `pallet` (`id`, `tag1_id`, `tag2_id`, `time_entrance`) VALUES (1, '01010172C6', '010102B29E', '2014-03-17 17:22:46');
INSERT INTO `pallet` (`id`, `tag1_id`, `tag2_id`, `time_entrance`) VALUES (2, 'sfdnsdkldae22312', 'ejfdlsa234312', '2014-03-19 12:49:28');
INSERT INTO `pallet` (`id`, `tag1_id`, `tag2_id`, `time_entrance`) VALUES (3, 'qwemlkasdmnkl', 'sadmnklögwoi', '2014-03-19 13:58:11');

INSERT INTO `set_of_article` (`id`, `article_id`, `amount`, `pallet_id`) VALUES (1, '123456', 500, 1);
INSERT INTO `set_of_article` (`id`, `article_id`, `amount`, `pallet_id`) VALUES (2, '123456', 10, 2);
INSERT INTO `set_of_article` (`id`, `article_id`, `amount`, `pallet_id`) VALUES (3, '123456', 15, 3);


INSERT INTO `pallet_slot` (`position`, `tag_id`) VALUES ('Slot 1', '0100A3A560');
INSERT INTO `pallet_slot` (`position`, `tag_id`) VALUES ('Slot 3', '01012034F2');
INSERT INTO `pallet_slot` (`position`, `tag_id`) VALUES ('Slot 2', '010886B011');



--Adding trigger scrips
/*

DELIMITER $$;
create trigger log_move after insert on moved_pallet
for each row begin 
  IF NEW.put_down THEN 
    insert into log_table (entity, identifier, event, date) values('pallet', NEW.pallet_id, CONCAT('Was placed on ',NEW.location_position,'.'), NEW.date);
  ELSE 
    insert into log_table (entity, identifier, event, date) values('pallet', NEW.pallet_id, CONCAT('Was taken from ',NEW.location_position,'.'), NEW.date);
  END IF;
END$$;

create trigger log_add_article after insert on article
for each row begin 
    insert into log_table (entity, identifier, event, date) values('article', NEW.id, CONCAT('Created as "',NEW.name,'".'), NOW());
END$$;

create trigger log_remove_article before delete on article
for each row begin 
    insert into log_table (entity, identifier, event, date) values('article', OLD.id, CONCAT('"',OLD.name,'" was removed from DB.'), NOW());
END$$;

create trigger log_update_article before update on article
for each row begin 
    insert into log_table (entity, identifier, event, date) values('article', OLD.id, CONCAT('Was changed from "',OLD.id,'" to "',NEW.id,'" and "',OLD.name,'" to "',NEW.name,'".'), NOW());
END$$;

create trigger log_add_pallet after insert on pallet
for each row begin 
    insert into log_table (entity, identifier, event, date) values('pallet', NEW.id, CONCAT('Added to system with tags "',NEW.tag1_id,'" and "',NEW.tag2_id,'".'), NOW());
END$$;

create trigger log_remove_pallet before delete on pallet
for each row begin 
    insert into log_table (entity, identifier, event, date) values('pallet', OLD.id, CONCAT('Removed pallets with tags "',OLD.tag1_id,'" and "',OLD.tag2_id,'" from DB.'), NOW());
END$$;

create trigger log_add_setOfA after insert on set_of_article
for each row begin 
    insert into log_table (entity, identifier, event, date) values('set of articles', NEW.id, CONCAT('Added ',NEW.amount,'of "',NEW.article_id, '" to pallet ',NEW.pallet_id,'.'), NOW());
END$$;

create trigger log_remove_setOfA after delete on set_of_article
for each row begin 
    insert into log_table (entity, identifier, event, date) values('set of articles', OLD.id, CONCAT('Removed ',OLD.amount,'of "',OLD.article_id, '" on pallet ',OLD.pallet_id,' from DB.'), NOW());
END$$;

create trigger log_update_setOfA after update on set_of_article
for each row begin 
    insert into log_table (entity, identifier, event, date) values('set of articles', OLD.id, CONCAT('Altered ',OLD.amount,'of "',OLD.article_id, '" on pallet ',OLD.pallet_id,' to ',OLD.amount,'of "',OLD.article_id,'.'), NOW());
END$$;

DELIMITER ;




DELIMITER $$;
drop trigger log_move$$;
DELIMITER ;



*/