-- 演出票务平台测试用户与收票地址
SET NAMES utf8mb4;
USE `ticket_user`;

-- 测试用户：tester / 123456，余额 10000.00 元
DELETE FROM `address` WHERE `user_id` = 1;
DELETE FROM `user` WHERE `id` = 1;

INSERT INTO `user` (`id`, `username`, `password`, `phone`, `status`, `balance`)
VALUES (1, 'tester', '$2b$10$i2.ansdpIRUhrnMksqamT.8THwyPzZeyl..AYlOeaeeJwWc4R0lAK', '13800000000', 1, 1000000);

INSERT INTO `address` (`id`, `user_id`, `province`, `city`, `town`, `mobile`, `street`, `contact`, `is_default`, `notes`)
VALUES (1, 1, '上海市', '上海市', '黄浦区', '13800000000', '人民大道300号', '张三', 1, '默认收票地址');
