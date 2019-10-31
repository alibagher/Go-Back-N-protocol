import java.util.*;

public class globals {
    // our timer
    public static Timer timer = new Timer();
    public static int senderRecievePort;
    //boolean telling the sender if the EOT packet has been received.
    public static boolean eot = true;
     // The start of our window
    public static int base = 1;
    // The next number we need to send
    public static int nextSeqNum = 1;
}