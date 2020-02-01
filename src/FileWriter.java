import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class FileWriter implements Runnable {

        private BlockingQueue<Chunk> bq;
        private WriterManager writerManager;

        /**
         * constructor for fileWriter
         * @param writerManager - the creator for this function
         * @param bq
         */
        public FileWriter(WriterManager writerManager, BlockingQueue<Chunk> bq) {
            this.writerManager = writerManager;
            this.bq = bq;
        }

        /**
         * Put out the data from the BlockingQueue and write it on the disk
         */
        public void run() {
            long chunksRemainder = this.writerManager.getMetadataFile().getStillChunksToComplet();
            for (int i = 0; i < chunksRemainder; i++) {
                try {
                    // After TIMEOUT_CHUNK minutes the there is error of timeout (in our case after 2 minutes)
                    Chunk dataChunk = this.bq.poll(5, TimeUnit.MINUTES);
                    if (dataChunk != null)
                        this.writerManager.writeToFile(dataChunk);
                    else{
                        this.writerManager.getManager().kill(new Exception("Waiting too mach time for a single chunk"));
                        break;
                    }
                } catch (InterruptedException e) {
                    this.writerManager.getManager().kill(e);
                    return;
                }
            }
        }
    }
