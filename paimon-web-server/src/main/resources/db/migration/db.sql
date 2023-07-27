-- ----------------------------
-- user table
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `username`    varchar(50)   NOT NULL COMMENT 'username',
    `password`    varchar(50) NULL DEFAULT NULL COMMENT 'password',
    `nickname`    varchar(50) NULL DEFAULT NULL COMMENT 'nickname',
    `user_type`   int DEFAULT 0 NOT NULL COMMENT 'login type (0:LOCAL,1:LDAP)',
    `url`         varchar(100) NULL DEFAULT NULL COMMENT 'avatar url',
    `mobile`      varchar(20) NULL DEFAULT NULL COMMENT 'mobile phone',
    `email`       varchar(100) NULL DEFAULT NULL COMMENT 'email',
    `enabled`     tinyint(1) NOT NULL DEFAULT 1 COMMENT 'is enable',
    `is_delete`   tinyint(1) NOT NULL DEFAULT 0 COMMENT 'is delete',
    `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'update time',
    unique key `idx_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;

-- ----------------------------
-- init user table data
-- ----------------------------
INSERT INTO `user` (id, username, password, nickname, mobile, email, enabled, is_delete)
VALUES (1, 'admin', '21232f297a57a5a743894a0e4a801fc3', 'Admin', 0, 'admin@paimon.com', 1, 0);
INSERT INTO `user` (id, username, password, nickname, mobile, email, enabled, is_delete)
VALUES (2, 'common', '21232f297a57a5a743894a0e4a801fc3', 'common', 0, 'common@paimon.com', 1, 0);

-- ----------------------------
-- tenant table
-- ----------------------------
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

-- ----------------------------
-- init tenant table data
-- ----------------------------
INSERT INTO `tenant` (id, name, description) VALUES (1, 'DefaultTenant', 'DefaultTenant');

-- ----------------------------
-- user tenant table
-- ----------------------------
DROP TABLE IF EXISTS `user_tenant`;

