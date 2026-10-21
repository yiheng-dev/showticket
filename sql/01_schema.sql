-- 演出票务平台数据库初始化脚本（仅表结构）
-- 执行顺序：01_schema.sql -> 02_seed_user_address.sql -> 03_seed_shows.sql

-- 演出票务平台初始化脚本
-- 注意：下面的 DROP TABLE 仅作用于新建的 ticket_* 库，不会影响原有 hm-* 库
SET NAMES utf8mb4;

-- ============================================================
-- 演出库
-- ============================================================
CREATE DATABASE IF NOT EXISTS `ticket_show` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `ticket_show`;

DROP TABLE IF EXISTS `show_info`;
CREATE TABLE `show_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '演出id',
  `name` VARCHAR(200) NOT NULL COMMENT '演出名称',
  `price` INT NOT NULL COMMENT '票价（分）',
  `stock` INT NOT NULL DEFAULT 0 COMMENT '余票数量',
  `poster` VARCHAR(500) DEFAULT NULL COMMENT '演出海报',
  `genre` VARCHAR(64) DEFAULT NULL COMMENT '演出类型',
  `venue` VARCHAR(128) DEFAULT NULL COMMENT '演出场馆',
  `ticket_type` VARCHAR(64) DEFAULT NULL COMMENT '票档',
  `show_time` DATETIME DEFAULT NULL COMMENT '场次时间',
  `sold` INT NOT NULL DEFAULT 0 COMMENT '已售数量',
  `review_count` INT NOT NULL DEFAULT 0 COMMENT '评价数',
  `is_featured` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否推荐位',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态：1上架 2下架 3删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `creater` BIGINT DEFAULT NULL,
  `updater` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_genre` (`genre`),
  KEY `idx_venue` (`venue`),
  KEY `idx_show_time` (`show_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='演出票档';

CREATE TABLE IF NOT EXISTS `undo_log` (
  `branch_id` BIGINT NOT NULL COMMENT 'branch transaction id',
  `xid` VARCHAR(100) NOT NULL COMMENT 'global transaction id',
  `context` VARCHAR(128) NOT NULL COMMENT 'undo_log context',
  `rollback_info` LONGBLOB NOT NULL COMMENT 'rollback info',
  `log_status` INT NOT NULL COMMENT '0:normal status,1:defense status',
  `log_created` DATETIME(6) NOT NULL COMMENT 'create datetime',
  `log_modified` DATETIME(6) NOT NULL COMMENT 'modify datetime',
  `ext` VARCHAR(100) DEFAULT NULL COMMENT 'reserved field',
  UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AT transaction mode undo table';



-- ============================================================
-- 选票清单库
-- ============================================================
CREATE DATABASE IF NOT EXISTS `ticket_selection` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `ticket_selection`;

DROP TABLE IF EXISTS `ticket_selection`;
CREATE TABLE `ticket_selection` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '选票清单条目id',
  `user_id` BIGINT NOT NULL COMMENT '用户id',
  `show_id` BIGINT NOT NULL COMMENT '演出id',
  `num` INT NOT NULL DEFAULT 1 COMMENT '购买数量',
  `name` VARCHAR(200) DEFAULT NULL COMMENT '演出名称',
  `ticket_type` VARCHAR(64) DEFAULT NULL COMMENT '票档',
  `price` INT DEFAULT NULL COMMENT '价格（分）',
  `poster` VARCHAR(500) DEFAULT NULL COMMENT '演出海报',
  `show_time` DATETIME DEFAULT NULL COMMENT '场次时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_show` (`user_id`, `show_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选票清单';

CREATE TABLE IF NOT EXISTS `undo_log` (
  `branch_id` BIGINT NOT NULL,
  `xid` VARCHAR(100) NOT NULL,
  `context` VARCHAR(128) NOT NULL,
  `rollback_info` LONGBLOB NOT NULL,
  `log_status` INT NOT NULL,
  `log_created` DATETIME(6) NOT NULL,
  `log_modified` DATETIME(6) NOT NULL,
  `ext` VARCHAR(100) DEFAULT NULL,
  UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AT transaction mode undo table';

-- ============================================================
-- 订单库
-- ============================================================
CREATE DATABASE IF NOT EXISTS `ticket_order` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `ticket_order`;

DROP TABLE IF EXISTS `ticket_order`;
CREATE TABLE `ticket_order` (
  `id` BIGINT NOT NULL COMMENT '订单id',
  `total_fee` INT NOT NULL COMMENT '总金额（分）',
  `payment_type` INT DEFAULT NULL COMMENT '支付类型',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户id',
  `status` INT NOT NULL DEFAULT 1 COMMENT '1待支付 2已支付待出票 3已出票 4已完成 5已关闭 6已评价',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `issue_time` DATETIME DEFAULT NULL COMMENT '出票时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '完成时间',
  `close_time` DATETIME DEFAULT NULL COMMENT '关闭时间',
  `review_time` DATETIME DEFAULT NULL COMMENT '评价时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_status` (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购票订单';

DROP TABLE IF EXISTS `order_ticket`;
CREATE TABLE `order_ticket` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单票明细id',
  `order_id` BIGINT NOT NULL COMMENT '订单id',
  `show_id` BIGINT NOT NULL COMMENT '演出id',
  `num` INT NOT NULL COMMENT '购票数量',
  `name` VARCHAR(200) DEFAULT NULL COMMENT '演出名称',
  `ticket_type` VARCHAR(64) DEFAULT NULL COMMENT '票档',
  `price` INT DEFAULT NULL COMMENT '价格（分）',
  `poster` VARCHAR(500) DEFAULT NULL COMMENT '演出海报',
  `show_time` DATETIME DEFAULT NULL COMMENT '场次时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_show_id` (`show_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单票明细';

DROP TABLE IF EXISTS `ticket_delivery`;
CREATE TABLE `ticket_delivery` (
  `order_id` BIGINT NOT NULL COMMENT '订单id',
  `pickup_code` VARCHAR(64) DEFAULT NULL COMMENT '取票码',
  `delivery_type` VARCHAR(32) DEFAULT NULL COMMENT '取票方式',
  `contact` VARCHAR(64) DEFAULT NULL COMMENT '联系人',
  `mobile` VARCHAR(32) DEFAULT NULL COMMENT '联系人手机号',
  `province` VARCHAR(64) DEFAULT NULL,
  `city` VARCHAR(64) DEFAULT NULL,
  `town` VARCHAR(64) DEFAULT NULL,
  `street` VARCHAR(255) DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出票信息';

CREATE TABLE IF NOT EXISTS `undo_log` (
  `branch_id` BIGINT NOT NULL,
  `xid` VARCHAR(100) NOT NULL,
  `context` VARCHAR(128) NOT NULL,
  `rollback_info` LONGBLOB NOT NULL,
  `log_status` INT NOT NULL,
  `log_created` DATETIME(6) NOT NULL,
  `log_modified` DATETIME(6) NOT NULL,
  `ext` VARCHAR(100) DEFAULT NULL,
  UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AT transaction mode undo table';

-- ============================================================
-- 支付库
-- ============================================================
CREATE DATABASE IF NOT EXISTS `ticket_pay` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `ticket_pay`;

DROP TABLE IF EXISTS `pay_order`;
CREATE TABLE `pay_order` (
  `id` BIGINT NOT NULL COMMENT '支付单id',
  `biz_order_no` BIGINT DEFAULT NULL COMMENT '业务订单号',
  `pay_order_no` BIGINT DEFAULT NULL COMMENT '支付单号',
  `biz_user_id` BIGINT DEFAULT NULL COMMENT '支付用户id',
  `pay_channel_code` VARCHAR(32) DEFAULT NULL COMMENT '支付渠道编码',
  `amount` INT DEFAULT NULL COMMENT '支付金额（分）',
  `pay_type` INT DEFAULT NULL COMMENT '支付类型',
  `status` INT DEFAULT NULL COMMENT '0待提交 1待支付 2已关闭 3支付成功',
  `expand_json` VARCHAR(1000) DEFAULT NULL COMMENT '扩展字段',
  `result_code` VARCHAR(64) DEFAULT NULL COMMENT '第三方返回业务码',
  `result_msg` VARCHAR(255) DEFAULT NULL COMMENT '第三方返回提示信息',
  `pay_success_time` DATETIME DEFAULT NULL COMMENT '支付成功时间',
  `pay_over_time` DATETIME DEFAULT NULL COMMENT '支付超时时间',
  `qr_code_url` VARCHAR(500) DEFAULT NULL COMMENT '支付二维码链接',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `creater` BIGINT DEFAULT NULL,
  `updater` BIGINT DEFAULT NULL,
  `is_delete` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_biz_order_no` (`biz_order_no`),
  KEY `idx_biz_user` (`biz_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付单';

CREATE TABLE IF NOT EXISTS `undo_log` (
  `branch_id` BIGINT NOT NULL,
  `xid` VARCHAR(100) NOT NULL,
  `context` VARCHAR(128) NOT NULL,
  `rollback_info` LONGBLOB NOT NULL,
  `log_status` INT NOT NULL,
  `log_created` DATETIME(6) NOT NULL,
  `log_modified` DATETIME(6) NOT NULL,
  `ext` VARCHAR(100) DEFAULT NULL,
  UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AT transaction mode undo table';

-- ============================================================
-- 用户库
-- ============================================================
CREATE DATABASE IF NOT EXISTS `ticket_user` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `ticket_user`;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(64) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT 'BCrypt 密码',
  `phone` VARCHAR(32) DEFAULT NULL COMMENT '手机号',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` INT NOT NULL DEFAULT 1 COMMENT '0冻结 1正常',
  `balance` INT NOT NULL DEFAULT 0 COMMENT '账户余额（分）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户';

DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `province` VARCHAR(64) DEFAULT NULL,
  `city` VARCHAR(64) DEFAULT NULL,
  `town` VARCHAR(64) DEFAULT NULL,
  `mobile` VARCHAR(32) DEFAULT NULL,
  `street` VARCHAR(255) DEFAULT NULL,
  `contact` VARCHAR(64) DEFAULT NULL,
  `is_default` INT DEFAULT 0 COMMENT '是否默认 1是 0否',
  `notes` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收票地址';

CREATE TABLE IF NOT EXISTS `undo_log` (
  `branch_id` BIGINT NOT NULL,
  `xid` VARCHAR(100) NOT NULL,
  `context` VARCHAR(128) NOT NULL,
  `rollback_info` LONGBLOB NOT NULL,
  `log_status` INT NOT NULL,
  `log_created` DATETIME(6) NOT NULL,
  `log_modified` DATETIME(6) NOT NULL,
  `ext` VARCHAR(100) DEFAULT NULL,
  UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AT transaction mode undo table';
