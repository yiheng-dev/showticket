package com.showticket.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.showticket.user.domain.dto.LoginFormDTO;
import com.showticket.user.domain.po.User;
import com.showticket.user.domain.vo.UserLoginVO;

/**
 * <p>
 * 用户表 服务类
 * </p>
 */
public interface IUserService extends IService<User> {

    UserLoginVO login(LoginFormDTO loginFormDTO);

    UserLoginVO queryMe();

    void deductBalance(String pw, Integer totalFee);
}
