package process;

import java.util.Hashtable;
import java.util.List;

import db.MYSQLControl;

public class TagsCount {

	public static void main(String[] args) {
		List<Object> datalist = MYSQLControl.getListOneBySQL("select tags from detailinfo_cloud", "tags");
		int allcount=0;
		Hashtable<String, Integer>  wordCount = new Hashtable<String, Integer>();
		for (int i = 0; i < datalist.size(); i++) {
			String tags = datalist.get(i).toString();
			String[] tagarr = tags.split(",");
			allcount +=tagarr.length;
			System.out.println(tagarr.length);
			for (int j = 0; j < tagarr.length; j++) {
                if (!wordCount.containsKey(tagarr[j])) {
                    wordCount.put(tagarr[j], Integer.valueOf(1));
                } else {
                    wordCount.put(tagarr[j], Integer.valueOf(wordCount.get(tagarr[j]).intValue() + 1));
                }
            }
		}
		System.out.println(allcount);
		System.out.println(wordCount.size());
		int allcount1=0;
		List<Object> datalist1 = MYSQLControl.getListOneBySQL("select content from detailinfo_cloud", "content");
		for (int i = 0; i < datalist1.size(); i++) {
			String tags = datalist1.get(i).toString();
			String[] tagarr = tags.split("\\s+");
			allcount1 +=tagarr.length;
		}
		System.out.println(allcount1/790);
	}

}
