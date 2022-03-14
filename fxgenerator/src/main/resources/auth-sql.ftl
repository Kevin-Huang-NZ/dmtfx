INSERT INTO `sys_page`(`page_name`,`page_title`,`memo`)VALUES('[=ucTableName]','[=ucTableName]','');

INSERT INTO `sys_fun`(`fun_no`,`page_name`,`action_type`,`action_no`,`action_name`)VALUES('[=ucTableName]/c','[=ucTableName]','c','c','[=ucTableName]-新建');
INSERT INTO `sys_fun`(`fun_no`,`page_name`,`action_type`,`action_no`,`action_name`)VALUES('[=ucTableName]/r','[=ucTableName]','r','r','[=ucTableName]-检索');
INSERT INTO `sys_fun`(`fun_no`,`page_name`,`action_type`,`action_no`,`action_name`)VALUES('[=ucTableName]/u','[=ucTableName]','u','u','[=ucTableName]-编辑');
INSERT INTO `sys_fun`(`fun_no`,`page_name`,`action_type`,`action_no`,`action_name`)VALUES('[=ucTableName]/d','[=ucTableName]','d','d','[=ucTableName]-删除');

INSERT INTO `sys_role_fun`(`role_no`,`fun_no`)VALUES('Z','[=ucTableName]/c');
INSERT INTO `sys_role_fun`(`role_no`,`fun_no`)VALUES('Z','[=ucTableName]/r');
INSERT INTO `sys_role_fun`(`role_no`,`fun_no`)VALUES('Z','[=ucTableName]/u');
INSERT INTO `sys_role_fun`(`role_no`,`fun_no`)VALUES('Z','[=ucTableName]/d');