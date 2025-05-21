## Overview
This project is a Patient Management System built with a microservices architecture. It handles patient data, authentication, billing, and analytics. The system is designed for local execution using Docker and LocalStack to simulate AWS services.

## Architecture
The system comprises 5 core microservices:

-   **`api-gateway`**: 
    -   Acts as the single entry point for all client requests.
    -   Routes requests to the appropriate backend services.
    -   Integrates with `auth-service` to protect routes using JWT authentication.

-   **`auth-service`**:
    -   Manages user authentication, including registration and login processes.
    -   Generates and validates JWT (JSON Web Tokens) for secure API access.
    -   Uses a PostgreSQL database to store user credentials.

-   **`patient-service`**:
    -   Manages patient records, supporting CRUD (Create, Read, Update, Delete) operations.
    -   Exposes RESTful APIs for patient data management.
    -   Uses a PostgreSQL database to store patient information.
    -   Communicates with `billing-service` via gRPC for billing-related actions.
    -   Publishes patient activity events (e.g., new patient registration, updated medical records) to a Kafka topic.

-   **`billing-service`**:
    -   Provides billing functionalities through gRPC endpoints.
    -   Handles processes such as payment processing or generating invoices based on patient activities.

-   **`analytics-service`**:
    -   Consumes events from Kafka topics (e.g., patient activity events from `patient-service`).
    -   Performs data analysis and can be used to generate reports or insights based on system activity.

## Testing
- Check **Documented_Images** for tests performed during deployment.
