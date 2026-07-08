package com.zhenyu.train.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhenyu.train.common.exception.BusinessException;
import com.zhenyu.train.common.result.ResultCode;
import com.zhenyu.train.common.util.JwtUtil;
import com.zhenyu.train.user.dto.*;
import com.zhenyu.train.user.entity.Passenger;
import com.zhenyu.train.user.entity.User;
import com.zhenyu.train.user.mapper.PassengerMapper;
import com.zhenyu.train.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户服务
 */
@Service
@RequiredArgsConstructor
public class UserService extends ServiceImpl<UserMapper, User> {

    private final PassengerMapper passengerMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户注册
     */
    @Transactional
    public void register(RegisterRequest request) {
        // 检查用户名是否已存在
        User existUser = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (existUser != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        // 检查手机号是否已注册
        User existPhone = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, request.getPhone()));
        if (existPhone != null) {
            throw new BusinessException(ResultCode.USER_PHONE_EXISTS);
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setVerifyStatus(0);
        user.setStatus(1);
        save(user);
    }

    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (user == null) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.FORBIDDEN, "账号已被禁用");
        }

        // 生成Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        return response;
    }

    /**
     * 获取用户信息
     */
    public User getUserInfo(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        user.setPassword(null); // 不返回密码
        return user;
    }

    /**
     * 身份核验
     */
    @Transactional
    public void verify(Long userId, VerifyRequest request) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        user.setRealName(request.getRealName());
        user.setIdType(request.getIdType());
        user.setIdNumber(request.getIdNumber());
        user.setVerifyStatus(1);
        updateById(user);
    }

    /**
     * 获取乘客列表
     */
    public List<Passenger> getPassengerList(Long userId) {
        return passengerMapper.selectList(new LambdaQueryWrapper<Passenger>()
                .eq(Passenger::getUserId, userId));
    }

    /**
     * 添加乘客
     */
    @Transactional
    public void addPassenger(Long userId, PassengerRequest request) {
        Passenger passenger = new Passenger();
        passenger.setUserId(userId);
        passenger.setName(request.getName());
        passenger.setIdType(request.getIdType());
        passenger.setIdNumber(request.getIdNumber());
        passenger.setPhone(request.getPhone());
        passenger.setPassengerType(request.getPassengerType() != null ? request.getPassengerType() : 0);
        passengerMapper.insert(passenger);
    }

    /**
     * 删除乘客
     */
    @Transactional
    public void deletePassenger(Long userId, Long passengerId) {
        Passenger passenger = passengerMapper.selectById(passengerId);
        if (passenger == null || !passenger.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "乘客不存在");
        }
        passengerMapper.deleteById(passengerId);
    }

    /**
     * 根据ID获取乘客
     */
    public Passenger getPassengerById(Long passengerId) {
        return passengerMapper.selectById(passengerId);
    }
}
