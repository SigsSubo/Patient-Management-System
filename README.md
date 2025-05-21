# Patient Management System - Microservices

## Overview
This project implements a Patient Management System using a microservices architecture. It is designed to handle patient data, user authentication, billing processes, and event-driven analytics. The entire system is configured for local execution using Docker and LocalStack, which emulates essential AWS cloud services.

## Architecture
The system is composed of five core microservices:

-   **`api-gateway`**:
    -   Serves as the single entry point for all incoming client requests.
    -   Intelligently routes requests to the appropriate backend microservice.
    -   Secures routes by integrating with `auth-service` for JWT-based authentication.

-   **`auth-service`**:
    -   Manages all aspects of user authentication, including user registration and login.
    -   Generates and validates JSON Web Tokens (JWTs) for secure API access across the system.
    -   Utilizes a PostgreSQL database to persist user credentials.

-   **`patient-service`**:
    -   Handles patient records, supporting Create, Read, Update, and Delete (CRUD) operations.
    -   Exposes a RESTful API for comprehensive patient data management.
    -   Stores patient information in a dedicated PostgreSQL database.
    -   Communicates with `billing-service` via gRPC for billing-related operations.
    -   Publishes events related to patient activities (e.g., new patient registration, updated medical records) to an Apache Kafka topic for consumption by other services.

-   **`billing-service`**:
    -   Provides billing functionalities through gRPC endpoints.
    -   Manages processes such as payment processing and invoice generation based on patient activities.

-   **`analytics-service`**:
    -   Consumes events from Kafka topics (e.g., patient activity events published by `patient-service`).
    -   Performs data analysis and can be used to generate reports or derive insights from system-wide activity.

## Prerequisites
To build, run, and test this project locally, you will need the following software installed on your system:

