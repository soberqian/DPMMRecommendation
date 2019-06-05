package process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import db.MYSQLControl;
import model.InfoModel;


public class GraphTopic {
	public static void main(String[] args) throws IOException {
		String filePath = "topicdata/documentassiment/";
        List<String> fileList = new ArrayList<String>();
        fileList= getFiles(filePath);
        Hashtable<String, List<InfoModel>> hashtable = new Hashtable<>();
        for (String file:fileList) {
            BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( new File(file)),"utf-8"));
//            BufferedWriter writer = new BufferedWriter(new FileWriter("topicdata/documentassimentprocess/"+file.replaceAll("\\D", "")));
            String s=null;
            List<InfoModel> list = new ArrayList<>();
            while ((s=reader.readLine())!=null) {
            	System.out.println(file+"\t"+s);
            	List<InfoModel> datalist = MYSQLControl.getListInfoBySQL("select api_name,tags,content from detailinfo_cloud where api_name='"+s.trim()+"'", InfoModel.class);
            	list.add(datalist.get(0));
//            	writer.write(s+"\t"+datalist.get(0).getTags()+"\t"+datalist.get(0).getContent().trim().replaceAll( "[\\pP+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]" , "").replaceAll("\t", "").replaceAll("[^\u0020-\u9FA5]", "")+"\n");
            }
            hashtable.put("topicdata/documentassimentprocess/"+file.replaceAll("\\D", ""), list);
//            writer.close();
            reader.close();
        }
        for (Map.Entry<String, List<InfoModel>> entry : hashtable.entrySet()) {  
        	getInput(entry.getKey() ,entry.getValue());
//            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());  
          
        }  

	}
	static List<String> getFiles( String filePath )
    {
        List<String> filelist = new ArrayList<String>();
        File root = new File( filePath );
        File[] files = root.listFiles();
        for ( File file : files )
        {
            if ( file.isDirectory() )
            {
                getFiles( file.getAbsolutePath() );
                filelist.add( file.getAbsolutePath() );
                //System.out.println( "显示" + filePath + "下所有子目录及其文件" + file.getAbsolutePath() );
            }else{
                filelist.add( file.getAbsolutePath() );
                //System.out.println("显示" + filePath + "下所有子目录" + file.getAbsolutePath() );
            }
        }
        return filelist;
    }
	static  void getInput(String filePath, List<InfoModel> datalist ) throws IOException{
		Hashtable<String, Integer>  word2IdVocabulary = new Hashtable<String, Integer>();
		int indexWord = -1;
		for (int i = 0; i < datalist.size(); i++) {
			String tags = datalist.get(i).getTags().toString();
			String[] tagarr = tags.split(",");
			ArrayList<String> words = new ArrayList<String>();
			for (int j = 0; j < tagarr.length; j++) {
				words.add(tagarr[j]);
			}
			for (String word : words) {
				if (word2IdVocabulary.containsKey(word)) {
				}
				else {
					indexWord += 1;
					word2IdVocabulary.put(word, indexWord);
				}
			}
		}
		//将排序后的结果输出
		BufferedWriter output = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File(filePath)),"gbk"));
		for (int i = 0; i < datalist.size(); i++) {
			String tags = datalist.get(i).getTags().toString();
			String[] tagarr = tags.split(",");
			ArrayList<String> words = new ArrayList<String>();
			for (int j = 0; j < tagarr.length; j++) {
				words.add(tagarr[j]);
			}
			for (String word : words) {
				int tagid=word2IdVocabulary.get(word);
				String oneinfo = datalist.get(i).getApi_name()+"\t"+tagid+"\t"+datalist.get(i).getContent().trim().replaceAll( "[\\pP+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]" , "").replaceAll("\t", "").replaceAll("[^\u0020-\u9FA5]", "");
				output.write(oneinfo+"\n");
			}
		}
		output.close();
	}
}
