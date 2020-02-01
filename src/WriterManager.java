import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.*;

/**
 * This class responsible for writing the data and tge temporary metadata file on the disk
 * and transfer the 2 temporary metadat file in to 1 (with ATOMIC_MOVE) that for saving the metadata file in case of
 * problem with writing on the disk
 */

public class WriterManager {

    private Manager manager;
    private MetaData metadataFile;
    private RandomAccessFile file;
    private String tempFileName;

    public WriterManager(String fileName, int size, Manager manager){
        this.tempFileName = fileName + ".tmp";
        try {
        	//We create a new file with the desired name. we check if any metadata exists. if yes - we deserealize it.
        	this.file = new RandomAccessFile(fileName , "rw");

            File tempFile = new File(tempFileName); // Open the temp File
            this.manager = manager;
            if(!tempFile.exists()) this.metadataFile = new MetaData(size, fileName);
            else {
                // The metadata file is already exists
                // Open the metadata file
                ObjectInputStream objectFile = new ObjectInputStream(new FileInputStream(tempFile));
                this.metadataFile = (MetaData) objectFile.readObject();
                objectFile.close();
            }
        }catch (Exception e) {
            System.err.println("There is some problem with the metadata file");
            return;
        }
    }

    public void writeToFile(Chunk dataChunk){
        try {
        	//we check the current percentage. than we simply write the chunk and it respected place
        	
            int previousPercentages = this.metadataFile.getPercentages();
            if(previousPercentages == 100) 	
            	return;
            
            this.file.seek(dataChunk.getStart());
            this.file.write(dataChunk.getData());

            //after we finish the work with the specific chunk, we take care of the metadata procedure.
            this.metadataFile.setBitMap(dataChunk.getBitMapLocation()); // Update the bit map
            ObjectOutputStream objectOut = new ObjectOutputStream(new FileOutputStream(this.tempFileName + "1"));
            objectOut.writeObject(this.metadataFile);

            objectOut.close();

            //we use 2 metadata files and an atomic function to tackle write bugs to the metadata (if interuption occurs while writing to metadata, for example
            File nowTempMetadata = new File(this.tempFileName);
            File prevTempMetadata = new File(this.tempFileName + "1");
            
            try {
                Files.move(prevTempMetadata.toPath(), nowTempMetadata.toPath(), StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException ignored) {}
            int currentPercentages = this.metadataFile.getPercentages();
            // Check if the percentages is change after this adding
            if(previousPercentages < currentPercentages){
                System.out.println("Downloaded " + currentPercentages + "%");
                if(currentPercentages == 100) {
                	this.deleteMetadataFile();
                	System.out.println("Download Succeeded");
                }
            }
        } catch (Exception e) {

        	System.out.println ("Exception start at index of start = "+ dataChunk.getBitMapLocation()+ " and the start of it is : " + dataChunk.getStart());
        	e.printStackTrace();
        }
    }

    /**
     *  Return the Metadata
     * @return
     */
    public MetaData getMetadataFile() {
        return this.metadataFile;
    }

    /**
     *
     * @return the Manager
     */
    public Manager getManager() {
        return this.manager;
    }

    /**
     *
     * @param bq
     * @return - new the thread to be used for the writer.
     */
    public Thread createThread(BlockingQueue<Chunk> bq){
        return new Thread(new FileWriter(this, bq));
    }

    /**
     * the function deleted the Metadata file from the disk after finish download
     */
    public void deleteMetadataFile() {
        String tempFile = this.tempFileName;

        File file = new File(tempFile);
        if (!file.delete()) 
            System.err.println("can't delete the Metadata file: " + tempFile);

        if (this.file != null) {
            try {
                this.file.close();
            } catch (IOException e) {
                System.err.println("Can't close the file: " + this.file);
            }
        }
    }
    }