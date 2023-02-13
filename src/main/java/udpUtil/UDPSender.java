/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package udpUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pocos pero locos
 */
public class UDPSender {

    private DatagramSocket udpSocket;
    private InetAddress address;
    private int port;
    private int nextSeqNum;
    private int packetSize;

    /*
        Método constructor que recibe como parámetros el socket UPD 
        el InetAddres y el puerto
     */
    public UDPSender(DatagramSocket socket, InetAddress address, int port, int packetSize) {
        this.udpSocket = socket;
        this.address = address;
        this.port = port;
        this.nextSeqNum = 0;
        this.packetSize = packetSize;
    }

    /**
     * Método que serializa un objeto y lo envía a través de un paquete.
     *
     * @param obj El objeto que se desea enviar
     * @throws IOException Si ocurre un error al enviar el paquete
     */
    public void sendObject(Serializable obj) throws IOException {
        byte[] data = serializeObject(obj);
        List<byte[]> packets = splitIntoPackets(data);
        sendPackets(packets);
    }

    /**
     * Envía un paquete de datos a través del socket UDP especificado.
     *
     * @param data Los datos a enviar en forma de matriz de bytes.
     * @throws IOException Si ocurre un error al enviar los datos.
     */
    public void sendPacket(byte[] data) throws IOException {
        byte[] seqNumBytes = intToBytes(nextSeqNum);
        byte[] packet = new byte[data.length + 4];

        System.arraycopy(seqNumBytes, 0, packet, 0, 4);
        System.arraycopy(data, 0, packet, 4, data.length);

        DatagramPacket sendPacket = new DatagramPacket(packet, packet.length, address, port);
        udpSocket.send(sendPacket);

        DatagramPacket receivePacket = new DatagramPacket(new byte[4], 4);
        boolean receivedAck = false;

        while (!receivedAck) {
            try {
                udpSocket.receive(receivePacket);
                int receivedSeqNum = bytesToInt(receivePacket.getData());

                if (receivedSeqNum == nextSeqNum) {
                    receivedAck = true;
                    nextSeqNum++;
                }
            } catch (SocketTimeoutException e) {
                udpSocket.send(sendPacket);
            }
        }
    }

    /**
     * Método que permite serializar un objeto para poder enviarlo a través de
     * la red.
     *
     * @param obj El objeto que se desea serializar. Debe ser serializable.
     * @return Un arreglo de bytes que representa al objeto serializado.
     * @throws IOException Si ocurre algún error durante el proceso de
     * serialización.
     */
    private byte[] serializeObject(Serializable obj) throws IOException {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        ObjectOutputStream objOutputStream = new ObjectOutputStream(byteArrayOS);

        objOutputStream.writeObject(obj);
        objOutputStream.close();

        return byteArrayOS.toByteArray();
    }

    /**
     * Método que convierte un entero en un arreglo de bytes.
     *
     * @param num Entero a convertir en bytes.
     * @return Arreglo de bytes resultante de la conversión.
     */
    private byte[] intToBytes(int num) {
        return new byte[]{
            (byte) (num >>> 24),
            (byte) (num >>> 16),
            (byte) (num >>> 8),
            (byte) num
        };
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

    private List<byte[]> splitIntoPackets(byte[] data) {
        List<byte[]> packets = new ArrayList<>();
        int numPackets = (int) Math.ceil(data.length / (double) packetSize);

        for (int i = 0; i < numPackets; i++) {
            int start = i * numPackets;
            int end = Math.min(start + numPackets, data.length); //checar

            byte[] packet = new byte[end - start];
            System.arraycopy(data, start, packet, 0, packet.length);

            packets.add(packet);

        }
        return packets;
    }

    private void sendPackets(List<byte[]> packets) throws IOException {
        byte[] numPackets = intToBytes(packets.size());
        this.sendPacket(numPackets);
        
        for (byte[] packet : packets) {
            this.sendPacket(packet);
        }
    }

}
