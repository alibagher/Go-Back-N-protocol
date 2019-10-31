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


class CodeRunner implements Runnable {

    // override the run function that the new thread will run
    @Override
    public void run() {
        try{
            // open a port to listen from. 
            DatagramSocket socket = new DatagramSocket(globals.senderRecievePort);

            byte[] buffer = new byte[1000];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);

            socket.receive(request);
            packet p = packet.createACK(0);
            p.parseUDPdata(buffer);
            globals.base = p.getSeqNum() + 1;
            if(globals.base == globals.nextSeqNum){
                globals.timer.cancel();
            }
            if(p.getType()== 2){
                globals.eot = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}



public class sender {

    private static int N = 10;

    
    private static DatagramSocket socket;
        private static InetAddress host; 
        private static int emulatorPort;

        private static Vector<packet> sndpkt = new Vector<>();


    public static void main(String args[]) throws IOException {
        String hostname = args[0];
        emulatorPort = Integer.valueOf(args[1]).intValue();
        globals.senderRecievePort = Integer.valueOf(args[2]).intValue();
        String fName = args[3];

        host = InetAddress.getByName(hostname);

        try{
            socket = new DatagramSocket(1024);
        }catch(SocketException e){
            System.out.println("Socket: " + e.getMessage());
        }
            
        //InetAddress aHost = InetAddress.getByName(args[0]);

        

        // first we need to create an array of packets ready to be sent.
        
        //populate the vector with packets. 
        //while we have more characters to parse from the file. 
        // but we have to open the file first
        // pass the path to the file as a parameter 
        FileReader fr = new FileReader(fName); 
  
        int i;
        String pktString = "";
        int pktL = 0;
        int seqNum = 0;
        // while loop reading in a char at a time.
        while ((i=fr.read()) != -1){
            if(pktL < 500){
                pktString += (char) i;
                pktL ++;
            }else{
                pktL = 0;
                // make the packet and add to the vector.
                try{
                    packet p = packet.createPacket(seqNum, pktString);
                    seqNum ++;
                    pktString = "";
                    sndpkt.add(p);
                }catch(Exception e){
                    e.printStackTrace();
                }

                
            }
        }

        // make the packet of the remainig chars
        if(pktL > 0){
            pktL = 0;
            try{
                packet p = packet.createPacket(seqNum, pktString);
                seqNum ++;
                sndpkt.add(p);
            }catch(Exception e){
                e.printStackTrace();
            }
            
        }

        // finally add the EOT packet
        try{
            packet p = packet.createEOT(seqNum);
            seqNum ++;
            sndpkt.add(p);
        }catch(Exception e){
            e.printStackTrace();
        }
       

        // now we have a vector that is populated with all of the packets.

        // create a new thread and to listen to acks
        CodeRunner runner = new CodeRunner();
        Thread thread = new Thread(runner);
        thread.start();

        while (!globals.eot){
            // checking if we can send the packet.
            if (globals.nextSeqNum < globals.base + N){
                //sndpkt[nextSeqNum] = make_pkt(nextSeqNum,data,chksum);
                //udt_send(sndpkt[nextSeqNum]);
                // copied from book, how to send packets using udp
                // send the packet
                try{
                    byte [] p = sndpkt.get((globals.nextSeqNum)-1).getUDPdata();
                    DatagramPacket request = new DatagramPacket(p, p.toString().length(), host, emulatorPort);
                    socket.send(request);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                if (globals.base == globals.nextSeqNum){
                    // this starts a timer for 100 miliseconds
                    globals.timer.schedule(new RemindTask(), 100);
                }
                // move the next seq number. 
                globals.nextSeqNum++;
                
            }else{
                //refuse_data(data)
            }
        }

    if (socket != null)
        socket.close();

    }


    static class RemindTask extends TimerTask {
        public void run() {
            //System.out.println("Time's up!");
            globals.timer.cancel(); //Terminate the timer thread
            globals.timer.purge();

            globals.timer = new Timer();
            
            // start the new timer.
            globals.timer.schedule(new RemindTask(), 100);
            
            for (int i = globals.base; i < globals.nextSeqNum; i++){
                // copied from book, how to send packets using udp
                try{
                    byte [] p = sndpkt.get(i-1).getUDPdata();
                    DatagramPacket request = new DatagramPacket(p, p.toString().length(), host , emulatorPort);
                    //DatagramPacket response = new DatagramPacket(new byte[1024], 1024);
                    socket.send(request);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}