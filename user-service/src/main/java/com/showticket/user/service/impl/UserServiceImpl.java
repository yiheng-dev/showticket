package com.showticket.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.showticket.common.exception.BadRequestException;
import com.showticket.common.exception.BizIllegalException;
import com.showticket.common.exception.ForbiddenException;
import com.showticket.common.utils.UserContext;
import com.showticket.user.config.JwtProperties;
import com.showticket.user.domain.dto.LoginFormDTO;
import com.showticket.user.domain.po.User;
import com.showticket.user.domain.vo.UserLoginVO;
import com.showticket.user.enums.UserStatus;
import com.showticket.user.mapper.UserMapper;
import com.showticket.user.service.IUserService;
import com.showticket.user.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final PasswordEncoder passwordEncoder;

    private final JwtTool jwtTool;

    private final JwtProperties jwtProperties;

    @Override
    public UserLoginVO login(LoginFormDTO loginDTO) {
        // 1.数据校验
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        // 2.根据用户名或手机号查询
        User user = lambdaQuery().eq(User::getUsername, username).one();
        Assert.notNull(user, "用户名错误");
        // 3.校验是否禁用
        if (user.getStatus() == UserStatus.FROZEN) {
            throw new ForbiddenException("用户被冻结");
        }
        // 4.校验密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("用户名或密码错误");
        }
        // 5.生成TOKEN
        String token = jwtTool.createToken(user.getId(), jwtProperties.getTokenTTL());
        // 6.封装VO返回
        UserLoginVO vo = new UserLoginVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setPhone(user.getPhone());
        vo.setBalance(user.getBalance());
        vo.setToken(token);
        return vo;
    }

    @Override
    public UserLoginVO queryMe() {
        User user = getById(UserContext.getUser());
        if (user == null) {
            throw new BadRequestException("用户不存在");
        }
        UserLoginVO vo = new UserLoginVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setPhone(user.getPhone());
        vo.setBalance(user.getBalance());
        return vo;
    }

    @Override
    public void deductBalance(String pw, Integer totalFee) {
        log.info("开始扣减余额");
        // 1.校验密码
        User user = getById(UserContext.getUser());
        if(user == null || !passwordEncoder.matches(pw, user.getPassword())){
            // 密码错误
            throw new BizIllegalException("用户密码错误");
        }

        // 2.尝试扣减余额
        try {
            baseMapper.updateBalance(UserContext.getUser(), totalFee);
        } catch (Exception e) {
            throw new RuntimeException("扣减余额失败，可能是余额不足！", e);
        }
        log.info("扣减余额成功");
    }
}
