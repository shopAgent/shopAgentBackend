# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Common Development Commands

### Building and Running
- **Build**: `./gradlew build`
- **Clean build**: `./gradlew clean build`
- **Run application**: `./gradlew bootRun`
- **Run tests**: `./gradlew test`
- **Create native image**: `./gradlew nativeCompile`

### Database
- **H2 Console**: Access at `http://localhost:8080/h2-console` when app is running
- **H2 Database file**: Located at `./data/shopagent-h2-db`

## Architecture Overview

This is a Spring Boot 3.4.7 application that provides AI-powered database querying capabilities for e-commerce data. The application acts as an intelligent agent that can connect to various databases and answer questions about shop data using AI.

### Core Architecture Components

**Dynamic Data Source Management**: The application supports connecting to multiple database types at runtime through `DynamicDataSourceConfig`. It excludes Spring Boot's default DataSource auto-configuration and manages connections dynamically.

**Dual Database System**:
- **H2 Database**: Used for storing application configuration, connection settings, and metadata
- **Target Database**: MySQL/other databases that the application connects to for querying shop data

**AI Integration**: Uses Google Gemini AI (`gemini-2.5-flash` model) via the `google-genai` library to process natural language queries and generate responses based on database results.

**MyBatis Integration**: Uses MyBatis for database operations with XML mapper files for complex queries. Dynamic mapper registration allows for runtime database schema adaptation.

### Key Package Structure

- `config/`: Database configuration, CORS, and application setup
- `chat/`: AI chat functionality for natural language database queries  
- `setting/`: Database connection management and configuration APIs
- `info/`: Database introspection and metadata services
- `common/`: Shared services for AI client calls and dynamic database operations
- `exception/`: Centralized error handling
- `response/`: Standardized API response structures

### Configuration Files

**application.yml**: 
- Configures H2 as the default/config database
- Sets up logging levels for MyBatis and database operations
- Enables H2 console for development

**MyBatis Mappers**:
- `h2mapper/H2Mapper.xml`: Operations on H2 configuration database
- `mapper/ShopMapper.xml`: Dynamic queries for target shop databases

### Native Image Support

The application is configured for GraalVM native image compilation with specific build arguments for MyBatis compatibility and reflection configuration.

## Key Features

1. **Dynamic Database Connection**: Connect to different databases at runtime via REST API
2. **AI-Powered Querying**: Natural language queries converted to SQL and executed
3. **Database Introspection**: Automatic schema discovery and table/column information
4. **Configuration Management**: Store and manage multiple database configurations
5. **Cross-Origin Support**: CORS configured for frontend integration

## Development Notes

- Uses Java 21 with Spring Boot 3.4.7
- Lombok for boilerplate reduction
- HikariCP for connection pooling
- Google Gemini AI for natural language processing
- H2 database file persists in `./data/` directory