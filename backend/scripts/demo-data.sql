SET NAMES utf8mb4;

UPDATE question
SET audit_status = 'REJECTED',
    audit_remark = '命中敏感词：辱骂内容',
    status = 0,
    audit_time = NOW()
WHERE title LIKE '%我日你妈%'
   OR content LIKE '%我日你妈%'
   OR title LIKE '%日你妈%'
   OR content LIKE '%日你妈%'
   OR title LIKE '%傻逼%'
   OR content LIKE '%傻逼%';

UPDATE question
SET status = 0,
    audit_remark = '演示环境清理无效占位数据'
WHERE title REGEXP '^[0-9]+$'
   OR content REGEXP '^[0-9]+$';

UPDATE material
SET status = 0,
    audit_remark = '演示环境清理无效占位数据'
WHERE title REGEXP '^[0-9]+$'
   OR description REGEXP '^[0-9]+$';

INSERT IGNORE INTO course_category (name, type, sort_no) VALUES
('操作系统', '专业核心课', 1),
('计算机网络', '专业核心课', 2),
('数据结构', '专业核心课', 3),
('数据库原理', '专业核心课', 4),
('软件工程', '专业核心课', 5),
('Java 程序设计', '专业课', 6),
('Web 开发技术', '专业课', 7),
('算法设计与分析', '专业核心课', 8);

INSERT IGNORE INTO tag (name, type) VALUES
('大一', 'GRADE'),
('大二', 'GRADE'),
('大三', 'GRADE'),
('大四', 'GRADE'),
('试卷', 'TYPE'),
('课件', 'TYPE'),
('笔记', 'TYPE'),
('实验指导', 'TYPE'),
('复习资料', 'TYPE'),
('项目代码', 'TYPE'),
('期末复习', 'SCENE'),
('考研', 'SCENE'),
('课程设计', 'SCENE'),
('平时作业', 'SCENE');

INSERT IGNORE INTO sys_user (username, password, nickname, role, phone, email) VALUES
('alice', '$2a$10$pFlEa5KtuaDLZ2ZApaWMbeSUOWxoU9CVzf21aPUbj7lldH9N4G5Qy', '资料整理员 Alice', 'STUDENT', '13800000001', 'alice@example.com'),
('bob', '$2a$10$pFlEa5KtuaDLZ2ZApaWMbeSUOWxoU9CVzf21aPUbj7lldH9N4G5Qy', '问答达人 Bob', 'STUDENT', '13800000002', 'bob@example.com'),
('chen', '$2a$10$pFlEa5KtuaDLZ2ZApaWMbeSUOWxoU9CVzf21aPUbj7lldH9N4G5Qy', '实验助教 Chen', 'STUDENT', '13800000003', 'chen@example.com');

INSERT INTO material (title, description, category_id, file_url, file_key, original_filename, file_type, file_size, uploader_id, audit_status, audit_remark, audit_time, view_count, download_count, like_count, favorite_count, status, create_time)
SELECT '软件工程课程设计验收模板', '覆盖需求分析、类图、顺序图、Docker 部署和答辩讲解，可直接作为课程设计验收参考。', c.id, '/api/files/download?fileKey=materials%2Fdemo-software-engineering.pdf', 'materials/demo-software-engineering.pdf', 'software-engineering-demo.pdf', 'PDF', 42896, u.id, 'APPROVED', '词库审核通过', NOW(), 156, 42, 37, 28, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)
FROM course_category c, sys_user u
WHERE c.name = '软件工程' AND u.username = 'alice'
  AND NOT EXISTS (SELECT 1 FROM material m WHERE m.file_key = 'materials/demo-software-engineering.pdf');

