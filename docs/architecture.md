# 架构说明

## 模块边界

| 模块 | 职责 | 存储 |
| --- | --- | --- |
| `web` | 商城首页、详情、订单、语音导购弹窗 | 浏览器状态 |
| `mall-service` | 商品目录、用户订单、支付回调、拼团结果通知 | MySQL |
| `group-buy-service` | 营销活动、折扣、库存、组队与交易规则 | MySQL、Redis |
| `voice-agent` | ASR、意图识别、检索推荐、商城动作、TTS | PostgreSQL/pgvector、Redis |

## 商品推荐到开团

1. 首页从商城服务读取真实商品目录，并注册当前 Agent 会话允许访问的商品集合。
2. 语音经 WebSocket 上传，ASR 输出最终文本。
3. Agent 提取品类和预算槽位，结合向量召回、SQL 硬过滤与画像重排生成推荐。
4. 推荐卡片携带商城 `goodsId` 与 `activityId`，不会在 Agent 数据库伪造商城订单。
5. 用户说“打开第一款拼团”后，Agent 返回结构化动作，父页面跳转详情并调用真实拼团流程。

## 部署边界

浏览器只访问 Nginx 的 `8088` 端口。`/mall-api`、`/group-api` 和 `/agent-api` 分别代理到三个内部服务，其中 `/agent-api/ws/voice` 支持 WebSocket Upgrade。服务器部署时只需将 `8088` 置于域名和 HTTPS 反向代理之后。

