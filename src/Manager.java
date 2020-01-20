import java.io.*;
import java.net.*;
import java.util.concurrent.*;
public class Manager {

    public static void main(String[] args){

        String url = args[0];
        File file = new File (url);

        LinkedBlockingQueue<RandomAccessFile> queue = new LinkedBlockingQueue<>();
        //×
        // ADD TO METADATA
        //

        if(file.exists());
            //take care of list
        else{
        	try {
	            int split = 1;
	            URL link = new URL(url);
	            
	            int size = link.openConnection().getContentLength();
	            System.out.println(size);
	            if(args.length > 1)
	                split = Integer.parseInt(args[1]);
	            ExecutorService pool = Executors.newFixedThreadPool(split);
	            int partSize = size / split;
	            for(int i = 1; i <= split; i++)
	                pool.submit(new Downloader(url, partSize*(i-1), (partSize*i)-1, queue));
	            pool.shutdown();
        	}
        	catch(Exception e){}
        	}
    }
}