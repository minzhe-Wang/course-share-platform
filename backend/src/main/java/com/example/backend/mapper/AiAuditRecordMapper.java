package com.example.backend.mapper;

import com.example.backend.entity.AiAuditRecord;
import com.example.backend.vo.AiAuditRecordDetailVO;
import com.example.backend.vo.AiAuditRecordListItemVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AiAuditRecordMapper {

    @Insert("""
            INSERT INTO ai_audit_record(
                target_type, target_id, audit_result, risk_score, reason,
                model_name, request_content, response_content
            )
            VALUES(
                #{targetType}, #{targetId}, #{auditResult}, #{riskScore}, #{reason},
                #{modelName}, #{requestContent}, #{responseContent}
            )
            """)
    int insert(AiAuditRecord aiAuditRecord);

    @Select("""
            <script>
            SELECT COUNT(1)
            FROM ai_audit_record
            WHERE 1 = 1
            <if test="targetType != null and targetType != ''">
              AND target_type = #{targetType}
            </if>
            <if test="auditResult != null and auditResult != ''">
              AND audit_result = #{auditResult}
            </if>
            </script>
            """)
    Long countRecords(@Param("targetType") String targetType, @Param("auditResult") String auditResult);

    @Select("""
            <script>
            SELECT id, target_type, target_id, audit_result, risk_score, reason, model_name, create_time
            FROM ai_audit_record
            WHERE 1 = 1
            <if test="targetType != null and targetType != ''">
              AND target_type = #{targetType}
            </if>
            <if test="auditResult != null and auditResult != ''">
              AND audit_result = #{auditResult}
            </if>
            ORDER BY create_time DESC, id DESC
            LIMIT #{pageSize} OFFSET #{offset}
            </script>
            """)
    List<AiAuditRecordListItemVO> findRecords(@Param("targetType") String targetType,
                                              @Param("auditResult") String auditResult,
                                              @Param("pageSize") Integer pageSize,
                                              @Param("offset") Integer offset);

    @Select("""
            SELECT id, target_type, target_id, audit_result, risk_score, reason,
                   model_name, request_content, response_content, create_time
            FROM ai_audit_record
            WHERE id = #{id}
            LIMIT 1
            """)
    AiAuditRecordDetailVO findDetailById(Long id);
}
