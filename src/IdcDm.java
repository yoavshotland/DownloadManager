import java.net.*;
import java.util.*;
import java.io.*;
import java.nio.*;
class IdcDm{
    public static void main (String[] args){
        try {
			URL link = new URL(args[0]);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
		}
        boolean isMultipleConnection = false;
        if(args.length > 1) {
            int numOfConnection = Integer.parseInt(args[1]);
            isMultipleConnection = true;
        }


    }
}