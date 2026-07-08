-- ============================================
-- 12306购票系统数据库设计
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS train_12306 DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE train_12306;

-- ============================================
-- 1. 用户服务
-- ============================================

-- 用户表
CREATE TABLE t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(256) NOT NULL COMMENT '密码(BCrypt加密)',
    real_name VARCHAR(32) DEFAULT NULL COMMENT '真实姓名',
    id_type INT DEFAULT 0 COMMENT '证件类型: 0-身份证 1-护照 2-军官证',
    id_number VARCHAR(32) DEFAULT NULL COMMENT '证件号码',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    email VARCHAR(64) DEFAULT NULL COMMENT '邮箱',
    verify_status INT DEFAULT 0 COMMENT '核验状态: 0-未核验 1-已核验',
    status INT DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_phone (phone),
    INDEX idx_id_number (id_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 乘客表(乘车人)
CREATE TABLE t_passenger (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '乘客ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    name VARCHAR(32) NOT NULL COMMENT '乘客姓名',
    id_type INT DEFAULT 0 COMMENT '证件类型: 0-身份证 1-护照 2-军官证',
    id_number VARCHAR(32) NOT NULL COMMENT '证件号码',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    passenger_type INT DEFAULT 0 COMMENT '乘客类型: 0-成人 1-儿童 2-学生 3-残疾军人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='乘客表';

-- ============================================
-- 2. 车次/线路服务
-- ============================================

-- 车站表
CREATE TABLE t_station (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '车站ID',
    name VARCHAR(64) NOT NULL UNIQUE COMMENT '车站名称',
    code VARCHAR(16) NOT NULL UNIQUE COMMENT '车站编码(如: BJP, SHH)',
    province VARCHAR(32) DEFAULT NULL COMMENT '所属省份',
    city VARCHAR(32) DEFAULT NULL COMMENT '所属城市',
    status INT DEFAULT 1 COMMENT '状态: 0-停用 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车站表';

-- 车次表
CREATE TABLE t_train (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '车次ID',
    train_no VARCHAR(16) NOT NULL UNIQUE COMMENT '车次号(如: G1, D302)',
    train_type INT NOT NULL COMMENT '车次类型: 1-高铁 2-动车 3-直达 4-特快 5-快速',
    start_station_id BIGINT NOT NULL COMMENT '始发站ID',
    end_station_id BIGINT NOT NULL COMMENT '终点站ID',
    start_time TIME NOT NULL COMMENT '发车时间',
    arrive_time TIME NOT NULL COMMENT '到达时间',
    duration INT NOT NULL COMMENT '运行时长(分钟)',
    status INT DEFAULT 1 COMMENT '状态: 0-停运 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_train_no (train_no),
    INDEX idx_start_station (start_station_id),
    INDEX idx_end_station (end_station_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车次表';

-- 车次站点表(车次经停站)
CREATE TABLE t_train_station (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    train_id BIGINT NOT NULL COMMENT '车次ID',
    station_id BIGINT NOT NULL COMMENT '车站ID',
    sequence INT NOT NULL COMMENT '站序(从1开始)',
    arrive_time TIME DEFAULT NULL COMMENT '到达时间(始发站为NULL)',
    depart_time TIME DEFAULT NULL COMMENT '出发时间(终点站为NULL)',
    stop_minutes INT DEFAULT 0 COMMENT '停靠时长(分钟)',
    distance INT DEFAULT 0 COMMENT '距始发站里程(km)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_train_id (train_id),
    INDEX idx_station_id (station_id),
    UNIQUE KEY uk_train_station_seq (train_id, station_id, sequence)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车次站点表';

-- 车厢类型配置表
CREATE TABLE t_carriage_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    train_id BIGINT NOT NULL COMMENT '车次ID',
    carriage_no INT NOT NULL COMMENT '车厢号',
    carriage_type INT NOT NULL COMMENT '车厢类型: 1-商务座 2-一等座 3-二等座 4-硬卧 5-软卧 6-硬座 7-无座',
    seat_count INT NOT NULL COMMENT '座位/铺位总数',
    row_count INT DEFAULT NULL COMMENT '排数(座席车厢)',
    seat_layout VARCHAR(16) DEFAULT NULL COMMENT '座位布局(如: ABC-DF 表示一边3座一边2座)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_train_id (train_id),
    UNIQUE KEY uk_train_carriage (train_id, carriage_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车厢类型配置表';

-- 票价表
CREATE TABLE t_price (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    train_id BIGINT NOT NULL COMMENT '车次ID',
    start_station_id BIGINT NOT NULL COMMENT '始发站ID',
    end_station_id BIGINT NOT NULL COMMENT '终点站ID',
    seat_type INT NOT NULL COMMENT '座位类型: 1-商务座 2-一等座 3-二等座 4-硬卧上 5-硬卧中 6-硬卧下 7-软卧上 8-软卧下 9-硬座 10-无座',
    price DECIMAL(10,2) NOT NULL COMMENT '票价(元)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_train_route (train_id, start_station_id, end_station_id),
    UNIQUE KEY uk_route_seat (train_id, start_station_id, end_station_id, seat_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='票价表';

-- ============================================
-- 3. 余票/库存服务
-- ============================================

-- 余票表(按车次+日期+车厢+座位类型管理库存)
CREATE TABLE t_ticket_stock (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    train_id BIGINT NOT NULL COMMENT '车次ID',
    train_date DATE NOT NULL COMMENT '乘车日期',
    carriage_no INT NOT NULL COMMENT '车厢号',
    seat_type INT NOT NULL COMMENT '座位类型',
    total_count INT NOT NULL DEFAULT 0 COMMENT '总票数',
    available_count INT NOT NULL DEFAULT 0 COMMENT '可用票数',
    sold_count INT NOT NULL DEFAULT 0 COMMENT '已售票数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_train_date (train_id, train_date),
    INDEX idx_query (train_id, train_date, seat_type),
    UNIQUE KEY uk_stock (train_id, train_date, carriage_no, seat_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='余票库存表';

-- 座位明细表(具体每个座位)
CREATE TABLE t_seat (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    train_id BIGINT NOT NULL COMMENT '车次ID',
    train_date DATE NOT NULL COMMENT '乘车日期',
    carriage_no INT NOT NULL COMMENT '车厢号',
    seat_no VARCHAR(8) NOT NULL COMMENT '座位号(如: 01A, 05F)',
    seat_row INT DEFAULT NULL COMMENT '排号',
    seat_col VARCHAR(2) DEFAULT NULL COMMENT '列号(A/B/C/D/F)',
    seat_type INT NOT NULL COMMENT '座位类型',
    status INT DEFAULT 0 COMMENT '状态: 0-可售 1-已售 2-锁定',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_train_date_carriage (train_id, train_date, carriage_no),
    UNIQUE KEY uk_seat (train_id, train_date, carriage_no, seat_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='座位明细表';

-- ============================================
-- 4. 订单服务
-- ============================================

-- 订单表
CREATE TABLE t_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    train_id BIGINT NOT NULL COMMENT '车次ID',
    train_no VARCHAR(16) NOT NULL COMMENT '车次号',
    train_date DATE NOT NULL COMMENT '乘车日期',
    start_station_id BIGINT NOT NULL COMMENT '出发站ID',
    start_station_name VARCHAR(64) NOT NULL COMMENT '出发站名称',
    end_station_id BIGINT NOT NULL COMMENT '到达站ID',
    end_station_name VARCHAR(64) NOT NULL COMMENT '到达站名称',
    depart_time TIME NOT NULL COMMENT '发车时间',
    arrive_time TIME NOT NULL COMMENT '到达时间',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    passenger_count INT NOT NULL DEFAULT 1 COMMENT '乘客人数',
    order_status INT DEFAULT 0 COMMENT '订单状态: 0-待支付 1-已支付 2-出票成功 3-已退票 4-已改签 5-已取消 6-支付超时',
    pay_time DATETIME DEFAULT NULL COMMENT '支付时间',
    cancel_time DATETIME DEFAULT NULL COMMENT '取消时间',
    expire_time DATETIME DEFAULT NULL COMMENT '支付过期时间(下单后10分钟)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_train_date (train_id, train_date),
    INDEX idx_order_no (order_no),
    INDEX idx_expire (order_status, expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单明细表(每个乘客的票)
CREATE TABLE t_order_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    passenger_id BIGINT NOT NULL COMMENT '乘客ID',
    passenger_name VARCHAR(32) NOT NULL COMMENT '乘客姓名',
    passenger_id_type INT NOT NULL COMMENT '证件类型',
    passenger_id_number VARCHAR(32) NOT NULL COMMENT '证件号码',
    carriage_no INT NOT NULL COMMENT '车厢号',
    seat_no VARCHAR(8) NOT NULL COMMENT '座位号',
    seat_type INT NOT NULL COMMENT '座位类型',
    price DECIMAL(10,2) NOT NULL COMMENT '票价',
    ticket_status INT DEFAULT 0 COMMENT '票状态: 0-正常 1-已退票 2-已改签',
    refund_time DATETIME DEFAULT NULL COMMENT '退票时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order_id (order_id),
    INDEX idx_passenger (passenger_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- ============================================
-- 5. 支付服务
-- ============================================

-- 支付表
CREATE TABLE t_pay (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    pay_no VARCHAR(64) NOT NULL UNIQUE COMMENT '支付流水号',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    pay_channel INT DEFAULT 1 COMMENT '支付渠道: 1-微信支付 2-支付宝',
    pay_status INT DEFAULT 0 COMMENT '支付状态: 0-待支付 1-支付成功 2-支付失败 3-已退款',
    transaction_id VARCHAR(64) DEFAULT NULL COMMENT '第三方交易号',
    pay_time DATETIME DEFAULT NULL COMMENT '支付时间',
    callback_content TEXT DEFAULT NULL COMMENT '回调内容(JSON)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_id (order_id),
    INDEX idx_user_id (user_id),
    INDEX idx_pay_no (pay_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付表';

-- ============================================
-- 6. 消息队列相关
-- ============================================

-- 购票请求队列表(用于异步处理)
CREATE TABLE t_ticket_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    request_no VARCHAR(64) NOT NULL UNIQUE COMMENT '请求编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    train_id BIGINT NOT NULL COMMENT '车次ID',
    train_date DATE NOT NULL COMMENT '乘车日期',
    start_station_id BIGINT NOT NULL COMMENT '出发站ID',
    end_station_id BIGINT NOT NULL COMMENT '到达站ID',
    seat_type INT NOT NULL COMMENT '座位类型',
    passenger_ids VARCHAR(512) NOT NULL COMMENT '乘客ID列表(逗号分隔)',
    status INT DEFAULT 0 COMMENT '处理状态: 0-待处理 1-处理中 2-成功 3-失败',
    result_msg VARCHAR(256) DEFAULT NULL COMMENT '处理结果信息',
    order_id BIGINT DEFAULT NULL COMMENT '生成的订单ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购票请求表';