INSERT INTO material (title, description, category_id, file_url, file_key, original_filename, file_type, file_size, uploader_id, audit_status, audit_remark, audit_time, view_count, download_count, like_count, favorite_count, status, create_time)
SELECT '数据库原理期末复习提纲', '整理 ER 图、范式、事务隔离级别、索引和 SQL 优化常见题型。', c.id, '/api/files/download?fileKey=materials%2Fdemo-database-review.pdf', 'materials/demo-database-review.pdf', 'database-review.pdf', 'PDF', 38920, u.id, 'APPROVED', '词库审核通过', NOW(), 98, 26, 21, 18, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)
FROM course_category c, sys_user u
WHERE c.name = '数据库原理' AND u.username = 'student'
  AND NOT EXISTS (SELECT 1 FROM material m WHERE m.file_key = 'materials/demo-database-review.pdf');

INSERT INTO material (title, description, category_id, file_url, file_key, original_filename, file_type, file_size, uploader_id, audit_status, audit_remark, audit_time, view_count, download_count, like_count, favorite_count, status, create_time)
SELECT '计算机网络实验指导合集', '包含 Socket 编程、HTTP 抓包、DNS 查询和 TCP 三次握手实验记录模板。', c.id, '/api/files/download?fileKey=materials%2Fdemo-network-lab.docx', 'materials/demo-network-lab.docx', 'network-lab-guide.docx', 'DOCX', 51200, u.id, 'APPROVED', '词库审核通过', NOW(), 87, 19, 18, 12, 1, DATE_SUB(NOW(), INTERVAL 3 DAY)
FROM course_category c, sys_user u
WHERE c.name = '计算机网络' AND u.username = 'alice'
  AND NOT EXISTS (SELECT 1 FROM material m WHERE m.file_key = 'materials/demo-network-lab.docx');

INSERT INTO material (title, description, category_id, file_url, file_key, original_filename, file_type, file_size, uploader_id, audit_status, audit_remark, audit_time, view_count, download_count, like_count, favorite_count, status, create_time)
SELECT '数据结构常见算法笔记', '覆盖链表、栈、队列、树、图、排序和动态规划的课堂笔记。', c.id, '/api/files/download?fileKey=materials%2Fdemo-data-structure.pdf', 'materials/demo-data-structure.pdf', 'data-structure-notes.pdf', 'PDF', 35600, u.id, 'APPROVED', '词库审核通过', NOW(), 116, 33, 29, 20, 1, DATE_SUB(NOW(), INTERVAL 4 DAY)
FROM course_category c, sys_user u
WHERE c.name = '数据结构' AND u.username = 'chen'
  AND NOT EXISTS (SELECT 1 FROM material m WHERE m.file_key = 'materials/demo-data-structure.pdf');

INSERT INTO material (title, description, category_id, file_url, file_key, original_filename, file_type, file_size, uploader_id, audit_status, audit_remark, audit_time, view_count, download_count, like_count, favorite_count, status, create_time)
SELECT 'Java Web 项目脚手架示例', '一个适合课程实训的 Spring Boot + Vue 前后端分离项目结构示例。', c.id, '/api/files/download?fileKey=materials%2Fdemo-java-web.zip', 'materials/demo-java-web.zip', 'java-web-starter.zip', 'ZIP', 102400, u.id, 'APPROVED', '词库审核通过', NOW(), 82, 25, 17, 14, 1, DATE_SUB(NOW(), INTERVAL 5 DAY)
FROM course_category c, sys_user u
WHERE c.name = 'Web 开发技术' AND u.username = 'bob'
  AND NOT EXISTS (SELECT 1 FROM material m WHERE m.file_key = 'materials/demo-java-web.zip');

