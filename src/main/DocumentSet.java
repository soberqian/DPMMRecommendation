package main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;


public class DocumentSet{
	public int D = 0;
	public ArrayList<Document> documents = new ArrayList<Document>();
	
	public DocumentSet(String dataDir, HashMap<String, Integer> wordToIdMap) 
			 					throws Exception
	{
		BufferedReader in = new BufferedReader(new FileReader(dataDir));
		String line;
		BufferedWriter Writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File("data/content1")),"gbk"));
		while((line=in.readLine()) != null){
			D++;
			String text = line.split("\t")[1];
			String[] wordarr = text.trim().replaceAll( "[\\pP+~$`^=|<>¡«£à¡ç£Þ£«£½£ü£¼£¾£¤¡Á]" , "").split("\\s+");
			ArrayList<String> words = new ArrayList<String>();
			for (int i = 0; i < wordarr.length; i++) {
				words.add(wordarr[i]);
			}
			//ÒÆ³ýÍ£ÓÃ´Ê
			for(int i = 0; i < words.size(); i++){
				if(Stopwords.isStopword(words.get(i))){
					words.remove(i);
					i--;
				}
			}
			String textnew="";
			for (int i = 0; i < words.size(); i++) {
				textnew+=words.get(i)+" ";
			}
			Writer.append(line.split("\t")[0]+"\t"+textnew+"\r\n");
//			System.out.println(text);
			Document document = new Document(text, wordToIdMap);
			documents.add(document);
		}
		Writer.close();
		in.close();
	}
}
