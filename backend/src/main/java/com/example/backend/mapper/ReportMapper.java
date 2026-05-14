package com.example.backend.mapper;

import com.example.backend.entity.Report;
import com.example.backend.vo.ReportDetailVO;
import com.example.backend.vo.ReportListItemVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReportMapper {

    @Insert("""
            INSERT INTO report(target_type, target_id, target_snapshot, report_user_id, reason, handle_status)
            VALUES(#{targetType}, #{targetId}, #{targetSnapshot}, #{reportUserId}, #{reason}, #{handleStatus})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Report report);

    @Select("""
            SELECT id, target_type, target_id, target_snapshot, report_user_id, reason,
                   handle_status, handle_user_id, handle_result, handle_time, create_time
            FROM report
            WHERE id = #{id}
            LIMIT 1
            """)
    Report findById(Long id);

    @Select("""
            SELECT
                r.id,
                r.target_type,
                r.target_id,
                r.target_snapshot,
                r.report_user_id,
                ru.nickname AS report_user_name,
                r.reason,
                r.handle_status,
                r.handle_user_id,
                hu.nickname AS handle_user_name,
                r.handle_result,
                r.handle_time,
                r.create_time
            FROM report r
            INNER JOIN sys_user ru ON ru.id = r.report_user_id
            LEFT JOIN sys_user hu ON hu.id = r.handle_user_id
            WHERE r.id = #{id}
            LIMIT 1
            """)
    ReportDetailVO findDetailById(Long id);

    @Select("""
            <script>
            SELECT COUNT(1)
            FROM report
            WHERE 1 = 1
            <if test="handleStatus != null and handleStatus != ''">
              AND handle_status = #{handleStatus}
            </if>
            </script>
            """)
    Long countReports(@Param("handleStatus") String handleStatus);

    @Select("""
            <script>
            SELECT
                r.id,
                r.target_type,
                r.target_id,
                r.target_snapshot,
                u.nickname AS report_user_name,
                r.reason,
                r.handle_status,
                r.create_time
            FROM report r
            INNER JOIN sys_user u ON u.id = r.report_user_id
            WHERE 1 = 1
            <if test="handleStatus != null and handleStatus != ''">
              AND r.handle_status = #{handleStatus}
            </if>
            ORDER BY r.create_time DESC, r.id DESC
            LIMIT #{pageSize} OFFSET #{offset}
            </script>
            """)
    List<ReportListItemVO> findReports(@Param("handleStatus") String handleStatus,
                                        @Param("pageSize") Integer pageSize,
                                        @Param("offset") Integer offset);

    @Update("""
            UPDATE report
            SET handle_status = #{handleStatus},
                handle_user_id = #{handleUserId},
                handle_result = #{handleResult},
                handle_time = #{handleTime}
            WHERE id = #{id}
            """)
    int updateHandleResult(@Param("id") Long id,
                           @Param("handleStatus") String handleStatus,
                           @Param("handleUserId") Long handleUserId,
                           @Param("handleResult") String handleResult,
                           @Param("handleTime") LocalDateTime handleTime);

    @Select("""
            SELECT title
            FROM material
            WHERE id = #{id}
              AND status = 1
            LIMIT 1
            """)
    String findMaterialSnapshot(Long id);

    @Select("""
            SELECT title
            FROM question
            WHERE id = #{id}
              AND status = 1
            LIMIT 1
            """)
    String findQuestionSnapshot(Long id);

    @Select("""
            SELECT content
            FROM answer
            WHERE id = #{id}
              AND status = 1
            LIMIT 1
            """)
    String findAnswerSnapshot(Long id);

    @Select("""
            SELECT content
            FROM answer_reply
            WHERE id = #{id}
              AND status = 1
            LIMIT 1
            """)
    String findReplySnapshot(Long id);

    @Update("UPDATE material SET status = 0 WHERE id = #{id}")
    int disableMaterial(Long id);

    @Update("UPDATE question SET status = 0 WHERE id = #{id}")
    int disableQuestion(Long id);

    @Update("UPDATE answer SET status = 0 WHERE id = #{id}")
    int disableAnswer(Long id);

    @Update("UPDATE answer_reply SET status = 0 WHERE id = #{id}")
    int disableReply(Long id);
}