CREATE TABLE `user_tenant`
(
    `id`          int(11)     NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `user_id`     int(11)     NOT NULL COMMENT 'user id',
    `tenant_id`   int(11)     NOT NULL COMMENT 'tenant id',
    `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;

-- ----------------------------
-- init user tenant table data
-- ----------------------------
INSERT INTO `user_tenant` (`id`, `user_id`, `tenant_id`)
VALUES (1, 1, 1);

-- ----------------------------
-- sys_role table
-- ----------------------------
drop table if exists sys_role;
create table sys_role
(
    id          int(11)      not null auto_increment primary key comment 'id',
    role_name   varchar(30)  not null comment 'role name',
    role_key    varchar(100) not null comment 'role key',
    sort        int(4)       not null comment 'sort',
    enabled     tinyint(1)   NOT NULL DEFAULT 1 COMMENT 'is enable',
    is_delete   tinyint(1)   NOT NULL DEFAULT 0 COMMENT 'is delete',
    remark      varchar(500)          default null comment 'remark',
    create_time datetime(0)  NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    update_time datetime(0)  NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'
) engine = innodb
  DEFAULT CHARSET = utf8mb4
  AUTO_INCREMENT = 1;

-- ----------------------------
-- init sys_role table data
-- ----------------------------
insert into sys_role (id, role_name, role_key, sort)
values (1, 'admin', 'admin', 1),
       (2, 'common', 'common', 2);

-- ----------------------------
-- sys_menu table
-- ----------------------------
drop table if exists sys_menu;
create table sys_menu
(
    id          int(11)  not null auto_increment primary key comment 'id',
    menu_name   varchar(50) not null comment 'menu name',
    parent_id   int(11)           default 0 comment 'parent id',
    sort        int(4)               default 0 comment 'sort',
    path        varchar(200)         default '' comment 'route path',
    query       varchar(255)         default null comment 'route params',
    is_cache    int(1)               default 0 comment 'is cache（0:cache 1:no_cache）',
    type        char(1)              default '' comment 'menu type（M:directory C:menu F:button）',
    visible     char(1)              default 0 comment 'is visible（0:display 1:hide）',
    component   varchar(255)         default null comment 'component path',
    is_frame    int(1)               default 0 comment 'is frame',
    enabled     tinyint(1)  NOT NULL DEFAULT 1 COMMENT 'is enable',
    is_delete   tinyint(1)  NOT NULL DEFAULT 0 COMMENT 'is delete',
    perms       varchar(100)         default null comment 'menu perms',
    icon        varchar(100)         default '#' comment 'menu icon',
    remark      varchar(500)         default '' comment 'remark',
    create_time datetime(0) NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    update_time datetime(0) NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'

) engine = innodb
  auto_increment = 2000
  DEFAULT CHARSET = utf8mb4;

-- ----------------------------
-- init sys_menu table data
-- ----------------------------
insert into sys_menu (id, menu_name, parent_id, sort, path, component, is_frame, type, perms, icon, remark)
values (1, 'all', 0, 1, 'system', null, 1, 'M', 'system', 'admin', 'system root path'),
       (100, 'user manager', 1, 1, 'user', 'user/index', 1, 'C', 'system:user:list', 'user', 'user manager'),
       (1000, 'user query', 100, 1, '', '', 1, 'F', 'system:user:query', '#', ''),
       (1001, 'user add', 100, 2, '', '', 1, 'F', 'system:user:add', '#', ''),
       (1002, 'user edit', 100, 3, '', '', 1, 'F', 'system:user:edit', '#', ''),
       (1003, 'user del', 100, 4, '', '', 1, 'F', 'system:user:remove', '#', ''),
       (1004, 'user reset', 100, 5, '', '', 1, 'F', 'system:user:resetPwd', '#', ''),
       (200, 'role manager', 1, 1, 'role', 'role/index', 1, 'C', 'system:role:list', 'role', 'role manager'),
       (2000, 'role query', 200, 1, '', '', 1, 'F', 'system:role:query', '#', ''),
       (2001, 'role add', 200, 2, '', '', 1, 'F', 'system:role:add', '#', ''),
       (2002, 'role edit', 200, 3, '', '', 1, 'F', 'system:role:edit', '#', ''),
       (2003, 'role del', 200, 4, '', '', 1, 'F', 'system:role:remove', '#', ''),
       (300, 'menu manager', 1, 1, 'menu', 'menu/index', 1, 'C', 'system:menu:list', 'menu', 'menu manager'),
       (3000, 'menu query', 300, 1, '', '', 1, 'F', 'system:menu:query', '#', ''),
       (3001, 'menu add', 300, 2, '', '', 1, 'F', 'system:menu:add', '#', ''),
       (3002, 'menu edit', 300, 3, '', '', 1, 'F', 'system:menu:edit', '#', ''),
       (3003, 'menu del', 300, 4, '', '', 1, 'F', 'system:menu:remove', '#', '');

-- ----------------------------
-- user_role table
-- ----------------------------
drop table if exists user_role;
create table user_role
(
    id          int(11)     not null auto_increment primary key comment 'id',
    user_id     int(11)     not null comment 'user id',
    role_id     int(11)     not null comment 'role id',
    create_time datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    update_time datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'update time',
    unique key `idx_user_role` (user_id, role_id)
) engine = innodb
  DEFAULT CHARSET = utf8mb4
  AUTO_INCREMENT = 1;

-- ----------------------------
-- init user_role table data
-- ----------------------------
insert into user_role (id, user_id, role_id)
values (1, 1, 1), (2, 2, 2);

-- ----------------------------
-- role_menu table
-- ----------------------------
drop table if exists role_menu;
create table role_menu
(
    id          int(11)     not null auto_increment primary key comment 'id',
    role_id     int(11)     not null comment 'role id',
    menu_id     int(11)     not null comment 'menu id',
    create_time datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    update_time datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'update time',
    unique key `idx_role_menu` (role_id, menu_id)
) engine = innodb
  DEFAULT CHARSET = utf8mb4
  AUTO_INCREMENT = 1;

-- ----------------------------
-- init role_menu table data
-- ----------------------------
insert into role_menu (role_id, menu_id)
values (1, 1),
       (1, 100),
       (1, 1000),
       (1, 1001),
       (1, 1002),
       (1, 1003),
       (1, 1004);
