package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * projectName: sky-take-out
 *
 * @author: 张轩鸣
 * description:
 */
@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     * @param openId
     * @return
     */
    @Select("select * from user where openid = #{openId}")
    User getByOpenId(String openId);

    /**
     * 插入数据
     * @param user
     */
    void insert(User user);

    /**
     * 根据id查询User
     * @param userId
     * @return
     */
    @Select("select * from user where id = #{userId}")
    User getById(Long userId);
}
