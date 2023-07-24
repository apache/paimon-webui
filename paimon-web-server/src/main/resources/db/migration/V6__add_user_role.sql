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

insert into user_role (id, user_id, role_id)
values (1, 1, 1), (2, 2, 2);