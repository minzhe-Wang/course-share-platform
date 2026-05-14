package com.example.backend.mapper;

import com.example.backend.entity.SysUser;
import com.example.backend.vo.AdminUserListItemVO;
import com.example.backend.vo.UserAnswerItemVO;
import com.example.backend.vo.UserDownloadRecordVO;
import com.example.backend.vo.UserFavoriteMaterialVO;
import com.example.backend.vo.UserMaterialItemVO;
import com.example.backend.vo.UserQuestionItemVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("""
            SELECT id, username, password, nickname, role, phone, email, avatar, status, create_time, update_time
            FROM sys_user
            WHERE username = #{username}
            LIMIT 1
            """)
    SysUser findByUsername(String username);

    @Select("""
            SELECT id, username, password, nickname, role, phone, email, avatar, status, create_time, update_time
            FROM sys_user
            WHERE id = #{id}
            LIMIT 1
            """)
    SysUser findById(Long id);

    @Insert("""
            INSERT INTO sys_user(username, password, nickname, role, status)
            VALUES(#{username}, #{password}, #{nickname}, #{role}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysUser user);

    @Select("""
            <script>
            SELECT COUNT(1)
            FROM sys_user
            WHERE 1 = 1
            <if test="keyword != null and keyword != ''">
              AND (username LIKE CONCAT('%', #{keyword}, '%')
                   OR nickname LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="role != null and role != ''">
              AND role = #{role}
            </if>
            </script>
            """)
    Long countUsers(@Param("keyword") String keyword, @Param("role") String role);

    @Select("""
            <script>
            SELECT id, username, nickname, role, status, create_time
            FROM sys_user
            WHERE 1 = 1
            <if test="keyword != null and keyword != ''">
              AND (username LIKE CONCAT('%', #{keyword}, '%')
                   OR nickname LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="role != null and role != ''">
              AND role = #{role}
            </if>
            ORDER BY create_time DESC, id DESC
            LIMIT #{pageSize} OFFSET #{offset}
            </script>
            """)
    List<AdminUserListItemVO> findUsers(@Param("keyword") String keyword,
                                        @Param("role") String role,
                                        @Param("pageSize") Integer pageSize,
                                        @Param("offset") Integer offset);

    @Update("""
            UPDATE sys_user
            SET status = #{status}
            WHERE id = #{id}
            """)
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("""
            UPDATE sys_user
            SET nickname = #{nickname},
                phone = #{phone},
                email = #{email},
                avatar = #{avatar}
            WHERE id = #{id}
            """)
    int updateProfile(@Param("id") Long id,
                      @Param("nickname") String nickname,
                      @Param("phone") String phone,
                      @Param("email") String email,
                      @Param("avatar") String avatar);

    @Update("""
            UPDATE sys_user
            SET password = #{password}
            WHERE id = #{id}
            """)
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    @Select("""
            SELECT COUNT(1)
            FROM material
            WHERE uploader_id = #{userId}
              AND status = 1
            """)
    Long countUserMaterials(Long userId);

    @Select("""
            SELECT
                m.id,
                m.title,
                m.description,
                c.name AS category_name,
                m.file_type,
                m.file_size,
                m.audit_status,
                m.audit_remark,
                m.view_count,
                m.download_count,
                m.like_count,
                m.favorite_count,
                m.create_time
            FROM material m
            INNER JOIN course_category c ON c.id = m.category_id
            WHERE m.uploader_id = #{userId}
              AND m.status = 1
            ORDER BY m.create_time DESC, m.id DESC
            LIMIT #{pageSize} OFFSET #{offset}
            """)
    List<UserMaterialItemVO> findUserMaterials(@Param("userId") Long userId,
                                               @Param("pageSize") Integer pageSize,
                                               @Param("offset") Integer offset);

    @Select("""
            SELECT COUNT(1)
            FROM material_favorite mf
            INNER JOIN material m ON m.id = mf.material_id
            WHERE mf.user_id = #{userId}
              AND m.audit_status = 'APPROVED'
              AND m.status = 1
            """)
    Long countUserFavorites(Long userId);

    @Select("""
            SELECT
                m.id,
                m.title,
                m.description,
                c.name AS category_name,
                m.file_type,
                m.file_size,
                u.nickname AS uploader_name,
                m.view_count,
                m.download_count,
                m.like_count,
                m.favorite_count,
                mf.create_time AS favorite_time
            FROM material_favorite mf
            INNER JOIN material m ON m.id = mf.material_id
            INNER JOIN course_category c ON c.id = m.category_id
            INNER JOIN sys_user u ON u.id = m.uploader_id
            WHERE mf.user_id = #{userId}
              AND m.audit_status = 'APPROVED'
              AND m.status = 1
            ORDER BY mf.create_time DESC, mf.id DESC
            LIMIT #{pageSize} OFFSET #{offset}
            """)
    List<UserFavoriteMaterialVO> findUserFavorites(@Param("userId") Long userId,
                                                   @Param("pageSize") Integer pageSize,
                                                   @Param("offset") Integer offset);

    @Select("""
            SELECT COUNT(1)
            FROM question
            WHERE user_id = #{userId}
              AND status = 1
            """)
    Long countUserQuestions(Long userId);

    @Select("""
            SELECT
                q.id,
                q.title,
                q.content,
                c.name AS category_name,
                q.audit_status,
                q.audit_remark,
                q.view_count,
                q.answer_count,
                q.like_count,
                q.create_time
            FROM question q
            INNER JOIN course_category c ON c.id = q.category_id
            WHERE q.user_id = #{userId}
              AND q.status = 1
            ORDER BY q.create_time DESC, q.id DESC
            LIMIT #{pageSize} OFFSET #{offset}
            """)
    List<UserQuestionItemVO> findUserQuestions(@Param("userId") Long userId,
                                               @Param("pageSize") Integer pageSize,
                                               @Param("offset") Integer offset);

    @Select("""
            SELECT COUNT(1)
            FROM answer
            WHERE user_id = #{userId}
              AND status = 1
            """)
    Long countUserAnswers(Long userId);

    @Select("""
            SELECT
                a.id,
                a.question_id,
                q.title AS question_title,
                a.content,
                a.audit_status,
                a.audit_remark,
                a.like_count,
                a.reply_count,
                a.create_time
            FROM answer a
            INNER JOIN question q ON q.id = a.question_id
            WHERE a.user_id = #{userId}
              AND a.status = 1
            ORDER BY a.create_time DESC, a.id DESC
            LIMIT #{pageSize} OFFSET #{offset}
            """)
    List<UserAnswerItemVO> findUserAnswers(@Param("userId") Long userId,
                                           @Param("pageSize") Integer pageSize,
                                           @Param("offset") Integer offset);

    @Select("""
            SELECT COUNT(1)
            FROM download_record dr
            INNER JOIN material m ON m.id = dr.material_id
            WHERE dr.user_id = #{userId}
              AND m.audit_status = 'APPROVED'
              AND m.status = 1
            """)
    Long countUserDownloads(Long userId);

    @Select("""
            SELECT
                m.id AS material_id,
                m.title,
                m.original_filename,
                m.file_type,
                m.file_size,
                m.file_url,
                dr.create_time AS download_time
            FROM download_record dr
            INNER JOIN material m ON m.id = dr.material_id
            WHERE dr.user_id = #{userId}
              AND m.audit_status = 'APPROVED'
              AND m.status = 1
            ORDER BY dr.create_time DESC, dr.id DESC
            LIMIT #{pageSize} OFFSET #{offset}
            """)
    List<UserDownloadRecordVO> findUserDownloads(@Param("userId") Long userId,
                                                 @Param("pageSize") Integer pageSize,
                                                 @Param("offset") Integer offset);
}
