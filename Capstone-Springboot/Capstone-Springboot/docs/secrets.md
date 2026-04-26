# Secrets Management for Capstone Event Booking Application

## Overview
This document outlines the environment variables required for the application and their intended AWS Systems Manager (SSM) Parameter Store names for production deployment.

## Environment Variables

### Database Configuration
| Variable Name | SSM Parameter Name | Description | Default Value (Development) |
|---------------|-------------------|-------------|----------------------------|
| `DB_URL` | `/capstone/prod/db/url` | MySQL database connection URL | `jdbc:mysql://localhost:3306/capstone_improved` |
| `DB_USERNAME` | `/capstone/prod/db/username` | Database username | `root` |
| `DB_PASSWORD` | `/capstone/prod/db/password` | Database password | `password` |

### JWT Configuration
| Variable Name | SSM Parameter Name | Description | Default Value (Development) |
|---------------|-------------------|-------------|----------------------------|
| `JWT_SECRET` | `/capstone/prod/jwt/secret` | Secret key for JWT token signing | `6f0e5d4412d5f3a0b6194c27b6f8a0ecbaf1942e22cc04f5774d1b1c2f2a4b7d` |
| `JWT_EXPIRATION` | `/capstone/prod/jwt/expiration` | JWT token expiration in milliseconds | `3600000` (1 hour) |

### Rate Limiting Configuration
| Variable Name | SSM Parameter Name | Description | Default Value (Development) |
|---------------|-------------------|-------------|----------------------------|
| `RATE_LIMIT_AUTH_REQUESTS` | `/capstone/prod/rate-limit/auth` | Maximum auth requests per minute per IP | `10` |
| `RATE_LIMIT_API_REQUESTS` | `/capstone/prod/rate-limit/api` | Maximum API requests per minute per IP | `100` |

## Development Setup

### Local Development
Create a `.env` file in the project root with the following variables:
```
DB_URL=jdbc:mysql://localhost:3306/capstone_improved
DB_USERNAME=root
DB_PASSWORD=password
JWT_SECRET=6f0e5d4412d5f3a0b6194c27b6f8a0ecbaf1942e22cc04f5774d1b1c2f2a4b7d
JWT_EXPIRATION=3600000
RATE_LIMIT_AUTH_REQUESTS=10
RATE_LIMIT_API_REQUESTS=100
```

### Running with Environment Variables
```bash
# Linux/Mac
export DB_URL=jdbc:mysql://localhost:3306/capstone_improved
export DB_PASSWORD=your_password
mvn spring-boot:run

# Windows (PowerShell)
$env:DB_URL="jdbc:mysql://localhost:3306/capstone_improved"
$env:DB_PASSWORD="your_password"
mvn spring-boot:run
```

## Production Deployment (AWS)

### SSM Parameter Store Setup
Create the following parameters in AWS Systems Manager Parameter Store:

1. **String Parameters:**
   - `/capstone/prod/db/url` - Database connection string
   - `/capstone/prod/db/username` - Database username
   - `/capstone/prod/jwt/secret` - JWT signing secret

2. **SecureString Parameters:**
   - `/capstone/prod/db/password` - Database password (encrypted)

3. **String Parameters (Configuration):**
   - `/capstone/prod/jwt/expiration` - JWT expiration
   - `/capstone/prod/rate-limit/auth` - Auth rate limit
   - `/capstone/prod/rate-limit/api` - API rate limit

### IAM Permissions
The application's IAM role should have the following permissions:
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "ssm:GetParameter",
        "ssm:GetParameters"
      ],
      "Resource": [
        "arn:aws:ssm:region:account-id:parameter/capstone/prod/*"
      ]
    }
  ]
}
```

## Application Configuration
The application uses Spring Boot's `@ConfigurationProperties` to read these values. See `application.properties` for the property mappings.

## Security Notes
1. **Never commit secrets to version control**
2. **Use different secrets for development, staging, and production**
3. **Rotate JWT secrets periodically**
4. **Use AWS KMS for encrypting SecureString parameters**
5. **Implement secret rotation for database credentials**

## Troubleshooting
If environment variables are not being read:
1. Check that the variable names match exactly (case-sensitive on some systems)
2. Verify the `.env` file is in the correct location
3. Restart the application after changing environment variables
4. Check Spring Boot logs for configuration binding errors