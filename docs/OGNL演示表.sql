CREATE TABLE `ognl_demo` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
  `status` tinyint unsigned NOT NULL COMMENT '状态，0=关闭，1=开启',
  `create_time` int unsigned NOT NULL COMMENT '创建时间',
  `update_time` int unsigned NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OGNL 动态 SQL 演示表';
