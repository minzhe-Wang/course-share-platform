package com.example.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DashboardMapper {

    @Select("SELECT COUNT(1) FROM sys_user")
    Long countUsers();

    @Select("SELECT COUNT(1) FROM sys_user WHERE status = #{status}")
    Long countUsersByStatus(Integer status);

    @Select("SELECT COUNT(1) FROM sys_user WHERE role = #{role}")
    Long countUsersByRole(String role);

    @Select("SELECT COUNT(1) FROM material")
    Long countMaterials();

    @Select("SELECT COUNT(1) FROM material WHERE audit_status = #{auditStatus}")
    Long countMaterialsByAuditStatus(String auditStatus);

    @Select("SELECT COUNT(1) FROM material WHERE status = #{status}")
    Long countMaterialsByStatus(Integer status);

    @Select("SELECT COUNT(1) FROM question")
    Long countQuestions();

    @Select("SELECT COUNT(1) FROM question WHERE audit_status = #{auditStatus}")
    Long countQuestionsByAuditStatus(String auditStatus);

    @Select("SELECT COUNT(1) FROM question WHERE status = #{status}")
    Long countQuestionsByStatus(Integer status);

    @Select("SELECT COUNT(1) FROM answer")
    Long countAnswers();

    @Select("SELECT COUNT(1) FROM answer_reply")
    Long countReplies();

    @Select("SELECT COUNT(1) FROM download_record")
    Long countDownloads();

    @Select("SELECT COUNT(1) FROM like_record")
    Long countLikes();

    @Select("SELECT COUNT(1) FROM material_favorite")
    Long countFavorites();

    @Select("SELECT COUNT(1) FROM report WHERE handle_status = #{handleStatus}")
    Long countReportsByHandleStatus(String handleStatus);

    @Select("SELECT COUNT(1) FROM ai_audit_record")
    Long countAiAudits();

    @Select("SELECT COUNT(1) FROM ai_audit_record WHERE audit_result = #{auditResult}")
    Long countAiAuditsByResult(@Param("auditResult") String auditResult);
}
