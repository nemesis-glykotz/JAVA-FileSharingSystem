# JAVA-FileSharingSystem
A Java-based local network file sharing system supporting server discovery, multi-threaded parallel file transfers, and a GUI interface using Swing.
 
 Team Members:
 Rounak Singh (Roll No: 24124038)
 Aaditya Saxena (Roll No: 24124001)
 Divyansh Soni (Roll No: 24124018)
 Divyansh Sahu (Roll No: 24124017)
 
 Project Idea
 Our project is a local network-based file-sharing system built using Java. The system allows users to
 discover available file servers on the network and transfer files seamlessly. It consists of two main
 components:
 1. File Server - Listens for file transfer requests, receives files, and provides its IP for discovery.
 2. File Client - Scans for available servers, allows users to select files, and sends them to the
 chosen server(s).
 The system uses TCP (Transmission Control Protocol) for reliable file transfers and UDP (User
 Datagram Protocol) for discovering servers dynamically.
 Features
 * Server Discovery - Uses UDP broadcasts to detect available servers.
 * Graphical User Interface (GUI) - Built with Swing for an interactive experience.
 * Multi-File Selection - Allows sending multiple files at once.
 * Progress Bar & Logging - Displays file transfer progress and logs events.
 * Multi-threading - Handles multiple connections simultaneously.

Parallel File Transfers
 1. Client -> Multiple Servers
   - The client creates a separate thread for each selected server in sendFiles().
   - This means it can send files simultaneously to multiple servers.
 2. Multiple Clients -> One Server
   - The server runs in a loop, accepting multiple client connections.
   - Each client connection is handled in a separate thread in startServer().
   - So, multiple clients can send files at the same time to the same server.
 Thus, the system supports parallel file transfers in both directions.
 
 Tech Stack
 Programming Language: Java
 GUI Framework: Swing, AWT (for layout management)
 Networking: Java Networking (java.net) - Uses TCP & UDP
 File Handling: Java I/O (java.io)
 Multithreading: Java Threads
 Java Extensions Used:
  - javax.swing - For GUI elements like JFrame, JTextArea, and JProgressBar.
  - java.awt - For layout management (e.g., BorderLayout).
  - java.net - For networking features (Socket, ServerSocket, DatagramPacket).
  - java.io - For reading and writing file data.

 
 Individual Contributions
 
 Rounak Singh (24124038) - GUI Development (Swing & AWT)
 Rounak handled the Graphical User Interface (GUI) using Swing and AWT. His responsibilities
 included:
- Designing the server application window with logs and a progress bar.- Implementing JFrame, JTextArea, JScrollPane, JProgressBar for a user-friendly interface.- Managing event handling for UI interactions.- Ensuring a smooth user experience with a well-structured layout.

Divyansh Soni (24124018) - Networking (java.net)
 Divyansh worked on implementing network communication using TCP and UDP. His tasks included:- Setting up server discovery using UDP broadcasts.- Implementing Socket programming for file transfers using TCP.- Handling client-server communication efficiently.

 Aaditya Saxena (24124001) - File Handling (java.io)
 Aaditya was responsible for managing file operations. His contributions included:- Implementing file selection using JFileChooser.- Managing file input/output streams for reading and writing files.- Ensuring efficient file transfer through buffered I/O streams.

 Divyansh Sahu (24124017) - Backend Logic & Functionality
 Divyansh focused on the core logic of the system, ensuring smooth execution. His contributions
 included:- Handling multithreading to support multiple connections.- Implementing the file reception process on the server side.- Optimizing performance by managing buffer sizes and exception handling
