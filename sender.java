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

public class sender {

    public static void main(String args[]) throws IOException {
        String hostname = args[0];
        int emulatorPort = Integer.valueOf(args[1]).intValue();
        int recievePort = Integer.valueOf(args[2]).intValue();
        String fName = args[3];

        // The start of our window
        int base = 1;
        // The next number we need to send
        int nextSeqNum = 1;

        int N = 10;

        // first we need to create an array of packets ready to be sent.
        Vector sndpkt = new Vector();
        //populate the vector with packets. 
        //while we have more characters to parse from the file. 
        // but we have to open the file first
        // pass the path to the file as a parameter 
        FileReader fr = new FileReader(fName); 
  
        int i;
        String pktString = "";
        int pktL = 0;
        while ((i=fr.read()) != -1){
            if(pktL < 500){
                pktString += (char) i;
                pktL ++;
            }else{
                pktL = 0;
                // make the packet and add to the vector.
                packet p = createPacket(seqnumber, string);
                sndpkt.add();
            }
        }


        try (DatagramSocket socket = new DatagramSocket(0)) {
            socket.setSoTimeout(10000);
            InetAddress host = InetAddress.getByName(hostname);
            DatagramPacket request = new DatagramPacket(new byte[1], 1, host , PORT);
            DatagramPacket response = new DatagramPacket(new byte[1024], 1024);
            socket.send(request);
            socket.receive(response);
            String result = new String(response.getData(), 0, response.getLength(), "US-ASCII");
            System.out.println(result);
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        
        // copied from slides, must change up. 
        if (nextSeqNum < base + N) {
            sndpkt[nextSeqNum] = make_pkt(nextSeqNum,data,chksum);
            udt_send(sndpkt[nextSeqNum]);

            if (base == nextSeqNum){
                start_timer();
                nextSeqNum++;
            }
        }else{
            refuse_data(data)
        }

    }

}