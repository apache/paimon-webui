DROP TABLE IF EXISTS `user_tenant`;

CREATE TABLE `user_tenant`
(
    `id`          int(11)     NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `user_id`     int(11)     NOT NULL COMMENT 'user id',
    `tenant_id`   int(11)     NOT NULL COMMENT 'tenant id',
    `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;

INSERT INTO `user_tenant` (`id`, `user_id`, `tenant_id`)
VALUES (1, 1, 1);