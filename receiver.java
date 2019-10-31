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

    private static expectedseqnum = 0;

    public static void main(String args[]) throws IOException {
        String hostname = args[0];
        int emulatorPort = Integer.valueOf(args[1]).intValue();
        int recievePort = Integer.valueOf(args[2]).intValue();
        String fName = args[3];
        

        
        packet p = createACK(0);
        


    }

}