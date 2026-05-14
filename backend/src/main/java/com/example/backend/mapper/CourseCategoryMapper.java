package com.example.backend.mapper;

import com.example.backend.entity.CourseCategory;
import com.example.backend.vo.AdminCourseCategoryVO;
import com.example.backend.vo.CourseCategoryVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CourseCategoryMapper {

    @Select("""
            SELECT id, name, type, sort_no, status, create_time, update_time
            FROM course_category
            WHERE id = #{id}
            LIMIT 1
            """)
    CourseCategory findById(Long id);

    @Select("""
            SELECT id, name, type, sort_no
            FROM course_category
            WHERE status = 1
            ORDER BY sort_no ASC, id ASC
            """)
    List<CourseCategoryVO> findEnabledCategories();

    @Select("""
            <script>
            SELECT COUNT(1)
            FROM course_category
            WHERE 1 = 1
            <if test="keyword != null and keyword != ''">
              AND (name LIKE CONCAT('%', #{keyword}, '%')
                   OR type LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="status != null">
              AND status = #{status}
            </if>
            </script>
            """)
    Long countAdminCategories(@Param("keyword") String keyword, @Param("status") Integer status);

    @Select("""
            <script>
            SELECT id, name, type, sort_no, status, create_time, update_time
            FROM course_category
            WHERE 1 = 1
            <if test="keyword != null and keyword != ''">
              AND (name LIKE CONCAT('%', #{keyword}, '%')
                   OR type LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="status != null">
              AND status = #{status}
            </if>
            ORDER BY sort_no ASC, id ASC
            LIMIT #{pageSize} OFFSET #{offset}
            </script>
            """)
    List<AdminCourseCategoryVO> findAdminCategories(@Param("keyword") String keyword,
                                                    @Param("status") Integer status,
                                                    @Param("pageSize") Integer pageSize,
                                                    @Param("offset") Integer offset);

    @Select("""
            SELECT COUNT(1)
            FROM course_category
            WHERE name = #{name}
              AND id <> #{excludeId}
            """)
    int countByNameExcludeId(@Param("name") String name, @Param("excludeId") Long excludeId);

    @Insert("""
            INSERT INTO course_category(name, type, sort_no, status)
            VALUES(#{name}, #{type}, #{sortNo}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CourseCategory courseCategory);

    @Update("""
            UPDATE course_category
            SET name = #{name},
                type = #{type},
                sort_no = #{sortNo}
            WHERE id = #{id}
            """)
    int update(CourseCategory courseCategory);

    @Update("""
            UPDATE course_category
            SET status = #{status}
            WHERE id = #{id}
            """)
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
