# ************************************************************
# Sequel Ace SQL dump
# 版本号： 20050
#
# https://sequel-ace.com/
# https://github.com/Sequel-Ace/Sequel-Ace
#
# 主机: 127.0.0.1 (MySQL 5.6.39)
# 数据库: aipintuan_mall
# 生成时间: 2025-02-06 09:26:46 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE='NO_AUTO_VALUE_ON_ZERO', SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE database if NOT EXISTS `aipintuan_mall` default character set utf8mb4 ;
use `aipintuan_mall`;

# 转储表 pay_order
# ------------------------------------------------------------

DROP TABLE IF EXISTS `pay_order`;

CREATE TABLE `pay_order` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `product_id` varchar(16) NOT NULL COMMENT '商品ID',
  `product_name` varchar(64) NOT NULL COMMENT '商品名称',
  `order_id` varchar(16) NOT NULL COMMENT '订单ID',
  `order_time` datetime NOT NULL COMMENT '下单时间',
  `total_amount` decimal(8,2) unsigned DEFAULT NULL COMMENT '订单金额',
  `status` varchar(32) NOT NULL COMMENT '订单状态；create-创建完成、pay_wait-等待支付、pay_success-支付成功、deal_done-交易完成、close-订单关单',
  `pay_url` varchar(2014) DEFAULT NULL COMMENT '支付信息',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `market_type` tinyint(1) DEFAULT NULL COMMENT '营销类型；0无营销、1拼团营销',
  `market_deduction_amount` decimal(8,2) DEFAULT NULL COMMENT '营销金额；优惠金额',
  `pay_amount` decimal(8,2) NOT NULL COMMENT '支付金额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  KEY `idx_user_id_product_id` (`user_id`,`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Payment records intentionally omitted from the public demo dataset.


# 爱拼团商品目录
# ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS `product_catalog` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `product_id` varchar(16) NOT NULL,
  `product_name` varchar(128) NOT NULL,
  `product_desc` varchar(256) NOT NULL,
  `category` varchar(32) NOT NULL,
  `activity_id` bigint NOT NULL,
  `original_price` decimal(10,2) NOT NULL,
  `group_price` decimal(10,2) NOT NULL,
  `image_url` varchar(256) NOT NULL,
  `badge` varchar(32) DEFAULT NULL,
  `participant_count` int NOT NULL DEFAULT 0,
  `status` tinyint(1) NOT NULL DEFAULT 1,
  `sort_order` int NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_product_id` (`product_id`),
  KEY `idx_category_status` (`category`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='爱拼团商品目录';

INSERT INTO `product_catalog`
(`product_id`,`product_name`,`product_desc`,`category`,`activity_id`,`original_price`,`group_price`,`image_url`,`badge`,`participant_count`,`status`,`sort_order`)
VALUES
('9890001','手写MyBatis：渐进式源码实践（全彩）','从零实现 MyBatis 核心原理，适合 Java 开发者系统进阶','图书',100123,100.00,80.00,'images/catalog/mybatis-book.svg','限时直降',238,1,1),
('9890002','星河 K87 三模机械键盘','客制化手感，蓝牙、2.4G 与有线三模连接','数码',100124,499.00,429.00,'images/catalog/keyboard.svg','人气爆款',126,1,2),
('9890003','云听 Pro 主动降噪耳机','沉浸式主动降噪，40 小时长续航','数码',100125,699.00,599.00,'images/catalog/headphones.svg','今日特惠',89,1,3),
('9890004','智光护眼桌面台灯','无频闪全光谱照明，支持亮度色温调节','家居',100126,259.00,219.00,'images/catalog/lamp.svg','护眼优选',174,1,4),
('9890005','山野精品挂耳咖啡礼盒','四种产区风味，随时享受现磨般香气','食品',100127,168.00,138.00,'images/catalog/coffee.svg','回购好物',316,1,5),
('9890006','逐风轻量缓震跑鞋','轻量回弹中底，适合日常慢跑与通勤','运动',100128,569.00,499.00,'images/catalog/running-shoes.svg','运动热卖',207,1,6),
('9890007','清润修护水乳护肤套装','舒缓保湿配方，改善干燥并强化肌肤屏障','美妆',100129,329.00,279.00,'images/catalog/skincare.svg','口碑套装',152,1,7),
('9890008','城市通勤多功能双肩包','独立电脑仓与防泼水面料，轻松收纳出行','百货',100130,299.00,249.00,'images/catalog/backpack.svg','通勤必备',98,1,8);



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

