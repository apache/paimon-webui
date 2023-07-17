DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `username`    varchar(50)   NOT NULL COMMENT 'username',
    `password`    varchar(50) NULL DEFAULT NULL COMMENT 'password',
    `nickname`    varchar(50) NULL DEFAULT NULL COMMENT 'nickname',
    `worknum`     varchar(50) NULL DEFAULT NULL COMMENT 'worknum',
    user_type     int DEFAULT 0 NOT NULL COMMENT 'login type (0:LOCAL,1:LDAP)',
    `avatar`      blob NULL COMMENT 'avatar',
    `mobile`      varchar(20) NULL DEFAULT NULL COMMENT 'mobile phone',
    `email`       varchar(100) NULL DEFAULT NULL COMMENT 'email',
    `enabled`     tinyint(1) NOT NULL DEFAULT 1 COMMENT 'is enable',
    `is_delete`   tinyint(1) NOT NULL DEFAULT 0 COMMENT 'is delete',
    `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;

INSERT INTO `user` (id, username, password, nickname, worknum, avatar, mobile, email, enabled, is_delete)
VALUES (1, 'admin', '21232f297a57a5a743894a0e4a801fc3', 'Admin', NULL, 0, NULL, 'admin@paimon.com', 1, 0);