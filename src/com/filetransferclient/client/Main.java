
package com.filetransferclient.client;

/**
 *
 * @author JOSÉ ALFREDO NÚÑEZ AGUIRRE 
 * HIRAM GARCIA HERMOSILLO
 * KEVIN DANIEL RIOS RANCANO
 * GABRIEL FRANCISCO PINUELAS RAMOS
 */
public class Main {
    public static void main(String[] args) {
        Thread thread = new Thread(new Client());
        thread.start();
    }
    
}
