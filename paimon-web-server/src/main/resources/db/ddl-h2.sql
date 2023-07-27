CREATE TABLE if not exists `user`
(
    `id`          int(11)      NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `username`    varchar(50)  NOT NULL COMMENT 'username',
    `password`    varchar(50)  NULL     DEFAULT NULL COMMENT 'password',
    `nickname`    varchar(50)  NULL     DEFAULT NULL COMMENT 'nickname',
    `user_type`   int          NOT NULL DEFAULT 0 COMMENT 'login type (0:LOCAL,1:LDAP)',
    `url`         varchar(100) NULL     DEFAULT NULL COMMENT 'avatar url',
    `mobile`      varchar(20)  NULL     DEFAULT NULL COMMENT 'mobile phone',
    `email`       varchar(100) NULL     DEFAULT NULL COMMENT 'email',
    `enabled`     tinyint(1)   NOT NULL DEFAULT 1 COMMENT 'is enable',
    `is_delete`   tinyint(1)   NOT NULL DEFAULT 0 COMMENT 'is delete',
    `create_time` datetime(0)  NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0)  NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'
) ENGINE = InnoDB;

CREATE TABLE if not exists `tenant`
(
    `id`          int(11)      NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `name`        varchar(64)  NULL     DEFAULT NULL COMMENT 'tenant name',
    `description` varchar(255) NULL     DEFAULT NULL COMMENT 'tenant description',
    `is_delete`   tinyint(1)   NOT NULL DEFAULT 0 COMMENT 'is delete',
    `create_time` datetime(0)  NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0)  NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'
) ENGINE = InnoDB;

CREATE TABLE if not exists `user_tenant`
(
    `id`          int(11)     NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `user_id`     int(11)     NOT NULL COMMENT 'user id',
    `tenant_id`   int(11)     NOT NULL COMMENT 'tenant id',
    `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'
) ENGINE = InnoDB;

