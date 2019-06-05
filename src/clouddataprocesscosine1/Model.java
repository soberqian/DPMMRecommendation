package clouddataprocesscosine1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;

import main.Document;
import main.DocumentSet;
//这里将簇讲成主题
public class Model{
	int K; 
	double alpha;
	double beta;
	String dataset;
	String ParametersStr;
	int V; 
	int D; 
	int iterNum; 
	int[] z; //文档对应的主题
	
	int[] m_z=null;    //主题包含的文档数量
	int[][] n_zv=null; //主题包含的单词v的数量
	int[] n_z=null; //主题包含的总的单词数量
	//模型参数初始化
	public Model(int K, int V, int D, int iterNum, double alpha, double beta, 
			String dataset, String ParametersStr)
	{
		
		this.dataset = dataset;  //数据集
		this.ParametersStr = ParametersStr; //参数
		this.alpha = alpha*D; //为什么设置为D*alpha,通过控制alpha可以控制主题的数量，alpha越大主题的数量越多（这里alpha可以自由调节）
		this.beta = beta;  //beta参数
		this.K = K;  //主题数目
		this.V = V;  //语料词的数量
		this.iterNum = iterNum;  //迭代次数
		
	}
	public void intialize(DocumentSet documentSet)
	{
		D = documentSet.D;  //文档数量
		z = new int[D];  //每篇文档对应一个主题
		
		for(int d = 0; d < D; d++){
			z[d] = 0; // initialize all document in No. 0 cluster first time 每篇文档初始化主题0
		}
	}
	//初始化簇，并做相关统计
	public void intialize_cluster(int[] m_z,int[][] n_zv,int[] n_z,DocumentSet documentSet)
	{
		//主题k包含的文档数量， 这里之所以使用map原因是，主题可能会被移除
		HashMap<Integer, Integer> countk = new HashMap<Integer, Integer>(); 
		//文档数量
		D = documentSet.D;
		int j=-1;
		//对每篇文档进行循环，重新对主题编号
		for(int i=0;i<D;i++){
			//如果包含的该主题，则将新主题赋值给他
			if (countk.containsKey(z[i])){
				//第i篇文档对应的主题
				countk.put(z[i],countk.get(z[i]));
			}else{  //如果不包含第i篇文档对应的主题，j++
				j++;
				countk.put(z[i], j);
			}
		}
		//主题数目
		K=countk.keySet().size();
		System.out.println("当前主题数目为："+K);
		//主题对应的文档数量
		for(int i=0;i<D;i++){
			z[i]=countk.get(z[i]); //Map的value值
		}
		this.m_z= new int [K]; //主题对应的文档数量
		this.n_zv= new int [K][V]; //主题z对应的单词v的数量
		this.n_z= new int [K]; //主题生成的单词数量
		//循环每个主题，做相关初始化赋值，这里的k又从0开始了
		for(int k = 0; k < K; k++){
			this.n_z[k] = 0;
			this.m_z[k] = 0;
			for(int t = 0; t < V; t++){
				this.n_zv[k][t] = 0;
			}
		}
		//针对每个文档，
		for(int d = 0; d < D; d++){
			//获取文档
			Document document = documentSet.documents.get(d);
			//获取文档所对应的主题
			int cluster = z[d];
			//主题对应的文档数量+1
			this.m_z[cluster] ++ ;
			//对每个单词循环，做有关单词方面的统计
			for(int w = 0; w < document.wordNum; w++){
				int wordNo = document.wordIdArray[w];
				int wordFre = document.wordFreArray[w];
				this.n_zv[cluster][wordNo] += wordFre; // in one cluster, count one word occur time 
				this.n_z[cluster] += wordFre; //in one cluster, count all words number
			}
		}
	}
	//gibbs 采样------需要详细看
	public void gibbsSampling(DocumentSet documentSet)
	{
		//迭代
		for(int i = 0; i < iterNum; i++){
			//每一代都要初始化
			intialize_cluster(m_z,n_zv,n_z,documentSet);
			//针对每个文档循环
			for(int d = 0; d < D; d++){
				Document document = documentSet.documents.get(d);
				//获取文档对应的主题
				int cluster = z[d];
				//主题对应的文档数量减1
				m_z[cluster]--;
				if(m_z[cluster]<0){//这里编写有问题，如果主题对应的文档数量为0，应该移除该主题，而这里却没有操作
					intialize_cluster(m_z,n_zv,n_z,documentSet);
				}
				//对每个单词进行循环，做移除文档后单词相关的统计，上面没有出现移除主题的情况，就有可能出现该统计全为0的情况
				for(int w = 0; w < document.wordNum; w++){
					int wordNo = document.wordIdArray[w];
					int wordFre = document.wordFreArray[w];
					n_zv[cluster][wordNo] -= wordFre;
					n_z[cluster] -= wordFre;
				}
				//使用轮盘赌采集主题
				int choose_cluster = sampleCluster(d, document);
				z[d] = choose_cluster;
				if(choose_cluster<K){ //如果选择一个已有的主题
					m_z[choose_cluster]++;  //主题对应的文档数量加1
					//做相关词的统计
					for(int w = 0; w < document.wordNum; w++){
						int wordNo = document.wordIdArray[w];
						int wordFre = document.wordFreArray[w];
						n_zv[choose_cluster][wordNo] += wordFre; 
						n_z[choose_cluster] += wordFre; 
					}
				}else if(choose_cluster==K){//如果选择一个新的主题
					//初始化，这里有些主题包含0篇文档的主题，自动过滤掉了
					intialize_cluster(m_z,n_zv,n_z,documentSet);
				}
				
			}
		
		}
		//初始化
		intialize_cluster(m_z,n_zv,n_z,documentSet);
	}
	//对文档的每个单词进行计算
	private int sampleCluster(int d, Document document)
	{ 
		double[] prob = new double[K+1];
		//计算属于已有簇的概率
		for(int k = 0; k < K; k++){
			//第一项
			prob[k] = (m_z[k]) / (D - 1 + alpha);
			double valueOfRule2 = 1.0;
			int i = 0;
			//计算连乘积
			for(int w=0; w < document.wordNum; w++){
				int wordNo = document.wordIdArray[w];
				int wordFre = document.wordFreArray[w];
				//依据公式进行计算
				for(int j = 0; j < wordFre; j++){
					
					valueOfRule2 *= (n_zv[k][wordNo] + beta + j) / (n_z[k] + V*beta + i);
					i++;
				}
			}
			prob[k] = prob[k] * valueOfRule2 ; 
		}
		//计算属于新簇的概率	
		prob[K]= (alpha) / (D - 1 + alpha);
		double valueOfRule3 = 1.0;
		int i = 0;
		//这里可以进行近似计算的
		for(int w=0; w < document.wordNum; w++){
			int wordFre = document.wordFreArray[w];
			for(int j = 0; j < wordFre; j++){
				valueOfRule3 *= (beta + j) /( beta*V + i);
				i++;
			}
			
		}
		prob[K] = prob[K] * valueOfRule3 ;
		//基于轮盘赌选择是已有的簇还是旧的簇
		for(int k = 1; k < K+1; k++){
			prob[k] += prob[k - 1];
		}
		
		double thred = Math.random() * prob[K];
		int kChoosed;
		for(kChoosed = 0; kChoosed < K+1; kChoosed++){
			if(thred < prob[kChoosed]){
				break;
			}
		}
		System.out.println("prob.length:"+prob.length+"\tkChoosed:"+kChoosed);
		return kChoosed;
	}
	
