import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
public class Manager {
	private static int num_of_chunks = 10000;
    public static void main(String[] args){

        String url = args[0];
        File file = new File (url);
        //String fileName = url.substring(url.lastIndexOf('/') + 1);        
        String fileName = "che.avi";
        LinkedBlockingQueue<Chunk> queue = new LinkedBlockingQueue<>();
        //×
        // ADD TO METADATA
        //

        /*in case we get a list of mirrors, we do the following -
         * We read and seperate all the urls
         * We determine how many bytes we download with each thread
         * We determine how many threads we use in each mirror
         * We start running the threads using downloadFrom Function
         */
        
      
        MetaData metadata = new MetaData(num_of_chunks, fileName);
        Writer writer = new Writer(queue, metadata, fileName, num_of_chunks);
        Thread write = new Thread(writer);
        write.start();
        if(file.exists()) {
            ArrayList<String> urls = new ArrayList<>();
            try {
				Scanner input = new Scanner(file);
				while (input.hasNext())
					urls.add(input.nextLine());
				URL link = new URL(urls.get(0));
				int size = link.openConnection().getContentLength();
				System.out.println(size);
				int sizeOfChunk = size / num_of_chunks;
				//int   = size % num_of_chunks; // the addition to the last chunk of the file
				if(args.length < 2)
					downloadFrom(1, 0, size, urls.get(0),true, 0,sizeOfChunk, queue);
				else {
					int threads = Integer.parseInt(args[1]);
					int partSize = size / threads; //How many bytes each thread downloads
					int tPerUrl[]  = new int [urls.size()];
					int length = tPerUrl.length;
					int calc = threads/ length; //How many threads on each mirror
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
							downloadFrom(tPerUrl[i], threadsStarted, partSize, urls.get(i), true, remain, sizeOfChunk, queue );
						else
							downloadFrom(tPerUrl[i], threadsStarted, partSize, urls.get(i), false, 0, sizeOfChunk,queue);
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
         //close unnecessary connections like for getting the length   
			try {
				URL link = new URL(args[0]);
				int size = link.openConnection().getContentLength();
				int sizeOfChunk = size / num_of_chunks;
				if(args.length < 2)
					downloadFrom(1, 0, size, args[0], true, 0, sizeOfChunk,queue);
				else {
					int threads = Integer.parseInt(args[1]);
					int partSize = size / threads;
					int remain = size % threads;
					downloadFrom(threads, 0, partSize , args[0], true, remain, sizeOfChunk,queue);
				}
				}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        }
    }
         
    public static void downloadFrom(int split, int start, int partSize ,String url, boolean isLast, int remain, int sizeOfChunk, LinkedBlockingQueue<Chunk> queue) {
    	if(split == 0) return;
    	
    	/*
    	 * We use threads pool to run the whole program. we notice if we're handling the last piece.
    	 */
    	try {
            ExecutorService pool = Executors.newFixedThreadPool(split);
            for(int i = 1; i <= split; i++)
            	if(isLast && (i == split))
            		pool.submit(new Downloader(url, (partSize*(i-1+start)), ((partSize*(i+start))-1+remain), sizeOfChunk, queue ));
            	else
            		pool.submit(new Downloader(url, (partSize*(i-1+start)), ((partSize*(i+start))-1), sizeOfChunk,queue));
            pool.shutdown();
    	}
    	catch(Exception e){}
    	}
    }
    
