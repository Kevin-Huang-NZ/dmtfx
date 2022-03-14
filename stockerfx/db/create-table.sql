CREATE TABLE `sys_page` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `page_name` VARCHAR(50) NOT NULL DEFAULT '',
    `page_title` VARCHAR(50) NOT NULL DEFAULT '',
    `memo` VARCHAR(500) NULL,
    PRIMARY KEY (`id`)
)  ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sys_fun` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `fun_no` VARCHAR(80) NOT NULL DEFAULT '' COMMENT '非输入值，等于page_name/action_no',
    `page_name` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '关联sys_page表page_name',
    `action_type` CHAR(1) NOT NULL DEFAULT 'c' COMMENT 'c-Create；r-Retrieve；u-Update；d-Delete；e-Else',
    `action_no` VARCHAR(30) NOT NULL DEFAULT '',
    `action_name` VARCHAR(50) NOT NULL DEFAULT '',
    `memo` VARCHAR(500) NULL,
    PRIMARY KEY (`id`)
)  ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sys_role` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `role_no` CHAR(1) NOT NULL DEFAULT '',
    `role_name` VARCHAR(20) NOT NULL DEFAULT '',
    `memo` VARCHAR(500) NULL,
    PRIMARY KEY (`id`)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `sys_role_fun` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `role_no` CHAR(1) NOT NULL DEFAULT '',
    `fun_no` VARCHAR(80) NOT NULL DEFAULT '',
    PRIMARY KEY (`id`)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `user` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `user_name` VARCHAR(50) NOT NULL DEFAULT '',
    `avatar` VARCHAR(200) NULL,
    `gender` CHAR(1) NULL COMMENT '0-未提供；1-男；2-女',
    `birthday` VARCHAR(10) NULL COMMENT '格式：yyyy-MM-dd',
    `phone` VARCHAR(20) NOT NULL DEFAULT '',
    `password` VARCHAR(100) NOT NULL DEFAULT '',
    `roles` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '多个角色使用分号连接',
    `status` CHAR(1) NOT NULL DEFAULT '1' COMMENT '0-禁用；1-启用',
    PRIMARY KEY (`id`),
    UNIQUE KEY `i_staff_0` (`phone`)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `standard` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `standard_name` VARCHAR(200) NOT NULL DEFAULT '',
    `version_code` VARCHAR(50) NOT NULL DEFAULT '',
    `publish_ymd` VARCHAR(10) NOT NULL DEFAULT '',
    `language_name` VARCHAR(50) NOT NULL DEFAULT '',
    `country_region` VARCHAR(400) NOT NULL DEFAULT '',
    `memo` VARCHAR(500) NULL,
    PRIMARY KEY (`id`)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `roman` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `standard_id` BIGINT(20) NOT NULL DEFAULT -1,
    `original_alpha` VARCHAR(10) NOT NULL DEFAULT '',
    `roman_alpha` VARCHAR(10) NOT NULL DEFAULT '',
    PRIMARY KEY (`id`),
    KEY `i_ct_roman_0` (`standard_id`)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `common_word` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `standard_id` BIGINT(20) NOT NULL DEFAULT -1,
    `original` VARCHAR(200) NOT NULL DEFAULT '',
    `original_abbr` VARCHAR(100) NULL,
    `original_type` CHAR(1) NOT NULL DEFAULT 'x' COMMENT '1-人名；2-通名；3-形容词；x-其它',
    `roman` VARCHAR(200) NULL COMMENT '罗马转写',
    `match_way` CHAR(1) NOT NULL DEFAULT '1' COMMENT '匹配方式：1-精确；2-前缀（词头）；3-后缀（词尾）；4-前置（在xxx之前）；5-后置（在xxx之后）',
    `match_params` VARCHAR(200) NULL COMMENT 'match_way为4/5时，需要xxx参数，如果存在多个，使用分号连接',
    `transliteration` VARCHAR(200) NULL COMMENT '音译。音译和意译至少有一个',
    `free_translation` VARCHAR(200) NULL COMMENT '意译。音译和意译至少有一个',
    PRIMARY KEY (`id`),
    KEY `i_ct_common_0` (`standard_id`)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `transliteration` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `standard_id` BIGINT(20) NOT NULL DEFAULT -1,
    `original` VARCHAR(20) NOT NULL DEFAULT '' COMMENT '辅音、元音、辅音+元音',
    `roman` VARCHAR(20) NULL COMMENT '罗马转写',
    `match_way` CHAR(1) NOT NULL DEFAULT '1' COMMENT '匹配方式：1-精确；2-前缀（词头）；3-后缀（词尾）；4-前置（在xxx之前）；5-后置（在xxx之后）',
    `match_params` VARCHAR(20) NULL COMMENT 'match_way为4/5时，需要xxx参数，如果存在多个，使用分号连接',
    `chinese` VARCHAR(10) NOT NULL DEFAULT '' COMMENT '汉字，同音异形字使用括号括起来',
    PRIMARY KEY (`id`),
    KEY `i_ct_transliteration_0` (`standard_id`)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `project` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `standard_id` BIGINT(20) NOT NULL DEFAULT -1,
    `project_name` VARCHAR(100) NOT NULL DEFAULT '',
    `start_date` VARCHAR(10) NOT NULL DEFAULT '',
    `due_date` VARCHAR(10) NOT NULL DEFAULT '',
    `status` CHAR(1) NOT NULL DEFAULT '1' COMMENT '1-执行中；9-已结束',
    `memo` VARCHAR(500) NULL,
    PRIMARY KEY (`id`)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `place_name` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `project_id` BIGINT(20) NOT NULL DEFAULT -1,
    `original` VARCHAR(200) NOT NULL DEFAULT '' COMMENT '导入数据-外文地名',
    `country` VARCHAR(100) NULL COMMENT '导入数据-国别',
    `language` VARCHAR(100) NULL COMMENT '导入数据-语种',
    `gec` VARCHAR(100) NULL COMMENT '导入数据-地理实体类别',
    `memo` VARCHAR(500) NULL COMMENT '导入数据-备注',
    `roman_status` CHAR(1) NOT NULL DEFAULT '0' COMMENT '罗马转写状态：0-未执行；1-已执行；9-未涉及',
    `roman` VARCHAR(200) NULL COMMENT '罗马字母转写。可以是导入数据，也可以是系统转写，也可为空',
    `trans_status` CHAR(1) NOT NULL DEFAULT '0' COMMENT '自动翻译状态：0-未执行；1-已翻译',
    `transliteration` VARCHAR(200) NULL COMMENT '音译。音译和意译至少有一个',
    `free_translation` VARCHAR(200) NULL COMMENT '意译。音译和意译至少有一个',
    `emit_standard` VARCHAR(1000) NULL COMMENT '翻译过程中触发的译写标准规则',
    `trans_result` VARCHAR(200) NULL COMMENT '经过人工校对的最终结果',
    PRIMARY KEY (`id`),
    KEY `i_place_name_0` (`project_id`)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8MB4;
