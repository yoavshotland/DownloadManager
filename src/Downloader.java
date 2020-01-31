import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.nio.*;

public class Downloader implements Runnable {

	
	
	// check what should be done concerning the "too small files"
	 
	private static int count = 0;
    private URL url;
    private int start, end;
    private LinkedBlockingQueue<Chunk> queue;
    private int sizeOfChunk;
    private int chunkRemain;
    // an object that takes care of a certain part of the download
    // uses the 4 fields - first is the url to download from, second is the range start
    // third is the range end and forth is the queue to save the data to.

    public Downloader(String url, int start, int end, int sizeOfChunk, LinkedBlockingQueue<Chunk> queue){
        try {
			this.url = new URL (url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
		}
        this.start = start;
        this.end = end;
        this.queue = queue;
        this.sizeOfChunk = sizeOfChunk;
    }

    @Override
    public void run() {
        try{
        	/*
        	 * We set up the connection and the get request
        	 * we start download with the BUFFER_SIZE mentioned
        	 */
        	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        String range = start + "-" + end;
	        connection.setRequestMethod("GET");
        	connection.setRequestProperty("Range", "bytes=" + range);
        	connection.connect();	      
	        System.out.println("thread number- "+ count++ +"  : "+start + "     " + end);
	        BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
	        int alreadyRead = 0;
	        int size = this.end-this.start+1;
	        int numOfChunks = size / sizeOfChunk;
	        int remainder = size % sizeOfChunk;
	        System.out.println("this is the size of current thread download : "+ size + " this is the number of chunks :" + numOfChunks +" this is the size of chunk :  "+ sizeOfChunk+" this is the remainder : "+ remainder + "and this is the remainder of the last chunk size");
	        int check = 0;
	        byte[] input;
	        int succeed;
	        for(int i = 0; i < numOfChunks; i++){
	        	if (i == (numOfChunks-1)) {
	        		int lastChunkSize = sizeOfChunk+remainder;
	        		byte[] remainderInput = new byte[lastChunkSize];
	        		while(alreadyRead < lastChunkSize) {
		        		succeed = in.read(remainderInput, alreadyRead, lastChunkSize - alreadyRead);
		        		if(succeed != -1)
				       		alreadyRead += succeed;
				       	else System.out.println("problem in the chunk loading");
	        		}
	        		System.out.println("we got to the last chunk. it starts at : " + start + " and its size is "+ lastChunkSize);
	        		queue.put(new Chunk(remainderInput, start));	
	        		if(alreadyRead != lastChunkSize) System.out.println("wierd trouble with the LAST chunk preparing"+ alreadyRead + "    , " + lastChunkSize);
	        		
	        		start+= alreadyRead;
	        		alreadyRead =0;
	        	}
	        	else {
	        		input = new byte[sizeOfChunk];
	        		while(alreadyRead < sizeOfChunk) {
			        	succeed = in.read(input, alreadyRead, sizeOfChunk-alreadyRead);
			        	if(succeed != -1)
			        		alreadyRead += succeed;
			        	else System.out.println("problem in the chunk loading");
	        		}
	        		queue.put(new Chunk(input, start));
		        	
	        		start+= alreadyRead;
		        	if(alreadyRead != sizeOfChunk) System.out.println("wierd trouble with the chunk preparing");
	        		alreadyRead = 0;
	        	}
	        }
	        System.out.println("last :"+count);
	        
        }
        catch (Exception e){
        	e.printStackTrace();
        }

        
    }

    }

