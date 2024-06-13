--  Licensed to the Apache Software Foundation (ASF) under one or more
--  contributor license agreements.  See the NOTICE file distributed with
--  this work for additional information regarding copyright ownership.
--  The ASF licenses this file to You under the Apache License, Version 2.0
--  (the "License"); you may not use this file except in compliance with
--  the License.  You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
--  Unless required by applicable law or agreed to in writing, software
--  distributed under the License is distributed on an "AS IS" BASIS,
--  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--  See the License for the specific language governing permissions and
--  limitations under the License.

DROP TABLE IF EXISTS `user`;
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
    ) ENGINE = InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `tenant`;
CREATE TABLE if not exists `tenant`
(
    `id`          int(11)      NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `name`        varchar(64)  NULL     DEFAULT NULL COMMENT 'tenant name',
    `description` varchar(255) NULL     DEFAULT NULL COMMENT 'tenant description',
    `is_delete`   tinyint(1)   NOT NULL DEFAULT 0 COMMENT 'is delete',
    `create_time` datetime(0)  NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0)  NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'
    ) ENGINE = InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `user_tenant`;
CREATE TABLE if not exists `user_tenant`
(
    `id`          int(11)     NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `user_id`     int(11)     NOT NULL COMMENT 'user id',
    `tenant_id`   int(11)     NOT NULL COMMENT 'tenant id',
    `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'
    ) ENGINE = InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE if not exists `sys_role`
(
    `id`          int(11)      not null auto_increment primary key comment 'id',
    `role_name`   varchar(30)  not null comment 'role name',
    `role_key`    varchar(100) not null comment 'role key',
    `sort`        int(4)       not null comment 'sort',
    `enabled`     tinyint(1)   NOT NULL DEFAULT 1 COMMENT 'is enable',
    `is_delete`   tinyint(1)   NOT NULL DEFAULT 0 COMMENT 'is delete',
    `remark`      varchar(500)          default null comment 'remark',
    `create_time` datetime(0)  NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0)  NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'
    ) ENGINE = InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE if not exists `sys_menu`
(
    `id`          int(11)  not null auto_increment primary key comment 'id',
    `menu_name`   varchar(50) not null comment 'menu name',
    `parent_id`   int(11)           default 0 comment 'parent id',
    `sort`        int(4)               default 0 comment 'sort',
    `path`        varchar(200)         default '' comment 'route path',
    `query`       varchar(255)         default null comment 'route params',
    `is_cache`    int(1)               default 0 comment 'is cache（0:cache 1:no_cache）',
    `type`        char(1)              default '' comment 'menu type（M:directory C:menu F:button）',
    `visible`     char(1)              default 0 comment 'is visible（0:display 1:hide）',
    `component`   varchar(255)         default null comment 'component path',
    `is_frame`    int(1)               default 0 comment 'is frame',
    `enabled`     tinyint(1)  NOT NULL DEFAULT 1 COMMENT 'is enable',
    `is_delete`   tinyint(1)  NOT NULL DEFAULT 0 COMMENT 'is delete',
    `perms`       varchar(100)         default null comment 'menu perms',
    `icon`        varchar(100)         default '#' comment 'menu icon',
    `remark`      varchar(500)         default '' comment 'remark',
    `create_time` datetime(0) NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0) NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'
    ) ENGINE = InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `user_role`;
CREATE TABLE if not exists `user_role`
(
    `id`          int(11)     not null auto_increment primary key comment 'id',
    `user_id`     int(11)     not null comment 'user id',
    `role_id`     int(11)     not null comment 'role id',
    `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'update time',
    unique key `idx_user_role` (`user_id`, `role_id`)
    ) ENGINE = InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `role_menu`;
