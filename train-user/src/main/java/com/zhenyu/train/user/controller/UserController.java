package com.zhenyu.train.user.controller;

import com.zhenyu.train.common.result.Result;
import com.zhenyu.train.common.util.UserContext;
import com.zhenyu.train.user.dto.*;
import com.zhenyu.train.user.entity.Passenger;
import com.zhenyu.train.user.entity.User;
import com.zhenyu.train.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.success();
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return Result.success(response);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo() {
        Long userId = UserContext.getUserId();
        User user = userService.getUserInfo(userId);
        return Result.success(user);
    }

    /**
     * 身份核验
     */
    @PostMapping("/verify")
    public Result<Void> verify(@Valid @RequestBody VerifyRequest request) {
        Long userId = UserContext.getUserId();
        userService.verify(userId, request);
        return Result.success();
    }

    /**
     * 获取乘客列表
     */
    @GetMapping("/passenger/list")
    public Result<List<Passenger>> getPassengerList() {
        Long userId = UserContext.getUserId();
        List<Passenger> list = userService.getPassengerList(userId);
        return Result.success(list);
    }

    /**
     * 添加乘客
     */
    @PostMapping("/passenger/add")
    public Result<Void> addPassenger(@Valid @RequestBody PassengerRequest request) {
        Long userId = UserContext.getUserId();
        userService.addPassenger(userId, request);
        return Result.success();
    }

    /**
     * 删除乘客
     */
    @DeleteMapping("/passenger/{id}")
    public Result<Void> deletePassenger(@PathVariable("id") Long id) {
        Long userId = UserContext.getUserId();
        userService.deletePassenger(userId, id);
        return Result.success();
    }

    /**
     * 获取乘客信息（供Feign调用）
     */
    @GetMapping("/passenger/info/{id}")
    public java.util.Map<String, Object> getPassengerInfo(@PathVariable Long id) {
        Passenger passenger = userService.getPassengerById(id);
        if (passenger == null) {
            return new java.util.HashMap<>();
        }
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("passengerId", passenger.getId());
        result.put("name", passenger.getName());
        result.put("idType", passenger.getIdType());
        result.put("idNumber", passenger.getIdNumber());
        result.put("passengerType", passenger.getPassengerType());
        return result;
    }
}
