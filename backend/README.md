# 拾光校园失物招领后端

## 本地启动

项目默认使用本地 H2 文件数据库，无需提前安装数据库。

```powershell
$env:JAVA_HOME='C:\Users\rosen\.jdks\ms-17.0.19'
.\mvnw.cmd spring-boot:run
```

后端地址为 `http://localhost:8080`，健康检查为 `/actuator/health`。

默认管理员账号：`admin`，默认密码：`Admin@123456`。部署前必须通过环境变量 `ADMIN_ACCOUNT` 和 `ADMIN_PASSWORD` 修改。

## 切换 MySQL

先创建数据库 `lost_found`，然后设置 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD` 并使用：

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql
```

前端开发服务器会把 `/api` 自动代理到本后端。线上部署时，将前端的 `VITE_API_BASE_URL` 设置为后端 HTTPS 地址（例如 `https://api.example.com/api`）。
