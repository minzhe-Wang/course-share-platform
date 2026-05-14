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
('Java程序设计', '专业课', 6),
('Web开发技术', '专业课', 7),
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
('student', '$2a$10$pFlEa5KtuaDLZ2ZApaWMbeSUOWxoU9CVzf21aPUbj7lldH9N4G5Qy', '测试学生', 'STUDENT');