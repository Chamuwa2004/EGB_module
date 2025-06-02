# Distributed Online Auction System - Frontend

This project contains the HTML, CSS, and JavaScript frontend for a Distributed Online Auction System.
It is structured as a Java Maven web application project, suitable for opening with IntelliJ IDEA or other IDEs that support Maven.

## Project Structure

-   `src/main/java`: Contains the Java source code (e.g., `Distributed Online Auction System.java` which might contain EJB/JMS logic).
-   `src/main/webapp`: Contains the web application files.
    -   `index.html`: The main page for the auction system.
    -   `css/style.css`: Stylesheet for the auction page.
    -   `js/script.js`: JavaScript for frontend logic and interactivity.
-   `src/test/java`: For Java unit tests.
-   `pom.xml`: Maven project object model file, defines project dependencies and build settings.

## Features (Frontend Simulation)

-   Displays a list of auction items (simulated data).
-   Shows current highest bid and auction timers (simulated).
-   Allows users to place bids (simulated, no backend interaction).
-   Provides feedback on bid placement.

## How to Run

1.  **Open in an IDE**: Import the project into IntelliJ IDEA or Eclipse as a Maven project.
2.  **Build the Project**:
    -   Using Maven command line: `mvn clean install`
    -   Or use the IDE's Maven build options. This will produce a WAR file in the `target/` directory (e.g., `OnlineAuctionSystem.war`).
3.  **Deploy the WAR file**:
    -   Deploy the generated WAR file to a Servlet container like Apache Tomcat, Jetty, or WildFly.
    -   Copy `target/OnlineAuctionSystem.war` to the webapps directory of your Tomcat server (e.g., `TOMCAT_HOME/webapps/`).
4.  **Access the Application**:
    -   Once deployed, you can usually access it at `http://localhost:8080/OnlineAuctionSystem/` (the port and context path may vary depending on your server configuration).

## Note

This part of the project focuses on the frontend (HTML, CSS, JS) and setting up the Java project structure. The EJB and JMS backend logic mentioned in the initial requirements would need to be implemented separately and integrated with this frontend. The JavaScript currently simulates backend interactions.
