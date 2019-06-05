package clouddataprocesscosine1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import db.MYSQLControl;
import model.InfoModel;

public class TestMain {
    public static void main(String[] args)
    {
    	List<InfoModel> datalist = MYSQLControl.getListInfoBySQL("select api_name,tags,content from detailinfo_cloud", InfoModel.class);
        //首先对所要处理的文本,进行两两组合
    	Hashtable<String, String> apitext = new Hashtable<>();
    	List<String> apilist = new ArrayList<>();
    	for (int i = 0; i < datalist.size(); i++) {
    		apilist.add(datalist.get(i).getApi_name());
    		apitext.put(datalist.get(i).getApi_name(), datalist.get(i).getContent().replaceAll( "[\\pP+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]" , "").replaceAll("\t", "").replaceAll("[^\u0020-\u9FA5]", ""));
		}
    	//组合完之后计算两两之间的相似度
    	System.out.println("开始排列组合:");
    	Hashtable<String, Double> cosinemap = new Hashtable<String, Double>();
    	List<String> combinelist = combine(apilist);
    	System.out.println(combinelist.size());
    	System.out.println("开始计算相似度:");
    	/*for (int i = 0; i < combinelist.size(); i++) {
    		String s1 = apitext.get(combinelist.get(i).split("\t")[0]);
            String s2 = apitext.get(combinelist.get(i).split("\t")[1].trim());
            CosineSimilarity cs = new CosineSimilarity(s1, s2);
            cosinemap.put(combinelist.get(i), cs.calcCosineSimilarity());
            System.out.println(cs.calcCosineSimilarity());
            
		}*/
    	int number = combinelist.size();
    	Parallel.loop(number, new Parallel.LoopInt()
        {
            //需要并行化的程序
            public void compute(int i)
            {
            	System.out.println("i:"+i);
            	String s1 = apitext.get(combinelist.get(i).split("\t")[0]);
                String s2 = apitext.get(combinelist.get(i).split("\t")[1].trim());
                System.out.println("s1:"+s1);
                System.out.println("s2:"+s2);
            	CosineSimilarity cs = new CosineSimilarity(s1, s2);
            	cosinemap.put(combinelist.get(i), cs.calcCosineSimilarity());
            	System.out.println(cs.calcCosineSimilarity());
                
            }
        });
    	//排序
    	List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(cosinemap.entrySet());  
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {  
			//降序排序  
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {  
				//return o1.getValue().compareTo(o2.getValue());  
				return o2.getValue().compareTo(o1.getValue());  
			}  
		});
		System.out.println(list.size());
    	
    }
    private static List<String> combine(List<String> mylist){
         List<String> list = new ArrayList<String>();  
         for(int i=0;i< mylist.size()-1;i++)  
         {  
              for(int j=i+1;j< mylist.size();j++)  
              {   
                    list.add(mylist.get(i)+"\t"+mylist.get(j));     
              }  
         }  
         return list;
    }
}
