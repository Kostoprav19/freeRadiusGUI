SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE=`TRADITIONAL,ALLOW_INVALID_DATES`;

CREATE SCHEMA IF NOT EXISTS `freeradiusgui` DEFAULT CHARACTER SET utf8;
USE `freeradiusgui` ;

-- -----------------------------------------------------
-- Table `freeradiusgui`.`logs`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `freeradiusgui`.`logs` ;

CREATE TABLE IF NOT EXISTS `freeradiusgui`.`logs` (
  `log_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `mac` VARCHAR(12) NOT NULL,
  `switch_id` INT UNSIGNED NOT NULL,
  `port` INT NOT NULL,
  `speed` INT NOT NULL,
  `duplex` INT NOT NULL,
  `tor` DATETIME NOT NULL,
  `status` INT NOT NULL,
  PRIMARY KEY (`log_id`)
)
  ENGINE = InnoDB
DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `freeradiusgui`.`devices`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `freeradiusgui`.`devices` ;

CREATE TABLE IF NOT EXISTS `freeradiusgui`.`devices` (
  `device_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `mac` VARCHAR(12) NOT NULL,
  `name` VARCHAR(60) NOT NULL,
  `descr` VARCHAR(100),
  `type` VARCHAR(20),
  `switch_id` BIGINT,
  `port` INT,
  `speed` INT,
  `duplex` INT,
  `tor` DATETIME,
  `lastseen` DATETIME,
  `access` INT,
  PRIMARY KEY (`device_id`, `mac`)
)
  ENGINE = InnoDB
DEFAULT CHARSET=utf8;


-- -----------------------------------------------------
-- Table `freeradiusgui`.`switches`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `freeradiusgui`.`switches` ;

CREATE TABLE IF NOT EXISTS `freeradiusgui`.`switches` (
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
-- Table `freeradiusgui`.`accounts`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `freeradiusgui`.`accounts` ;

CREATE TABLE IF NOT EXISTS `freeradiusgui`.`accounts` (
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
-- Table `freeradiusgui`.`roles`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `freeradiusgui`.`roles` ;

CREATE TABLE IF NOT EXISTS `freeradiusgui`.`roles` (
  `role_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `roleName` varchar(45) NOT NULL,
  PRIMARY KEY (`role_id`)
)
  ENGINE = InnoDB
DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `freeradiusgui`.`accounts_roles`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `freeradiusgui`.`accounts_roles`;

CREATE TABLE IF NOT EXISTS `freeradiusgui`.`accounts_roles` (
  `account_id` INT UNSIGNED NOT NULL,
  `role_id` INT UNSIGNED NOT NULL,
INDEX `FK_to_users_idx` (`account_id` ASC),
INDEX `FK_to_roles_idx` (`role_id` ASC),
CONSTRAINT `FK_to_roles`
  FOREIGN KEY (`role_id`)
  REFERENCES `freeradiusgui`.`roles` (`role_id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
CONSTRAINT `FK_to_accounts`
  FOREIGN KEY (`account_id`)
  REFERENCES `freeradiusgui`.`accounts` (`account_id`)
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


INSERT INTO roles (role_id, roleName)
VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (role_id, roleName)
VALUES (2, 'ROLE_USER');


INSERT INTO accounts_roles (account_id, role_id)
VALUES ('1', '1');
INSERT INTO accounts_roles (account_id, role_id)
VALUES ('2', '2');