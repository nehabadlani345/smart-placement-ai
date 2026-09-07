# Smart Placement AI

A Spring Boot backend for resume analysis, job matching, and placement readiness assessment.

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- PostgreSQL
- MongoDB
- Apache PDFBox
- Apache POI
- Maven

## Features

- Resume upload and processing
- Resume structure analysis
- Resume quality assessment
- Resume-to-job matching
- ATS match analysis
- Placement readiness assessment
- Experience confidence analysis
- PostgreSQL and MongoDB integration
- RESTful API architecture

## Architecture

```text
Client
   │
   ▼
REST APIs
   │
   ▼
Spring Boot
   ├── Controllers
   ├── Services
   ├── Repositories
   ├── Models
   └── Exception Handling
        │
        ├── PostgreSQL
        └── MongoDB


## Project Structure 

src/main/java/com/smartplacementai
│
├── config
├── controller
├── exception
├── handler
├── model
├── repository
├── service
│
└── SmartPlacementAiApplication.java
