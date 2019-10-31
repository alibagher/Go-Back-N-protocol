// sender code
// Ali Bagheri Nov. 2019

//imports
import java.net.*;
import java.io.*;
import  java.lang.*;
import java.util.concurrent.TimeUnit;
// scanner
import java.util.Scanner;

public class sender {

    private static int expectedseqnum = 1;

    public static void main(String args[]) throws IOException {
        String hostname = args[0];
        int emulatorPort = Integer.valueOf(args[1]).intValue();
        int recievePort = Integer.valueOf(args[2]).intValue();
        String fName = args[3];
        
        try{
            // open a port to listen from. 
            DatagramSocket socket = new DatagramSocket(recievePort);
            InetAddress host = InetAddress.getByName(hostname);

            boolean b = true;

            while(b){
                byte[] buffer = new byte[1000];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                packet gotP = parseUDPdata(buffer);

                if (expectedseqnum + 1 == gotP.getSeqNum()){
                    expectedseqnum++;
                }
            
                packet rPacket;
                if(gotP.getType() == 2){
                    rPacket = createEOT(expectedseqnum);
                    b = false;
                }else {
                    rPacket = createACK(expectedseqnum);
                }

                byte [] p = rPacket.getUDPdata();
                DatagramPacket request = new DatagramPacket(p, p.toString().length(), host, emulatorPort);
                socket.send(request);
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally{
            if (socket != null)
                socket.close();
        }


    }

}