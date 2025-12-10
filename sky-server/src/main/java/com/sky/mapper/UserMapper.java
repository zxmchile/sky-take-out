package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Map;

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

    /**
     * 根据创建时间统计用户数量
     * @param beginTime
     * @param endTime
     * @return
     */
    @Select("select count(id) from user where create_time between #{beginTime} and #{endTime}")
    Integer countByCreateTime(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 统计用户数量
     * @return
     */
    @Select("select count(id) from user")
    Integer count();

    /**
     * 根据条件统计用户数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
