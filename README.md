# StudyBuddy starter

A Java 17 + Spring Boot + Thymeleaf starter website for a student study planner.

## Included
- Responsive landing page
- Study planner page
- Add tasks with subject, due date, and priority
- Mark tasks complete and delete them
- Sample tasks on first launch
- Responsive CSS styling

## Requirements
- Java 17 or later
- IntelliJ IDEA
- Internet access for Maven to download dependencies on first run

## Run in IntelliJ
1. Extract the ZIP.
2. In IntelliJ, choose **Open** and select the extracted `StudyBuddy` folder (the folder containing `pom.xml`).
3. Trust/open the Maven project and wait for dependency import to finish.
4. Open `src/main/java/com/studybuddy/StudyBuddyApplication.java`.
5. Run `StudyBuddyApplication`.
6. Open http://localhost:8080 in your browser.

## Important starter limitations
- Planner tasks are stored in memory and reset when the application restarts.
- There is no registration/login, persistent database, real reminder delivery, or AI API integration yet.
- Do not publish this starter as a multi-user service until authentication, database persistence, per-user access control, and production configuration are implemented.
