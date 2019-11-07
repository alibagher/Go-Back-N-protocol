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

import java.lang.Integer.*;
import java.lang.Object;
import java.lang.Number;

class RemindTask extends TimerTask {
        public void run() {
            //System.out.println("Time's up!");
            globals.timer.cancel(); //Terminate the timer thread
            globals.timer.purge();

            globals.timer = new Timer();
            globals.timer.schedule(new RemindTask(), 115);
            
            // start the new timer.
            
            if(!globals.eot){
                
                // (globals.mod*32)+globals.base+
                
                //globals.lock.lock();
                int n = globals.nextSeqNum;
                
                //System.out.println("before the for loop");
                for (int i = globals.base + globals.mod * 32; i < n+1; i++){
                    System.out.println("in loop");
                    // copied from book, how to send packets using udp
                    
                    if (i < globals.sndpkt.size() -1  && !globals.eot){
                    try{
                         System.out.println("sending packet seq number: " + globals.sndpkt.get(i).getSeqNum() + "at index: " + (i) + " mod: " + globals.mod);
                        byte [] p = globals.sndpkt.get(i).getUDPdata();
                        DatagramPacket request = new DatagramPacket(p, 512, globals.host , globals.emulatorPort);
                        //DatagramPacket response = new DatagramPacket(new byte[1024], 1024);
                        globals.socket.send(request);
                        globals.brSeq.write(new Integer(globals.sndpkt.get(i).getSeqNum()).toString() + "\n");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    }else if(globals.eot){
                        //globals.lock.unlock();
                        globals.timer.cancel();
                    }
                }
                //globals.lock.unlock();
                

            }else{
                globals.timer.cancel();
            }
        }
    }


class CodeRunner implements Runnable {

    // override the run function that the new thread will run
    @Override
    public void run() {
        
        // the sequence number we recieved from the 
        int receivedNum = 0;
        
        try{
            // open a port to listen from. 
            DatagramSocket aSocket = new DatagramSocket(globals.senderRecievePort);

            //while(!globals.eot){
            while (true){
                byte[] buffer = new byte[512];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);

                aSocket.receive(request);

                packet p = packet.parseUDPdata(buffer);

                if (p.getType() == 2){
                    System.out.println("got the eof from reciever");
                    System.out.println("got the eof from reciever");
                    System.out.println("got the eof from reciever");
                    System.out.println("got the eof from reciever");
                    System.out.println("got the eof from reciever");
                    globals.eot = true;
                    break;
                }

                globals.brAck.write(new Integer(p.getSeqNum()).toString() + "\n");

                if(p.getSeqNum() == 0 && globals.base > 1 || p.getSeqNum() == 1 && globals.base > 1 || p.getSeqNum() == 2 && globals.base > 2
                    || p.getSeqNum() == 3 && globals.base > 3 || p.getSeqNum() == 4 && globals.base > 4
                        || p.getSeqNum() == 5 && globals.base > 5 || p.getSeqNum() == 6 && globals.base > 6 || p.getSeqNum() == 7 && globals.base > 7){
                    globals.mod ++;
                }

                receivedNum = p.getSeqNum() + (globals.mod * 32);
                int b = globals.base + (globals.mod * 32);
    

                globals.base = p.getSeqNum();

                if (globals.nextSeqNum < receivedNum) {
                    globals.nextSeqNum = receivedNum;
                }

                //globals.lock.lock();
                if(globals.base + (globals.mod * 32) == globals.nextSeqNum){
                    globals.timer.cancel();
                    //globals.timer = new Timer();
                    //globals.timer.schedule(new RemindTask(), 115);
                }else{
                    globals.timer.cancel();
                    globals.timer = new Timer();
                    globals.timer.schedule(new RemindTask(), 115);
                }
                //globals.lock.unlock();


                System.out.println("got packet with seqnum: " + p.getSeqNum() + "globals.pktnum: " + globals.pktNum);
                System.out.println("recievednum" + receivedNum + " base: " + globals.base + " mod: " + globals.mod); 

                if(receivedNum >= (globals.pktNum - 2)){
                    globals.eot = true;
                    break;
                }
            }
            aSocket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        
    }
}


public class sender {

    // window size
    private static int N = 10;

