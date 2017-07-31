package de.dkt.eservices.enlg.linguistic;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Paraphrasing {

	
	
	public static void main(String[] args) throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/jumo04/Downloads/ppdb-1.0-s-all"), "utf-8"));
		String line = br.readLine();
		int counter = 0;
		int length[] = new int[6];
		while(line!=null){
//			System.out.println(line);
			String parts[]= line.split("\\|\\|\\|");
			//System.out.println(parts[1].trim() + "-->" + parts[2].trim());
			String ss = parts[1].trim();
			String parts2[]=ss.split(" ");
			line = br.readLine();
			counter++;
			length[parts2.length-1]++;
		}
		br.close();
		System.out.println("NUMBER OF ROWS: "+counter);
		System.err.println("NUMBER OF EVERY LENGTH: ");
		for (int i = 0; i < length.length; i++) {
			System.out.println("\t"+i+" --> "+length[i]);
		}
	}
	
	
	
	
}
