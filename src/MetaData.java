
public class MetaData {
	private boolean[] bitMap;
	private String name;
	private int numOfChunks;
	public MetaData (int numOfChunks, String name) {
		this.bitMap = new boolean[numOfChunks];
		this.numOfChunks = numOfChunks;
		this.name = name+".tmp";
	}
	
	public void declareInsertion(int location) {
		
	}
	
	
}
