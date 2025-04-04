# JAVA-FileSharingSystem
A Java-based local network file sharing system supporting server discovery, multi-threaded parallel file transfers, and a GUI interface using Swing.
 
## Features

- Server-side multithreading to handle multiple file requests
- File selection and transmission via Swing GUI
- Client-server communication over Java sockets
- Real-time file progress display
- Lightweight and easy to run on any system with Java installed

## Technologies Used

- Java
- Java Swing
- Multithreading
- Socket Programming

## How to Run

1. **Compile the server and client code**:
   ```bash
   javac FileServer.java
   javac FileClient.java
   
2. Start the server:
   java FileServer

3. Start the client:
   java FileClient

4. Use the GUI to select and send files from the client to the server over the local network.
