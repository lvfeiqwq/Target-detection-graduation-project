package com.lvfei.sys.service;

import com.lvfei.common.vo.Result;
import com.lvfei.sys.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author laocai
 * @since 2023-02-07
 */
public interface IUserService extends IService<User> {

    Map<String, Object> login(User user);

    Map<String, Object> getUserInfo(String token);

    User getAllInfo(String token);

    void logout(String token);
}
