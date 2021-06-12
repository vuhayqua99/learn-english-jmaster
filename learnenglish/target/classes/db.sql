CREATE TABLE IF NOT EXISTS `persistent_logins` (
  `username` varchar(64) NOT NULL,
  `series` varchar(64) NOT NULL,
  `token` varchar(64) NOT NULL,
  `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`series`)
);

INSERT INTO role(id, name) values(1, 'ROLE_ADMIN') ON DUPLICATE KEY UPDATE name = 'ROLE_ADMIN';
INSERT INTO role(id, name) values(2, 'ROLE_STAFF') ON DUPLICATE KEY UPDATE name = 'ROLE_STAFF';
INSERT INTO role(id, name) values(3, 'ROLE_MEMBER') ON DUPLICATE KEY UPDATE name = 'ROLE_MEMBER';

INSERT INTO user(id, created_date, address, enabled, name, phone, password, role_id)
values (1, now(), 'HN', 1, 'admin', '0123456789','$2a$12$cZpjQBhfxpEd3xBoywRTU.jx3f.UD/ygj/nR373ebd/0uZe.xtYZ2',1) ON DUPLICATE KEY UPDATE role_id = 1;