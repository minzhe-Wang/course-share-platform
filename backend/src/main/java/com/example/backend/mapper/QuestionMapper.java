package com.example.backend.mapper;

import com.example.backend.entity.Answer;
import com.example.backend.entity.AnswerReply;
import com.example.backend.entity.Question;
import com.example.backend.vo.AdminAnswerItemVO;
import com.example.backend.vo.AdminQuestionItemVO;
import com.example.backend.vo.AdminReplyItemVO;
import com.example.backend.vo.QuestionAnswerVO;
import com.example.backend.vo.QuestionDetailVO;
import com.example.backend.vo.QuestionListItemVO;
import com.example.backend.vo.QuestionReplyVO;
import com.example.backend.vo.RecommendationItemVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface QuestionMapper {

    @Insert("""
            INSERT INTO question(title, content, category_id, user_id, audit_status, status)
            VALUES(#{title}, #{content}, #{categoryId}, #{userId}, #{auditStatus}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertQuestion(Question question);

    @Update("""
            UPDATE question
            SET audit_status = #{auditStatus},
                audit_remark = #{auditRemark},
                audit_time = #{auditTime}
            WHERE id = #{id}
            """)
    int updateQuestionAudit(@Param("id") Long id,
                            @Param("auditStatus") String auditStatus,
                            @Param("auditRemark") String auditRemark,
                            @Param("auditTime") LocalDateTime auditTime);

    @Select("""
            SELECT id, title, content, category_id, user_id, audit_status, status
            FROM question
            WHERE id = #{id}
            LIMIT 1
            """)
    Question findQuestionById(Long id);

    @Select("""
            <script>
            SELECT COUNT(1)
            FROM question q
            WHERE q.audit_status = 'APPROVED'
              AND q.status = 1
            <if test="keyword != null and keyword != ''">
              AND (q.title LIKE CONCAT('%', #{keyword}, '%')
                   OR q.content LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="categoryId != null">
              AND q.category_id = #{categoryId}
            </if>
            </script>
            """)
    Long countApprovedQuestions(@Param("keyword") String keyword, @Param("categoryId") Long categoryId);

    @Select("""
            <script>
            SELECT
                q.id,
                q.title,
                q.content,
                c.name AS category_name,
                u.nickname AS user_name,
                q.view_count,
                q.answer_count,
                q.like_count,
                q.create_time
            FROM question q
            INNER JOIN course_category c ON c.id = q.category_id
            INNER JOIN sys_user u ON u.id = q.user_id
            WHERE q.audit_status = 'APPROVED'
              AND q.status = 1
            <if test="keyword != null and keyword != ''">
              AND (q.title LIKE CONCAT('%', #{keyword}, '%')
                   OR q.content LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="categoryId != null">
              AND q.category_id = #{categoryId}
            </if>
            ORDER BY ${orderBy}
            LIMIT #{pageSize} OFFSET #{offset}
            </script>
            """)
    List<QuestionListItemVO> findApprovedQuestions(@Param("keyword") String keyword,
                                                   @Param("categoryId") Long categoryId,
                                                   @Param("orderBy") String orderBy,
                                                   @Param("pageSize") Integer pageSize,
                                                   @Param("offset") Integer offset);

    @Select("""
            SELECT
                q.id,
                q.title,
                q.content,
                c.name AS category_name,
                u.nickname AS user_name,
                q.view_count,
                q.like_count
            FROM question q
            INNER JOIN course_category c ON c.id = q.category_id
            INNER JOIN sys_user u ON u.id = q.user_id
            WHERE q.id = #{id}
              AND q.audit_status = 'APPROVED'
              AND q.status = 1
            LIMIT 1
            """)
    QuestionDetailVO findApprovedQuestionDetail(Long id);

    @Update("""
            UPDATE question
            SET view_count = view_count + 1
            WHERE id = #{id}
            """)
    int incrementQuestionViewCount(Long id);

    @Insert("""
            INSERT INTO answer(question_id, content, user_id, audit_status, status)
            VALUES(#{questionId}, #{content}, #{userId}, #{auditStatus}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertAnswer(Answer answer);

    @Update("""
            UPDATE answer
            SET audit_status = #{auditStatus},
                audit_remark = #{auditRemark},
                audit_time = #{auditTime}
            WHERE id = #{id}
            """)
    int updateAnswerAudit(@Param("id") Long id,
                          @Param("auditStatus") String auditStatus,
                          @Param("auditRemark") String auditRemark,
                          @Param("auditTime") LocalDateTime auditTime);

    @Update("""
            UPDATE question
            SET answer_count = answer_count + 1
            WHERE id = #{id}
            """)
    int incrementAnswerCount(Long id);

    @Select("""
            SELECT id, question_id, content, user_id, audit_status, status
            FROM answer
            WHERE id = #{id}
            LIMIT 1
            """)
    Answer findAnswerById(Long id);

    @Select("""
            SELECT
                a.id,
                a.content,
                u.nickname AS user_name,
                a.like_count,
                a.reply_count
            FROM answer a
            INNER JOIN sys_user u ON u.id = a.user_id
            WHERE a.question_id = #{questionId}
              AND a.audit_status = 'APPROVED'
              AND a.status = 1
            ORDER BY a.create_time ASC
            """)
    List<QuestionAnswerVO> findApprovedAnswers(Long questionId);

    @Insert("""
            INSERT INTO answer_reply(answer_id, user_id, reply_to_user_id, content, audit_status, status)
            VALUES(#{answerId}, #{userId}, #{replyToUserId}, #{content}, #{auditStatus}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertReply(AnswerReply answerReply);

    @Update("""
            UPDATE answer_reply
            SET audit_status = #{auditStatus},
                audit_remark = #{auditRemark},
                audit_time = #{auditTime}
            WHERE id = #{id}
            """)
    int updateReplyAudit(@Param("id") Long id,
                         @Param("auditStatus") String auditStatus,
                         @Param("auditRemark") String auditRemark,
                         @Param("auditTime") LocalDateTime auditTime);

    @Update("""
            UPDATE answer
            SET reply_count = reply_count + 1
            WHERE id = #{id}
            """)
    int incrementReplyCount(Long id);

    @Select("""
            SELECT COUNT(1)
            FROM answer_reply
            WHERE answer_id = #{answerId}
              AND user_id = #{replyToUserId}
              AND status = 1
            """)
    int countReplyUserInAnswer(@Param("answerId") Long answerId, @Param("replyToUserId") Long replyToUserId);

    @Select("""
            SELECT
                r.id,
                r.user_id,
                r.content,
                u.nickname AS user_name,
                ru.nickname AS reply_to_user_name,
                r.like_count
            FROM answer_reply r
            INNER JOIN sys_user u ON u.id = r.user_id
            LEFT JOIN sys_user ru ON ru.id = r.reply_to_user_id
            WHERE r.answer_id = #{answerId}
              AND r.audit_status = 'APPROVED'
              AND r.status = 1
            ORDER BY r.create_time ASC
            """)
    List<QuestionReplyVO> findApprovedReplies(Long answerId);

    @Select("""
            SELECT id
            FROM answer_reply
            WHERE id = #{id}
              AND audit_status = 'APPROVED'
              AND status = 1
            LIMIT 1
            """)
    Long findApprovedReplyId(Long id);

    @Select("""
            SELECT COUNT(1)
            FROM like_record
            WHERE user_id = #{userId}
              AND target_type = #{targetType}
              AND target_id = #{targetId}
            """)
    int countLikeRecord(@Param("userId") Long userId,
                        @Param("targetType") String targetType,
                        @Param("targetId") Long targetId);

    @Insert("""
            INSERT INTO like_record(user_id, target_type, target_id)
            VALUES(#{userId}, #{targetType}, #{targetId})
            """)
    int insertLikeRecord(@Param("userId") Long userId,
                         @Param("targetType") String targetType,
                         @Param("targetId") Long targetId);

    @Update("""
            UPDATE question
            SET like_count = like_count + 1
            WHERE id = #{id}
            """)
    int incrementQuestionLikeCount(Long id);

    @Update("""
            UPDATE answer
            SET like_count = like_count + 1
            WHERE id = #{id}
            """)
    int incrementAnswerLikeCount(Long id);

    @Update("""
            UPDATE answer_reply
            SET like_count = like_count + 1
            WHERE id = #{id}
            """)
    int incrementReplyLikeCount(Long id);

    @Select("""
            SELECT id, answer_id, content, audit_status, status
            FROM answer_reply
            WHERE id = #{id}
            LIMIT 1
            """)
    AnswerReply findReplyById(Long id);

    @Select("""
            <script>
            SELECT COUNT(1)
            FROM question q
            WHERE 1 = 1
            <if test="keyword != null and keyword != ''">
              AND (q.title LIKE CONCAT('%', #{keyword}, '%')
                   OR q.content LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="auditStatus != null and auditStatus != ''">
              AND q.audit_status = #{auditStatus}
            </if>
            <if test="status != null">
              AND q.status = #{status}
            </if>
            </script>
            """)
    Long countAdminQuestions(@Param("keyword") String keyword,
                             @Param("auditStatus") String auditStatus,
                             @Param("status") Integer status);

    @Select("""
            <script>
            SELECT
                q.id,
                q.title,
                c.name AS category_name,
                u.nickname AS user_name,
                q.audit_status,
                q.audit_remark,
                q.status,
                q.view_count,
                q.answer_count,
                q.like_count,
                q.create_time
            FROM question q
            INNER JOIN course_category c ON c.id = q.category_id
            INNER JOIN sys_user u ON u.id = q.user_id
            WHERE 1 = 1
            <if test="keyword != null and keyword != ''">
              AND (q.title LIKE CONCAT('%', #{keyword}, '%')
                   OR q.content LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="auditStatus != null and auditStatus != ''">
              AND q.audit_status = #{auditStatus}
            </if>
            <if test="status != null">
              AND q.status = #{status}
            </if>
            ORDER BY q.create_time DESC, q.id DESC
            LIMIT #{pageSize} OFFSET #{offset}
            </script>
            """)
    List<AdminQuestionItemVO> findAdminQuestions(@Param("keyword") String keyword,
                                                 @Param("auditStatus") String auditStatus,
                                                 @Param("status") Integer status,
                                                 @Param("pageSize") Integer pageSize,
                                                 @Param("offset") Integer offset);

    @Select("""
            <script>
            SELECT COUNT(1)
            FROM answer a
            INNER JOIN question q ON q.id = a.question_id
            WHERE 1 = 1
            <if test="keyword != null and keyword != ''">
              AND (a.content LIKE CONCAT('%', #{keyword}, '%')
                   OR q.title LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="auditStatus != null and auditStatus != ''">
              AND a.audit_status = #{auditStatus}
            </if>
            <if test="status != null">
              AND a.status = #{status}
            </if>
            </script>
            """)
    Long countAdminAnswers(@Param("keyword") String keyword,
                           @Param("auditStatus") String auditStatus,
                           @Param("status") Integer status);

    @Select("""
            <script>
            SELECT
                a.id,
                a.question_id,
                q.title AS question_title,
                a.content,
                u.nickname AS user_name,
                a.audit_status,
                a.audit_remark,
                a.status,
                a.like_count,
                a.reply_count,
                a.create_time
            FROM answer a
            INNER JOIN question q ON q.id = a.question_id
            INNER JOIN sys_user u ON u.id = a.user_id
            WHERE 1 = 1
            <if test="keyword != null and keyword != ''">
              AND (a.content LIKE CONCAT('%', #{keyword}, '%')
                   OR q.title LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="auditStatus != null and auditStatus != ''">
              AND a.audit_status = #{auditStatus}
            </if>
            <if test="status != null">
              AND a.status = #{status}
            </if>
            ORDER BY a.create_time DESC, a.id DESC
            LIMIT #{pageSize} OFFSET #{offset}
            </script>
            """)
    List<AdminAnswerItemVO> findAdminAnswers(@Param("keyword") String keyword,
                                             @Param("auditStatus") String auditStatus,
                                             @Param("status") Integer status,
                                             @Param("pageSize") Integer pageSize,
                                             @Param("offset") Integer offset);

    @Select("""
            <script>
            SELECT COUNT(1)
            FROM answer_reply r
            INNER JOIN answer a ON a.id = r.answer_id
            INNER JOIN question q ON q.id = a.question_id
            WHERE 1 = 1
            <if test="keyword != null and keyword != ''">
              AND (r.content LIKE CONCAT('%', #{keyword}, '%')
                   OR q.title LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="auditStatus != null and auditStatus != ''">
              AND r.audit_status = #{auditStatus}
            </if>
            <if test="status != null">
              AND r.status = #{status}
            </if>
            </script>
            """)
    Long countAdminReplies(@Param("keyword") String keyword,
                           @Param("auditStatus") String auditStatus,
                           @Param("status") Integer status);

    @Select("""
            <script>
            SELECT
                r.id,
                r.answer_id,
                a.question_id,
                q.title AS question_title,
                r.content,
                u.nickname AS user_name,
                ru.nickname AS reply_to_user_name,
                r.audit_status,
                r.audit_remark,
                r.status,
                r.like_count,
                r.create_time
            FROM answer_reply r
            INNER JOIN answer a ON a.id = r.answer_id
            INNER JOIN question q ON q.id = a.question_id
            INNER JOIN sys_user u ON u.id = r.user_id
            LEFT JOIN sys_user ru ON ru.id = r.reply_to_user_id
            WHERE 1 = 1
            <if test="keyword != null and keyword != ''">
              AND (r.content LIKE CONCAT('%', #{keyword}, '%')
                   OR q.title LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="auditStatus != null and auditStatus != ''">
              AND r.audit_status = #{auditStatus}
            </if>
            <if test="status != null">
              AND r.status = #{status}
            </if>
            ORDER BY r.create_time DESC, r.id DESC
            LIMIT #{pageSize} OFFSET #{offset}
            </script>
            """)
    List<AdminReplyItemVO> findAdminReplies(@Param("keyword") String keyword,
                                            @Param("auditStatus") String auditStatus,
                                            @Param("status") Integer status,
                                            @Param("pageSize") Integer pageSize,
                                            @Param("offset") Integer offset);

    @Update("UPDATE question SET status = #{status} WHERE id = #{id}")
    int updateQuestionStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE answer SET status = #{status} WHERE id = #{id}")
    int updateAnswerStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE answer_reply SET status = #{status} WHERE id = #{id}")
    int updateReplyStatus(@Param("id") Long id, @Param("status") Integer status);

    @Select("""
            SELECT
                q.id,
                q.title,
                q.content,
                c.name AS category_name,
                u.nickname AS user_name,
                q.view_count,
                q.answer_count,
                q.like_count,
                q.create_time
            FROM question q
            INNER JOIN course_category c ON c.id = q.category_id
            INNER JOIN sys_user u ON u.id = q.user_id
            WHERE q.audit_status = 'APPROVED'
              AND q.status = 1
            ORDER BY
                (q.answer_count * 3 + q.like_count * 2 + q.view_count) DESC,
                q.create_time DESC,
                q.id DESC
            LIMIT #{limit}
            """)
    List<QuestionListItemVO> findHotQuestions(Integer limit);

    @Select("""
            SELECT
                'QUESTION' AS target_type,
                q.id AS target_id,
                q.title,
                q.content AS description,
                c.name AS category_name,
                (
                    q.answer_count * 3
                    + q.like_count * 2
                    + q.view_count
                    + CASE
                        WHEN q.create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) THEN 12
                        WHEN q.create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY) THEN 6
                        ELSE 0
                      END
                ) AS score,
                CASE
                    WHEN q.answer_count >= 3 THEN '讨论活跃'
                    WHEN q.like_count >= 3 THEN '近期热度较高'
                    WHEN q.view_count >= 5 THEN '近期热度较高'
                    ELSE '适合优先查看'
                END AS reason,
                q.create_time
            FROM question q
            INNER JOIN course_category c ON c.id = q.category_id
            WHERE q.audit_status = 'APPROVED'
              AND q.status = 1
            ORDER BY score DESC, q.create_time DESC, q.id DESC
            LIMIT #{limit}
            """)
    List<RecommendationItemVO> findRecommendedQuestions(Integer limit);
}

