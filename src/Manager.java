import java.io.*;
import java.net.*;
import java.util.concurrent.*;
public class Manager {

    public static void main(String[] args){

        String url = args[0];
        File file = new File (url);

        BlockingQueue<RandomAccessFile> queue = new BlockingQueue<RandomAccessFile>() {
        }
        if(file.exists())
            //take care of list
        else{
            int split = 1;
            URL link = new URL(url);
            int size = link.openConnection().getContentLength();
            if(args.length > 1)
                split = Integer.parseInt(args[1]);
            ExecutorService pool = Executors.newFixedThreadPool(split);
            int partSize = size / split;
            for(int i = 1; i <= split; i++)
                pool.submit(new Downloader(url, partSize*(i-1), (partSize*i)-1, queue));
            pool.shutdown();

             }
}
