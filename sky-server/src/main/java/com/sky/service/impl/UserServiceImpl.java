package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * projectName: sky-take-out
 *
 * @author: 张轩鸣
 * description:
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取微信登录需要的openid
     * @param code
     * @return
     */
    public String getOpenId(String code) {
        log.info("微信登录code：{}", code);
        log.info("获取微信登录需要的openid");

        // 1.创建map集合，存放微信需要参数
        Map<String,String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");

        // 2.发请求给微信服务器
        String json = HttpClientUtil.doGet(WX_LOGIN, map);

        log.info("微信登录返回的json数据：{}", json);

        // 3.解析json数据，获取openid
        JSONObject jsonObject = JSON.parseObject(json);
        String openId = jsonObject.getString("openid");

        return openId;
    }

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        // 获取微信登录需要的openid
        String openId = getOpenId(userLoginDTO.getCode());
        // 判断openId是否为空？
        if (openId == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        // 根据openid查询数据库，判断该用户是否存在
        User user = userMapper.getByOpenId(openId);
        // 判断用户是否存在，不存在则创建
        if (user == null) {
            user = User.builder()
                    .openid(openId)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        return user;
    }
}
