package com.example.backend.mapper;

import com.example.backend.entity.Material;
import com.example.backend.vo.AdminMaterialItemVO;
import com.example.backend.vo.MaterialDetailVO;
import com.example.backend.vo.MaterialListItemVO;
import com.example.backend.vo.RecommendationItemVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MaterialMapper {

    @Insert("""
            INSERT INTO material(
                title, description, category_id, file_url, file_key, original_filename,
                file_type, file_size, uploader_id, audit_status, status
            )
            VALUES(
                #{title}, #{description}, #{categoryId}, #{fileUrl}, #{fileKey}, #{originalFilename},
                #{fileType}, #{fileSize}, #{uploaderId}, #{auditStatus}, #{status}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Material material);

    @Insert("""
            INSERT INTO material_tag(material_id, tag_id)
            VALUES(#{materialId}, #{tagId})
            """)
    int insertMaterialTag(@Param("materialId") Long materialId, @Param("tagId") Long tagId);

    @Update("""
            UPDATE material
            SET audit_status = #{auditStatus},
                audit_remark = #{auditRemark},
                audit_time = #{auditTime}
            WHERE id = #{id}
            """)
    int updateAuditResult(@Param("id") Long id,
                          @Param("auditStatus") String auditStatus,
                          @Param("auditRemark") String auditRemark,
                          @Param("auditTime") LocalDateTime auditTime);

    @Select("""
            <script>
            SELECT COUNT(DISTINCT m.id)
            FROM material m
            <if test="tagId != null">
                INNER JOIN material_tag mt ON mt.material_id = m.id
            </if>
            WHERE m.audit_status = 'APPROVED'
              AND m.status = 1
            <if test="keyword != null and keyword != ''">
              AND (m.title LIKE CONCAT('%', #{keyword}, '%')
                   OR m.description LIKE CONCAT('%', #{keyword}, '%')
                   OR m.original_filename LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="categoryId != null">
              AND m.category_id = #{categoryId}
            </if>
            <if test="tagId != null">
              AND mt.tag_id = #{tagId}
            </if>
            </script>
            """)
    Long countApprovedMaterials(@Param("keyword") String keyword,
                                @Param("categoryId") Long categoryId,
                                @Param("tagId") Long tagId);

    @Select("""
            <script>
            SELECT DISTINCT
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
                m.create_time
            FROM material m
            INNER JOIN course_category c ON c.id = m.category_id
            INNER JOIN sys_user u ON u.id = m.uploader_id
            <if test="tagId != null">
                INNER JOIN material_tag mt ON mt.material_id = m.id
            </if>
            WHERE m.audit_status = 'APPROVED'
              AND m.status = 1
            <if test="keyword != null and keyword != ''">
              AND (m.title LIKE CONCAT('%', #{keyword}, '%')
                   OR m.description LIKE CONCAT('%', #{keyword}, '%')
                   OR m.original_filename LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="categoryId != null">
              AND m.category_id = #{categoryId}
            </if>
            <if test="tagId != null">
              AND mt.tag_id = #{tagId}
            </if>
            ORDER BY ${orderBy}
            LIMIT #{pageSize} OFFSET #{offset}
            </script>
            """)
    List<MaterialListItemVO> findApprovedMaterials(@Param("keyword") String keyword,
                                                   @Param("categoryId") Long categoryId,
                                                   @Param("tagId") Long tagId,
                                                   @Param("orderBy") String orderBy,
                                                   @Param("pageSize") Integer pageSize,
                                                   @Param("offset") Integer offset);

    @Select("""
            SELECT
                m.id,
                m.title,
                m.description,
                m.category_id,
                c.name AS category_name,
                m.file_url,
                m.file_key,
                m.file_type,
                m.file_size,
                u.nickname AS uploader_name,
                m.view_count,
                m.download_count,
                m.like_count,
                m.favorite_count,
                m.create_time
            FROM material m
            INNER JOIN course_category c ON c.id = m.category_id
            INNER JOIN sys_user u ON u.id = m.uploader_id
            WHERE m.id = #{id}
              AND m.audit_status = 'APPROVED'
              AND m.status = 1
            LIMIT 1
            """)
    MaterialDetailVO findApprovedDetailById(Long id);

    @Update("""
            UPDATE material
            SET view_count = view_count + 1
            WHERE id = #{id}
            """)
    int incrementViewCount(Long id);

    @Update("""
            UPDATE material
            SET download_count = download_count + 1
            WHERE id = #{id}
            """)
    int incrementDownloadCount(Long id);

    @Update("""
            UPDATE material
            SET like_count = like_count + 1
            WHERE id = #{id}
            """)
    int incrementLikeCount(Long id);

    @Update("""
            UPDATE material
            SET favorite_count = favorite_count + 1
            WHERE id = #{id}
            """)
    int incrementFavoriteCount(Long id);

    @Update("""
            UPDATE material
            SET favorite_count = CASE WHEN favorite_count > 0 THEN favorite_count - 1 ELSE 0 END
            WHERE id = #{id}
            """)
    int decrementFavoriteCount(Long id);

    @Insert("""
            INSERT INTO download_record(user_id, material_id, ip)
            VALUES(#{userId}, #{materialId}, #{ip})
            """)
    int insertDownloadRecord(@Param("userId") Long userId, @Param("materialId") Long materialId, @Param("ip") String ip);

    @Select("""
            SELECT COUNT(1)
            FROM like_record
            WHERE user_id = #{userId}
              AND target_type = 'MATERIAL'
              AND target_id = #{materialId}
            """)
    int countMaterialLike(@Param("userId") Long userId, @Param("materialId") Long materialId);

    @Insert("""
            INSERT INTO like_record(user_id, target_type, target_id)
            VALUES(#{userId}, 'MATERIAL', #{materialId})
            """)
    int insertMaterialLike(@Param("userId") Long userId, @Param("materialId") Long materialId);

    @Select("""
            SELECT COUNT(1)
            FROM material_favorite
            WHERE user_id = #{userId}
              AND material_id = #{materialId}
            """)
    int countMaterialFavorite(@Param("userId") Long userId, @Param("materialId") Long materialId);

    @Insert("""
            INSERT INTO material_favorite(user_id, material_id)
            VALUES(#{userId}, #{materialId})
            """)
    int insertMaterialFavorite(@Param("userId") Long userId, @Param("materialId") Long materialId);

    @Delete("""
            DELETE FROM material_favorite
            WHERE user_id = #{userId}
              AND material_id = #{materialId}
            """)
    int deleteMaterialFavorite(@Param("userId") Long userId, @Param("materialId") Long materialId);

    @Select("""
            SELECT id, title, audit_status, status
            FROM material
            WHERE id = #{id}
            LIMIT 1
            """)
    Material findById(Long id);

    @Select("""
            <script>
            SELECT COUNT(1)
            FROM material m
            WHERE 1 = 1
            <if test="keyword != null and keyword != ''">
              AND (m.title LIKE CONCAT('%', #{keyword}, '%')
                   OR m.description LIKE CONCAT('%', #{keyword}, '%')
                   OR m.original_filename LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="auditStatus != null and auditStatus != ''">
              AND m.audit_status = #{auditStatus}
            </if>
            <if test="status != null">
              AND m.status = #{status}
            </if>
            </script>
            """)
    Long countAdminMaterials(@Param("keyword") String keyword,
                             @Param("auditStatus") String auditStatus,
                             @Param("status") Integer status);

    @Select("""
            <script>
            SELECT
                m.id,
                m.title,
                c.name AS category_name,
                u.nickname AS uploader_name,
                m.file_type,
                m.audit_status,
                m.audit_remark,
                m.status,
                m.view_count,
                m.download_count,
                m.like_count,
                m.favorite_count,
                m.create_time
            FROM material m
            INNER JOIN course_category c ON c.id = m.category_id
            INNER JOIN sys_user u ON u.id = m.uploader_id
            WHERE 1 = 1
            <if test="keyword != null and keyword != ''">
              AND (m.title LIKE CONCAT('%', #{keyword}, '%')
                   OR m.description LIKE CONCAT('%', #{keyword}, '%')
                   OR m.original_filename LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="auditStatus != null and auditStatus != ''">
              AND m.audit_status = #{auditStatus}
            </if>
            <if test="status != null">
              AND m.status = #{status}
            </if>
            ORDER BY m.create_time DESC, m.id DESC
            LIMIT #{pageSize} OFFSET #{offset}
            </script>
            """)
    List<AdminMaterialItemVO> findAdminMaterials(@Param("keyword") String keyword,
                                                 @Param("auditStatus") String auditStatus,
                                                 @Param("status") Integer status,
                                                 @Param("pageSize") Integer pageSize,
                                                 @Param("offset") Integer offset);

    @Update("""
            UPDATE material
            SET status = #{status}
            WHERE id = #{id}
            """)
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

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
                m.create_time
            FROM material m
            INNER JOIN course_category c ON c.id = m.category_id
            INNER JOIN sys_user u ON u.id = m.uploader_id
            WHERE m.audit_status = 'APPROVED'
              AND m.status = 1
            ORDER BY
                (m.download_count * 3 + m.favorite_count * 2 + m.like_count * 2 + m.view_count) DESC,
                m.create_time DESC,
                m.id DESC
            LIMIT #{limit}
            """)
    List<MaterialListItemVO> findHotMaterials(Integer limit);

    @Select("""
            SELECT
                'MATERIAL' AS target_type,
                m.id AS target_id,
                m.title,
                m.description,
                c.name AS category_name,
                (
                    m.download_count * 3
                    + m.favorite_count * 2
                    + m.like_count * 2
                    + m.view_count
                    + CASE
                        WHEN m.create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) THEN 12
                        WHEN m.create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY) THEN 6
                        ELSE 0
                      END
                ) AS score,
                CASE
                    WHEN m.download_count >= 3 THEN '收藏和下载较多'
                    WHEN m.favorite_count >= 3 THEN '收藏和下载较多'
                    WHEN m.like_count >= 3 THEN '近期热度较高'
                    WHEN m.view_count >= 5 THEN '近期热度较高'
                    ELSE '适合优先查看'
                END AS reason,
                m.create_time
            FROM material m
            INNER JOIN course_category c ON c.id = m.category_id
            WHERE m.audit_status = 'APPROVED'
              AND m.status = 1
            ORDER BY score DESC, m.create_time DESC, m.id DESC
            LIMIT #{limit}
            """)
    List<RecommendationItemVO> findRecommendedMaterials(Integer limit);
}

