# Sales Bundle Service

A Spring Boot application that monitors S3 for sales bundle data (e.g., from Humble Bundle), ingests it into a PostgreSQL database, and publishes new bundle notifications to a Discord channel with affiliate links.

## Table of Contents

- [Introduction](#introduction)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Contributing](#contributing)
- [License](#license)

## Introduction

The **Sales Bundle Service** is designed to automate the discovery and sharing of sales bundles. It periodically (or on-demand) checks an S3 bucket for new JSON data files containing bundle information. When new data is found, the service:

1.  Parses the JSON to extract bundle details (Games, Books, Software).
2.  Stores the data in a local PostgreSQL database, ensuring no duplicates.
3.  Checks for registered partners (affiliates).
4.  Sends a formatted embed message to a configured Discord channel for each new bundle, tagged with the partner's affiliate ID.

## Architecture

-   **Backend**: Spring Boot 3 (Java 25)
-   **Database**: PostgreSQL
-   **Storage**: AWS S3 (for source JSON files)
-   **Notifications**: Discord API (via Discord4J)
-   **Containerization**: Docker & Docker Compose

## Prerequisites

-   Java 25 JDK
-   Docker & Docker Compose
-   An AWS Account with an S3 bucket containing bundle data JSON files.
-   A Discord Bot Token and Channel ID.

## Configuration

The application uses environment variables for sensitive configuration. You can set these in the `docker-compose.yml` file or export them in your shell.

| Variable | Description |
| :--- | :--- |
| `AWS_ACCESS_KEY_ID` | AWS Access Key for S3 access. |
| `AWS_SECRET_ACCESS_KEY` | AWS Secret Key for S3 access. |
| `AWS_BUCKET_NAME` | Name of the S3 bucket containing `bundle-data-*.json` files. |
| `DISCORD_TOKEN` | Your Discord Bot Token. |
| `DISCORD_CHANNEL_ID` | The ID of the Discord channel where notifications will be posted. |
| `DB_USERNAME` | PostgreSQL username (default: `user`). |
| `DB_PASSWORD` | PostgreSQL password (default: `password`). |

## Usage

### Running with Docker Compose (Recommended)

This will start both the application and the PostgreSQL database.

1.  Update `docker-compose.yml` with your credentials or set them as environment variables.
2.  Run the following command:

```bash
docker-compose up --build
```

The service will be available at `http://localhost:8080`.

### Running Locally

1.  Ensure a PostgreSQL database is running and accessible.
2.  Update `src/main/resources/application.properties` or pass environment variables.
3.  Run the application:

```bash
./gradlew bootRun
```

## API Endpoints

### 1. Subscribe a Partner
Register a new affiliate partner ID. Notifications will be generated for this partner.

```bash
POST /subscribe/{partnerId}
```

**Example:**
```bash
curl -X POST http://localhost:8080/subscribe/humblepartner
```

### 2. Trigger Processing
Manually trigger the service to check S3 for the latest file, ingest data, and send notifications.

```bash
POST /process
```

**Example:**
```bash
curl -X POST http://localhost:8080/process
```

### 3. Health Check
Verify the service is running.

```bash
GET /status
```

## Contributing

If you have any suggestions or improvements, please submit a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.