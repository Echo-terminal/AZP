# Task Manager

A simple and convenient application for task management, designed to help individuals organize their tasks efficiently.

## About the Project

This application allows you to:

- **Create tasks**:
  - Add descriptions
  - Set deadlines
  - Define priority levels

- **Assign tasks**:
  - Assign tasks to yourself

- **Track progress**:
  - View tasks in a list
  - Track tasks on a calendar
  - Analyze progress with graphs

- **Attach files**:
  - Attach files to tasks
  - Share information

- **View tasks**:
  - Sort by chronological order
  - Filter by priority
  - View tasks by assignee

## Goals and Objectives

**Goal**:  
- Improve task and project management
- Simplify task handling

**Objectives**:
- Create a user-friendly interface for task management
- Provide options to track progress
- Support file attachments for sharing information

## Technical Specifications

**Programming Language**: Kotlin  
**Framework**: Android Studio  

**Libraries Used**:
- **Firebase BoM** (`32.8.0`): Firebase services like authentication, database, and file storage are used to manage tasks, store task data, and allow file attachments.
- **Core KTX** (`1.12.0`): Provides Kotlin extensions for cleaner code.
- **Compose**: Used for building the UI, including `Activity Compose` (`1.8.2`) and `Compose BoM` (`2023.08.00`).
- **Lifecycle Runtime KTX** (`2.7.0`): Helps in managing UI-related data lifecycles.
- **Firebase Auth** (`22.3.1`): Used for user authentication to ensure data privacy and access to personal tasks.
- **Firebase Database** (`20.3.1`) and **Firebase Firestore** (`25.0.0`): To store and retrieve tasks and their details.
- **Firebase Storage KTX** (`21.0.0`): Used for storing files attached to tasks.
- **Material Design** (`1.11.0`): Provides a modern design and responsive layout for the application.
- **ConstraintLayout** (`2.1.4`): Helps in designing complex UI layouts.
- **Gson** (`2.10.1`): For JSON parsing to handle data efficiently.

## Development Team

- **Artsiom Kalahryu** - Back-end  
- **Aliaksandr Ihnatsyeu** - Front-end  
- **Igor Egorov** - Project Manager, Full-stack

## Getting Started

1. Clone the repository:

   ```bash
   git clone https://github.com/Echo-terminal/AZP.git
   
2. Open the project in Android Studio.
3. Compile the project to build the application.
4. Once compiled, install the APK on your Android device and start managing tasks.

**Note**: This project was created for educational purposes as part of a university assignment.

## Development Plans

- Add tracking for task progress in more detailed graphs.

## License

This project is licensed under the Apache 2.0 License.

## Disclaimer

This project was created purely for educational purposes as part of a university assignment and is not intended for commercial use.

## Links

Repository: [GitHub](https://github.com/Echo-terminal/AZP)

## Usage Examples

- **Personal task management**:
  - Create to-do lists
  - Set reminders for deadlines
  - Manage time effectively