INSERT INTO material (title, description, category_id, file_url, file_key, original_filename, file_type, file_size, uploader_id, audit_status, audit_remark, audit_time, view_count, download_count, like_count, favorite_count, status, create_time)
SELECT '操作系统进程线程速记卡', '用对比表整理进程、线程、同步互斥、死锁必要条件和银行家算法。', c.id, '/api/files/download?fileKey=materials%2Fdemo-os-process.pdf', 'materials/demo-os-process.pdf', 'os-process-thread.pdf', 'PDF', 28600, u.id, 'APPROVED', '词库审核通过', NOW(), 69, 13, 16, 9, 1, DATE_SUB(NOW(), INTERVAL 6 DAY)
FROM course_category c, sys_user u
WHERE c.name = '操作系统' AND u.username = 'chen'
  AND NOT EXISTS (SELECT 1 FROM material m WHERE m.file_key = 'materials/demo-os-process.pdf');

INSERT INTO material (title, description, category_id, file_url, file_key, original_filename, file_type, file_size, uploader_id, audit_status, audit_remark, audit_time, view_count, download_count, like_count, favorite_count, status, create_time)
SELECT '违规广告资料示例', '包含广告引流内容，应该被词库审核拒绝，不会出现在公开资料库。', c.id, '/api/files/download?fileKey=materials%2Fdemo-rejected.pdf', 'materials/demo-rejected.pdf', 'rejected-demo.pdf', 'PDF', 12000, u.id, 'REJECTED', '命中敏感词：广告', NOW(), 0, 0, 0, 0, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)
FROM course_category c, sys_user u
WHERE c.name = '软件工程' AND u.username = 'student'
  AND NOT EXISTS (SELECT 1 FROM material m WHERE m.file_key = 'materials/demo-rejected.pdf');

INSERT IGNORE INTO material_tag (material_id, tag_id)
SELECT m.id, t.id FROM material m JOIN tag t ON t.name IN ('大三', '项目代码', '课程设计') WHERE m.file_key = 'materials/demo-software-engineering.pdf';
INSERT IGNORE INTO material_tag (material_id, tag_id)
SELECT m.id, t.id FROM material m JOIN tag t ON t.name IN ('大三', '复习资料', '期末复习') WHERE m.file_key = 'materials/demo-database-review.pdf';
INSERT IGNORE INTO material_tag (material_id, tag_id)
SELECT m.id, t.id FROM material m JOIN tag t ON t.name IN ('大二', '实验指导', '平时作业') WHERE m.file_key = 'materials/demo-network-lab.docx';
INSERT IGNORE INTO material_tag (material_id, tag_id)
SELECT m.id, t.id FROM material m JOIN tag t ON t.name IN ('大二', '笔记', '考研') WHERE m.file_key = 'materials/demo-data-structure.pdf';
INSERT IGNORE INTO material_tag (material_id, tag_id)
SELECT m.id, t.id FROM material m JOIN tag t ON t.name IN ('大三', '项目代码', '课程设计') WHERE m.file_key = 'materials/demo-java-web.zip';
INSERT IGNORE INTO material_tag (material_id, tag_id)
SELECT m.id, t.id FROM material m JOIN tag t ON t.name IN ('大二', '笔记', '期末复习') WHERE m.file_key = 'materials/demo-os-process.pdf';

INSERT INTO question (title, content, category_id, user_id, audit_status, audit_remark, audit_time, view_count, answer_count, like_count, status, create_time)
SELECT '软件工程答辩时怎么讲设计模式？', '项目里用了分层架构、Service 接口和统一异常，答辩时应该怎么讲才清楚？', c.id, u.id, 'APPROVED', '词库审核通过', NOW(), 88, 2, 16, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)
FROM course_category c, sys_user u
WHERE c.name = '软件工程' AND u.username = 'student'
  AND NOT EXISTS (SELECT 1 FROM question q WHERE q.title = '软件工程答辩时怎么讲设计模式？');

INSERT INTO question (title, content, category_id, user_id, audit_status, audit_remark, audit_time, view_count, answer_count, like_count, status, create_time)
SELECT '数据库索引为什么会失效？', 'LIKE、函数、隐式类型转换这些场景如何解释？有没有适合课堂展示的例子？', c.id, u.id, 'APPROVED', '词库审核通过', NOW(), 64, 2, 11, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)
FROM course_category c, sys_user u
WHERE c.name = '数据库原理' AND u.username = 'alice'
  AND NOT EXISTS (SELECT 1 FROM question q WHERE q.title = '数据库索引为什么会失效？');

