import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MyClient extends JFrame {
    private final String SERVER_ADDRESS = "127.0.0.1";
    private final Integer SERVER_PORT = 8880;

    private JTextField msgInputField;
    private JTextArea chatArea;

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean noTakedEnd = true;

    public MyClient() throws HeadlessException, IOException {
        connectionToServer();
        prepareGUI();
    }

    private void connectionToServer() throws IOException {
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    String message = dis.readUTF();
                    if (message.startsWith("/end")) {
                        dos.writeUTF(message); //new
                        chatArea.append("Сервер закрыл соединение\n");
                        noTakedEnd = false;
                        break;
                    }
                    if (message.startsWith("/start")) {
                        chatArea.append(message + "\n");
                        break;
                    }
                    chatArea.append(message + "\n");
                }

                while (true) {
                    if (!noTakedEnd) {
                        closeConnection();
                        msgInputField.setText("");
                        msgInputField.setEditable(false);
                        break;
                    }

                    String inputText = dis.readUTF();
                    if (inputText.startsWith("/end")) {
                        closeConnection();
                        msgInputField.setText("");
                        msgInputField.setEditable(false);
                        noTakedEnd = false;
                        break;
                    }
                    chatArea.append(inputText + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ошибка подключения к серверу.");
            }
            return;
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void closeConnection() {
        try {
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String msg = msgInputField.getText();
        if (msg != null && !msg.trim().isEmpty()) {
            try {
                dos.writeUTF(msg);
                msgInputField.setText("");
                msgInputField.grabFocus();
                if (msg.startsWith("/end")) {
                    chatArea.append("-------------Вы закрыли соединение-------------\n");
                    msgInputField.setText("");
                    msgInputField.setEditable(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ошибка отправки сообщения.");
            }
        }
    }

    public void prepareGUI() {
        // Параметры окна
        setBounds(600, 300, 500, 500);
        setTitle("Клиент");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton btnSendMsg = new JButton("Отправить");
        bottomPanel.add(btnSendMsg, BorderLayout.EAST);
        msgInputField = new JTextField();
        add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.add(msgInputField, BorderLayout.CENTER);
        btnSendMsg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        msgInputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (noTakedEnd) {
                    try {
                        dos.writeUTF("/end");
                    } catch (IOException exc) {
                        exc.printStackTrace();
                        System.out.println("работа закончилась");
                    }
                }
            }
        });

        setVisible(true);
    }
}
