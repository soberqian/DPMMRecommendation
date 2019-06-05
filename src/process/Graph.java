package process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import db.MYSQLControl;
import model.InfoModel;

public class Graph {
	public static void main(String[] args) throws IOException {
		List<InfoModel> datalist = MYSQLControl.getListInfoBySQL("select api_name,tags,content from detailinfo_cloud", InfoModel.class);
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
		BufferedWriter output = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File("tagdata/tag.txt")),"gbk"));
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
