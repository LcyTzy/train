# 铁路12306购票系统 - 前端技术架构文档

## 1. 项目结构
```
train-frontend/
── src/
│   ├── api/          # API请求封装
│   ├── components/   # 公共组件
│   ├── pages/        # 页面组件
│   ├── router/       # 路由配置
│   ├── stores/       # 状态管理
│   ├── utils/        # 工具函数
│   ├── App.vue
│   └── main.ts
├── index.html
├── package.json
├── tsconfig.json
── vite.config.ts
└── tailwind.config.js
```

## 2. 路由设计
- / - 首页（车次查询）
- /login - 登录
- /register - 注册
- /order/confirm - 订单确认
- /orders - 订单列表
- /pay/:orderNo - 支付页
- /passengers - 乘客管理

## 3. 状态管理
- userStore: 用户信息、token
- searchStore: 查询条件、查询结果
- orderStore: 当前订单信息

## 4. API封装
- 使用Axios，配置baseURL和拦截器
- 请求拦截器：添加token
- 响应拦截器：统一错误处理

## 5. 组件设计
- StationSearch: 车站搜索选择器
- TrainCard: 车次信息卡片
- PassengerSelector: 乘客选择器
- OrderCard: 订单卡片