	public void output(DocumentSet documentSet, String outputPath) throws Exception
	{
		String outputDir = outputPath + dataset + ParametersStr + "/";
		
		File file = new File(outputDir);
		if(!file.exists()){
			if(!file.mkdirs()){
				System.out.println("Failed to create directory:" + outputDir);
			}
		}
		
		outputClusteringResult(outputDir, documentSet);
	}

	public void outputClusteringResult(String outputDir, DocumentSet documentSet) throws Exception
	{
		HashMap<Integer, Integer> count = new HashMap<Integer, Integer>(); 
		
		String outputPath = outputDir + dataset + "ClusteringResult_DP.txt";
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter
				(new FileOutputStream(outputPath), "UTF-8"));
		for(int d = 0; d < documentSet.D; d++){
			int topic = z[d];
			writer.write(topic + "\n");
			
			if (!count.containsKey(topic)){
				count.put(topic, 1);
			}else{
				count.put(topic, count.get(topic) + 1);
			}
		}
		@SuppressWarnings("rawtypes")
		Iterator iterator = count.keySet().iterator();
		@SuppressWarnings("unused")
		int i=0;
		while (iterator.hasNext()){
			i=(Integer) iterator.next();
			//System.out.println("topic : "+i+" number :"+count.get(i));
		}
		System.out.println("k size "+K);
		System.out.println("count size "+count.keySet().size());
		System.out.println("count "+count.keySet());
		System.out.println("count "+count.values());
		
		
		
		writer.flush();
		writer.close();
	}
}