    public static void main(String args[]) throws IOException {
        String hostname = args[0];
        globals.emulatorPort = Integer.valueOf(args[1]).intValue();
        globals.senderRecievePort = Integer.valueOf(args[2]).intValue();
        String fName = args[3];


        // get all of the files ready for writing
        try{
            File file = new File("seqnum.log");
            globals.fSeq = new FileWriter(file);
            globals.brSeq = new BufferedWriter(globals.fSeq);

            file = new File("ack.log");
            globals.fAck = new FileWriter(file);
            globals.brAck = new BufferedWriter(globals.fAck);

            file = new File("time.log");
            globals.fTime = new FileWriter(file);
            globals.brTime = new BufferedWriter(globals.fTime);
        }catch(IOException e){
            e.printStackTrace();
        }  


        // asign host address and open a socket.
        globals.host = InetAddress.getByName(hostname);
        try{
            globals.socket = new DatagramSocket(1024);
        }catch(SocketException e){
            System.out.println("Socket: " + e.getMessage());
        }

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
                    globals.sndpkt.add(p);
                }catch(Exception e){
                    e.printStackTrace();
                }
                pktString += (char) i;
                pktL ++;
            }
        }

        // make the packet of the remainig chars
        if(pktL > 0){
            pktL = 0;
            try{
                // System.out.println("creating a packet of size: " + pktString.length() + "seqNum: " + seqNum % 32);
                packet p = packet.createPacket(seqNum, pktString);
                seqNum ++;
                globals.sndpkt.add(p);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        // finally add the EOT packet
        try{
            packet p = packet.createEOT(seqNum);
            seqNum ++;
            globals.sndpkt.add(p);
        }catch(Exception e){
            e.printStackTrace();
        }
        globals.pktNum = seqNum;

        

        // now we have a vector that is populated with all of the packets.

        // create a new thread and to listen to acks
        CodeRunner runner = new CodeRunner();
        Thread thread = new Thread(runner);
        thread.start();

        // start the timer
        long startTime = System.currentTimeMillis();

        while (!globals.eot){
            // checking if we can send the packet.
            try{
                //Thread.sleep(5);
            }catch(Exception e){
                //System.out.println(e.getMessage());
            }

            globals.lock.lock();
            if (globals.nextSeqNum  < (globals.base +(globals.mod * 32) + N) && globals.nextSeqNum < globals.sndpkt.size()-2){
                System.out.println("sending sequm: " + (globals.nextSeqNum) + "base: " + (globals.base +(globals.mod * 32)) + " size of vecotr: " + globals.sndpkt.size());
                //sndpkt[nextSeqNum] = make_pkt(nextSeqNum,data,chksum);
                //udt_send(sndpkt[nextSeqNum]);
                // copied from book, how to send packets using udp
                // send the packet
                try{
                    byte [] p = globals.sndpkt.get((globals.nextSeqNum)).getUDPdata();
                    //System.out.println("sending something of size: " + p.toString().length());
                    DatagramPacket request = new DatagramPacket(p, 512, globals.host, globals.emulatorPort);
                    globals.socket.send(request);
                    globals.brSeq.write(new Integer(globals.sndpkt.get(globals.nextSeqNum).getSeqNum()).toString() + "\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                //&& !globals.eot
                if (globals.base + (globals.mod * 32) == globals.nextSeqNum){
                    System.out.println("base = nextq");
                    // this starts a timer for 100 miliseconds
                    globals.timer.cancel(); //Terminate the timer thread
                    globals.timer.purge(); 

                    globals.timer = new Timer();
                    globals.timer.schedule(new RemindTask(), 115);
                }

                // move the next seq number.
                ////// ******** not working 
                // TO DO: akshdf k
                
                
                
                
                // && globals.nextSeqNum < globals.base + (globals.mod * 32) +N
                if (globals.nextSeqNum < globals.sndpkt.size()-2){
                    globals.nextSeqNum++;
                }
            
            }else{
                //refuse_data(data)
            }
            globals.lock.unlock();
            
        }

        // send the eot because we have sent everything and got acks for them as well.
        try{
            byte [] p = globals.sndpkt.get(globals.sndpkt.size() - 1).getUDPdata();
            DatagramPacket request = new DatagramPacket(p, 512, globals.host, globals.emulatorPort);
            globals.socket.send(request);
            globals.brSeq.write(new Integer(globals.sndpkt.get(globals.sndpkt.size() - 1).getSeqNum()).toString() + "\n");
            globals.eot = true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // calculate and record the transmition time.
        long endTime = System.currentTimeMillis();
        globals.brTime.write(new Integer((int)((int)endTime - (int)startTime)).toString() + "\n");


        if (globals.socket != null){
            globals.socket.close();
        }

        // close all of the files. 
        try {
            globals.brSeq.close();
            globals.fSeq.close();

            globals.brAck.close();
            globals.fAck.close();

            globals.brTime.close();
            globals.fTime.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //thread.stop();
        //return;

    }
}

