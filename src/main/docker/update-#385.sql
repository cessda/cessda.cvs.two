ALTER TABLE `concept` ADD COLUMN `deprecated` TINYINT(1) NOT NULL DEFAULT '0' AFTER `version_id`;
