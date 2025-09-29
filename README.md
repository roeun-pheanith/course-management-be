# 🎓 Course Management Backend (course-management-be)

## 📝 Description

This repository contains the backend service for a comprehensive Course Management System (CMS). It provides a secure and feature-rich **RESTful API** for managing educational content, including courses, modules, user accounts, and supporting media uploads.

The application is built using **Java** and the **Spring Boot** framework, providing a robust and scalable foundation for your CMS platform.

---

## ✨ Features

* **Course CRUD:** Full Create, Read, Update, and Delete capabilities for Course entities and their nested structures (e.g., Modules/Lessons).
* **User Management:** Secure endpoints for user registration, authentication, and authorization (Admin/Instructor/Student roles, if implemented).
* **File Uploads:** Dedicated services for handling and storing course-related images and files (indicated by the `uploads/images/course` structure).
* **Data Persistence:** Uses Spring Data JPA for object-relational mapping to a relational database.
* **RESTful API:** Clean, logical, and versioned API design for easy integration with any frontend.

---

## 🛠️ Tech Stack

The `course-management-be` is built with:

* **Language:** Java
* **Framework:** Spring Boot 3+
* **Build Tool:** Apache Maven
* **Data Access:** Spring Data JPA / Hibernate
* **Security:** (If implemented: Spring Security)
* **Database:** (Specify your choice, e.g., PostgreSQL or MySQL)

---

## ⚙️ Getting Started

Follow these steps to get a local copy of the project up and running.

### Prerequisites

* **Java Development Kit (JDK):** Version 17+
* **Apache Maven**
* **Git**

### Installation & Setup

1.  **Clone the Repository**

    ```bash
    git clone [https://github.com/roeun-pheanith/course-management-be.git](https://github.com/roeun-pheanith/course-management-be.git)
    cd course-management-be
    ```

2.  **Configure Database**

    Update your database connection and Spring JPA settings in the `src/main/resources/application.properties` file.

    ```properties
    # Example Database Configuration
    server.port=8080
    spring.datasource.url=jdbc:mysql://localhost:3306/cms_db
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    spring.jpa.hibernate.ddl-auto=update 
    
    # Configure path for file uploads (ensure this directory exists!)
    # file.upload-dir=./uploads/images/course
    ```

3.  **Build the Application**

    Package the application into an executable JAR file:
    ```bash
    mvn clean install
    ```

### 🏃 Running the Application

You can run the service directly using the Maven plugin:

```bash
mvn spring-boot:run
