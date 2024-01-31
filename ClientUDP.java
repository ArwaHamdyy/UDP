package udp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

class ClientUDP{
    public static void main(String[] args) throws Exception
    {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket datagramSocket = new DatagramSocket();
        InetAddress addressIP = InetAddress.getByName("127.0.0.1");
        byte[] dataToSend =  new byte[100];
        byte[] dataToReceive = new byte[100];
        byte[] nameToSend = new byte[20];

        System.out.println("Please Enter Your Name:");
        String name = input.readLine();
        nameToSend = name.getBytes();
        DatagramPacket namePacket = new DatagramPacket(nameToSend,nameToSend.length, addressIP, 9876);
        datagramSocket.send(namePacket);


        while(true)
        {
            String sentence = input.readLine();
            dataToSend = sentence.getBytes();

            DatagramPacket sentPacket = new DatagramPacket(dataToSend,dataToSend.length, addressIP, 9876);
            datagramSocket.send(sentPacket);
            if(sentence.equalsIgnoreCase("exit"))
            {
                System.out.println("Exiting....");
                break;
            }
            DatagramPacket receivedPacket = new DatagramPacket(dataToReceive,dataToReceive.length);
            datagramSocket.receive(receivedPacket);
            String response = new String(receivedPacket.getData(),0,receivedPacket.getLength());
            System.out.println("Received From Server:" + response + ".");
        }
        

        datagramSocket.close();
    }
}