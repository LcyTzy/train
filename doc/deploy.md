# 12306购票系统 - 部署说明

## 项目架构

```
train-12306/
├── train-common/       # 公共模块(工具类、异常处理、配置)
├── train-user/         # 用户服务 (端口: 8081)
├── train-train/        # 车次服务 (端口: 8082)
├── train-ticket/       # 余票服务 (端口: 8083)
├── train-order/        # 订单服务 (端口: 8084)
├── train-pay/          # 支付服务 (端口: 8085)
└── train-gateway/      # API网关 (端口: 8080)
```

## 技术栈

- **Spring Boot 3.2.0**
- **Spring Cloud 2023.0.0**
- **Spring Cloud Alibaba 2023.0.0.0**
- **Nacos** - 注册中心/配置中心
- **Sentinel** - 流量控制/熔断降级
- **MyBatis Plus** - ORM框架
- **MySQL 8.0** - 数据库
- **Redis** - 缓存/分布式锁
- **Redisson** - 分布式锁实现
- **RocketMQ** - 消息队列
- **JWT** - 身份认证

## 环境准备

### 1. 安装基础软件

确保已安装以下软件：
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.x (下载地址: https://github.com/alibaba/nacos/releases)
- RocketMQ 5.x (下载地址: https://rocketmq.apache.org/zh/download/)

### 2. 初始化数据库

```bash
# 登录MySQL
mysql -u root -p

# 执行数据库脚本
source doc/database.sql

# 导入测试数据
source doc/test-data.sql
```

### 3. 启动Nacos

```bash
# Windows
startup.cmd -m standalone

# Linux/Mac
sh startup.sh -m standalone
```

访问 Nacos 控制台: http://localhost:8848/nacos
- 用户名: nacos
- 密码: nacos

### 4. 启动RocketMQ

```bash
# 启动NameServer
mqnamesrv

# 启动Broker
mqbroker -n localhost:9876
```

### 5. 启动Redis

```bash
redis-server
```

## 项目构建

```bash
# 编译打包
mvn clean package -DskipTests

# 或只编译不打包
mvn clean install -DskipTests
```

## 启动服务

按以下顺序启动微服务：

### 1. 启动网关服务
```bash
cd train-gateway
mvn spring-boot:run
```

### 2. 启动用户服务
```bash
cd train-user
mvn spring-boot:run
```

### 3. 启动车次服务
```bash
cd train-train
mvn spring-boot:run
```

### 4. 启动余票服务
```bash
cd train-ticket
mvn spring-boot:run
```

### 5. 启动订单服务
```bash
cd train-order
mvn spring-boot:run
```

### 6. 启动支付服务
```bash
cd train-pay
mvn spring-boot:run
```

## API接口测试

### 1. 用户注册
```bash
curl -X POST http://localhost:8080/api/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456",
    "phone": "13800138000"
  }'
```

### 2. 用户登录
```bash
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456"
  }'
```

响应示例：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "username": "testuser"
  }
}
```

### 3. 查询车次
```bash
curl -X GET "http://localhost:8080/api/train/query?startStation=北京南&endStation=上海虹桥&trainDate=2026-02-01"
```

### 4. 查询余票
```bash
curl -X GET "http://localhost:8080/api/ticket/stock?trainId=1&trainDate=2026-02-01&startStationId=1&endStationId=2"
```

### 5. 提交购票请求
```bash
curl -X POST http://localhost:8080/api/order/ticket/submit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "trainId": 1,
    "trainDate": "2026-02-01",
    "startStationId": 1,
    "endStationId": 2,
    "seatType": 3,
    "passengerIds": [1, 2]
  }'
```

### 6. 查询订单列表
```bash
curl -X GET "http://localhost:8080/api/order/list?page=1&size=10" \
  -H "Authorization: Bearer {token}"
```

## 核心功能说明

### 1. 防超卖机制
- **Redis Lua脚本**: 原子性扣减库存
- **Redisson分布式锁**: 防止同一座位并发下单
- **数据库乐观锁**: 最终一致性保障

### 2. 流量削峰
- 购票请求写入RocketMQ消息队列
- 消费者异步处理下单逻辑
- 前端轮询或WebSocket获取结果

### 3. 缓存预热
- 热门车次库存提前加载到Redis
- 余票查询全部走Redis缓存
- 缓存与数据库双写一致性

### 4. 分布式事务
- Seata AT模式处理跨服务事务
- 最终一致性方案(消息队列)

## 配置说明

### 数据库配置
各服务的 `application.yml` 中配置数据库连接：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/train_12306
    username: root
    password: root
```

### Redis配置
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

### Nacos配置
```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
```

### RocketMQ配置
```yaml
rocketmq:
  name-server: localhost:9876
  producer:
    group: xxx-producer-group
```

## 常见问题

### 1. 服务注册失败
- 检查Nacos是否正常启动
- 检查网络连通性
- 查看服务日志

### 2. Redis连接失败
- 检查Redis是否启动
- 检查Redis密码配置
- 检查防火墙设置

### 3. 数据库连接失败
- 检查MySQL是否启动
- 检查数据库用户名密码
- 检查数据库是否存在

### 4. RocketMQ连接失败
- 检查NameServer和Broker是否启动
- 检查端口9876是否开放

## 性能优化建议

1. **数据库优化**
   - 合理配置连接池参数
   - 使用分库分表(ShardingSphere)
   - 添加合适的索引

2. **缓存优化**
   - 合理设置缓存过期时间
   - 使用Redis集群
   - 缓存预热策略

3. **消息队列优化**
   - 合理配置消费者线程数
   - 批量消费提高吞吐量
   - 消息重试机制

4. **JVM优化**
   - 调整堆内存大小
   - 选择合适的垃圾回收器
   - 启用JVM监控

## 监控与运维

### 1. 服务监控
- Spring Boot Actuator
- Nacos服务健康检查
- Sentinel实时监控

### 2. 日志管理
- 统一日志格式
- 日志级别配置
- 日志收集(ELK)

### 3. 链路追踪
- Spring Cloud Sleuth
- Zipkin/SkyWalking

## 下一步开发计划

- [ ] 实现改签功能
- [ ] 完善退票逻辑
- [ ] 添加短信验证码
- [ ] 实现身份核验接口
- [ ] 配置ShardingSphere分库分表
- [ ] 集成Seata分布式事务
- [ ] 添加Sentinel限流规则
- [ ] 实现WebSocket推送购票结果
- [ ] 前端页面开发

## 联系方式

如有问题，请联系开发团队。