-   **Java Development Kit (JDK):** Version 21 or later is required.
-   **Apache Maven:** Used for managing project dependencies and building the individual microservices. Installation instructions can be found on the [official Apache Maven website](https://maven.apache.org/install.html).
-   **Docker:** Essential for running services in containers and for utilizing LocalStack. Installation guides are available on the [official Docker website](https://docs.docker.com/get-docker/).
-   **AWS Command Line Interface (CLI):** Required for interacting with LocalStack. For installation and configuration instructions, please consult the [official AWS CLI documentation](https://aws.amazon.com/cli/).

## Setup
Follow these steps carefully to configure your local environment for the project:

### 1. LocalStack Configuration
LocalStack allows for the emulation of AWS cloud services (such as RDS, MSK, ECS, ELB) on your local machine. This facilitates development and testing without requiring an actual AWS account.

-   **Ensure Docker is Running:** LocalStack operates as a Docker container. Verify that your Docker daemon (e.g., Docker Desktop) is running before you proceed.
-   **AWS CLI Configuration for LocalStack:**
    The `infrastructure/localstack-deploy.sh` script (used later) specifies the LocalStack endpoint (`http://localhost:4566`) in its commands. However, the AWS CLI still needs to be installed and minimally configured with placeholder credentials for local use.
    Execute the following commands in your terminal:
    ```bash
    aws configure set aws_access_key_id "test"
    aws configure set aws_secret_access_key "test"
    aws configure set default.region "us-east-1"
    ```
    Using "test" for the access key/secret key and "us-east-1" as the region are common conventions for LocalStack development. These are not real AWS credentials.
-   **Deploy Infrastructure to LocalStack:**
    Once Docker is active and the AWS CLI is configured, navigate to the `infrastructure` directory and execute the deployment script:
    ```bash
    cd infrastructure
    ./localstack-deploy.sh
    ```
    This script employs AWS CDK to define and deploy all necessary resources (databases, Kafka cluster, ECS task definitions, etc.) to your running LocalStack instance.

### 2. Environment Variables
Most service-specific configurations (e.g., database connection strings, Kafka broker addresses, JWT secrets) are automatically managed and injected as environment variables into the services when they are deployed via the LocalStack and AWS CDK setup described above. These configurations are defined within the AWS CDK stack (the generated CloudFormation template can be found at `infrastructure/cdk.out/localstack.template.json`).

If you intend to run services individually (e.g., directly from your IDE or using `java -jar`) outside of this orchestrated LocalStack/ECS environment, you will need to manually configure the required environment variables or update the respective `application.properties` or `application.yml` files within each service's source code.

## Building the Project
Each microservice in this system is an independent Apache Maven project. There is no overarching parent POM file; therefore, each service must be built individually.

To build a specific microservice:
1.  Navigate to the root directory of the service you wish to build. For example:
    ```bash
    cd auth-service
    ```
    (Replace `auth-service` with `api-gateway`, `patient-service`, `billing-service`, or `analytics-service` as needed.)
2.  Execute the Maven build command:
    ```bash
    mvn clean install
    ```
This command will compile the source code, run unit tests, and package the application into a JAR file (e.g., `service-name-0.0.1-SNAPSHOT.jar`), which will be located in the `target` directory within that service's folder.

The core services to build are:
-   `api-gateway`
-   `auth-service`
-   `patient-service`
-   `billing-service`
-   `analytics-service`

Repeat these steps for each service you plan to run or modify.

## Running the Project
To run the complete Patient Management System locally using LocalStack, follow these steps in the specified order:

### 1. Building Docker Images
Before initiating the infrastructure with LocalStack, you must build the Docker image for each microservice. The AWS CDK deployment process expects these images to be available in your local Docker image registry, tagged appropriately.

For each of the core services (`api-gateway`, `auth-service`, `patient-service`, `billing-service`, `analytics-service`), navigate to its root directory and execute the `docker build` command.

Example for `auth-service`:
```bash
cd auth-service
docker build -t auth-service:latest .
cd .. 
```
Repeat this process for all services, substituting `auth-service` with the respective service name (e.g., `patient-service:latest`, `api-gateway:latest`). The tag `:latest` is a common convention, but ensure it matches any specific tags expected by the CDK configuration if modified.

### 2. Starting the Infrastructure and Services (LocalStack)
Once all Docker images are built and locally available:

-   **Ensure Docker is Running:** LocalStack and all microservices will operate as Docker containers.
-   Navigate to the `infrastructure` directory:
    ```bash
    cd infrastructure
    ```
-   Execute the deployment script:
    ```bash
    sh localstack-deploy.sh
    ```
    This script orchestrates the deployment of all backend infrastructure (PostgreSQL databases, Kafka cluster) and the microservices themselves as emulated ECS Fargate tasks within LocalStack. This process may take several minutes, especially during the initial run.

### 3. Verifying Infrastructure and Services
-   **API Gateway Endpoint:** Upon successful completion, the `localstack-deploy.sh` script will output the DNS name of the API Gateway's load balancer. This URL serves as the primary entry point for interacting with the system's APIs.
-   **Docker Containers:** You can inspect running containers using Docker Desktop or by executing `docker ps` in your terminal. This will show the LocalStack container and containers for the deployed services.
-   **AWS CLI Checks (LocalStack):** To verify that ECS services are operational within LocalStack, use the AWS CLI, ensuring commands target your LocalStack endpoint:
    ```bash
    # Replace 'PatientManagementCluster' if your cluster name differs (check CDK output or template)
    aws --endpoint-url=http://localhost:4566 ecs list-services --cluster PatientManagementCluster 
    aws --endpoint-url=http://localhost:4566 ecs list-tasks --cluster PatientManagementCluster
    ```
    Service logs can be accessed via CloudWatch Logs (as emulated by LocalStack) or directly from the containers if their log output is accessible.

### (Optional) Running Services Individually (for Development/Debugging)
For development or debugging, you might prefer to run one or more microservices directly from your IDE (e.g., IntelliJ IDEA, Eclipse) or using Maven (e.g., `mvn spring-boot:run` from the service's directory), while still relying on the LocalStack-managed backend infrastructure.

If you take this approach:
-   Ensure LocalStack is running with the necessary infrastructure (databases, Kafka cluster) deployed via `localstack-deploy.sh`.
-   You will likely need to modify the `application.properties` or `application.yml` file for each service you run individually. These configuration files must point to the correct LocalStack resource URLs (e.g., `spring.datasource.url` for PostgreSQL, `spring.kafka.bootstrap-servers` for Kafka). For instance, database URLs might change from internal ECS DNS names to something like `jdbc:postgresql://localhost:XXXX/<db_name>` (if LocalStack exposes RDS ports directly), and Kafka brokers to `localhost:XXXX` (refer to LocalStack's Kafka port mappings).

## Technologies Used
-   **Java:** Version 21
-   **Spring Boot:** Version 3.4.4 (or as specified in individual `pom.xml` files)
-   **Spring Cloud Gateway:** For the `api-gateway` service
-   **Apache Maven:** For dependency management and project builds
-   **gRPC:** For inter-service communication, notably with `billing-service`
-   **Apache Kafka:** For event-driven asynchronous communication
-   **PostgreSQL:** As the relational database for services requiring persistence
-   **Docker:** For containerizing services and running LocalStack
-   **AWS CDK (Cloud Development Kit):** For defining and deploying local infrastructure on LocalStack
-   **LocalStack:** For emulating AWS services locally

## Testing the System
This project utilizes several testing approaches:

### Unit Tests
Each microservice (`api-gateway`, `auth-service`, `patient-service`, `billing-service`, `analytics-service`) includes its own suite of unit tests. These tests are designed to verify individual components or methods in isolation, typically mocking external dependencies. They do not require the full infrastructure to be running.

To execute unit tests for a specific service:
1.  Navigate to the root directory of the service (e.g., `cd patient-service`).
2.  Run the Maven test command:
    ```bash
    mvn test
    ```

### Integration Tests
The `integration-tests/` module is dedicated to testing the interactions between different microservices and their integration with the backend infrastructure (databases, message queues, etc.) emulated by LocalStack.

-   **Location:** Tests are located in the `integration-tests/` directory.
-   **Prerequisite:** The LocalStack environment, with all services deployed as described in the "Running the Project" section, **must be operational** before running these tests.
-   To execute integration tests:
    1.  Navigate to the `integration-tests` directory:
        ```bash
        cd integration-tests
        ```
    2.  Run the Maven command:
        ```bash
        mvn test
        ```
        (Using `mvn clean test` or `mvn clean install` might also be appropriate depending on the test setup).

### Manual Test Documentation
The `Documented_Images/` folder contains screenshots, logs, or other records from manual testing performed during development. This serves as a visual confirmation of certain functionalities.

### API Request Collections
For manual testing of API endpoints or as a reference for creating automated tests, sample requests are provided:
-   **`api-requests/`**: This directory should contain examples of HTTP requests (e.g., Postman collections, `.http` files, or cURL commands) for interacting with RESTful APIs (e.g., `auth-service`, `patient-service`) via the `api-gateway`.
-   **`grpc-requests/`**: This directory should contain examples or instructions for making requests to gRPC services like `billing-service`. This might include sample `.proto` message payloads or scripts for gRPC client tools (e.g., `grpcurl`).

## API Endpoints and Usage
All HTTP-based interactions with the microservices are routed through the **API Gateway**. The base URL for the API Gateway (referred to as `<gateway-url>` below) is the DNS name of the load balancer, which is provided as output by the `infrastructure/localstack-deploy.sh` script upon successful deployment.

### Key API Endpoints

#### Authentication Service (`auth-service`)
Endpoints are typically accessed via `http://<gateway-url>/auth/...` (the exact path prefix `/auth` depends on the API Gateway configuration).

-   **`POST /register`**: Register a new user.
    *   *Request Body:* JSON object with user details (e.g., `{"username": "user", "password": "password"}`).
-   **`POST /login`**: Log in an existing user.
    *   *Request Body:* JSON object with username and password.
    *   *Response:* JWT token upon successful authentication.
-   **`GET /validate`**: Validate a JWT token.
    *   *Query Parameter:* `token=<JWT_TOKEN_TO_VALIDATE>`
    *   *Note:* Depending on the specific implementation, this might also require an `Authorization` header.

#### Patient Service (`patient-service`)
Endpoints are typically accessed via `http://<gateway-url>/patients/...` (the exact path prefix `/patients` depends on the API Gateway configuration). All these endpoints generally require a valid JWT Token in the `Authorization: Bearer <TOKEN>` header.

-   **`POST /`**: Create a new patient record.
    *   *Request Body:* JSON object with patient details.
-   **`GET /`**: Retrieve a list of all patient records.
-   **`GET /{id}`**: Retrieve a specific patient record by its ID.
-   **`PUT /{id}`**: Update an existing patient record.
    *   *Request Body:* JSON object with updated patient details.
-   **`DELETE /{id}`**: Delete a patient record by its ID.

### Using API Requests
The `api-requests/` directory in the repository is intended to store sample HTTP requests.

**Example `curl` command for login:**
(Ensure you replace `<gateway-url>` with the actual URL. Create a `login_payload.json` file or provide the JSON directly.)
```bash
# Example login_payload.json:
# {
#   "username": "your_username",
#   "password": "your_password"
# }

curl -X POST \
  -H "Content-Type: application/json" \
  -d @login_payload.json \
  http://<gateway-url>/auth/login
```
For protected endpoints, include the obtained JWT token in the request header: `Authorization: Bearer <YOUR_JWT_TOKEN>`.

### gRPC Services (`billing-service`)
The `billing-service` uses gRPC for communication.
-   The `patient-service` includes a gRPC client to interact with `billing-service`.
-   For direct testing of `billing-service`:
    *   Refer to the `.proto` files (usually within `billing-service`'s source or a shared module) for service and message definitions.
    *   The `grpc-requests/billing-service/` directory should provide example requests or usage instructions for tools like `grpcurl`.
    *   A gRPC client tool is needed. The service's gRPC port (e.g., `9001`) must be known (check service configuration or logs).
    ```bash
    # Generic grpcurl example (replace placeholders):
    # grpcurl -plaintext -d '{"field_name": "value"}' <billing-service-host>:<port> <package.ServiceName/MethodName>
    # Example: grpcurl -plaintext -d '{}' localhost:9001 com.pm.billing.BillingService/GetBillingInfo
    ```
