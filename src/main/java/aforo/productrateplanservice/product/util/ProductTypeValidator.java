package aforo.productrateplanservice.product.util;

import aforo.productrateplanservice.product.enums.DBType;
import aforo.productrateplanservice.product.enums.FileFormat;

import java.util.regex.Pattern;

public class ProductTypeValidator {

    // API Endpoint URL validation - must be a valid HTTP/HTTPS URL with optional path parameters
    private static final Pattern API_ENDPOINT_PATTERN = Pattern.compile(
        "^https?://[a-zA-Z0-9.-]+(:[0-9]+)?(/[a-zA-Z0-9._~:/?#\\[\\]@!$&'()*+,;=%-]*)?$"
    );

    // LLM Endpoint URL validation - similar to API endpoint
    private static final Pattern LLM_ENDPOINT_PATTERN = Pattern.compile(
        "^https?://[a-zA-Z0-9.-]+(:[0-9]+)?(/[a-zA-Z0-9._~:/?#\\[\\]@!$&'()*+,;=%-]*)?$"
    );

    // File path validation based on format
    private static final Pattern CSV_PATH_PATTERN = Pattern.compile(".*\\.csv$", Pattern.CASE_INSENSITIVE);
    private static final Pattern JSON_PATH_PATTERN = Pattern.compile(".*\\.json$", Pattern.CASE_INSENSITIVE);
    private static final Pattern XML_PATH_PATTERN = Pattern.compile(".*\\.xml$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PARQUET_PATH_PATTERN = Pattern.compile(".*\\.parquet$", Pattern.CASE_INSENSITIVE);

    // Connection string patterns for different database types
    private static final Pattern MYSQL_CONN_PATTERN = Pattern.compile(
        "^jdbc:mysql://[a-zA-Z0-9.-]+(:[0-9]+)?/[a-zA-Z0-9_-]+(\\?.*)?$"
    );
    private static final Pattern POSTGRES_CONN_PATTERN = Pattern.compile(
        "^jdbc:postgresql://[a-zA-Z0-9.-]+(:[0-9]+)?/[a-zA-Z0-9_-]+(\\?.*)?$"
    );
    private static final Pattern SQLSERVER_CONN_PATTERN = Pattern.compile(
        "^jdbc:sqlserver://[a-zA-Z0-9.-]+(:[0-9]+)?(;.*)?$"
    );
    private static final Pattern ORACLE_CONN_PATTERN = Pattern.compile(
        "^jdbc:oracle:(thin|oci):@[a-zA-Z0-9.-]+(:[0-9]+)?[:/][a-zA-Z0-9_-]+(\\?.*)?$"
    );
    private static final Pattern BIGQUERY_CONN_PATTERN = Pattern.compile(
        "^jdbc:bigquery://https://www\\.googleapis\\.com/bigquery/v2:443;.*$"
    );
    private static final Pattern SNOWFLAKE_CONN_PATTERN = Pattern.compile(
        "^jdbc:snowflake://[a-zA-Z0-9.-]+\\.snowflakecomputing\\.com(:\\d+)?/?(\\?.*)?$"
    );

    /**
     * Validates API endpoint URL format
     */
    public static void validateApiEndpointUrl(String endpointUrl) {
        if (endpointUrl == null || endpointUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Endpoint URL cannot be empty");
        }
        if (!API_ENDPOINT_PATTERN.matcher(endpointUrl.trim()).matches()) {
            throw new IllegalArgumentException(
                "Invalid API endpoint URL format. Expected format: https://api.example.com/v1/{endpoint}"
            );
        }
    }

    /**
     * Validates LLM endpoint URL format
     */
    public static void validateLlmEndpointUrl(String endpointUrl) {
        if (endpointUrl == null || endpointUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("LLM Endpoint URL cannot be empty");
        }
        if (!LLM_ENDPOINT_PATTERN.matcher(endpointUrl.trim()).matches()) {
            throw new IllegalArgumentException(
                "Invalid LLM endpoint URL format. Expected format: https://api.example.com/v1/chat/completions"
            );
        }
    }

    /**
     * Validates file location based on the selected file format
     */
    public static void validateFileLocation(String fileLocation, FileFormat format) {
        if (fileLocation == null || fileLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("File location cannot be empty");
        }
        
        if (format == null) {
            throw new IllegalArgumentException("File format must be specified");
        }

        String location = fileLocation.trim();
        boolean isValid = false;
        String expectedExtension = "";

        switch (format) {
            case CSV:
                isValid = CSV_PATH_PATTERN.matcher(location).matches();
                expectedExtension = ".csv";
                break;
            case JSON:
                isValid = JSON_PATH_PATTERN.matcher(location).matches();
                expectedExtension = ".json";
                break;
            case XML:
                isValid = XML_PATH_PATTERN.matcher(location).matches();
                expectedExtension = ".xml";
                break;
            case PARQUET:
                isValid = PARQUET_PATH_PATTERN.matcher(location).matches();
                expectedExtension = ".parquet";
                break;
            case OTHERS:
                // For OTHERS, we accept any path
                isValid = true;
                break;
        }

        if (!isValid) {
            throw new IllegalArgumentException(
                "File location must have " + expectedExtension + " extension for " + format + " format. " +
                "Example: /path/to/file" + expectedExtension
            );
        }
    }

    /**
     * Validates connection string based on the selected database type
     */
    public static void validateConnectionString(String connectionString, DBType dbType) {
        if (connectionString == null || connectionString.trim().isEmpty()) {
            throw new IllegalArgumentException("Connection string cannot be empty");
        }
        
        if (dbType == null) {
            throw new IllegalArgumentException("Database type must be specified");
        }

        String connStr = connectionString.trim();
        boolean isValid = false;
        String expectedFormat = "";

        switch (dbType) {
            case MYSQL:
                isValid = MYSQL_CONN_PATTERN.matcher(connStr).matches();
                expectedFormat = "jdbc:mysql://hostname:port/database";
                break;
            case POSTGRES:
                isValid = POSTGRES_CONN_PATTERN.matcher(connStr).matches();
                expectedFormat = "jdbc:postgresql://hostname:port/database";
                break;
            case SQLSERVER:
                isValid = SQLSERVER_CONN_PATTERN.matcher(connStr).matches();
                expectedFormat = "jdbc:sqlserver://hostname:port;databaseName=database";
                break;
            case ORACLE:
                isValid = ORACLE_CONN_PATTERN.matcher(connStr).matches();
                expectedFormat = "jdbc:oracle:thin:@hostname:port:database";
                break;
            case BIGQUERY:
                isValid = BIGQUERY_CONN_PATTERN.matcher(connStr).matches();
                expectedFormat = "jdbc:bigquery://https://www.googleapis.com/bigquery/v2:443;ProjectId=project;";
                break;
            case SNOWFLAKE:
                isValid = SNOWFLAKE_CONN_PATTERN.matcher(connStr).matches();
                expectedFormat = "jdbc:snowflake://account.snowflakecomputing.com/?db=database";
                break;
            case OTHERS:
                // For OTHERS, we accept any JDBC connection string
                isValid = connStr.startsWith("jdbc:");
                expectedFormat = "jdbc:driver://connection-details";
                break;
        }

        if (!isValid) {
            throw new IllegalArgumentException(
                "Invalid connection string format for " + dbType + ". Expected format: " + expectedFormat
            );
        }
    }
}
