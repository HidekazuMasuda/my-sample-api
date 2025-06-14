package com.example.demo.mapper;

import com.example.demo.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Options;

import java.util.List;

@Mapper
public interface UserMapper {
    
    @Select("SELECT id, name, email, created_at as createdAt FROM users ORDER BY created_at DESC")
    List<User> selectAll();
    
    @Select("SELECT id, name, email, created_at as createdAt FROM users WHERE id = #{id}")
    User selectById(Long id);
    
    @Insert("INSERT INTO users (name, email, created_at) VALUES (#{name}, #{email}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    @Select("SELECT COUNT(*) FROM users")
    int count();
    
    @Delete("DELETE FROM users")
    int deleteAll();
}