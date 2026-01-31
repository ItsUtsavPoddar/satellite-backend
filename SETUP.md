# Setup and Deployment Guide

## What's Changed

This backend has been upgraded to **Spring Boot 3.2.2** with **Java 17** and includes:

- ✅ Fixed database connection pool issues for cloud deployment (Render)
- ✅ Proper timeout configurations for external API calls
- ✅ Retry logic for resilient Celestrak API integration
- ✅ Comprehensive error handling with custom exceptions
- ✅ Multi-stage Docker build for optimized images
- ✅ GitHub Actions CI/CD pipeline
- ✅ Health checks and monitoring

## Prerequisites

- Java 17 or higher
- Maven 3.9+
- Docker (for containerized deployment)
- MySQL 8.0+

## Local Development

### 1. Using Docker Compose (Recommended)

```bash
cd satellite-backend
docker-compose up --build
```

This will start:

- MySQL database on port 3306
- Application on port 8081

### 2. Manual Setup

Set environment variables:

```bash
export DB_URL="jdbc:mysql://localhost:3306/satellite_db"
export DB_USERNAME="your_username"
export DB_PASSWORD="your_password"
```

Build and run:

```bash
cd satellite
mvn clean package
java -jar target/satellite-backend-0.0.1-SNAPSHOT.jar
```

## Render Deployment

### Method 1: Using render.yaml (Automatic)

1. Push your code to GitHub
2. Connect your repository to Render
3. Render will automatically detect `render.yaml`
4. Set environment variables in Render dashboard:
   - `DB_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`

### Method 2: Using GitHub Actions (CI/CD)

1. Add these secrets to your GitHub repository:
   - `DOCKER_USERNAME`: Your Docker Hub username
   - `DOCKER_PASSWORD`: Your Docker Hub password or access token
   - `RENDER_DEPLOY_HOOK_URL`: Your Render deploy hook URL

2. Push to main branch - GitHub Actions will:
   - Build and test the application
   - Build and push Docker image
   - Trigger Render deployment

### Method 3: Manual Docker Deployment

```bash
cd satellite
docker build -t yourusername/satellite-backend:latest .
docker push yourusername/satellite-backend:latest
```

Then update your Render service to use the new image.

## Environment Variables

Required environment variables for production:

```bash
DB_URL=jdbc:mysql://your-host:3306/database?useSSL=true
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password
```

Optional (already configured with defaults):

```bash
JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
```

## API Endpoints

- `GET /` - Welcome message
- `GET /{satNumber}` - Get TLE data for satellite
- `GET /most-fetched` - Get most requested satellite
- `GET /all` - Get all cached satellites
- `DELETE /{id}` - Delete satellite data
- `GET /actuator/health` - Health check

## Troubleshooting

### Database Connection Issues

If you see "Connection is not available" errors:

- Check that MySQL is running and accessible
- Verify connection string format: `jdbc:mysql://host:port/database`
- Ensure firewall rules allow connection
- For cloud databases, check IP whitelist settings

### Celestrak API Timeouts

The application now includes:

- 10-second connect timeout
- 30-second read timeout
- 3 retry attempts with exponential backoff

If still timing out, check your network connectivity to celestrak.org

### Docker Build Issues

Make sure you're building from the correct directory:

```bash
cd satellite  # Must be in satellite directory
docker build -t satellite-backend .
```

## Health Monitoring

Access health endpoint:

```bash
curl http://localhost:8081/actuator/health
```

Expected response:

```json
{
  "status": "UP"
}
```

## Performance Optimization

The application includes:

- HikariCP connection pooling (optimized for cloud)
- Response caching (5-hour TLE cache)
- Multi-stage Docker builds for smaller images
- JVM container-aware memory settings

## Support

For issues related to:

- Database: Check HikariCP logs
- External API: Check retry logs
- Application errors: Check GlobalExceptionHandler output
