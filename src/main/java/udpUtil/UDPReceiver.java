/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package udpUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pocos pero locos
 */
public class UDPReceiver {

    private DatagramSocket udpSocket;
    private byte buffer[];

    public UDPReceiver(DatagramSocket udpSocket) {
        this.udpSocket = udpSocket;
        buffer = new byte[1024];
    }

    /**
     * Método que permite deserializar un objeto
     *
     * @param data El arreglo de bytes que se desea deserializar.
     * @return Un objeto deserializado.
     * @throws IOException Si ocurre algún error durante el proceso de
     * serialización. ClassNotFoundException en caso de no encontrar la clase.
     */
    public Object deserealizeObject(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayIS = new ByteArrayInputStream(data);
        ObjectInputStream objInputStream = new ObjectInputStream(byteArrayIS);

        Object obj = objInputStream.readObject();
        objInputStream.close();

        return obj;
    }

    public List<byte[]> receivePackets() throws IOException {
        int packetReceiver = 0;
        List<byte[]> packets = new ArrayList<>();
        DatagramPacket packetDatagram = new DatagramPacket(buffer, buffer.length);
        
        udpSocket.receive(packetDatagram);
        int numPackets = bytesToInt(packetDatagram.getData());

        while (packetReceiver < numPackets) {
            udpSocket.receive(packetDatagram);
            packets.add(packetDatagram.getData());
            packetReceiver++;

        }
        return packets;
    }
    
    
    public byte[] combinePackets(List<byte[]> packets){
        int length = 0;
        
        for (byte[] packet : packets) {
            length += packet.length;
        }
        
        byte[] data = new byte[length];
        
        for (byte[] packet : packets) {
            int currentIndex = 0;
            System.arraycopy(packet, 0, data, currentIndex, packet.length);
            currentIndex += packet.length;
        }
        
        return data;
    }

    /**
     * Convierte un arreglo de bytes en un número entero de 32 bits.
     *
     * @param bytes El arreglo de bytes a convertir.
     * @return El número entero resultante.
     */
    private int bytesToInt(byte[] bytes) {
        return (bytes[0] << 24)
                + ((bytes[1] & 0xFF) << 16)
                + ((bytes[2] & 0xFF) << 8)
                + (bytes[3] & 0xFF);
    }

}
