# 铁路12306购票系统 - 前端产品需求文档

## 1. 项目概述
高仿铁路12306购票系统的前端页面，提供车次查询、余票查看、购票下单、订单管理、支付等完整流程。

## 2. 技术栈
- Vue 3 + TypeScript + Vite
- Tailwind CSS
- Vue Router
- Axios（HTTP请求）

## 3. 后端API
- 网关地址：http://localhost:8090/api
- 直连地址：http://localhost:8081~8085

### 用户服务 (8081)
- POST /user/register - 注册
- POST /user/login - 登录
- GET /user/info - 获取用户信息
- GET /user/passenger/list - 乘客列表
- POST /user/passenger/add - 添加乘客
- DELETE /user/passenger/{id} - 删除乘客

### 车次服务 (8082)
- GET /train/station/list - 车站列表
- GET /train/query - 查询车次
- GET /train/detail/{trainId} - 车次详情

### 余票服务 (8083)
- GET /ticket/stock - 查询余票

### 订单服务 (8084)
- POST /order/ticket/submit - 提交购票
- GET /order/ticket/result/{requestNo} - 购票结果
- GET /order/list - 订单列表
- GET /order/detail/{orderNo} - 订单详情
- POST /order/cancel/{orderNo} - 取消订单

### 支付服务 (8085)
- POST /pay/create - 创建支付
- GET /pay/query/{orderNo} - 查询支付状态

## 4. 页面设计

### 4.1 首页 - 车次查询
- 出发站/到达站选择（带搜索）
- 出发日期选择
- 车次类型筛选
- 出发时间筛选
- 查询结果列表：车次号、出发/到达时间、历时、各席别余票和价格

### 4.2 登录/注册页
- 用户名、密码、手机号注册
- 用户名、密码登录
- 登录后存储token

### 4.3 订单确认页
- 选择乘客
- 选择席别
- 显示票价合计
- 提交订单

### 4.4 订单列表页
- 订单列表展示
- 订单状态筛选
- 取消/退票操作

### 4.5 支付页
- 订单信息展示
- 模拟支付
- 支付结果

### 4.6 乘客管理页
- 乘客列表
- 添加/删除乘客

## 5. 设计风格
- 主题色：铁路蓝 (#1a5276) + 金色 (#d4a843)
- 风格：简洁现代，信息密度高
- 字体：系统默认字体
- 布局：居中卡片式布局，最大宽度1200px
