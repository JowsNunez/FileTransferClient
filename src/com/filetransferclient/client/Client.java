package com.filetransferclient.client;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JOSÉ ALFREDO NÚÑEZ AGUIRRE 
 * HIRAM GARCIA HERMOSILLO
 * KEVIN DANIEL RIOS RANCANO
 * GABRIEL FRANCISCO PINUELAS RAMOS
 */
public class Client implements Runnable {

    private DatagramSocket socketClient;
    private DatagramPacket packet;
    private final int BUFFER_SIZE = 1024;
    private byte[] buffer = new byte[BUFFER_SIZE];
    private InetAddress serverAddress;
    private final int PORT = 9090;
    private boolean firstConnection = true;
    private String nameFile;
    private String files;
    private final String HOST = "localhost";

    public Client() {
        initHost();
    }

    private void initHost() {
        try {
            this.socketClient = new DatagramSocket();
            this.serverAddress = InetAddress.getByName(HOST);
        } catch (UnknownHostException | SocketException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void run() {

        while (true) {
            try {

                if (firstConnection) {
                    // Enviar un paquete al servidor
                    String message = "Connection";
                    sendData(message);

                    // Esperar la respuesta del servidor
                    receiveData(buffer, BUFFER_SIZE);
                    // Mostrar la respuesta del servidor
                    String response = new String(packet.getData(), 0, packet.getLength());
                    this.files = response;
                    firstConnection = false;

                } else {
                    System.out.println("Escribe el nombre del archivo a descargar:");
                    BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
                    this.nameFile = bf.readLine();
                    // Enviamos el nombre del archivo a descargar
                    sendData(this.nameFile);

                    System.out.println("Download.... " + this.nameFile);
                    // esperamos la respuesta del servidor
                    receiveData(buffer, BUFFER_SIZE);
                    String response = new String(packet.getData(), 0, packet.getLength());
                    // si el archivo no existe en el servidor muestra un mensaje en consola
                    if (response.equals("File Not Found")) {
                        System.out.println("File Not Found: " + this.nameFile);
                    } else {
                        // y si no entonces escribe el archivo recibido 
                        writeFile(this.packet);
                    }

                }
                System.out.print("\033[H\033[2J");
                System.out.flush();
                System.out.println("Files:\n" + this.files);

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

    }

    public void receiveData(byte[] buffer, int length) throws IOException {
        this.packet = new DatagramPacket(buffer, length);
        this.socketClient.receive(this.packet);
    }

    public void sendData(String msg) throws IOException {
        DatagramPacket sendPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, this.serverAddress, PORT);
        socketClient.send(sendPacket);
    }

    public void writeFile(DatagramPacket receivePacket) {
        // verificamos si existe la ruta download en la carpeta raiz del proyecto
        File folder = new File("download");
        if (!folder.exists()) {
            folder.mkdir();
        }
        // se escribe el archivo recibido.
        File file = new File("download\\" + this.nameFile);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
            // se reciben los datagramas que conforman el archivo
            while (true) {
                bos.write(receivePacket.getData(), 0, receivePacket.getLength());
                bos.flush();
                // si el paquete es  menor al tamanio del buffer quiere decir que se leyo el ultimo, por lo que el archivo es escirto completamente
                if (receivePacket.getLength() < BUFFER_SIZE) {
                    break;
                }

                this.receiveData(buffer, BUFFER_SIZE);
                this.socketClient.receive(receivePacket);
            }
            bos.flush();
            System.out.println("Download Coomplete...");
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            this.nameFile = "";

        }

    }

}
