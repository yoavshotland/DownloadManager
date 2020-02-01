
public class MetaData implements java.io.Serializable{
	private boolean[] bitMap;
	private String name;
	private int chunksWritten;
	public MetaData (int numOfChunks, String name) {
		this.bitMap = new boolean[numOfChunks];
		this.name = name;
		this.chunksWritten =0;
	}
	
	public void declareInsertion(int location) {
		this.bitMap[location] = true;
		
	}

    /**
     *  Get chunks until now
     * @return chunks until now
     */
    public long getChunksUntilNow() {
        return chunksWritten;
    }

    /**
     * Get size of the file
     * @return size of the file
     */
    public long getSize() {
        return this.bitMap.length;
    }

    /**
     * Get the bit map
     * @return the bit map that array of  boolean
     */
    public boolean[] getBitMap() {
        return bitMap;
    }

    /**
     * Get the name of the file
     * @return
     */
    public String getFileName() {
        return name;
    }

    /**
     *  Set the bit map array with the current index to true
     * @param index - index on the bit map
     */
    public void setBitMap(int index) {
        this.bitMap[index] = true;
        this.chunksWritten++;
    }

    /**
     * Get the percentages in integer that already completed
     * @return the percentages that already completed in integer
     */
    public int getPercentages(){
        return (int)(((float) chunksWritten / bitMap.length) * 100);
    }

    /**
     * Return how many chunks needed to complete the downloader
     * @return int of how many chunks needed to complete
     */
    public long getStillChunksToComplet(){
        return bitMap.length - this.chunksWritten;
    }

    /**
     *
     * @return to string of the metadata file
     */
    @Override
    public String toString() {
        return "MetaData: chunksUntilNow- " + chunksWritten + " size- " + bitMap.length + " bitMapSize- " + bitMap.length +
                " Percentages- " + getPercentages();
    }

	
}