INSERT INTO question (title, content, category_id, user_id, audit_status, audit_remark, audit_time, view_count, answer_count, like_count, status, create_time)
SELECT 'Docker Compose 启动很慢怎么排查？', '同学电脑拉镜像失败或者后端一直 unhealthy，应该按什么顺序排查？', c.id, u.id, 'APPROVED', '词库审核通过', NOW(), 93, 3, 18, 1, DATE_SUB(NOW(), INTERVAL 3 DAY)
FROM course_category c, sys_user u
WHERE c.name = 'Web 开发技术' AND u.username = 'bob'
  AND NOT EXISTS (SELECT 1 FROM question q WHERE q.title = 'Docker Compose 启动很慢怎么排查？');

INSERT INTO question (title, content, category_id, user_id, audit_status, audit_remark, audit_time, view_count, answer_count, like_count, status, create_time)
SELECT '操作系统进程和线程区别怎么记？', '希望有一版适合期末复习的对比说明。', c.id, u.id, 'APPROVED', '词库审核通过', NOW(), 51, 1, 8, 1, DATE_SUB(NOW(), INTERVAL 4 DAY)
FROM course_category c, sys_user u
WHERE c.name = '操作系统' AND u.username = 'chen'
  AND NOT EXISTS (SELECT 1 FROM question q WHERE q.title = '操作系统进程和线程区别怎么记？');

INSERT INTO answer (question_id, content, user_id, audit_status, audit_remark, audit_time, like_count, reply_count, status, create_time)
SELECT q.id, '可以按“为什么分层、每层负责什么、后续如何扩展”三个角度讲，比单纯背概念更像真实工程。', u.id, 'APPROVED', '词库审核通过', NOW(), 12, 2, 1, DATE_SUB(NOW(), INTERVAL 22 HOUR)
FROM question q, sys_user u
WHERE q.title = '软件工程答辩时怎么讲设计模式？' AND u.username = 'alice'
  AND NOT EXISTS (SELECT 1 FROM answer a WHERE a.question_id = q.id AND a.content LIKE '可以按“为什么分层%');

INSERT INTO answer (question_id, content, user_id, audit_status, audit_remark, audit_time, like_count, reply_count, status, create_time)
SELECT q.id, '设计模式不要硬套，重点讲 Controller-Service-Mapper 的职责边界和统一返回格式。', u.id, 'APPROVED', '词库审核通过', NOW(), 9, 1, 1, DATE_SUB(NOW(), INTERVAL 20 HOUR)
FROM question q, sys_user u
WHERE q.title = '软件工程答辩时怎么讲设计模式？' AND u.username = 'bob'
  AND NOT EXISTS (SELECT 1 FROM answer a WHERE a.question_id = q.id AND a.content LIKE '设计模式不要硬套%');

INSERT INTO answer (question_id, content, user_id, audit_status, audit_remark, audit_time, like_count, reply_count, status, create_time)
SELECT q.id, '索引失效可以准备三条 SQL：左模糊、对字段使用函数、字符串数字隐式转换，执行计划对比最直观。', u.id, 'APPROVED', '词库审核通过', NOW(), 10, 1, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)
FROM question q, sys_user u
WHERE q.title = '数据库索引为什么会失效？' AND u.username = 'bob'
  AND NOT EXISTS (SELECT 1 FROM answer a WHERE a.question_id = q.id AND a.content LIKE '索引失效可以准备%');

