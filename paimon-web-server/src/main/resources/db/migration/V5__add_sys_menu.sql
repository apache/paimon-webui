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

insert into sys_menu (id, menu_name, parent_id, sort, path, component, is_frame, type, perms, icon, remark)
values (1, 'all', 0, 1, 'system', null, 1, 'M', 'system', 'admin', 'system root path'),
       (100, 'user manager', 1, 1, 'user', 'user/index', 1, 'C', 'system:user:list', 'user', 'user manager'),
       (1000, 'user query', 100, 1, '', '', 1, 'F', 'system:user:query', '#', ''),
       (1001, 'user add', 100, 2, '', '', 1, 'F', 'system:user:add', '#', ''),
       (1002, 'user edit', 100, 3, '', '', 1, 'F', 'system:user:edit', '#', ''),
       (1003, 'user del', 100, 4, '', '', 1, 'F', 'system:user:remove', '#', ''),
       (1004, 'user reset', 100, 5, '', '', 1, 'F', 'system:user:resetPwd', '#', '');