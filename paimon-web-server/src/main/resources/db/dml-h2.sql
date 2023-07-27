INSERT INTO `user` ( id, username, password, nickname, mobile
                   , email, enabled, is_delete)
VALUES ( 1, 'admin', '21232f297a57a5a743894a0e4a801fc3', 'Admin', 0
       , 'admin@paimon.com', 1, 0);

INSERT INTO `tenant` (id, name, description)
VALUES (1, 'DefaultTenant', 'DefaultTenant');

INSERT INTO `user_tenant` (`id`, `user_id`, `tenant_id`)
VALUES (1, 1, 1);