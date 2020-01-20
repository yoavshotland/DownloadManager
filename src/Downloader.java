import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.nio.*;

public class Downloader implements Runnable {

	private static int BUFFER_SIZE = 1024;
	private static int count = 0;
    private String filePath;
    private URL url;
    private int start, end;
    private LinkedBlockingQueue<RandomAccessFile> queue;
    // an object that takes care of a certain part of the download
    // uses the 4 fields - first is the url to download from, second is the range start
    // third is the range end and forth is the queue to save the data to.

    public Downloader(String url, int start, int end, LinkedBlockingQueue<RandomAccessFile> queue){
        try {
			this.url = new URL (url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
		}
        this.filePath = url.substring(url.lastIndexOf('/') + 1)+""+count++;        
        this.start = start;
        this.end = end;
        this.queue = queue;
    }

    public int download(){
        try{
        	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    
	        BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
	        RandomAccessFile output = new RandomAccessFile(filePath, "rw");
	        System.out.println("thread number- "+ count +"  : "+start + "     " + end);
	        String range = 0 + "-" + end;
	        connection.setRequestProperty("Range", "bytes=" + range);
	        System.out.println("thread number- "+ count +"  : "+start + "     " + end);
	        connection.connect();
	        output.seek(0);
	        byte[] input = new byte[BUFFER_SIZE];
	        int check = 0;
	        while(true){
	        	int succeed = in.read(input, 0, BUFFER_SIZE);
	        	check++;
	        	if (succeed == -1) break;
	        	output.write(input, 0, succeed);
	        }
	        System.out.println("last :"+check);
	        queue.put(output);
        }
        catch (Exception e){
        	
        }

        return 1;
    }

    @Override
    public void run() {
        download();
    }
}
