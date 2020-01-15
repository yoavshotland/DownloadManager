import java.io.*;
import java.net.*;
import java.util.concurrent.BlockingQueue;

public class Downloader implements Runnable {
    static int count = 0;
    private File file;
    private URL url;
    private int start, end;
    private BlockingQueue<RandomAccessFile> queue;
    // a function that takes care of a certain part of the download
    // uses the 4 fields - first is the url to download from, second is the range start
    // third is the range end and forth is the queue to save the data to.

    public Downloader(String url, int start, int end,BlockingQueue<RandomAccessFile> queue){
        this.url = new URL (url);
        this.file = new File ("/downloads/tmpFileNumber"+count++);
        this.start = start;
        this.end = end;
        this.queue = queue;
    }

    public int download(){
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
        RandomAccessFile output = new RandomAccessFile(file);
        connection.setRequestProperty("Range", "bytes="+start+"-"+end);
        connection.connect();
        while(true){
            int next = in.read() ;
            if (next == -1) break;
            output.write(next);
        }

        queue.add(output);
        return 1;
    }

    @Override
    public void run() {
        download();
    }
}
