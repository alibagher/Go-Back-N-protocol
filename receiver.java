// sender code
// Ali Bagheri Nov. 2019

//imports
import java.net.*;
import java.io.*;
import java.util.*;
import  java.lang.*;
import java.util.concurrent.TimeUnit;
// scanner
import java.util.Scanner;

public class receiver {

    private static int expectedseqnum = 0;

    public static void main(String args[]) throws IOException {
        String hostname = args[0];
        int emulatorPort = Integer.valueOf(args[1]).intValue();
        int recievePort = Integer.valueOf(args[2]).intValue();
        String fName = args[3];
        
        DatagramSocket socket = null;
        try{
            // open a port to listen from. 
            socket = new DatagramSocket(recievePort);
            InetAddress host = InetAddress.getByName(hostname);

            boolean b = true;

            // System.out.println("before the while loop");

            //File file = new File(fName);
            //FileWriter fr = new FileWriter(file, true);
            FileOutputStream f = new FileOutputStream(fName);
            

            while(b){
                byte[] buffer = new byte[512];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                
                // System.out.println("buffer size: " + buffer.length);
                packet gotP = packet.parseUDPdata(buffer);

                int s = gotP.getSeqNum();

                if ((expectedseqnum % 32) == s){
                    expectedseqnum++;
                    //write into the output file
                    f.write(gotP.getData());
                }

                // System.out.println("got packet with seqnumber: " + s);
            
                packet rPacket;

                if(gotP.getType() == 2){
                    // System.out.println("preparing EOT");
                    rPacket = packet.createEOT(expectedseqnum);
                    b = false;
                }else {
                    // System.out.println("preparing ack");
                    rPacket = packet.createACK(expectedseqnum);
                }



                byte [] p = rPacket.getUDPdata();
                request = new DatagramPacket(p, 512, host, emulatorPort);
                // System.out.println("sending");
                socket.send(request);

                packet pp = packet.parseUDPdata(request.getData());
                // System.out.println("sent a packet with seqnum: " + expectedseqnum%32);

                
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally{
            if (socket != null)
                socket.close();
        }


    }

}