CREATE TABLE if not exists `role_menu`
(
    `id`          int(11)     not null auto_increment primary key comment 'id',
    `role_id`     int(11)     not null comment 'role id',
    `menu_id`     int(11)     not null comment 'menu id',
    `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'update time',
    unique key `idx_role_menu` (`role_id`, `menu_id`)
    )  ENGINE = InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `catalog`;
CREATE TABLE if not exists `catalog`
(
    id            int(11)  not null auto_increment primary key comment 'id',
    catalog_type  varchar(50)                          not null comment 'catalog type',
    catalog_name  varchar(100)                         not null comment 'catalog name',
    warehouse     varchar(200)                         not null comment 'warehouse',
    hive_uri      varchar(200)                         null comment 'hive uri',
    hive_conf_dir varchar(100)                         null comment 'catalog name',
    is_delete     tinyint(1) default 0                 not null comment 'is delete',
    create_time   datetime   default CURRENT_TIMESTAMP null comment 'create time',
    update_time   datetime   default CURRENT_TIMESTAMP null comment 'update time',
    options       varchar(512)                         null
    ) engine = innodb;

DROP TABLE IF EXISTS `cluster`;
CREATE TABLE if not exists `cluster`
(
    `id`          int(11)     not null auto_increment primary key comment 'id',
    `cluster_name`  varchar(100)  comment 'cluster name',
    `host`     varchar(100)  comment 'host',
    `port`   int(11) COMMENT 'port',
    `enabled`   tinyint(1)  NOT NULL DEFAULT 1 COMMENT 'enabled',
    `type`    varchar(100)  comment 'cluster type',
    `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'
    )  ENGINE = InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `cdc_job_definition`;
CREATE TABLE if not exists `cdc_job_definition`
(
    id          int(11)  not null auto_increment primary key comment 'id',
    `name`      varchar(20)                        not null comment 'name',
    description varchar(200)                       null comment 'description',
    cdc_type    int                                not null comment 'cdc type ',
    config      text                               null comment 'cdc job config',
    create_user varchar(20)                        null comment 'create user',
    create_time datetime default CURRENT_TIMESTAMP null comment 'create time',
    update_time datetime default CURRENT_TIMESTAMP null comment 'update time',
    is_delete   tinyint(1) default 0               not null comment 'is delete',
    constraint cdc_job_definition_pk
    unique (`name`)
) engine = innodb;

CREATE TABLE if not exists `job`
(
    `id`          int(11)     not null auto_increment primary key comment 'id',
    `job_id`     varchar(100)  not null comment 'job id',
    `job_name`     varchar(200) comment 'job name',
    `type`     varchar(100)  comment 'job type',
    `execute_mode`     varchar(50)  comment 'execute mode',
    `cluster_id`     varchar(100)  comment 'cluster id',
    `uid`          INT(11) COMMENT 'User ID',
    `config`     text   comment 'config',
    `statements`   text COMMENT 'statements',
    `status` varchar(50) COMMENT 'status',
    `start_time` datetime(0) NULL COMMENT 'start time',
    `end_time` datetime(0) NULL COMMENT 'end time',
    `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'
    )  ENGINE = InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `statement`;
CREATE TABLE if not exists `statement`
(
    `id`          int(11)     not null auto_increment primary key comment 'id',
    `statement_name`     varchar(200)  not null comment 'task type',
    `task_type`     varchar(100)  not null comment 'task type',
    `is_streaming`  tinyint(1)  comment 'is steaming',
    `uid`     int(11)  comment 'user id',
    `cluster_id`     int(11)  comment 'cluster id',
    `statements`   text COMMENT 'statements',
    `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'
    )  ENGINE = InnoDB DEFAULT CHARSET=utf8;
    
