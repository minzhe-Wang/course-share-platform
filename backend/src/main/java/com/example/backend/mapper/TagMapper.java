package com.example.backend.mapper;

import com.example.backend.entity.Tag;
import com.example.backend.vo.AdminTagVO;
import com.example.backend.vo.TagVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TagMapper {

    @Select("""
            <script>
            SELECT id, name, type, status, create_time, update_time
            FROM tag
            WHERE id IN
            <foreach collection="ids" item="id" open="(" separator="," close=")">
                #{id}
            </foreach>
            </script>
            """)
    List<Tag> findByIds(@Param("ids") List<Long> ids);

    @Select("""
            <script>
            SELECT id, name, type
            FROM tag
            WHERE status = 1
            <if test="type != null and type != ''">
              AND type = #{type}
            </if>
            ORDER BY id ASC
            </script>
            """)
    List<TagVO> findEnabledTags(@Param("type") String type);

    @Select("""
            SELECT t.id, t.name, t.type
            FROM tag t
            INNER JOIN material_tag mt ON mt.tag_id = t.id
            WHERE mt.material_id = #{materialId}
            ORDER BY t.id ASC
            """)
    List<TagVO> findByMaterialId(Long materialId);

    @Select("""
            SELECT id, name, type, status, create_time, update_time
            FROM tag
            WHERE id = #{id}
            LIMIT 1
            """)
    Tag findById(Long id);

    @Select("""
            <script>
            SELECT COUNT(1)
            FROM tag
            WHERE 1 = 1
            <if test="keyword != null and keyword != ''">
              AND (name LIKE CONCAT('%', #{keyword}, '%')
                   OR type LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="type != null and type != ''">
              AND type = #{type}
            </if>
            <if test="status != null">
              AND status = #{status}
            </if>
            </script>
            """)
    Long countAdminTags(@Param("keyword") String keyword,
                        @Param("type") String type,
                        @Param("status") Integer status);

    @Select("""
            <script>
            SELECT id, name, type, status, create_time, update_time
            FROM tag
            WHERE 1 = 1
            <if test="keyword != null and keyword != ''">
              AND (name LIKE CONCAT('%', #{keyword}, '%')
                   OR type LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="type != null and type != ''">
              AND type = #{type}
            </if>
            <if test="status != null">
              AND status = #{status}
            </if>
            ORDER BY id ASC
            LIMIT #{pageSize} OFFSET #{offset}
            </script>
            """)
    List<AdminTagVO> findAdminTags(@Param("keyword") String keyword,
                                   @Param("type") String type,
                                   @Param("status") Integer status,
                                   @Param("pageSize") Integer pageSize,
                                   @Param("offset") Integer offset);

    @Select("""
            SELECT COUNT(1)
            FROM tag
            WHERE name = #{name}
              AND type = #{type}
              AND id <> #{excludeId}
            """)
    int countByNameAndTypeExcludeId(@Param("name") String name,
                                    @Param("type") String type,
                                    @Param("excludeId") Long excludeId);

    @Insert("""
            INSERT INTO tag(name, type, status)
            VALUES(#{name}, #{type}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Tag tag);

    @Update("""
            UPDATE tag
            SET name = #{name},
                type = #{type}
            WHERE id = #{id}
            """)
    int update(Tag tag);

    @Update("""
            UPDATE tag
            SET status = #{status}
            WHERE id = #{id}
            """)
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
