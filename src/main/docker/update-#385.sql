ALTER TABLE `concept`
ADD COLUMN `deprecated` TINYINT(1) NOT NULL DEFAULT '0' AFTER `version_id`,
ADD COLUMN `replacedBy` BIGINT(20) DEFAULT NULL AFTER `deprecated`;