INSERT INTO answer (question_id, content, user_id, audit_status, audit_remark, audit_time, like_count, reply_count, status, create_time)
SELECT q.id, '先看 docker compose ps，再看 logs，最后确认端口、环境变量、数据库初始化和镜像源网络。', u.id, 'APPROVED', '词库审核通过', NOW(), 15, 2, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)
FROM question q, sys_user u
WHERE q.title = 'Docker Compose 启动很慢怎么排查？' AND u.username = 'student'
  AND NOT EXISTS (SELECT 1 FROM answer a WHERE a.question_id = q.id AND a.content LIKE '先看 docker compose ps%');

INSERT INTO answer_reply (answer_id, user_id, reply_to_user_id, content, audit_status, audit_remark, audit_time, like_count, status, create_time)
SELECT a.id, u.id, NULL, '这个思路适合答辩，我会把它整理成三页 PPT。', 'APPROVED', '词库审核通过', NOW(), 4, 1, DATE_SUB(NOW(), INTERVAL 18 HOUR)
FROM answer a JOIN question q ON q.id = a.question_id JOIN sys_user u ON u.username = 'student'
WHERE q.title = '软件工程答辩时怎么讲设计模式？' AND a.content LIKE '可以按“为什么分层%'
  AND NOT EXISTS (SELECT 1 FROM answer_reply r WHERE r.answer_id = a.id AND r.content = '这个思路适合答辩，我会把它整理成三页 PPT。');

INSERT INTO answer_reply (answer_id, user_id, reply_to_user_id, content, audit_status, audit_remark, audit_time, like_count, status, create_time)
SELECT a.id, u.id, ru.id, '可以再补一个“后续换 JWT”作为扩展点，会更像工程项目。', 'APPROVED', '词库审核通过', NOW(), 5, 1, DATE_SUB(NOW(), INTERVAL 16 HOUR)
FROM answer a JOIN question q ON q.id = a.question_id JOIN sys_user u ON u.username = 'chen' LEFT JOIN sys_user ru ON ru.username = 'student'
WHERE q.title = '软件工程答辩时怎么讲设计模式？' AND a.content LIKE '可以按“为什么分层%'
  AND NOT EXISTS (SELECT 1 FROM answer_reply r WHERE r.answer_id = a.id AND r.content = '可以再补一个“后续换 JWT”作为扩展点，会更像工程项目。');

INSERT INTO answer_reply (answer_id, user_id, reply_to_user_id, content, audit_status, audit_remark, audit_time, like_count, status, create_time)
SELECT a.id, u.id, NULL, '执行计划截图很有说服力，答辩现场可以直接演示。', 'APPROVED', '词库审核通过', NOW(), 3, 1, DATE_SUB(NOW(), INTERVAL 12 HOUR)
FROM answer a JOIN question q ON q.id = a.question_id JOIN sys_user u ON u.username = 'alice'
WHERE q.title = '数据库索引为什么会失效？' AND a.content LIKE '索引失效可以准备%'
  AND NOT EXISTS (SELECT 1 FROM answer_reply r WHERE r.answer_id = a.id AND r.content = '执行计划截图很有说服力，答辩现场可以直接演示。');

INSERT INTO report (target_type, target_id, target_snapshot, report_user_id, reason, handle_status, create_time)
SELECT 'MATERIAL', m.id, m.title, u.id, '资料简介里出现广告引流，建议下架。', 'PENDING', DATE_SUB(NOW(), INTERVAL 2 HOUR)
FROM material m, sys_user u
WHERE m.file_key = 'materials/demo-rejected.pdf' AND u.username = 'student'
  AND NOT EXISTS (SELECT 1 FROM report r WHERE r.target_type = 'MATERIAL' AND r.target_id = m.id AND r.reason = '资料简介里出现广告引流，建议下架。');

