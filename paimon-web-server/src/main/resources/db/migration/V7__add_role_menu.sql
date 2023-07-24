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

insert into role_menu (role_id, menu_id)
values (1, 1),
       (1, 100),
       (1, 1000),
       (1, 1001),
       (1, 1002),
       (1, 1003),
       (1, 1004);