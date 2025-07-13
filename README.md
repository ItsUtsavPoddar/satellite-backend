# 🛰️ Satellite Tracker – Backend

This is the backend service for the [Satellite Tracker](https://satellite.utsv.tech) project.  
It is built with **Spring Boot**, containerized with **Docker**, and powered by a **MySQL database**.

It handles:
- Fetching and caching TLE data from Celestrak  
- Computing satellite passes and visibility windows  
- Eclipse and sunlight calculations  
- Serving a REST API consumed by the frontend

---

## 🧭 Full Project Overview

You can learn more about the overall project, frontend, astronomical models, and visual features in the main README:

👉 [Satellite Tracker – Frontend (Main Project Repo)](https://github.com/ItsUtsavPoddar/satellite-frontend)

---

## 🚀 Live Deployment

- 🔗 API URL: [https://satellite-backend-v1.onrender.com](https://satellite-backend-v1.onrender.com)
- 🌐 Frontend: [https://satellite.utsv.tech](https://satellite.utsv.tech)

> This is a free Render instance kept alive via GitHub Actions and CronJobs.

---

## ⚙️ Tech Stack

- Spring Boot (Java 17)
- MySQL (CleverCloud)
- RESTful API
- Docker for packaging
- Hosted on [Render](https://render.com)

---

## 🔧 Running Locally

### ✅ Requirements:
- Java 17+  
- Docker installed  
- MySQL database (local or cloud like CleverCloud)

### 🔹 Steps:

```bash
git clone https://github.com/ItsUtsavPoddar/satellite-backend
cd satellite-backend
docker build -t satellite-backend .
docker run -p 8080:8080 \
  -e DB_USERNAME=your_user \
  -e DB_PASSWORD=your_password \
  -e DB_URL=your_db_url \
  satellite-backend
