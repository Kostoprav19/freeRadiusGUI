SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `Java2_ACS` DEFAULT CHARACTER SET utf8 ;
USE `Java2_ACS` ;

-- -----------------------------------------------------
-- Table `Java2_ACS`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `java2_ACS`.`users` ;

CREATE TABLE IF NOT EXISTS `java2_ACS`.`users` (
  `UserID` BIGINT NOT NULL AUTO_INCREMENT,
  `FirstName` VARCHAR(32) NOT NULL,
  `LastName` VARCHAR(32) NOT NULL,
  PRIMARY KEY (`UserID`)
)
ENGINE = InnoDB
DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `Java2_ACS`.`accessObjects`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `java2_ACS`.`accessObjects` ;

CREATE TABLE IF NOT EXISTS `java2_ACS`.`accessObjects` (
  `AccessObjectID` BIGINT NOT NULL AUTO_INCREMENT,
  `Name` VARCHAR(30) NOT NULL,
  `Description` TEXT NOT NULL,
  `Platform` VARCHAR(60),
  `URL` VARCHAR(80),
  PRIMARY KEY (`AccessObjectID`)
)
ENGINE = InnoDB
DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `Java2_ACS`.`groups`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `java2_ACS`.`groups` ;

CREATE TABLE IF NOT EXISTS `java2_ACS`.`groups` (
  `UserID` BIGINT NOT NULL AUTO_INCREMENT,
  `FirstName` VARCHAR(32) NOT NULL,
  `LastName` VARCHAR(32) NOT NULL,
  PRIMARY KEY (`UserID`)
)
ENGINE = InnoDB
DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `Java2_ACS`.`auditLogs`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `java2_ACS`.`auditLogs` ;

CREATE TABLE IF NOT EXISTS `java2_ACS`.`auditLogs` (
  `LogID` BIGINT NOT NULL AUTO_INCREMENT,
  `UserID` BIGINT NOT NULL,
  `IP` VARCHAR(30),
  `Command` VARCHAR(100),
  `Value` VARCHAR(100),
  `AccessObjectID` BIGINT NOT NULL,
  `Date` DATETIME NOT NULL,
  PRIMARY KEY (`LogID`)
)
ENGINE = InnoDB
DEFAULT CHARSET=utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;