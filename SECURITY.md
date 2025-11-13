# 🔒 Security Configuration Guide

## Environment Variables Setup

This application uses environment variables for all sensitive configuration to prevent security breaches.

### Required Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_URL` | Database connection URL | `jdbc:postgresql://localhost:5432/mydb` |
| `DB_USERNAME` | Database username | `myuser` |
| `DB_PASSWORD` | Database password | `securePassword123!` |
| `JASYPT_ENCRYPTOR_PASSWORD` | Encryption key for sensitive data | `myEncryptionKey456!` |

### Setup Instructions

#### 1. Local Development
```bash
# Copy the example file
cp .env.example .env

# Edit .env with your actual values
# NEVER commit .env to version control!
```

#### 2. Production Deployment
Set environment variables in your deployment platform:

**Docker:**
```bash
docker run -e DB_PASSWORD=yourpassword -e JASYPT_ENCRYPTOR_PASSWORD=yourkey myapp
```

**Kubernetes:**
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
data:
  DB_PASSWORD: <base64-encoded-password>
  JASYPT_ENCRYPTOR_PASSWORD: <base64-encoded-key>
```

**AWS ECS/Fargate:**
```json
{
  "environment": [
    {"name": "DB_PASSWORD", "value": "yourpassword"},
    {"name": "JASYPT_ENCRYPTOR_PASSWORD", "value": "yourkey"}
  ]
}
```

#### 3. IDE Configuration (IntelliJ/Eclipse)
Add environment variables to your run configuration:
```
DB_PASSWORD=yourpassword;JASYPT_ENCRYPTOR_PASSWORD=yourkey
```

### Security Best Practices

✅ **DO:**
- Use strong, unique passwords (min 12 characters)
- Rotate passwords regularly
- Use different passwords for different environments
- Store secrets in secure secret management systems (AWS Secrets Manager, Azure Key Vault, etc.)
- Use encrypted connections (SSL/TLS) for databases

❌ **DON'T:**
- Hardcode passwords in configuration files
- Commit .env files to version control
- Share passwords in plain text (Slack, email, etc.)
- Use default or weak passwords
- Log sensitive information

### Password Requirements

- **Database Password:** Minimum 12 characters, mix of letters, numbers, symbols
- **Encryption Password:** Minimum 16 characters, high entropy
- **JWT Secret:** Minimum 32 characters, cryptographically secure

### Troubleshooting

**Application won't start:**
- Check all required environment variables are set
- Verify database connectivity
- Check password special characters are properly escaped

**Database connection fails:**
- Verify DB_URL format is correct
- Check network connectivity to database
- Verify credentials are correct

### Security Incident Response

If credentials are compromised:
1. **Immediately** change all affected passwords
2. Rotate encryption keys
3. Check logs for unauthorized access
4. Update all deployment environments
5. Review and update access controls
