import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
public class Manager {

    public static void main(String[] args){

        String url = args[0];
        File file = new File (url);

        LinkedBlockingQueue<RandomAccessFile> queue = new LinkedBlockingQueue<>();
        //×
        // ADD TO METADATA
        //

        /*in case we get a list of mirrors, we do the following -
         * We read and seperate all the urls
         * We determine how many bytes we download with each thread
         * We determine how many threads we use in each mirror
         * We start running the threads using downloadFrom Function
         */
        
        if(file.exists()) {
            ArrayList<String> urls = new ArrayList<>();
            try {
				Scanner input = new Scanner(file);
				while (input.hasNext())
					urls.add(input.nextLine());
				URL link = new URL(urls.get(0));
				int size = link.openConnection().getContentLength();
				System.out.println(size);
				
				if(args.length < 2)
					downloadFrom(1, 0, size, urls.get(0),true, 0, queue);
				else {
					int threads = Integer.parseInt(args[1]);
					int partSize = size / threads; //How many bytes each thread downloads
					int tPerUrl[]  = new int [urls.size()];
					int length = tPerUrl.length;
					int calc = threads/ length; //How many threads on each byte
					for (int i = 0; i < length; i++) 
						tPerUrl[i] = calc;
					int remainder = threads % length; //Checking if some mirrors have more than others 
					if (remainder != 0)
						for (int i = 0; i < remainder; i ++)
							tPerUrl[i]++;
					int remain = size % threads; //Used to determine whether the last piece downloaded is some bits bigger than the others
					int threadsStarted = 0;
					for (int i = 0; i < urls.size(); i++) {
						if (i == urls.size()-1)
							downloadFrom(tPerUrl[i], threadsStarted, partSize, urls.get(i), true, remain, queue);
						else
							downloadFrom(tPerUrl[i], threadsStarted, partSize, urls.get(i), false, 0, queue);
						threadsStarted+=tPerUrl[i];
					}
					
				}
            } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            
        }
        /*
         * If we don't use mirrors, we just summon the downloadFrom function with the same methodology 
         */
        else {
            
			try {
			
				URL link = new URL(args[0]);
				int size = link.openConnection().getContentLength();  
				int threads = Integer.parseInt(args[1]);
				int partSize = size / threads;
				int remain = size % threads;
	            if(args.length > 1)
	        		downloadFrom(threads, 0, partSize , args[0], true, remain, queue);
	        	else
	        		downloadFrom(1, 0, partSize, args[0], true, 0, queue);
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        }
    }
         
    public static void downloadFrom(int split, int start, int partSize ,String url, boolean isLast, int remain, LinkedBlockingQueue<RandomAccessFile> queue) {
    	if(split == 0) return;
    	
    	/*
    	 * We use threads pool to run the whole program. we notice if we're handling the last piece.
    	 */
    	try {
            ExecutorService pool = Executors.newFixedThreadPool(split);
            for(int i = 1; i <= split; i++)
            	if(isLast && (i == split))
            		pool.submit(new Downloader(url, (partSize*(i-1+start)), ((partSize*(i+start))-1+remain), queue));
            	else
            		pool.submit(new Downloader(url, (partSize*(i-1+start)), ((partSize*(i+start))-1), queue));
            pool.shutdown();
    	}
    	catch(Exception e){}
    	}
    }
    