DROP TABLE IF EXISTS `history`;
CREATE TABLE if not exists `history`
(
    `id`          int(11)     not null auto_increment primary key comment 'id',
    `name`     varchar(100)  not null comment 'name',
    `task_type`     varchar(100)  not null comment 'task type',
    `is_streaming`  tinyint(1)  comment 'is steaming',
    `uid`     int(11)  comment 'user id',
    `cluster_id`     int(11)  comment 'cluster id',
    `statements`   text COMMENT 'statements',
    `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'
    )  ENGINE = InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `user` ( id, username, password, nickname, mobile
                   , email, enabled, is_delete)
VALUES ( 1, 'admin', '21232f297a57a5a743894a0e4a801fc3', 'Admin', 0
       , 'admin@paimon.com', 1, 0);
INSERT INTO `user` (id, username, password, nickname, mobile, email, enabled, is_delete)
VALUES (2, 'common', '21232f297a57a5a743894a0e4a801fc3', 'common', 0, 'common@paimon.com', 1, 0);

INSERT INTO `tenant` (id, name, description)
VALUES (1, 'DefaultTenant', 'DefaultTenant');

INSERT INTO `user_tenant` (`id`, `user_id`, `tenant_id`)
VALUES (1, 1, 1);

insert into sys_role (id, role_name, role_key, sort)
values (1, 'admin', 'admin', 1),
       (2, 'common', 'common', 2);

insert into sys_menu (id, menu_name, parent_id, sort, path, component, is_frame, type, perms, icon, remark)
values (1, 'system', 0, 1, 'system', null, 1, 'M', 'system', 'admin', 'system root path'),
       (100, 'user_manager', 1, 1, 'user', 'user/index', 1, 'C', 'system:user:list', 'user', 'user manager'),
       (1000, 'user_query', 100, 1, '', '', 1, 'F', 'system:user:query', '#', ''),
       (1001, 'user_add', 100, 2, '', '', 1, 'F', 'system:user:add', '#', ''),
       (1002, 'user_update', 100, 3, '', '', 1, 'F', 'system:user:update', '#', ''),
       (1003, 'user_delete', 100, 4, '', '', 1, 'F', 'system:user:delete', '#', ''),
       (1004, 'user_reset', 100, 5, '', '', 1, 'F', 'system:user:change:password', '#', ''),
       (101, 'role_manager', 1, 2, 'role', 'role/index', 1, 'C', 'system:role:list', 'role', 'role manager'),
       (1010, 'role_query', 101, 1, '', '', 1, 'F', 'system:role:query', '#', ''),
       (1011, 'role_add', 101, 2, '', '', 1, 'F', 'system:role:add', '#', ''),
       (1012, 'role_update', 101, 3, '', '', 1, 'F', 'system:role:update', '#', ''),
       (1013, 'role_delete', 101, 4, '', '', 1, 'F', 'system:role:delete', '#', ''),
       (102, 'menu_manager', 1, 3, 'menu', 'menu/index', 1, 'C', 'system:menu:list', 'menu', 'menu manager'),
       (1020, 'menu_query', 102, 1, '', '', 1, 'F', 'system:menu:query', '#', ''),
       (1021, 'menu_add', 102, 2, '', '', 1, 'F', 'system:menu:add', '#', ''),
       (1022, 'menu_update', 102, 3, '', '', 1, 'F', 'system:menu:update', '#', ''),
       (1023, 'menu_delete', 102, 4, '', '', 1, 'F', 'system:menu:delete', '#', ''),
       (103, 'cluster_manager', 1, 4, 'cluster', 'cluster/index', 1, 'C', 'system:cluster:list', 'cluster', 'cluster manager'),
       (1030, 'cluster_query', 103, 1, '', '', 1, 'F', 'system:cluster:query', '#', ''),
       (1031, 'cluster_add', 103, 2, '', '', 1, 'F', 'system:cluster:add', '#', ''),
       (1032, 'cluster_update', 103, 3, '', '', 1, 'F', 'system:cluster:update', '#', ''),
       (1033, 'cluster_delete', 103, 4, '', '', 1, 'F', 'system:cluster:delete', '#', ''),
       (2, 'metadata', 0, 2, 'metadata', null, 1, 'M', 'metadata', 'metadata', 'metadata root path'),
       (200, 'catalog_manager', 2, 1, 'catalog', 'catalog/index', 1, 'C', 'metadata:catalog:list', 'catalog', 'catalog manager'),
       (2000, 'catalog_create', 200, 1, '', '', 1, 'F', 'metadata:catalog:create', '#', ''),
       (2001, 'catalog_remove', 200, 2, '', '', 1, 'F', 'metadata:catalog:remove', '#', ''),
       (201, 'database_manager', 2, 2, 'database', 'database/index', 1, 'C', 'metadata:database:list', 'database', 'database manager'),
       (2010, 'database_create', 201, 1, '', '', 1, 'F', 'metadata:database:create', '#', ''),
       (2011, 'database_drop', 201, 2, '', '', 1, 'F', 'metadata:database:drop', '#', ''),
       (202, 'table_manager', 2, 3, 'table', 'table/index', 1, 'C', 'metadata:table:list', 'table', 'table manager'),
       (2020, 'table_create', 202, 1, '', '', 1, 'F', 'metadata:table:create', '#', ''),
       (2021, 'table_update', 202, 2, '', '', 1, 'F', 'metadata:table:update', '#', ''),
       (2022, 'table_drop', 202, 3, '', '', 1, 'F', 'metadata:table:drop', '#', ''),
       (203, 'column_manager', 2, 4, 'column', 'column/index', 1, 'C', 'metadata:column:list', 'column', 'column manager'),
       (2030, 'column_add', 203, 1, '', '', 1, 'F', 'metadata:column:add', '#', ''),
       (2031, 'column_drop', 203, 2, '', '', 1, 'F', 'metadata:column:drop', '#', ''),
       (204, 'option_manager', 2, 5, 'option', 'option/index', 1, 'C', 'metadata:option:list', 'option', 'option manager'),
       (2040, 'option_add', 204, 1, '', '', 1, 'F', 'metadata:option:add', '#', ''),
       (2041, 'option_remove', 204, 2, '', '', 1, 'F', 'metadata:option:remove', '#', ''),
       (205, 'schema_manager', 2, 6, 'schema', 'schema/index', 1, 'C', 'metadata:schema:list', 'schema', 'schema manager'),
       (206, 'snapshot_manager', 2, 7, 'snapshot', 'snapshot/index', 1, 'C', 'metadata:snapshot:list', 'snapshot', 'schema manager'),
       (207, 'manifest_manager', 2, 8, 'manifest', 'manifest/index', 1, 'C', 'metadata:manifest:list', 'manifest', 'manifest manager'),
       (208, 'datafile_manager', 2, 9, 'datafile', 'datafile/index', 1, 'C', 'metadata:datafile:list', 'datafile', 'datafile manager'),
       (3, 'cdc', 0, 3, 'cdc', null, 1, 'M', 'cdc', 'cdc', 'cdc root path'),
       (300, 'cdc_job_manager', 3, 1, 'cdc', 'cdc/index', 1, 'C', 'cdc:job:list', 'cdc', 'cdc job manager'),
       (3000, 'cdc_job_create', 3, 2, '', '', 1, 'F', 'cdc:job:create', '#', ''),
       (3001, 'cdc_job_query', 300, 1, '', '', 1, 'F', 'cdc:job:query', '#', ''),
       (3002, 'cdc_job_update', 300, 2, '', '', 1, 'F', 'cdc:job:update', '#', ''),
       (3003, 'cdc_job_delete', 300, 3, '', '', 1, 'F', 'cdc:job:delete', '#', ''),
       (3004, 'cdc_job_submit', 300, 4, '', '', 1, 'F', 'cdc:job:submit', '#', ''),
       (4, 'playground', 0, 4, 'playground', null, 1, 'M', 'playground', 'playground', 'playground root path'),
       (400, 'playground_job_manager', 4, 1, 'playground', 'playground/index', 1, 'C', 'playground:job:list', 'playground', 'playground job manager'),
       (4000, 'playground_job_query', 400, 1, '', '', 1, 'F', 'playground:job:query', '#', ''),
       (4001, 'playground_job_submit', 4, 2, '', '', 1, 'F', 'playground:job:submit', '#', ''),
       (4002, 'playground_job_stop', 4, 3, '', '', 1, 'F', 'playground:job:stop', '#', '');

insert into user_role (id, user_id, role_id)
values (1, 1, 1), (2, 2, 2);

insert into role_menu (role_id, menu_id)
values (1, 1),
       (1, 100),
       (1, 1000),
       (1, 1001),
       (1, 1002),
       (1, 1003),
       (1, 1004);
