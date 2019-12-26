# chatroomremote
聊天集群消息中转服务。

一台机器websocket长连接有极限，为了实现无限的用户支撑、排除单机故障，在各个websocket前端集群之间进行消息通讯，构建此项目，依赖scala、akka、netty。

架构图画好后补充
