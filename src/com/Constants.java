package com;

import java.io.File;

public class Constants {
	public static final String FILEPATH = new File("").getAbsolutePath();
	public static final String READFILE=FILEPATH+"/src/a.txt";
	public static final String WRITEFILE=FILEPATH+"/src/b.txt";
	public static final String TEMPFILE=FILEPATH+"/src/c.txt";
	public static final int PORTA=9090;
	public static final int PORTB=9091;
	public static final int NUMOFLINES=5;
	//number of characters that can be sent at a time.
	public static final int THRESHOLDMEMORY=200; 
												

}
