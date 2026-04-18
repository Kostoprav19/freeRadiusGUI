-- Dev seed data for the compose stack: two switches + four devices whose
-- MAC / IP / port values line up with freeradius/requests.txt so radclient
-- produces auth-detail entries the GUI resolves to real rows.
-- Mounted into db as /docker-entrypoint-initdb.d/20-dev-seed.sql — runs once
-- on an empty volume, right after databaseCreationScript.sql.

USE `freeradiusgui`;

INSERT INTO switches (switch_id, mac, name, descr, ip, secret)
VALUES (1, '111111111111', 'dev-sw-office', 'Dev seed office switch', '10.0.0.11', 'testing123');
INSERT INTO switches (switch_id, mac, name, descr, ip, secret)
VALUES (2, '222222222222', 'dev-sw-lab', 'Dev seed lab switch', '10.0.0.12', 'testing123');

INSERT INTO devices (device_id, mac, name, descr, type, switch_id, port, speed, duplex, access)
VALUES (1, '001122334455', 'seed-printer-office', 'Office laser printer', 'Printer',  1,  7,  100, 1, 1);
INSERT INTO devices (device_id, mac, name, descr, type, switch_id, port, speed, duplex, access)
VALUES (2, 'aabbccddeeff', 'seed-workstation-lab', 'Lab workstation',    'Computer', 2, 14, 1000, 1, 1);
INSERT INTO devices (device_id, mac, name, descr, type, switch_id, port, speed, duplex, access)
VALUES (3, 'ccddeeff0011', 'seed-laptop-dev',      'Developer laptop',   'Computer', 1, 23, 1000, 1, 1);
INSERT INTO devices (device_id, mac, name, descr, type, switch_id, port, speed, duplex, access)
VALUES (4, 'deadbeef1234', 'seed-rogue-device',    'Intentional reject', 'Other',    2,  4,  100, 0, 0);
