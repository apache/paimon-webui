DROP TABLE IF EXISTS `tenant`;

CREATE TABLE `tenant`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `name`        varchar(64) NULL DEFAULT NULL COMMENT 'tenant name',
    `description` varchar(255) NULL DEFAULT NULL COMMENT 'tenant description',
    `is_delete`   tinyint(1) NOT NULL DEFAULT 0 COMMENT 'is delete',
    `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;

INSERT INTO `tenant` (id, name, description) VALUES (1, 'DefaultTenant', 'DefaultTenant');