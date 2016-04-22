CREATE TABLE IF NOT EXISTS `registrations` (
  'id' INTEGER PRIMARY KEY AUTOINCREMENT,
  'mac' TEXT,
  'switch' INTEGER,
  'port' INTEGER,
  'speed' INTEGER,
  'tor' INTEGER,
  'status' INTEGER
);

CREATE TABLE IF NOT EXISTS 'devices' (
  'id'	INTEGER PRIMARY KEY AUTOINCREMENT,
  'mac'	TEXT,
  'name'	TEXT,
  'descr'	TEXT,
  'switch'	INTEGER,
  'port'	INTEGER,
  'speed'	INTEGER,
  'tor'	INTEGER,
  'lastseen' INTEGER,
  'access'	INTEGER
);

CREATE TABLE IF NOT EXISTS 'switches' (
  'id' INTEGER PRIMARY KEY AUTOINCREMENT,
  'mac' TEXT,
  'name' TEXT,
  'descr' TEXT,
  'ip' TEXT
)

