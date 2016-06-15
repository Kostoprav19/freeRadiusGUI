SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE=`TRADITIONAL,ALLOW_INVALID_DATES`;

CREATE SCHEMA IF NOT EXISTS `freeradiusGUI` DEFAULT CHARACTER SET utf8;
USE `freeradiusGUI` ;

-- -----------------------------------------------------
-- Table `freeradiusGUI`.`logs`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `freeradiusGUI`.`logs` ;

CREATE TABLE IF NOT EXISTS `freeradiusGUI`.`logs` (
  `log_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `mac` VARCHAR(12) NOT NULL,
  `switch_id` INT UNSIGNED NOT NULL,
  `port` INT NOT NULL,
  `speed` INT NOT NULL,
  `tor` DATETIME NOT NULL,
  `status` INT NOT NULL,
  PRIMARY KEY (`log_id`)
)
  ENGINE = InnoDB
DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `freeradiusGUI`.`devices`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `freeradiusGUI`.`devices` ;

CREATE TABLE IF NOT EXISTS `freeradiusGUI`.`devices` (
  `device_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `mac` VARCHAR(12) NOT NULL,
  `name` VARCHAR(30) NOT NULL,
  `descr` VARCHAR(100),
  `switch_id` BIGINT,
  `port` INT,
  `speed` INT,
  `tor` DATETIME,
  `lastseen` DATETIME,
  `access` INT,
  PRIMARY KEY (`device_id`)
)
  ENGINE = InnoDB
DEFAULT CHARSET=utf8;


-- -----------------------------------------------------
-- Table `freeradiusGUI`.`switches`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `freeradiusGUI`.`switches` ;

CREATE TABLE IF NOT EXISTS `freeradiusGUI`.`switches` (
  `switch_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `mac` VARCHAR(12),
  `name` VARCHAR(30) NOT NULL,
  `descr` VARCHAR(100),
  `ip` VARCHAR(16) NOT NULL,
  `secret` VARCHAR(60),
  PRIMARY KEY (`switch_id`)
)
  ENGINE = InnoDB
DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `freeradiusGUI`.`accounts`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `freeradiusGUI`.`accounts` ;

CREATE TABLE IF NOT EXISTS `freeradiusGUI`.`accounts` (
  `account_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `login` VARCHAR(45) NOT NULL,
  `password` VARCHAR(60) NOT NULL,
  `name` VARCHAR(50),
  `surname` VARCHAR(50),
  `email` VARCHAR(50),
  `created` DATETIME NOT NULL,
  `enabled` TINYINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`account_id`)
)
  ENGINE = InnoDB
DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `freeradiusGUI`.`roles`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `freeradiusGUI`.`roles` ;

CREATE TABLE IF NOT EXISTS `freeradiusGUI`.`roles` (
  `role_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`role_id`)
)
  ENGINE = InnoDB
DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `freeradiusGUI`.`accounts_roles`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `freeradiusGUI`.`accounts_roles`;

CREATE TABLE IF NOT EXISTS `freeradiusGUI`.`accounts_roles` (
  `account_id` INT UNSIGNED NOT NULL,
  `role_id` INT UNSIGNED NOT NULL,
INDEX `FK_to_users_idx` (`account_id` ASC),
INDEX `FK_to_roles_idx` (`role_id` ASC),
CONSTRAINT `FK_to_roles`
  FOREIGN KEY (`role_id`)
  REFERENCES `freeradiusGUI`.`roles` (`role_id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
CONSTRAINT `FK_to_accounts`
  FOREIGN KEY (`account_id`)
  REFERENCES `freeradiusGUI`.`accounts` (`account_id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Fill data. Default password is 123456
-- -----------------------------------------------------

INSERT INTO accounts(account_id, login,password,enabled, created)
VALUES (1, 'admin','$2a$10$04TVADrR6/SPLBjsK0N30.Jf5fNjBugSACeGv1S69dZALR7lSov0y', true, '2016-03-05 10:00:00');
INSERT INTO accounts(account_id, login,password,enabled, created)
VALUES (2, 'user','$2a$10$04TVADrR6/SPLBjsK0N30.Jf5fNjBugSACeGv1S69dZALR7lSov0y', true, '2016-03-05 11:00:00');


INSERT INTO roles (role_id, name)
VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (role_id, name)
VALUES (2, 'ROLE_USER');


INSERT INTO accounts_roles (account_id, role_id)
VALUES ('1', '1');
INSERT INTO accounts_roles (account_id, role_id)
VALUES ('2', '2');