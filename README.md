# 🛰️ Satellite Tracker – Backend

This is the backend service for the [Satellite Tracker](https://satellite.utsv.tech) project.  
It is built with **Spring Boot**, containerized using **Docker**, and connected to a **MySQL database**.

It handles:
- Fetching and caching TLE data from Celestrak  
- Computing satellite passes and visibility windows  
- Eclipse and sunlight calculations  
- Serving a REST API consumed by the frontend

---

## 🧭 Full Project Overview

You can learn more about the overall project, frontend logic, orbital math, and visual features here:

👉 [Satellite Tracker – Frontend (Main Project Repo)](https://github.com/ItsUtsavPoddar/satellite-frontend)  
👉 🌐 [Live Demo](https://satellite.utsv.tech)

---

## 🚀 Live Deployment

- 🔗 **Backend API**: [https://satellite-backend-v1.onrender.com](https://satellite-backend-v1.onrender.com)
- 🛰️ **Frontend**: [https://satellite.utsv.tech](https://satellite.utsv.tech)

> Hosted on Render's free tier with GitHub Actions and CronJobs to avoid auto-sleep during inactivity.

---

## ⚙️ Tech Stack

- Java 17 with Spring Boot
- MySQL (CleverCloud Free Tier)
- Docker for packaging
- Render for backend hosting

---

## 🔧 Running Locally

### ✅ Requirements:
- Java 17+  
- Docker installed  
- MySQL database (local or hosted)

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
