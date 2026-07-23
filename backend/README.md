# 拾光校园失物招领后端

## 本地启动

项目默认使用本地 H2 文件数据库，无需提前安装数据库。

数据访问层使用 MyBatis，Mapper 接口位于 `auth` 包，SQL 映射文件位于 `src/main/resources/mappers`。

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

本地前端直接访问 `http://localhost:8080/api`。线上部署时，将前端的 `VITE_API_BASE_URL` 设置为后端 HTTPS 地址（例如 `https://api.example.com/api`）。

## 图片对象存储

开发环境使用 MinIO，生产环境使用 Cloudflare R2。两者都通过 S3 API 接入，业务代码无需切换。

启动本地 MinIO：

```powershell
docker compose up -d
```

- S3 API：`http://localhost:9100`
- MinIO 控制台：`http://localhost:9101`
- 本地账号：`minioadmin`
- 本地密码：`minioadmin123`
- 默认 Bucket：`lost-found-images`（首次上传时自动创建）

图片上传接口为 `POST /api/files/images`，使用 `multipart/form-data`，字段名为 `file`。支持 JPG、PNG、WebP，单张最大 5MB。

线上切换 R2 时设置 `.env.example` 中的 `R2_*` 环境变量，并启动：

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql,r2
```

R2 密钥只能设置为服务器环境变量，不能提交到 GitHub。MySQL 仅保存图片的对象 Key 和 URL，图片字节保存在 MinIO/R2。
