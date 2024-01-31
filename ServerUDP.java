package udp;

import java.net.*;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;


class ServerUDP{
    public static void main(String[] args) throws Exception
    {
        DatagramSocket datagramSocket = new DatagramSocket(9876);
        
        String jdbcUrl = "jdbc:postgresql://127.0.0.1:5432/chatdb";
        String username = "postgres";
        String password = "root";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            System.out.println("Connected to the database.");
        
        
        byte[] dataToReceive = new byte[100];
        byte[] name = new byte[20];
        
        System.out.println("Waiting For DatagramPacket...");
        DatagramPacket receivedname = new DatagramPacket(name,name.length);
        datagramSocket.receive(receivedname);
        String nameToPrint = new String(receivedname.getData(),0,receivedname.getLength());
        System.out.println("Welcome " + nameToPrint);
        
        
        
        
        while(true)
        {
            DatagramPacket receivedPacket = new DatagramPacket(dataToReceive,dataToReceive.length);
            
            datagramSocket.receive(receivedPacket);

            String sentence = new String(receivedPacket.getData(),0,receivedPacket.getLength());
            if(sentence.equalsIgnoreCase("exit"))
            {
                System.out.println("Exiting....");
                break;
            }
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO chat_messages (name, message) VALUES (?, ?) RETURNING timestamp")) {
                    preparedStatement.setString(1, nameToPrint);
                    preparedStatement.setString(2, sentence);
                    try (ResultSet resultSet = preparedStatement.executeQuery()){
                    if (resultSet.next()) {
                        String dbTimestamp = resultSet.getString("timestamp");
                        System.out.println(dbTimestamp + " " + nameToPrint + " : " + sentence + ".");
                    }
                }
            }
            
            InetAddress addressIP = receivedPacket.getAddress();
            int port = receivedPacket.getPort();
            String upperCaseResponse = sentence.toUpperCase();

            byte[] dataToSend = upperCaseResponse.getBytes();
            DatagramPacket sentPacket = new DatagramPacket(upperCaseResponse.getBytes(),upperCaseResponse.getBytes().length, addressIP, port);
            datagramSocket.send(sentPacket);

        }
        
        datagramSocket.close();
    }
}
}