INSERT INTO report (target_type, target_id, target_snapshot, report_user_id, reason, handle_status, create_time)
SELECT 'QUESTION', q.id, q.title, u.id, '重复问题较多，请审核员确认是否需要合并。', 'PENDING', DATE_SUB(NOW(), INTERVAL 1 HOUR)
FROM question q, sys_user u
WHERE q.title = 'Docker Compose 启动很慢怎么排查？' AND u.username = 'alice'
  AND NOT EXISTS (SELECT 1 FROM report r WHERE r.target_type = 'QUESTION' AND r.target_id = q.id AND r.reason = '重复问题较多，请审核员确认是否需要合并。');

INSERT INTO report (target_type, target_id, target_snapshot, report_user_id, reason, handle_status, create_time)
SELECT 'ANSWER', a.id, a.content, u.id, '回答有帮助，误报演示用，可驳回。', 'PENDING', DATE_SUB(NOW(), INTERVAL 30 MINUTE)
FROM answer a JOIN question q ON q.id = a.question_id JOIN sys_user u ON u.username = 'bob'
WHERE q.title = 'Docker Compose 启动很慢怎么排查？' AND a.content LIKE '先看 docker compose ps%'
  AND NOT EXISTS (SELECT 1 FROM report r WHERE r.target_type = 'ANSWER' AND r.target_id = a.id AND r.reason = '回答有帮助，误报演示用，可驳回。');

INSERT IGNORE INTO like_record (user_id, target_type, target_id, create_time)
SELECT u.id, 'MATERIAL', m.id, NOW() FROM sys_user u JOIN material m ON m.file_key IN ('materials/demo-software-engineering.pdf', 'materials/demo-database-review.pdf', 'materials/demo-data-structure.pdf') WHERE u.username IN ('student', 'alice', 'bob', 'chen');

INSERT IGNORE INTO like_record (user_id, target_type, target_id, create_time)
SELECT u.id, 'QUESTION', q.id, NOW() FROM sys_user u JOIN question q ON q.title IN ('软件工程答辩时怎么讲设计模式？', 'Docker Compose 启动很慢怎么排查？') WHERE u.username IN ('student', 'alice', 'bob', 'chen');

INSERT IGNORE INTO material_favorite (user_id, material_id, create_time)
SELECT u.id, m.id, NOW() FROM sys_user u JOIN material m ON m.file_key IN ('materials/demo-software-engineering.pdf', 'materials/demo-database-review.pdf', 'materials/demo-java-web.zip') WHERE u.username IN ('student', 'alice', 'bob');

INSERT INTO download_record (user_id, material_id, ip, create_time)
SELECT u.id, m.id, '127.0.0.1', NOW() FROM sys_user u JOIN material m ON m.file_key IN ('materials/demo-software-engineering.pdf', 'materials/demo-database-review.pdf', 'materials/demo-java-web.zip') WHERE u.username IN ('student', 'alice', 'bob')
  AND NOT EXISTS (SELECT 1 FROM download_record d WHERE d.user_id = u.id AND d.material_id = m.id);

INSERT INTO ai_audit_record (target_type, target_id, audit_result, risk_score, reason, model_name, request_content, response_content, create_time)
SELECT 'MATERIAL', m.id, 'PASS', 5.00, '词库审核通过', 'lexicon-audit', m.title, '词库审核通过', NOW()
FROM material m
WHERE m.file_key = 'materials/demo-software-engineering.pdf'
  AND NOT EXISTS (SELECT 1 FROM ai_audit_record a WHERE a.target_type = 'MATERIAL' AND a.target_id = m.id);

INSERT INTO ai_audit_record (target_type, target_id, audit_result, risk_score, reason, model_name, request_content, response_content, create_time)
SELECT 'MATERIAL', m.id, 'REJECT', 95.00, '命中敏感词：广告', 'lexicon-audit', m.title, '命中敏感词：广告', NOW()
FROM material m
WHERE m.file_key = 'materials/demo-rejected.pdf'
  AND NOT EXISTS (SELECT 1 FROM ai_audit_record a WHERE a.target_type = 'MATERIAL' AND a.target_id = m.id);
