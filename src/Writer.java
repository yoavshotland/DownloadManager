import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.*;

public class Writer implements Runnable {
	LinkedBlockingQueue<Chunk> queue;
	MetaData meta;
	String fileName;
    URL url;
    RandomAccessFile output;
    int totalNumOfChunks, numOfChunksWritten;

	
	public Writer (LinkedBlockingQueue<Chunk> queue, MetaData meta, String name, int totalNumberOfChunks) {
		try {
			this.queue = queue;
			this.meta = meta;
	        this.fileName = name;        
	        this.totalNumOfChunks = totalNumberOfChunks;
	        this.numOfChunksWritten = 0;
	        this.output = new RandomAccessFile(fileName, "rw");
		}
		catch(Exception e) {
			
		}
	}
	
	public void writeData(MetaData history, Chunk data) {
		try {
			
			output.seek(data.getStart());
			output.write(data.getData());
			history.declareInsertion(data.getBitMapLocation());
			this.numOfChunksWritten++;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void run() {
		try {
			 //We let the thread to sleep until some chunks (hopefully) get into the queue
		while(true) {
			if(!(this.queue.isEmpty()))
				writeData(meta, queue.remove() );
			
			if(this.totalNumOfChunks == this.numOfChunksWritten)
				break;
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
