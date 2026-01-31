# 🧪 Local Testing Guide

## Quick Start (CleverCloud Database)

Your `.env` file is already configured with CleverCloud MySQL credentials.

### Option 1: Run Directly (Fastest)

**Windows:**

```bash
run-local.bat
```

**Linux/Mac:**

```bash
chmod +x run-local.sh
./run-local.sh
```

The script will:

1. Load environment variables from `.env`
2. Build the application with Maven
3. Start the Spring Boot server on `http://localhost:8081`

### Option 2: Run with Docker

```bash
docker-compose up --build
```

This builds and runs the application in a container using your CleverCloud database.

### Option 3: Manual Run (For Development)

```bash
cd satellite

# Build
mvnw clean package -DskipTests

# Set environment variables (Windows)
set DB_URL=jdbc:mysql://boidhcbqdajwkz3osmlx-mysql.services.clever-cloud.com:3306/boidhcbqdajwkz3osmlx?useSSL=true
set DB_USERNAME=uwlpwroo9i5xxjrp
set DB_PASSWORD=Ahwn6sAUydmQaKCPLrJt

# Set environment variables (Linux/Mac)
export DB_URL=jdbc:mysql://boidhcbqdajwkz3osmlx-mysql.services.clever-cloud.com:3306/boidhcbqdajwkz3osmlx?useSSL=true
export DB_USERNAME=uwlpwroo9i5xxjrp
export DB_PASSWORD=Ahwn6sAUydmQaKCPLrJt

# Run
java -jar target/satellite-backend-0.0.1-SNAPSHOT.jar
```

## 📡 Test the API

Once the server is running, test these endpoints:

### 1. Health Check

```bash
curl http://localhost:8081/actuator/health
```

### 2. Welcome Message

```bash
curl http://localhost:8081/
```

### 3. Get Satellite TLE Data (ISS - 25544)

```bash
curl http://localhost:8081/25544
```

### 4. Get All Cached TLE Data

```bash
curl http://localhost:8081/all
```

### 5. Get Most Fetched Satellite

```bash
curl http://localhost:8081/most-fetched
```

## 🐛 Troubleshooting

### Database Connection Issues

If you see `HikariPool-1 - Connection is not available`:

1. **Check CleverCloud Database Status**
   - Verify the database is running in CleverCloud dashboard
   - Check if IP restrictions are enabled (CleverCloud free tier should allow all IPs)

2. **Verify Credentials**
   - Make sure `.env` file has correct credentials
   - Check if password contains special characters that need escaping

3. **Test Direct Connection**

   ```bash
   mysql -h boidhcbqdajwkz3osmlx-mysql.services.clever-cloud.com -P 3306 -u uwlpwroo9i5xxjrp -p
   # Enter password: Ahwn6sAUydmQaKCPLrJt
   ```

4. **Check Logs**
   ```bash
   # In another terminal
   cd satellite
   tail -f logs/spring.log
   ```

### Build Issues

If Maven build fails:

```bash
cd satellite
mvnw clean install -U
```

### Port Already in Use

If port 8081 is busy:

```bash
# Windows
netstat -ano | findstr :8081
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8081 | xargs kill -9
```

## 📊 Database Connection Details

Your CleverCloud MySQL database:

- **Host:** boidhcbqdajwkz3osmlx-mysql.services.clever-cloud.com
- **Port:** 3306
- **Database:** boidhcbqdajwkz3osmlx
- **User:** uwlpwroo9i5xxjrp
- **Connection Pooling:** HikariCP (5 max connections, 2 min idle)
- **SSL:** Enabled

## 🔒 Security Note

The `.env` file containing your actual credentials is **already in .gitignore** and will NOT be committed to Git.

For production deployment on Render, set these as environment variables in Render dashboard.
