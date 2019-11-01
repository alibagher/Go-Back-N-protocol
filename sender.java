// sender code
// Ali Bagheri Nov. 2019

//imports
import java.net.*;
import java.io.*;

import java.util.*;

import  java.lang.*;
import java.util.concurrent.TimeUnit;

import java.lang.Math.*;

// scanner
import java.util.Scanner;


class CodeRunner implements Runnable {

    // override the run function that the new thread will run
    @Override
    public void run() {
        //System.out.println("runnable");
        int receivedNum = 0;
        // the number of times we wrapped around


        int[] n = new int[32];
        for(int i=0;i<32;i++)
            n[i] = 0;
        
        try{
            // open a port to listen from. 
            DatagramSocket aSocket = new DatagramSocket(globals.senderRecievePort);

            while(!globals.eot){
            byte[] buffer = new byte[512];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);

            //System.out.println("ready to recieve acks");
            aSocket.receive(request);

            packet p = packet.parseUDPdata(buffer);

            if(p.getSeqNum() == 0 && globals.base > 25){
                globals.mod ++;
            }

            if( (Math.abs((p.getSeqNum() + 1) - globals.base) > 9) || (p.getSeqNum() + 1 > globals.base)){
                globals.base = p.getSeqNum();
            }

            //globals.base = Math.max(p.getSeqNum() + 1, globals.base);
            //n[p.getSeqNum()]++;

            // check if we have wrapped around.
            //if (p.getSeqNum() == 0 && n[0] > (mod+1)){
            //    mod++;
            //}
            
            receivedNum = p.getSeqNum() + (globals.mod * 32);

            // if(globals.base == globals.nextSeqNum){
            //     globals.timer.cancel();
            // }else{
            //     globals.timer = new Timer();
            //     globals.timer.schedule(new RemindTask(), 100);
            // }

            //System.out.println("got packet with seqnum: " + p.getSeqNum() + "globals.pktnum: " + globals.pktNum);
            // System.out.println("recievednum" + receivedNum); 

            if(receivedNum == (globals.pktNum - 1)){
                globals.eot = true;
                // System.out.println("eot set to zero");
            }
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
        // System.out.println("start");
        String hostname = args[0];
        emulatorPort = Integer.valueOf(args[1]).intValue();
        globals.senderRecievePort = Integer.valueOf(args[2]).intValue();
        String fName = args[3];



        host = InetAddress.getByName(hostname);
        // System.out.println("before socket 1024");
        try{
            socket = new DatagramSocket(1024);
        }catch(SocketException e){
            System.out.println("Socket: " + e.getMessage());
        }
            
        //InetAddress aHost = InetAddress.getByName(args[0]);

        // System.out.println("after socket 1024 is open for sending info");

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
                    // System.out.println("creating a packet of size: " + pktString.length() + "seqNum: " + seqNum % 32);
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
                // System.out.println("creating a packet of size: " + pktString.length() + "seqNum: " + seqNum % 32);
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
        globals.pktNum = seqNum;
       

    //    System.out.println("before new thread");

        // now we have a vector that is populated with all of the packets.

        // create a new thread and to listen to acks
        CodeRunner runner = new CodeRunner();
        Thread thread = new Thread(runner);
        thread.start();

        // System.out.println("after new thread");


        // && globals.nextSeqNum < sndpkt.size()
        while (!globals.eot){
            //System.out.println("beginning of while loop");
            // checking if we can send the packet.
            try{
            Thread.sleep(20);
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
            if (globals.nextSeqNum +(globals.mod *32) < (globals.base +(globals.mod * 32) + N)){
                // System.out.println("sending sequm: " + (globals.nextSeqNum) + " size of vecotr: " + sndpkt.size());
                //sndpkt[nextSeqNum] = make_pkt(nextSeqNum,data,chksum);
                //udt_send(sndpkt[nextSeqNum]);
                // copied from book, how to send packets using udp
                // send the packet
                try{
                    byte [] p = sndpkt.get((globals.nextSeqNum)).getUDPdata();
                    //System.out.println("sending something of size: " + p.toString().length());
                    DatagramPacket request = new DatagramPacket(p, 512, host, emulatorPort);
                    socket.send(request);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                if (globals.base == globals.nextSeqNum && !globals.eot){
                    // this starts a timer for 100 miliseconds
                    globals.timer.schedule(new RemindTask(), 100);
                }
                // move the next seq number.
                if (globals.nextSeqNum < sndpkt.size()-2){
                    globals.nextSeqNum++;
                }
                
            }else{
                //refuse_data(data)
            }
        }
        // send the eot
        // System.out.println("sending eot");
        try{
            byte [] p = sndpkt.get(sndpkt.size() - 1).getUDPdata();
            DatagramPacket request = new DatagramPacket(p, 512, host, emulatorPort);
            socket.send(request);
            globals.eot = true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        if (socket != null){
            socket.close();
        }

    }


    static class RemindTask extends TimerTask {
        public void run() {
            //System.out.println("Time's up!");
            globals.timer.cancel(); //Terminate the timer thread
            globals.timer.purge();

            globals.timer = new Timer();
            
            // start the new timer.
            // System.out.println("globals.eot: " + globals.eot);
            if(!globals.eot){
                globals.timer.schedule(new RemindTask(), 100);
                for (int i = globals.base + (globals.mod * 32); i < (globals.mod*32)+globals.base+N; i++){
                    // copied from book, how to send packets using udp
                    
                    if (i < sndpkt.size() -1  && !globals.eot){
                    try{
                        // System.out.println("sending packet seq number: " + sndpkt.get(i).getSeqNum() + "at index: " + (i));
                        byte [] p = sndpkt.get(i).getUDPdata();
                        DatagramPacket request = new DatagramPacket(p, 512, host , emulatorPort);
                        //DatagramPacket response = new DatagramPacket(new byte[1024], 1024);
                        socket.send(request);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    }else if(globals.eot){
                        globals.timer.cancel();
                    }
                }
            }else{
                globals.timer.cancel();
            }
            
            
            
        }
    }
}