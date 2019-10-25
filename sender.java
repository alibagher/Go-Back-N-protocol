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

    public static void main(String args[]) throws IOException {
        int hostAddr = Integer.valueOf(args[0]).intValue();
        int emulatorPort = Integer.valueOf(args[1]).intValue();
        int recievePort = Integer.valueOf(args[2]).intValue();
        String fName = args[3];

        // The start of our window
        int base = 1;
        // The next number we need to send
        int nextSeqNum = 1;

        int N = 10;







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