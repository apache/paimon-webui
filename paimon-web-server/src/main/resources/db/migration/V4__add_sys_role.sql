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

insert into sys_role (id, role_name, role_key, sort)
values (1, 'admin', 'admin', 1),
       (2, 'common', 'common', 2);