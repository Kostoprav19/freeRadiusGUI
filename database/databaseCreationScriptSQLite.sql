-- -----------------------------------------------------
-- Table `Registrations`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `registrations` ;
CREATE TABLE IF NOT EXISTS `status` (
  `id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `mac` TEXT NOT NULL,
  `switch` INTEGER NOT NULL,
  `port` INTEGER NOT NULL,
  `speed` INTEGER NOT NULL,
  `tor` INTEGER NOT NULL,
  `status` INTEGER NOT NULL
);

-- -----------------------------------------------------
-- Table `devices`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `devices` ;
CREATE TABLE IF NOT EXISTS `devices` (
  `id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `mac` TEXT NOT NULL,
  `name` TEXT NOT NULL,
  `descr` TEXT NOT NULL,
  `switch` INTEGER NOT NULL,
  `port` INTEGER NOT NULL,
  `speed` INTEGER NOT NULL,
  `tor` INTEGER NOT NULL,
  `lastseen` INTEGER NOT NULL,
  `access` INTEGER NOT NULL
);

-- -----------------------------------------------------
-- Table `switches`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `switches` ;
CREATE TABLE IF NOT EXISTS `devices` (
  `id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `mac` TEXT NOT NULL,
  `name` TEXT NOT NULL,
  `descr` TEXT NOT NULL,
  `ip` TEXT NOT NULL
)