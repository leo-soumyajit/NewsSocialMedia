# 📢 Social Media Platform API (Backend)

A powerful and scalable backend service built with **Spring Boot**, tailored for a **news-focused social media platform**. This **RESTful API** allows users to create, engage, and interact with posts, manage likes and comments, and ensures secure access through robust authentication and authorization mechanisms.

---

<div align="center">
  <img src="https://forthebadge.com/images/badges/built-with-love.svg" />&nbsp;
  <img src="https://forthebadge.com/images/badges/uses-brains.svg" />&nbsp;
  <img src="https://forthebadge.com/images/badges/powered-by-responsibility.svg"/>
</div>
<br/>

---

## 🚀 Features

- 📝 **Create & Retrieve News Posts**
- 👍 **Like / ❤️ Remove Like** on Posts
- 💬 **Comment** on Posts
- 🔐 **User Registration & Secure Authentication**
- 🧑‍⚖️ **Role-based Authorization** (User / Admin)
- ✉️ **Email Verification via Java Mail**
- 🧰 **Robust Error Handling** & Response Management
- 📘 **Interactive API Testing with Swagger UI**
- 🔒 **Password Encryption** using BCrypt

---

## 🛠️ Tech Stack

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/POSTGRESQL-005C84?style=for-the-badge&logo=postgresql&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![Docker](https://img.shields.io/badge/Docker-4169E1?style=for-the-badge&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-635BFF?style=for-the-badge&logo=redis&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ_IDEA-000000?style=for-the-badge&logo=intellij-idea&logoColor=white)

---


## 🐳 Docker & 🔧 Deployment Instructions
pull the public image from Docker Hub:
```bash
docker pull soumyajit2005/newsly:v0.0.1
```
```bash
docker run -p 8000:8000 soumyajit2005/newsly:v0.0.1
```

🌐 Deployment
This backend is hosted live on Render using Docker.

📍 API Base URL:
```bash
https://newsly-v0-0-1.onrender.com/api/v1/posts
```

🐦 Try Endpoints in Postman:
🔗 with https://newsly-v0-0-1.onrender.com/api/v1/auth/signup
> 🔗 View the full API reference in [Postman Collection](https://www.postman.com/newsly-0222/workspace/newsly-workspace)

---


## ⚙️ Setup & Run Locally

### 📦 Clone the Repository

```bash
git clone https://github.com/leo-soumyajit/NewsSocialMedia.git
cd NewsSocialMedia
```
🛠 Configure Database Connection
Edit the application.properties file:
```bash
spring.datasource.url=jdbc:postgresql://localhost:5432/<your_db_name>
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
```
🛠 Configure Redis Cache
To enable Redis caching in your Spring Boot application, add the following to your application.properties:
```bash
# Redis Configuration
spring.cache.type=redis
spring.data.redis.host=<your host>
spring.data.redis.port=<your port>
spring.data.redis.username=default
spring.data.redis.password=<your password>
```
▶ Run the Application
```bash
./mvnw spring-boot:run
```

### 📄 Access Swagger API Docs
Once the server is running, open in browser:
```bash
http://localhost:8000/swagger-ui/index.html
```
---
