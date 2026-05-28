SET NAMES utf8mb4;

DROP DATABASE IF EXISTS course_share;
CREATE DATABASE course_share DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE course_share;

CREATE TABLE sys_user (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'user id',
  username VARCHAR(50) NOT NULL COMMENT 'login username',
  password VARCHAR(100) NOT NULL COMMENT 'BCrypt password',
  nickname VARCHAR(50) DEFAULT NULL COMMENT 'display name',
  role VARCHAR(20) NOT NULL DEFAULT 'STUDENT' COMMENT 'STUDENT/REVIEWER/ADMIN',
  phone VARCHAR(20) DEFAULT NULL COMMENT 'phone number',
  email VARCHAR(100) DEFAULT NULL COMMENT 'email',
  avatar VARCHAR(500) DEFAULT NULL COMMENT 'avatar url',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1 enabled, 0 disabled',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated time',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='users';

CREATE TABLE course_category (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'category id',
  name VARCHAR(100) NOT NULL COMMENT 'course name',
  type VARCHAR(50) DEFAULT NULL COMMENT 'course type',
  sort_no INT NOT NULL DEFAULT 0 COMMENT 'sort number',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1 enabled, 0 disabled',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated time',
  PRIMARY KEY (id),
  UNIQUE KEY uk_course_category_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='course categories';

CREATE TABLE tag (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'tag id',
  name VARCHAR(50) NOT NULL COMMENT 'tag name',
  type VARCHAR(20) NOT NULL COMMENT 'GRADE/TYPE/SCENE',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1 enabled, 0 disabled',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated time',
  PRIMARY KEY (id),
  UNIQUE KEY uk_tag_name_type (name, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='tags';

CREATE TABLE material (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'material id',
  title VARCHAR(200) NOT NULL COMMENT 'title',
  description TEXT DEFAULT NULL COMMENT 'description',
  category_id BIGINT NOT NULL COMMENT 'category id',
  file_url VARCHAR(500) NOT NULL COMMENT 'file url',
  file_key VARCHAR(255) NOT NULL COMMENT 'file object key',
  original_filename VARCHAR(255) NOT NULL COMMENT 'original file name',
  file_type VARCHAR(20) NOT NULL COMMENT 'PDF/DOC/DOCX/ZIP',
  file_size BIGINT NOT NULL DEFAULT 0 COMMENT 'file size bytes',
  uploader_id BIGINT NOT NULL COMMENT 'uploader id',
  audit_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED',
  auditor_id BIGINT DEFAULT NULL COMMENT 'manual auditor id',
  audit_remark VARCHAR(255) DEFAULT NULL COMMENT 'audit remark',
  audit_time DATETIME DEFAULT NULL COMMENT 'audit time',
  view_count INT NOT NULL DEFAULT 0 COMMENT 'view count',
  download_count INT NOT NULL DEFAULT 0 COMMENT 'download count',
  like_count INT NOT NULL DEFAULT 0 COMMENT 'like count',
  favorite_count INT NOT NULL DEFAULT 0 COMMENT 'favorite count',
  is_top TINYINT NOT NULL DEFAULT 0 COMMENT '0 no, 1 yes',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1 normal, 0 removed',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated time',
  PRIMARY KEY (id),
  KEY idx_material_category_id (category_id),
  KEY idx_material_uploader_id (uploader_id),
  KEY idx_material_audit_status (audit_status),
  KEY idx_material_status_create_time (status, create_time),
  KEY idx_material_like_count (like_count),
  KEY idx_material_favorite_count (favorite_count),
  KEY idx_material_hot (audit_status, status, like_count, favorite_count),
  CONSTRAINT fk_material_category FOREIGN KEY (category_id) REFERENCES course_category (id),
  CONSTRAINT fk_material_uploader FOREIGN KEY (uploader_id) REFERENCES sys_user (id),
  CONSTRAINT fk_material_auditor FOREIGN KEY (auditor_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='materials';

CREATE TABLE material_tag (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'id',
  material_id BIGINT NOT NULL COMMENT 'material id',
  tag_id BIGINT NOT NULL COMMENT 'tag id',
  PRIMARY KEY (id),
  UNIQUE KEY uk_material_tag (material_id, tag_id),
  KEY idx_material_tag_tag_id (tag_id),
  CONSTRAINT fk_material_tag_material FOREIGN KEY (material_id) REFERENCES material (id),
  CONSTRAINT fk_material_tag_tag FOREIGN KEY (tag_id) REFERENCES tag (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='material tags';

CREATE TABLE material_favorite (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'favorite id',
  user_id BIGINT NOT NULL COMMENT 'user id',
  material_id BIGINT NOT NULL COMMENT 'material id',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
  PRIMARY KEY (id),
  UNIQUE KEY uk_favorite_user_material (user_id, material_id),
  KEY idx_favorite_material_id (material_id),
  CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES sys_user (id),
  CONSTRAINT fk_favorite_material FOREIGN KEY (material_id) REFERENCES material (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='material favorites';

CREATE TABLE download_record (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'download id',
  user_id BIGINT NOT NULL COMMENT 'user id',
  material_id BIGINT NOT NULL COMMENT 'material id',
  ip VARCHAR(50) DEFAULT NULL COMMENT 'download ip',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
  PRIMARY KEY (id),
  KEY idx_download_user_id (user_id),
  KEY idx_download_material_id (material_id),
  KEY idx_download_create_time (create_time),
  CONSTRAINT fk_download_user FOREIGN KEY (user_id) REFERENCES sys_user (id),
  CONSTRAINT fk_download_material FOREIGN KEY (material_id) REFERENCES material (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='download records';

CREATE TABLE question (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'question id',
  title VARCHAR(200) NOT NULL COMMENT 'title',
  content TEXT NOT NULL COMMENT 'content',
  category_id BIGINT NOT NULL COMMENT 'category id',
  user_id BIGINT NOT NULL COMMENT 'user id',
  audit_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED',
  auditor_id BIGINT DEFAULT NULL COMMENT 'manual auditor id',
  audit_remark VARCHAR(255) DEFAULT NULL COMMENT 'audit remark',
  audit_time DATETIME DEFAULT NULL COMMENT 'audit time',
  view_count INT NOT NULL DEFAULT 0 COMMENT 'view count',
  answer_count INT NOT NULL DEFAULT 0 COMMENT 'answer count',
  like_count INT NOT NULL DEFAULT 0 COMMENT 'like count',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1 normal, 0 removed',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated time',
  PRIMARY KEY (id),
  KEY idx_question_user_id (user_id),
  KEY idx_question_category_id (category_id),
  KEY idx_question_audit_status (audit_status),
  KEY idx_question_status_create_time (status, create_time),
  KEY idx_question_like_count (like_count),
  CONSTRAINT fk_question_user FOREIGN KEY (user_id) REFERENCES sys_user (id),
  CONSTRAINT fk_question_category FOREIGN KEY (category_id) REFERENCES course_category (id),
  CONSTRAINT fk_question_auditor FOREIGN KEY (auditor_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='questions';

CREATE TABLE answer (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'answer id',
  question_id BIGINT NOT NULL COMMENT 'question id',
  content TEXT NOT NULL COMMENT 'content',
  user_id BIGINT NOT NULL COMMENT 'user id',
  audit_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED',
  auditor_id BIGINT DEFAULT NULL COMMENT 'manual auditor id',
  audit_remark VARCHAR(255) DEFAULT NULL COMMENT 'audit remark',
  audit_time DATETIME DEFAULT NULL COMMENT 'audit time',
  like_count INT NOT NULL DEFAULT 0 COMMENT 'like count',
  reply_count INT NOT NULL DEFAULT 0 COMMENT 'reply count',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1 normal, 0 removed',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated time',
  PRIMARY KEY (id),
  KEY idx_answer_question_id (question_id),
  KEY idx_answer_user_id (user_id),
  KEY idx_answer_audit_status (audit_status),
  KEY idx_answer_status_create_time (status, create_time),
  KEY idx_answer_like_count (like_count),
  CONSTRAINT fk_answer_question FOREIGN KEY (question_id) REFERENCES question (id),
  CONSTRAINT fk_answer_user FOREIGN KEY (user_id) REFERENCES sys_user (id),
  CONSTRAINT fk_answer_auditor FOREIGN KEY (auditor_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='answers';

CREATE TABLE answer_reply (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'reply id',
  answer_id BIGINT NOT NULL COMMENT 'answer id',
  user_id BIGINT NOT NULL COMMENT 'user id',
  reply_to_user_id BIGINT DEFAULT NULL COMMENT 'reply target user id',
  content TEXT NOT NULL COMMENT 'content',
  audit_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED',
  auditor_id BIGINT DEFAULT NULL COMMENT 'manual auditor id',
  audit_remark VARCHAR(255) DEFAULT NULL COMMENT 'audit remark',
  audit_time DATETIME DEFAULT NULL COMMENT 'audit time',
  like_count INT NOT NULL DEFAULT 0 COMMENT 'like count',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1 normal, 0 removed',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated time',
  PRIMARY KEY (id),
  KEY idx_reply_answer_id (answer_id),
  KEY idx_reply_user_id (user_id),
  KEY idx_reply_to_user_id (reply_to_user_id),
  KEY idx_reply_audit_status (audit_status),
  KEY idx_reply_status_create_time (status, create_time),
  CONSTRAINT fk_reply_answer FOREIGN KEY (answer_id) REFERENCES answer (id),
  CONSTRAINT fk_reply_user FOREIGN KEY (user_id) REFERENCES sys_user (id),
  CONSTRAINT fk_reply_to_user FOREIGN KEY (reply_to_user_id) REFERENCES sys_user (id),
  CONSTRAINT fk_reply_auditor FOREIGN KEY (auditor_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='answer replies';

CREATE TABLE like_record (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'like id',
  user_id BIGINT NOT NULL COMMENT 'user id',
  target_type VARCHAR(20) NOT NULL COMMENT 'MATERIAL/QUESTION/ANSWER/REPLY',
  target_id BIGINT NOT NULL COMMENT 'target id',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
  PRIMARY KEY (id),
  UNIQUE KEY uk_like_user_target (user_id, target_type, target_id),
  KEY idx_like_target (target_type, target_id),
  KEY idx_like_user_id (user_id),
  CONSTRAINT fk_like_user FOREIGN KEY (user_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='like records';

CREATE TABLE report (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'report id',
  target_type VARCHAR(20) NOT NULL COMMENT 'MATERIAL/QUESTION/ANSWER/REPLY',
  target_id BIGINT NOT NULL COMMENT 'target id',
  target_snapshot VARCHAR(500) DEFAULT NULL COMMENT 'target snapshot',
  report_user_id BIGINT NOT NULL COMMENT 'report user id',
  reason VARCHAR(500) NOT NULL COMMENT 'reason',
  handle_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/RESOLVED/REJECTED',
  handle_user_id BIGINT DEFAULT NULL COMMENT 'handler id',
  handle_result VARCHAR(255) DEFAULT NULL COMMENT 'handle result',
  handle_time DATETIME DEFAULT NULL COMMENT 'handle time',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
  PRIMARY KEY (id),
  KEY idx_report_target (target_type, target_id),
  KEY idx_report_user_id (report_user_id),
  KEY idx_report_handle_status (handle_status),
  CONSTRAINT fk_report_user FOREIGN KEY (report_user_id) REFERENCES sys_user (id),
  CONSTRAINT fk_report_handler FOREIGN KEY (handle_user_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='reports';

CREATE TABLE ai_audit_record (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'audit record id',
  target_type VARCHAR(20) NOT NULL COMMENT 'MATERIAL/QUESTION/ANSWER/REPLY',
  target_id BIGINT NOT NULL COMMENT 'target id',
  audit_result VARCHAR(20) NOT NULL COMMENT 'PASS/REJECT/RISK',
  risk_score DECIMAL(5,2) DEFAULT NULL COMMENT 'risk score 0-100',
  reason VARCHAR(500) DEFAULT NULL COMMENT 'reason',
  model_name VARCHAR(100) DEFAULT NULL COMMENT 'model name',
  request_content TEXT DEFAULT NULL COMMENT 'request content',
  response_content TEXT DEFAULT NULL COMMENT 'response content',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
  PRIMARY KEY (id),
  KEY idx_ai_audit_target (target_type, target_id),
  KEY idx_ai_audit_result (audit_result),
  KEY idx_ai_audit_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ai audit records';

INSERT INTO course_category (name, type, sort_no) VALUES
('操作系统', '专业核心课', 1),
('计算机网络', '专业核心课', 2),
('数据结构', '专业核心课', 3),
('数据库原理', '专业核心课', 4),
('软件工程', '专业核心课', 5),
('Java 程序设计', '专业课', 6),
('Web 开发技术', '专业课', 7),
('算法设计与分析', '专业核心课', 8);

INSERT INTO tag (name, type) VALUES
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

INSERT INTO sys_user (username, password, nickname, role) VALUES
('admin', '$2a$10$c3Nop73ev22Jybv/J9ZhHOKgvKKJHc2.LSCJv6MmgBMFMqEXG9HNC', '系统管理员', 'ADMIN'),
('reviewer', '$2a$10$4kBwEdUMGFOlk13Kt9btKuUhmY6CvXniaaqEchwH54/Vzk3Nmc10.', '举报审核员', 'REVIEWER'),
('student', '$2a$10$pFlEa5KtuaDLZ2ZApaWMbeSUOWxoU9CVzf21aPUbj7lldH9N4G5Qy', '测试学生', 'STUDENT'),
('alice', '$2a$10$pFlEa5KtuaDLZ2ZApaWMbeSUOWxoU9CVzf21aPUbj7lldH9N4G5Qy', '资料整理员 Alice', 'STUDENT'),
('bob', '$2a$10$pFlEa5KtuaDLZ2ZApaWMbeSUOWxoU9CVzf21aPUbj7lldH9N4G5Qy', '问答达人 Bob', 'STUDENT');

INSERT INTO material (title, description, category_id, file_url, file_key, original_filename, file_type, file_size, uploader_id, audit_status, audit_remark, audit_time, view_count, download_count, like_count, favorite_count, status, create_time) VALUES
('软件工程课程设计验收模板', '覆盖需求分析、类图、顺序图、Docker 部署和答辩讲解，可直接作为课程设计验收参考。', 5, 'http://localhost:9000/course-material/materials/demo-software-engineering.pdf', 'materials/demo-software-engineering.pdf', 'software-engineering-demo.pdf', 'PDF', 42896, 4, 'APPROVED', '词库审核通过', NOW(), 86, 18, 24, 16, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('数据库原理期末复习提纲', '整理 ER 图、范式、事务隔离级别、索引和 SQL 优化常见题型。', 4, 'http://localhost:9000/course-material/materials/demo-database-review.pdf', 'materials/demo-database-review.pdf', 'database-review.pdf', 'PDF', 38920, 3, 'APPROVED', '词库审核通过', NOW(), 64, 14, 17, 11, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('计算机网络实验指导合集', '包含 Socket 编程、HTTP 抓包、DNS 查询和 TCP 三次握手实验记录模板。', 2, 'http://localhost:9000/course-material/materials/demo-network-lab.docx', 'materials/demo-network-lab.docx', 'network-lab-guide.docx', 'DOCX', 51200, 4, 'APPROVED', '词库审核通过', NOW(), 51, 9, 12, 8, 1, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('数据结构常见算法笔记', '覆盖链表、栈、队列、树、图、排序和动态规划的课堂笔记。', 3, 'http://localhost:9000/course-material/materials/demo-data-structure.pdf', 'materials/demo-data-structure.pdf', 'data-structure-notes.pdf', 'PDF', 35600, 5, 'APPROVED', '词库审核通过', NOW(), 73, 21, 19, 13, 1, DATE_SUB(NOW(), INTERVAL 4 DAY)),
('Java Web 项目脚手架示例', '一个适合课程实训的 Spring Boot + Vue 前后端分离项目结构示例。', 7, 'http://localhost:9000/course-material/materials/demo-java-web.zip', 'materials/demo-java-web.zip', 'java-web-starter.zip', 'ZIP', 102400, 5, 'APPROVED', '词库审核通过', NOW(), 58, 16, 13, 10, 1, DATE_SUB(NOW(), INTERVAL 5 DAY)),
('违规广告资料示例', '包含广告引流内容，应该被词库审核拒绝，不会出现在公开资料库。', 5, 'http://localhost:9000/course-material/materials/demo-rejected.pdf', 'materials/demo-rejected.pdf', 'rejected-demo.pdf', 'PDF', 12000, 3, 'REJECTED', '命中敏感词：广告', NOW(), 0, 0, 0, 0, 1, DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO material_tag (material_id, tag_id) VALUES
(1, 3), (1, 11), (1, 14),
(2, 3), (2, 9), (2, 12),
(3, 2), (3, 8), (3, 15),
(4, 2), (4, 7), (4, 13),
(5, 3), (5, 11), (5, 14);

INSERT INTO question (title, content, category_id, user_id, audit_status, audit_remark, audit_time, view_count, answer_count, like_count, status, create_time) VALUES
('软件工程答辩时怎么讲设计模式？', '项目里用了分层架构、Service 接口和统一异常，答辩时应该怎么讲才清楚？', 5, 3, 'APPROVED', '词库审核通过', NOW(), 45, 2, 9, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('数据库索引为什么会失效？', 'LIKE、函数、隐式类型转换这些场景如何解释？有没有适合课堂展示的例子？', 4, 4, 'APPROVED', '词库审核通过', NOW(), 38, 2, 7, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('Docker Compose 启动很慢怎么排查？', '同学电脑拉镜像失败或者后端一直 unhealthy，应该按什么顺序排查？', 7, 5, 'APPROVED', '词库审核通过', NOW(), 52, 3, 11, 1, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('操作系统进程和线程区别怎么记？', '希望有一版适合期末复习的对比说明。', 1, 3, 'APPROVED', '词库审核通过', NOW(), 29, 1, 5, 1, DATE_SUB(NOW(), INTERVAL 4 DAY));

INSERT INTO answer (question_id, content, user_id, audit_status, audit_remark, audit_time, like_count, reply_count, status, create_time) VALUES
(1, '可以按“为什么分层、每层负责什么、后续如何扩展”三个角度讲，比单纯背概念更像真实工程。', 4, 'APPROVED', '词库审核通过', NOW(), 8, 2, 1, DATE_SUB(NOW(), INTERVAL 22 HOUR)),
(1, '设计模式不要硬套，重点讲 Controller-Service-Mapper 的职责边界和统一异常处理。', 5, 'APPROVED', '词库审核通过', NOW(), 6, 1, 1, DATE_SUB(NOW(), INTERVAL 20 HOUR)),
(2, '索引失效可以准备三条 SQL：左模糊、对字段使用函数、字符串数字隐式转换，执行计划对比最直观。', 5, 'APPROVED', '词库审核通过', NOW(), 7, 1, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, '先看 docker compose ps，再看 logs，最后确认端口、环境变量、数据库初始化和镜像源网络。', 3, 'APPROVED', '词库审核通过', NOW(), 10, 2, 1, DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT INTO answer_reply (answer_id, user_id, reply_to_user_id, content, audit_status, audit_remark, audit_time, like_count, status, create_time) VALUES
(1, 3, NULL, '这个思路适合答辩，我会把它整理成三页 PPT。', 'APPROVED', '词库审核通过', NOW(), 4, 1, DATE_SUB(NOW(), INTERVAL 18 HOUR)),
(1, 5, 3, '可以再补一个“后续换 JWT”作为扩展点，会更像工程项目。', 'APPROVED', '词库审核通过', NOW(), 5, 1, DATE_SUB(NOW(), INTERVAL 16 HOUR)),
(2, 4, NULL, '确实，讲清楚边界比强行套模式更重要。', 'APPROVED', '词库审核通过', NOW(), 3, 1, DATE_SUB(NOW(), INTERVAL 15 HOUR)),
(3, 4, NULL, '执行计划截图很有说服力，答辩现场可以直接演示。', 'APPROVED', '词库审核通过', NOW(), 3, 1, DATE_SUB(NOW(), INTERVAL 12 HOUR)),
(4, 4, NULL, '网络问题可以先切换镜像源，很多同学卡在拉取基础镜像。', 'APPROVED', '词库审核通过', NOW(), 4, 1, DATE_SUB(NOW(), INTERVAL 10 HOUR)),
(4, 5, 4, '还要提醒他们看 backend 的健康检查日志。', 'APPROVED', '词库审核通过', NOW(), 2, 1, DATE_SUB(NOW(), INTERVAL 8 HOUR));

INSERT INTO report (target_type, target_id, target_snapshot, report_user_id, reason, handle_status, create_time) VALUES
('MATERIAL', 6, '违规广告资料示例', 3, '资料简介里出现广告引流，建议下架。', 'PENDING', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('QUESTION', 3, 'Docker Compose 启动很慢怎么排查？', 4, '重复问题较多，请审核员确认是否需要合并。', 'PENDING', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('ANSWER', 4, '先看 docker compose ps，再看 logs，最后确认端口、环境变量、数据库初始化和镜像源网络。', 5, '回答有帮助，误报演示用，可驳回。', 'PENDING', DATE_SUB(NOW(), INTERVAL 30 MINUTE));

INSERT INTO like_record (user_id, target_type, target_id, create_time) VALUES
(3, 'MATERIAL', 1, NOW()), (4, 'MATERIAL', 1, NOW()), (5, 'MATERIAL', 2, NOW()),
(3, 'QUESTION', 1, NOW()), (4, 'QUESTION', 3, NOW()), (5, 'QUESTION', 3, NOW());

INSERT INTO material_favorite (user_id, material_id, create_time) VALUES
(3, 1, NOW()), (3, 2, NOW()), (4, 1, NOW()), (5, 5, NOW());

INSERT INTO download_record (user_id, material_id, ip, create_time) VALUES
(3, 1, '127.0.0.1', NOW()), (4, 1, '127.0.0.1', NOW()), (5, 2, '127.0.0.1', NOW()), (3, 5, '127.0.0.1', NOW());

INSERT INTO ai_audit_record (target_type, target_id, audit_result, risk_score, reason, model_name, request_content, response_content, create_time) VALUES
('MATERIAL', 1, 'PASS', 5.00, '词库审核通过', 'lexicon-audit', '软件工程课程设计验收模板', '词库审核通过', NOW()),
('MATERIAL', 6, 'REJECT', 95.00, '命中敏感词：广告', 'lexicon-audit', '违规广告资料示例', '命中敏感词：广告', NOW()),
('QUESTION', 1, 'PASS', 5.00, '词库审核通过', 'lexicon-audit', '软件工程答辩时怎么讲设计模式？', '词库审核通过', NOW());
