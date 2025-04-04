import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class FileClient {
    private static final int DISCOVERY_PORT = 5001;
    private static final int SERVER_PORT = 5000;

    private JFrame frame;
    private DefaultListModel<String> serverListModel;
    private JList<String> serverList;
    private JProgressBar progressBar;
    private JTextArea logArea;
    private JButton discoverButton, sendButton, selectFilesButton;
    private File[] selectedFiles;

    public FileClient() {
        frame = new JFrame("File Sharing Client");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        Color bgColor = new Color(30, 30, 30);
        Color textColor = new Color(200, 200, 200);
        UIManager.put("Panel.background", bgColor);
        UIManager.put("OptionPane.background", bgColor);
        UIManager.put("OptionPane.messageForeground", textColor);
        UIManager.put("List.background", new Color(40, 40, 40));
        UIManager.put("List.foreground", textColor);
        UIManager.put("Button.background", new Color(50, 50, 50));
        UIManager.put("Button.foreground", textColor);
        UIManager.put("ProgressBar.background", new Color(70, 70, 70));
        UIManager.put("ProgressBar.foreground", new Color(100, 200, 100));
        UIManager.put("TextArea.background", new Color(40, 40, 40));
        UIManager.put("TextArea.foreground", textColor);

        serverListModel = new DefaultListModel<>();
        serverList = new JList<>(serverListModel);
        serverList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        frame.add(new JScrollPane(serverList), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 3));

        discoverButton = new JButton("Discover Servers");
        discoverButton.addActionListener(this::discoverServers);
        bottomPanel.add(discoverButton);

        selectFilesButton = new JButton("Select Files");
        selectFilesButton.addActionListener(this::selectFiles);
        bottomPanel.add(selectFilesButton);

        sendButton = new JButton("Send Files");
        sendButton.addActionListener(this::sendFiles);
        sendButton.setEnabled(false);
        bottomPanel.add(sendButton);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        JPanel progressPanel = new JPanel(new BorderLayout());
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressPanel.add(progressBar, BorderLayout.NORTH);

        logArea = new JTextArea(5, 30);
        logArea.setEditable(false);
        progressPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        frame.add(progressPanel, BorderLayout.NORTH);

        frame.setVisible(true);
    }

    private void discoverServers(ActionEvent e) {
        serverListModel.clear();
        java.util.List<String> servers = findServers();
        if (servers.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No servers found.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            for (String server : servers) {
                serverListModel.addElement(server);
            }
        }
    }

    private void selectFiles(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            selectedFiles = fileChooser.getSelectedFiles();
            sendButton.setEnabled(true);
        }
    }

    private void sendFiles(ActionEvent e) {
        java.util.List<String> selectedServers = serverList.getSelectedValuesList();
        if (selectedServers.isEmpty() || selectedFiles == null || selectedFiles.length == 0) {
            JOptionPane.showMessageDialog(frame, "Please select a server and files first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (String serverInfo : selectedServers) {
            String serverIP = serverInfo.split("\\|")[1].trim();
            new Thread(() -> transferFiles(serverIP)).start();
        }
    }

    private void transferFiles(String serverIP) {
        try (Socket socket = new Socket(serverIP, SERVER_PORT)) {
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            dos.writeInt(selectedFiles.length);

            for (File file : selectedFiles) {
                dos.writeUTF(file.getName());
                dos.writeLong(file.length());
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                    byte[] buffer = new byte[1024 * 1024];
                    int bytesRead;
                    long totalSent = 0;
                    long fileSize = file.length();

                    while ((bytesRead = bis.read(buffer)) > 0) {
                        dos.write(buffer, 0, bytesRead);
                        totalSent += bytesRead;
                        updateProgress((int) ((totalSent * 100) / fileSize));
                    }
                }
                log("Sent: " + file.getName() + " to " + serverIP);
            }
            dos.flush();
            JOptionPane.showMessageDialog(frame, "Files sent successfully to " + serverIP, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            log("Failed to send files to " + serverIP);
        }
    }

    private void updateProgress(int percent) {
        SwingUtilities.invokeLater(() -> progressBar.setValue(percent));
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
    }

    private java.util.List<String> findServers() {
        java.util.List<String> servers = new ArrayList<>();
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            byte[] requestData = "DISCOVER_SERVER".getBytes();
            DatagramPacket packet = new DatagramPacket(requestData, requestData.length, InetAddress.getByName("255.255.255.255"), DISCOVERY_PORT);
            socket.send(packet);

            byte[] buffer = new byte[256];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
            socket.setSoTimeout(2000);

            while (true) {
                try {
                    socket.receive(responsePacket);
                    String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
                    if (!servers.contains(response)) {
                        servers.add(response);
                    }
                } catch (SocketTimeoutException ex) {
                    break;
                }
            }
        } catch (IOException ex) {
            log("Error discovering servers.");
        }
        return servers;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FileClient::new);
    }
}