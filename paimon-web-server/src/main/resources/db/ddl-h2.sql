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
) engine = innodb;

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
) engine = innodb;

CREATE TABLE if not exists `user_role`
(
    `id`          int(11)     not null auto_increment primary key comment 'id',
    `user_id`     int(11)     not null comment 'user id',
    `role_id`     int(11)     not null comment 'role id',
    `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'update time',
    unique key `idx_user_role` (`user_id`, `role_id`)
) engine = innodb;

CREATE TABLE if not exists `role_menu`
(
    `id`          int(11)     not null auto_increment primary key comment 'id',
    `role_id`     int(11)     not null comment 'role id',
    `menu_id`     int(11)     not null comment 'menu id',
    `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'update time',
    unique key `idx_role_menu` (`role_id`, `menu_id`)
) engine = innodb;

CREATE TABLE if not exists `catalog`
(
    `id`          int(11)     not null auto_increment primary key comment 'id',
    `catalog_type`     varchar(50)  not null comment 'catalog type',
    `catalog_name`     varchar(100)     not null comment 'catalog name',
    `warehouse`     varchar(200)     not null comment 'warehouse',
    `hive_uri`     varchar(200)     comment 'hive uri',
    `hive_conf_dir`     varchar(100)   comment 'catalog name',
    `is_delete`   tinyint(1)   NOT NULL DEFAULT 0 COMMENT 'is delete',
    `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'
) engine = innodb;

CREATE TABLE if not exists `databases`
(
    `id`          int(11)     not null auto_increment primary key comment 'id',
    `database_name`     varchar(50)  not null comment 'database name',
    `catalog_id`     int(11)     not null comment 'catalog id',
    `description`     varchar(200)  comment 'description',
    `is_delete`   tinyint(1)   NOT NULL DEFAULT 0 COMMENT 'is delete',
    `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'
) engine = innodb;

CREATE TABLE if not exists `flink_job_task`
(
    `id`                      int(11)      not null auto_increment primary key comment 'id',
    `job_name`                varchar(100) not null comment 'flink job name',
    `execution_runtime_mode`  varchar(10)  not null default 'STREAMING' comment 'execution.runtime-mode: STREAMING、BATCH、AUTOMATIC',
    `execution_target`        varchar(100) not null comment 'flink job submit type.eg:yarn-application、 yarn-session、yarn-per-job、local，kubernetes-session、kubernetes-application',
    `job_memory`              varchar(10)  not null DEFAULT '1gb' comment 'job memory',
    `task_memory`             varchar(10)  not null DEFAULT '2gb' comment 'task memory',
    `parallelism`             int(11)      NOT NULL DEFAULT 1 comment 'Job parallelism',
    `flink_version`           varchar(10)  not null DEFAULT '1.17.0' comment 'flink version',
    `flink_config_path`       varchar(100) comment 'flink deployment config dir.eg: /opt/soft/flink-1.17.0/conf',
    `hadoop_config_path`      varchar(100) comment 'hadoop deployment config dir.eg: /opt/soft/hadoop-3.3.3/etc/hadoop/conf',
    `checkpoint_path`         varchar(200) comment 'flink checkpoints dir. eg: 	hdfs://hacluster/flink_meta/flink-checkpoints',
    `checkpoint_interval`     varchar(21) not null default '600000' comment 'flink checkpoint interval, in milliseconds.Default 10 minutes',
    `savepoint_path`          varchar(200) comment 'flink savepoints dir. eg: hdfs://hacluster/flink_meta/flink-savepoints',
    `user_jar_path`           varchar(200) comment 'user jar path. eg: hdfs://hacluster/usr/xxx.jar',
    `user_jar_main_app_class` varchar(100) comment 'flink start class',
    `flink_lib_path`          varchar(200) comment 'user jar path. eg: hdfs://hacluster/flink_lib',
    `job_id`                  varchar(100) comment 'job id',
    `application_id`          varchar(100) comment 'application id',
    `flink_web_url`           varchar(200) comment 'flink job task web url',
    `job_status`              varchar(200) comment 'flink job JobStatus. rg:INITIALIZING、RUNNING、FAILED、FINISHED',
    `admin_user`              varchar(100) comment 'job admin user',
    `other_params`            varchar(500) comment 'Other parameters required for the flink job task to run, in JSON string format',
    `flink_sql`               longtext comment 'flink sql',
    `create_time`             datetime(0)  NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time`             datetime(0)  NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'update time',
    UNIQUE INDEX `idx_job_name` (`job_name`),
    INDEX `idx_admin_user` (`admin_user`)
    ) ENGINE = InnoDB;
