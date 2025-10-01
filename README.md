# Task Management API

This is a comprehensive task management API built with Spring Boot. It allows users to create, retrieve, update, and delete tasks, as well as manage task priorities and completion status. The application is designed to be containerized with Docker and deployed to a Kubernetes cluster, with a CI/CD pipeline managed by Jenkins.

## Features

*   **CRUD Operations:** Create, Read, Update, and Delete tasks.
*   **Priority Management:** Assign priority levels to tasks (e.g., URGENT, HIGH, MEDIUM, LOW).
*   **Completion Tracking:** Mark tasks as completed or pending.
*   **Filtering and Sorting:** Retrieve tasks based on priority, completion status, or creation date.
*   **API Documentation:** Interactive API documentation with Swagger UI.
*   **Containerization:** Docker support for easy deployment and scaling.
*   **CI/CD:** Jenkins pipeline for continuous integration and deployment.
*   **Kubernetes Deployment:** A full set of manifests for a production-ready Kubernetes deployment.

## Technologies Used

*   **Backend:** Spring Boot, Spring Data JPA, Spring Web
*   **Database:** PostgreSQL
*   **Build Tool:** Maven
*   **API Documentation:** Springdoc OpenAPI (Swagger UI)
*   **Containerization:** Docker
*   **CI/CD:** Jenkins
*   **Orchestration:** Kubernetes

## Prerequisites

*   Java 21 or later
*   Maven 3.6 or later
*   Docker
*   `kubectl`
*   An Ingress controller (like NGINX Ingress Controller) installed in your Kubernetes cluster.

## Getting Started (Local Development)

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/apik8s.git
    cd apik8s
    ```

2.  **Configure the database:**
    For local development, open `src/main/resources/application.properties` and update the following properties to match your PostgreSQL configuration:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/your-database
    spring.datasource.username=your-username
    spring.datasource.password=your-password
    ```

3.  **Build and run the application:**
    ```bash
    mvn spring-boot:run
    ```
    The application will be available at `http://localhost:8080`.

## API Documentation

The API documentation is available at `http://localhost:8080/swagger-ui.html` when running locally.

## Docker Support

1.  **Build the Docker image:**
    ```bash
    docker build -t abdulmunim/apik8s:latest .
    ```

2.  **Run the Docker container:**
    ```bash
    docker run -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:postgresql://your-host:5432/your-database -e SPRING_DATASOURCE_USERNAME=your-username -e SPRING_DATASOURCE_PASSWORD=your-password abdulmunim/apik8s:latest
    ```

## Kubernetes Deployment

The `Manifest` directory contains a full set of Kubernetes manifests for a production-ready deployment. This setup emphasizes security, high availability, and best practices.

### Manifest Overview

*   **`task.yaml` (Deployment):** Deploys the application with a security context that runs the container as a non-root user with a read-only filesystem. It also defines resource requests and limits and sources its configuration from a `ConfigMap` and `Secret`.
*   **`service.yaml` (Service):** Exposes the application within the cluster using a `ClusterIP` service.
*   **`configmap.yaml` (ConfigMap):** Externalizes non-sensitive configuration, such as the database URL.
*   **`secret.yaml` (Secret):** Securely stores sensitive data, like database credentials. **You must create this secret manually.**
*   **`ingress.yaml` (Ingress):** Manages external access to the application, providing an entry point for users and other services.
*   **`hpa.yaml` (HorizontalPodAutoscaler):** Automatically scales the number of pods based on CPU utilization.
*   **`network-policy.yaml` (NetworkPolicy):** Restricts ingress traffic to the application, only allowing connections from the Ingress controller.
*   **`pod-disruption-budget.yaml` (PodDisruptionBudget):** Ensures a minimum number of replicas are available during voluntary disruptions.

### Deployment Steps

1.  **Create the Secret:**
    Before applying the manifests, you must create the `api-secret` with your base64-encoded database credentials. Update the `secret.yaml` file with your encoded credentials, or create the secret directly using `kubectl`:

    ```bash
    kubectl create secret generic api-secret --from-literal=SPRING_DATASOURCE_USERNAME=<your-username> --from-literal=SPRING_DATASOURCE_PASSWORD=<your-password>
    ```

2.  **Apply the Manifests:**
    Apply all the manifests in the `Manifest` directory:
    ```bash
    kubectl apply -f Manifest/
    ```

3.  **Access the Application:**
    Once the Ingress is set up, you can access the application through the Ingress controller's external IP address at the `/api` path.

## CI/CD Pipeline

This project includes a `Jenkinsfile` that defines a CI/CD pipeline with the following stages:

1.  **Build:** Compiles the code and runs unit tests.
2.  **Docker Build & Push:** Builds a Docker image and pushes it to a container registry.
3.  **Deploy to Kubernetes:** Deploys the application to a Kubernetes cluster.
