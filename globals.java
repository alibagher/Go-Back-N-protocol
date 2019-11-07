// global file for sender.

// imports
import java.util.*;
import java.net.*;
import java.io.*;
import  java.lang.*;
import java.util.concurrent.locks.ReentrantLock;

public class globals {
    // our timer
    public static Timer timer = new Timer();
    public static int senderRecievePort;
    //boolean telling the sender if the EOT packet has been received.
    public static boolean eot = false;
     // The start of our window
    public static int base = 0;
    // The next number we need to send
    public static int nextSeqNum = 0;

    //size of the vector.
    public static int pktNum = 0;

    public static int mod = 0;


    // ports and sockets. 
    public static int emulatorPort;
    public static DatagramSocket socket;
    public static InetAddress host; 

    public static Vector<packet> sndpkt = new Vector<>();

    // files
    public static FileWriter fSeq;
    public static BufferedWriter brSeq;
    public static FileWriter fAck;
    public static BufferedWriter brAck;
    public static FileWriter fTime ;
    public static BufferedWriter brTime;

    public static ReentrantLock lock = new ReentrantLock();
}