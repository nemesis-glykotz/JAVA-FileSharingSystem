import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class FileServer {
    private static final int PORT = 5000;
    private static final int DISCOVERY_PORT = 5001;
    private static JTextArea logArea;
    private static JProgressBar progressBar;

    public static void main(String[] args) {
        JFrame frame = new JFrame("File Server");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        logArea = new JTextArea();
        logArea.setEditable(false);
        frame.add(new JScrollPane(logArea), BorderLayout.CENTER);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        frame.add(progressBar, BorderLayout.SOUTH);

        new Thread(FileServer::startServer).start();
        new Thread(FileServer::startDiscoveryListener).start();

        frame.setVisible(true);
    }

    private static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            log("Server started on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            log("Error: " + e.getMessage());
        }
    }

    private static void startDiscoveryListener() {
        try (DatagramSocket socket = new DatagramSocket(DISCOVERY_PORT)) {
            byte[] buffer = new byte[256];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String request = new String(packet.getData(), 0, packet.getLength());

                if ("DISCOVER_SERVER".equals(request)) {
                    String serverName = InetAddress.getLocalHost().getHostName(); // Get device name
                    String serverIP = InetAddress.getLocalHost().getHostAddress(); // Get IP address
                    String response = serverName + "|" + serverIP; // Send name + IP
                    byte[] responseData = response.getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(
                            responseData, responseData.length, packet.getAddress(), packet.getPort());
                    socket.send(responsePacket);
                }
            }
        } catch (IOException e) {
            log("Discovery error: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            int fileCount = dis.readInt();

            for (int i = 0; i < fileCount; i++) {
                String fileName = dis.readUTF();
                long fileSize = dis.readLong();
                log("Receiving file: " + fileName + " (" + fileSize + " bytes)");

                FileOutputStream fos = new FileOutputStream("received_" + fileName);
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                byte[] buffer = new byte[1024 * 1024];
                int bytesRead;
                long totalRead = 0;

                while (totalRead < fileSize && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalRead))) != -1) {
                    bos.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                    updateProgress(totalRead, fileSize);
                }

                bos.flush();
                bos.close();
                log("File received successfully: " + fileName);
            }
            dis.close();
            updateProgress(100, 100);
        } catch (IOException e) {
            log("Error handling client: " + e.getMessage());
        }
    }

    private static void log(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
    }

    private static void updateProgress(long totalRead, long fileSize) {
        int percent = (int) ((totalRead * 100) / fileSize);
        SwingUtilities.invokeLater(() -> progressBar.setValue(percent));
 
    }
}
