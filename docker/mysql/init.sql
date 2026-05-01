DROP DATABASE IF EXISTS course_share;
CREATE DATABASE course_share DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE course_share;

CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '登录账号',
  `password` VARCHAR(100) NOT NULL COMMENT '加密密码',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `role` VARCHAR(20) NOT NULL DEFAULT 'STUDENT' COMMENT '角色：STUDENT/REVIEWER/ADMIN',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE `course_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(100) NOT NULL COMMENT '分类名称/课程名称，如操作系统、计算机网络、软件工程',
  `type` VARCHAR(50) DEFAULT NULL COMMENT '课程类型：公共基础课/专业核心课/选修课',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序号',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_category_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程分类表';

CREATE TABLE `tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` VARCHAR(50) NOT NULL COMMENT '标签名，如大二、期末复习、试卷、实验指导',
  `type` VARCHAR(20) NOT NULL COMMENT '标签类型：GRADE/TYPE/SCENE',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tag_name_type` (`name`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

CREATE TABLE `material` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '资料ID',
  `title` VARCHAR(200) NOT NULL COMMENT '资料标题',
  `description` TEXT DEFAULT NULL COMMENT '资料简介',
  `category_id` BIGINT NOT NULL COMMENT '课程分类ID',
  `file_url` VARCHAR(500) NOT NULL COMMENT 'OSS/MinIO文件访问地址',
  `file_key` VARCHAR(255) NOT NULL COMMENT 'OSS/MinIO对象Key，用于删除或管理文件',
  `original_filename` VARCHAR(255) NOT NULL COMMENT '原始文件名',
  `file_type` VARCHAR(20) NOT NULL COMMENT '文件类型：PDF/DOC/DOCX/ZIP',
  `file_size` BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小，单位字节',
  `uploader_id` BIGINT NOT NULL COMMENT '上传人ID，仅学生可上传',
  `audit_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING/APPROVED/REJECTED',
  `auditor_id` BIGINT DEFAULT NULL COMMENT '人工审核人ID，AI自动审核时为空',
  `audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '审核意见，可存AI审核结论',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `download_count` INT NOT NULL DEFAULT 0 COMMENT '下载次数',
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `favorite_count` INT NOT NULL DEFAULT 0 COMMENT '收藏数',
  `is_top` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶：0否 1是',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 0下架/删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_material_category_id` (`category_id`),
  KEY `idx_material_uploader_id` (`uploader_id`),
  KEY `idx_material_audit_status` (`audit_status`),
  KEY `idx_material_status_create_time` (`status`, `create_time`),
  KEY `idx_material_like_count` (`like_count`),
  KEY `idx_material_favorite_count` (`favorite_count`),
  KEY `idx_material_hot` (`audit_status`, `status`, `like_count`, `favorite_count`),
  CONSTRAINT `fk_material_category` FOREIGN KEY (`category_id`) REFERENCES `course_category` (`id`),
  CONSTRAINT `fk_material_uploader` FOREIGN KEY (`uploader_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_material_auditor` FOREIGN KEY (`auditor_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资料表';

CREATE TABLE `material_tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `material_id` BIGINT NOT NULL COMMENT '资料ID',
  `tag_id` BIGINT NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_material_tag` (`material_id`, `tag_id`),
  KEY `idx_material_tag_tag_id` (`tag_id`),
  CONSTRAINT `fk_material_tag_material` FOREIGN KEY (`material_id`) REFERENCES `material` (`id`),
  CONSTRAINT `fk_material_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资料-标签关联表';

CREATE TABLE `material_favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID，仅学生可收藏',
  `material_id` BIGINT NOT NULL COMMENT '资料ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_favorite_user_material` (`user_id`, `material_id`),
  KEY `idx_favorite_material_id` (`material_id`),
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_favorite_material` FOREIGN KEY (`material_id`) REFERENCES `material` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资料收藏表';

CREATE TABLE `download_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '下载记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `material_id` BIGINT NOT NULL COMMENT '资料ID',
  `ip` VARCHAR(50) DEFAULT NULL COMMENT '下载IP',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下载时间',
  PRIMARY KEY (`id`),
  KEY `idx_download_user_id` (`user_id`),
  KEY `idx_download_material_id` (`material_id`),
  KEY `idx_download_create_time` (`create_time`),
  CONSTRAINT `fk_download_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_download_material` FOREIGN KEY (`material_id`) REFERENCES `material` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='下载记录表';

CREATE TABLE `question` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '问题ID',
  `title` VARCHAR(200) NOT NULL COMMENT '问题标题',
  `content` TEXT NOT NULL COMMENT '问题内容',
  `category_id` BIGINT NOT NULL COMMENT '所属课程分类ID',
  `user_id` BIGINT NOT NULL COMMENT '提问人ID，仅学生可提问',
  `audit_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING/APPROVED/REJECTED',
  `auditor_id` BIGINT DEFAULT NULL COMMENT '人工审核人ID，AI自动审核时为空',
  `audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '审核意见，可存AI审核结论',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `answer_count` INT NOT NULL DEFAULT 0 COMMENT '回答数',
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 0下架/删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_question_user_id` (`user_id`),
  KEY `idx_question_category_id` (`category_id`),
  KEY `idx_question_audit_status` (`audit_status`),
  KEY `idx_question_status_create_time` (`status`, `create_time`),
  KEY `idx_question_like_count` (`like_count`),
  CONSTRAINT `fk_question_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_question_category` FOREIGN KEY (`category_id`) REFERENCES `course_category` (`id`),
  CONSTRAINT `fk_question_auditor` FOREIGN KEY (`auditor_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问题表';

CREATE TABLE `answer` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '回答ID',
  `question_id` BIGINT NOT NULL COMMENT '所属问题ID',
  `content` TEXT NOT NULL COMMENT '回答内容',
  `user_id` BIGINT NOT NULL COMMENT '回答人ID，仅学生可回答',
  `audit_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING/APPROVED/REJECTED',
  `auditor_id` BIGINT DEFAULT NULL COMMENT '人工审核人ID，AI自动审核时为空',
  `audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '审核意见，可存AI审核结论',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `reply_count` INT NOT NULL DEFAULT 0 COMMENT '回复数',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 0下架/删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_answer_question_id` (`question_id`),
  KEY `idx_answer_user_id` (`user_id`),
  KEY `idx_answer_audit_status` (`audit_status`),
  KEY `idx_answer_status_create_time` (`status`, `create_time`),
  KEY `idx_answer_like_count` (`like_count`),
  CONSTRAINT `fk_answer_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`),
  CONSTRAINT `fk_answer_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_answer_auditor` FOREIGN KEY (`auditor_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回答表';

CREATE TABLE `answer_reply` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '回复ID',
  `answer_id` BIGINT NOT NULL COMMENT '所属回答ID',
  `user_id` BIGINT NOT NULL COMMENT '回复人ID，仅学生可回复',
  `reply_to_user_id` BIGINT DEFAULT NULL COMMENT '被回复用户ID',
  `content` TEXT NOT NULL COMMENT '回复内容',
  `audit_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING/APPROVED/REJECTED',
  `auditor_id` BIGINT DEFAULT NULL COMMENT '人工审核人ID，AI自动审核时为空',
  `audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '审核意见，可存AI审核结论',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 0下架/删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_reply_answer_id` (`answer_id`),
  KEY `idx_reply_user_id` (`user_id`),
  KEY `idx_reply_to_user_id` (`reply_to_user_id`),
  KEY `idx_reply_audit_status` (`audit_status`),
  KEY `idx_reply_status_create_time` (`status`, `create_time`),
  CONSTRAINT `fk_reply_answer` FOREIGN KEY (`answer_id`) REFERENCES `answer` (`id`),
  CONSTRAINT `fk_reply_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_reply_to_user` FOREIGN KEY (`reply_to_user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_reply_auditor` FOREIGN KEY (`auditor_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回答回复表';

CREATE TABLE `like_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `user_id` BIGINT NOT NULL COMMENT '点赞用户ID，仅学生可点赞',
  `target_type` VARCHAR(20) NOT NULL COMMENT '点赞对象类型：MATERIAL/QUESTION/ANSWER/REPLY',
  `target_id` BIGINT NOT NULL COMMENT '点赞对象ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_like_user_target` (`user_id`, `target_type`, `target_id`),
  KEY `idx_like_target` (`target_type`, `target_id`),
  KEY `idx_like_user_id` (`user_id`),
  CONSTRAINT `fk_like_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞记录表';

CREATE TABLE `report` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '举报ID',
  `target_type` VARCHAR(20) NOT NULL COMMENT '举报对象类型：MATERIAL/QUESTION/ANSWER/REPLY',
  `target_id` BIGINT NOT NULL COMMENT '举报对象ID',
  `target_snapshot` VARCHAR(500) DEFAULT NULL COMMENT '被举报内容快照',
  `report_user_id` BIGINT NOT NULL COMMENT '举报人ID',
  `reason` VARCHAR(500) NOT NULL COMMENT '举报原因',
  `handle_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '处理状态：PENDING/RESOLVED/REJECTED',
  `handle_user_id` BIGINT DEFAULT NULL COMMENT '处理人ID',
  `handle_result` VARCHAR(255) DEFAULT NULL COMMENT '处理结果说明',
  `handle_time` DATETIME DEFAULT NULL COMMENT '处理时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
  PRIMARY KEY (`id`),
  KEY `idx_report_target` (`target_type`, `target_id`),
  KEY `idx_report_user_id` (`report_user_id`),
  KEY `idx_report_handle_status` (`handle_status`),
  CONSTRAINT `fk_report_user` FOREIGN KEY (`report_user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_report_handler` FOREIGN KEY (`handle_user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='举报表';

CREATE TABLE `ai_audit_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'AI审核记录ID',
  `target_type` VARCHAR(20) NOT NULL COMMENT '审核对象类型：MATERIAL/QUESTION/ANSWER/REPLY',
  `target_id` BIGINT NOT NULL COMMENT '审核对象ID',
  `audit_result` VARCHAR(20) NOT NULL COMMENT 'AI审核结果：PASS/REJECT/RISK',
  `risk_score` DECIMAL(5,2) DEFAULT NULL COMMENT '风险分数，0-100',
  `reason` VARCHAR(500) DEFAULT NULL COMMENT 'AI审核原因',
  `model_name` VARCHAR(100) DEFAULT NULL COMMENT 'AI模型名称',
  `request_content` TEXT DEFAULT NULL COMMENT '送审内容摘要',
  `response_content` TEXT DEFAULT NULL COMMENT 'AI原始返回结果',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_ai_audit_target` (`target_type`, `target_id`),
  KEY `idx_ai_audit_result` (`audit_result`),
  KEY `idx_ai_audit_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI审核记录表';

INSERT INTO `course_category` (`name`, `type`, `sort_no`) VALUES
('操作系统', '专业核心课', 1),
('计算机网络', '专业核心课', 2),
('数据结构', '专业核心课', 3),
('数据库原理', '专业核心课', 4),
('软件工程', '专业核心课', 5),
('Java程序设计', '专业课', 6),
('Web开发技术', '专业课', 7),
('算法设计与分析', '专业核心课', 8);

INSERT INTO `tag` (`name`, `type`) VALUES
('大一', 'GRADE'),
('大二', 'GRADE'),
('大三', 'GRADE'),
('大四', 'GRADE'),
('试卷', 'TYPE'),
('课件', 'TYPE'),
('笔记', 'TYPE'),
('实验指导', 'TYPE'),
('复习资料', 'TYPE'),
('作业答案', 'TYPE'),
('项目代码', 'TYPE'),
('期末复习', 'SCENE'),
('考研', 'SCENE'),
('课程设计', 'SCENE'),
('平时作业', 'SCENE');

INSERT INTO `sys_user` (`username`, `password`, `nickname`, `role`) VALUES
('admin', '123456', '系统管理员', 'ADMIN'),
('reviewer', '123456', '举报审核员', 'REVIEWER'),
('student', '123456', '测试学生', 'STUDENT');
