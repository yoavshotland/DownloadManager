import java.net.*;
import java.util.*;
import java.io.*;
import java.nio.*;
class IdcDm{
    public static void main (String[] args){
        URL link = new URL(args[0]);
        boolean isMultipleConnection = false;
        if(args.length > 1) {
            int numOfConnection = Integer.parseInt(args[1]);
            isMultipleConnection = true;
        }


    }
}