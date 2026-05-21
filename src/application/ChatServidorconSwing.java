package application;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;

public class ChatServidorconSwing extends JFrame {

    private JTextArea txt_chat;
    private JTextField txt_mensaje;
    private JButton btn_enviar;

    private ServerSocket serverSocket;
    private Socket socket;
    private DataInputStream dataint;
    private DataOutputStream dataout;

    public ChatServidorconSwing() {
        setTitle("Servidor Chat - VS Code");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        txt_chat = new JTextArea();
        txt_chat.setEditable(false);
        txt_chat.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(txt_chat);

        txt_mensaje = new JTextField();
        btn_enviar = new JButton("Enviar");

        JPanel panelInferior = new JPanel(new BorderLayout(5, 5));
        panelInferior.add(txt_mensaje, BorderLayout.CENTER);
        panelInferior.add(btn_enviar, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        btn_enviar.addActionListener(e -> enviarMensaje());
        txt_mensaje.addActionListener(e -> enviarMensaje());

        iniciarServidor();
    }

    private void iniciarServidor() {
        Thread hiloServidor = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(5000);
                SwingUtilities.invokeLater(() -> txt_chat.append("Servidor iniciado. Esperando cliente en el puerto 5000...\n"));

                socket = serverSocket.accept();
                SwingUtilities.invokeLater(() -> txt_chat.append("¡Cliente conectado desde: " + socket.getInetAddress().getHostAddress() + "!\n"));

                dataint = new DataInputStream(socket.getInputStream());
                dataout = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    String mensaje = dataint.readUTF();
                    SwingUtilities.invokeLater(() -> {
                        txt_chat.append("Cliente: " + mensaje + "\n");
                    });
                }

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> txt_chat.append("El servidor se ha cerrado o hubo un error.\n"));
            }
        });
        hiloServidor.start();
    }

    private void enviarMensaje() {
        try {
            String texto = txt_mensaje.getText().trim();
            if (!texto.isEmpty() && dataout != null) {
                dataout.writeUTF(texto);
                txt_chat.append("Yo (Servidor): " + texto + "\n");
                txt_mensaje.setText("");
            }
        } catch (Exception e) {
            txt_chat.append("Error al enviar el mensaje.\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChatServidorconSwing().setVisible(true);
        });
    }
}
