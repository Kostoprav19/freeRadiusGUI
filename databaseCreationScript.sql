SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `freeradiusGUI` DEFAULT CHARACTER SET utf8 ;
USE `freeradiusGUI` ;

-- -----------------------------------------------------
-- Table `freeradiusGUI`.`registrations`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `freeradiusGUI`.`registrations` ;

CREATE TABLE IF NOT EXISTS `freeradiusGUI`.`registrations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `mac` VARCHAR(16) NOT NULL,
  `switch` BIGINT NOT NULL,
  `port` INT NOT NULL,
  `speed` INT NOT NULL,
  `tor` DATETIME NOT NULL,
  `status` INT NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `freeradiusGUI`.`devices`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `freeradiusGUI`.`devices` ;

CREATE TABLE IF NOT EXISTS `freeradiusGUI`.`devices` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `mac` VARCHAR(16) NOT NULL,
  `name` VARCHAR(30) NOT NULL,
  `descr` VARCHAR(100) NOT NULL,
  `switch` BIGINT,
  `port` INT NOT NULL,
  `speed` INT NOT NULL,
  `tor` DATETIME NOT NULL,
  `lastseen` DATETIME NOT NULL,
  `access` INT NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
DEFAULT CHARSET=utf8;


-- -----------------------------------------------------
-- Table `freeradiusGUI`.`switches`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `freeradiusGUI`.`switches` ;

CREATE TABLE IF NOT EXISTS `freeradiusGUI`.`switches` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `mac` VARCHAR(16) NOT NULL,
  `name` VARCHAR(30) NOT NULL,
  `descr` VARCHAR(100) NOT NULL,
  `ip` VARCHAR(16) NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
DEFAULT CHARSET=utf8;

