package application;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;

public class ChatClienteSwing extends JFrame {
    
    private JTextArea txt_chat;
    private JTextField txt_mensaje;
    private JButton btn_enviar;
    
    private Socket socket;
    private DataInputStream dataint;
    private DataOutputStream dataout;

    public ChatClienteSwing() {
        setTitle("Chat Cliente - VS Code");
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

        conectarAlServidor();
    }

    private void conectarAlServidor() {
        Thread hiloEscucha = new Thread(() -> {
            try {
                txt_chat.append("Conectando al servidor...\n");
                socket = new Socket("127.0.0.1", 5000); 
                
                dataint = new DataInputStream(socket.getInputStream());
                dataout = new DataOutputStream(socket.getOutputStream());
                
                txt_chat.append("¡Conectado exitosamente!\n");

                while (true) {
                    String mensaje = dataint.readUTF();
                    SwingUtilities.invokeLater(() -> {
                        txt_chat.append(mensaje + "\n");
                    });
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    txt_chat.append("Error: No se pudo conectar con el servidor.\n");
                });
            }
        });
        hiloEscucha.start();
    }

    private void enviarMensaje() {
        try {
            String texto = txt_mensaje.getText().trim();
            if (!texto.isEmpty() && dataout != null) {
                dataout.writeUTF(texto);
                txt_chat.append("Yo: " + texto + "\n");
                txt_mensaje.setText("");
            }
        } catch (Exception e) {
            txt_chat.append("Error al enviar el mensaje.\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChatClienteSwing().setVisible(true);
        });
    }
}
