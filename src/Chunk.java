import java.util.*;

public class Chunk {
	private byte[] data;
	private int start;
	private int bitMapLocation;
	public Chunk (byte[] input, int start) {
		this.data = new byte[input.length];
		for(int i = 0; i < data.length; i++)
			this.data[i] = input[i];
		this.start = start;
	}
	public byte[] getData() {
		return this.data;
	}
	public int getStart() {
		return this.start;
	}
	public int getBitMapLocation() {
		return this.bitMapLocation;
	}

}
