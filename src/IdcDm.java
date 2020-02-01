import java.net.*;
import java.util.*;
import java.io.*;
import java.nio.*;
class IdcDm{
    public static void main (String[] args){
    	//a "useless" class i created in order to maintain downloads when internet breaks down
    	// long story short a found out that my main class should be an oject with a kill method
    	Manager manager = new Manager();
    	manager.downloadAll(args);
    }
}