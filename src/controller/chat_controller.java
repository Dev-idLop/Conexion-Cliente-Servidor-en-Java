package controller;


import java.net.*;
import java.io.*;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class chat_controller {
	@FXML
	private TextField txt_mensaje;
	
	@FXML
	private TextArea txt_chat;
	
	@FXML
	private Button btn_enviar;
	
	private Socket socket;
	private ServerSocket serversocket;
	private DataInputStream dataint;
	private DataOutputStream dataout;
	
	public void initialize() {
		Thread hiloEscucha = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					serversocket = new ServerSocket(5000);
					socket = serversocket.accept();
					dataint = new DataInputStream(socket.getInputStream());
					dataout = new DataOutputStream(socket.getOutputStream());
					while (true) {
						String mensaje = dataint.readUTF();
						Platform.runLater(() -> {
							txt_chat.appendText("Dispositivo Cliente: " + mensaje + "\n");
						});		
					}
					
				} catch (Exception e) {

				}
				
			}
		});
		hiloEscucha.start();
	}


	public void enviarMensaje() {
		try {
			dataout.writeUTF(txt_mensaje.getText());
			txt_chat.appendText("Dispositivo Servidor: " + txt_mensaje.getText() + "\n");
			txt_mensaje.clear();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}

