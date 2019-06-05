package clouddataprocesscosine1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;

import main.Document;
import main.DocumentSet;
//���ｫ�ؽ�������
public class Model{
	int K; 
	double alpha;
	double beta;
	String dataset;
	String ParametersStr;
	int V; 
	int D; 
	int iterNum; 
	int[] z; //�ĵ���Ӧ������
	
	int[] m_z=null;    //����������ĵ�����
	int[][] n_zv=null; //��������ĵ���v������
	int[] n_z=null; //����������ܵĵ�������
	//ģ�Ͳ�����ʼ��
	public Model(int K, int V, int D, int iterNum, double alpha, double beta, 
			String dataset, String ParametersStr)
	{
		
		this.dataset = dataset;  //���ݼ�
		this.ParametersStr = ParametersStr; //����
		this.alpha = alpha*D; //Ϊʲô����ΪD*alpha,ͨ������alpha���Կ��������������alphaԽ�����������Խ�ࣨ����alpha�������ɵ��ڣ�
		this.beta = beta;  //beta����
		this.K = K;  //������Ŀ
		this.V = V;  //���ϴʵ�����
		this.iterNum = iterNum;  //��������
		
	}
	public void intialize(DocumentSet documentSet)
	{
		D = documentSet.D;  //�ĵ�����
		z = new int[D];  //ÿƪ�ĵ���Ӧһ������
		
		for(int d = 0; d < D; d++){
			z[d] = 0; // initialize all document in No. 0 cluster first time ÿƪ�ĵ���ʼ������0
		}
	}
	//��ʼ���أ��������ͳ��
	public void intialize_cluster(int[] m_z,int[][] n_zv,int[] n_z,DocumentSet documentSet)
	{
		//����k�������ĵ������� ����֮����ʹ��mapԭ���ǣ�������ܻᱻ�Ƴ�
		HashMap<Integer, Integer> countk = new HashMap<Integer, Integer>(); 
		//�ĵ�����
		D = documentSet.D;
		int j=-1;
		//��ÿƪ�ĵ�����ѭ�������¶�������
		for(int i=0;i<D;i++){
			//��������ĸ����⣬�������⸳ֵ����
			if (countk.containsKey(z[i])){
				//��iƪ�ĵ���Ӧ������
				countk.put(z[i],countk.get(z[i]));
			}else{  //�����������iƪ�ĵ���Ӧ�����⣬j++
				j++;
				countk.put(z[i], j);
			}
		}
		//������Ŀ
		K=countk.keySet().size();
		System.out.println("��ǰ������ĿΪ��"+K);
		//�����Ӧ���ĵ�����
		for(int i=0;i<D;i++){
			z[i]=countk.get(z[i]); //Map��valueֵ
		}
		this.m_z= new int [K]; //�����Ӧ���ĵ�����
		this.n_zv= new int [K][V]; //����z��Ӧ�ĵ���v������
		this.n_z= new int [K]; //�������ɵĵ�������
		//ѭ��ÿ�����⣬����س�ʼ����ֵ�������k�ִ�0��ʼ��
		for(int k = 0; k < K; k++){
			this.n_z[k] = 0;
			this.m_z[k] = 0;
			for(int t = 0; t < V; t++){
				this.n_zv[k][t] = 0;
			}
		}
		//���ÿ���ĵ���
		for(int d = 0; d < D; d++){
			//��ȡ�ĵ�
			Document document = documentSet.documents.get(d);
			//��ȡ�ĵ�����Ӧ������
			int cluster = z[d];
			//�����Ӧ���ĵ�����+1
			this.m_z[cluster] ++ ;
			//��ÿ������ѭ�������йص��ʷ����ͳ��
			for(int w = 0; w < document.wordNum; w++){
				int wordNo = document.wordIdArray[w];
				int wordFre = document.wordFreArray[w];
				this.n_zv[cluster][wordNo] += wordFre; // in one cluster, count one word occur time 
				this.n_z[cluster] += wordFre; //in one cluster, count all words number
			}
		}
	}
	//gibbs ����------��Ҫ��ϸ��
	public void gibbsSampling(DocumentSet documentSet)
	{
		//����
		for(int i = 0; i < iterNum; i++){
			//ÿһ����Ҫ��ʼ��
			intialize_cluster(m_z,n_zv,n_z,documentSet);
			//���ÿ���ĵ�ѭ��
			for(int d = 0; d < D; d++){
				Document document = documentSet.documents.get(d);
				//��ȡ�ĵ���Ӧ������
				int cluster = z[d];
				//�����Ӧ���ĵ�������1
				m_z[cluster]--;
				if(m_z[cluster]<0){//�����д�����⣬��������Ӧ���ĵ�����Ϊ0��Ӧ���Ƴ������⣬������ȴû�в���
					intialize_cluster(m_z,n_zv,n_z,documentSet);
				}
				//��ÿ�����ʽ���ѭ�������Ƴ��ĵ��󵥴���ص�ͳ�ƣ�����û�г����Ƴ��������������п��ܳ��ָ�ͳ��ȫΪ0�����
				for(int w = 0; w < document.wordNum; w++){
					int wordNo = document.wordIdArray[w];
					int wordFre = document.wordFreArray[w];
					n_zv[cluster][wordNo] -= wordFre;
					n_z[cluster] -= wordFre;
				}
				//ʹ�����̶Ĳɼ�����
				int choose_cluster = sampleCluster(d, document);
				z[d] = choose_cluster;
				if(choose_cluster<K){ //���ѡ��һ�����е�����
					m_z[choose_cluster]++;  //�����Ӧ���ĵ�������1
					//����شʵ�ͳ��
					for(int w = 0; w < document.wordNum; w++){
						int wordNo = document.wordIdArray[w];
						int wordFre = document.wordFreArray[w];
						n_zv[choose_cluster][wordNo] += wordFre; 
						n_z[choose_cluster] += wordFre; 
					}
				}else if(choose_cluster==K){//���ѡ��һ���µ�����
					//��ʼ����������Щ�������0ƪ�ĵ������⣬�Զ����˵���
					intialize_cluster(m_z,n_zv,n_z,documentSet);
				}
				
			}
		
		}
		//��ʼ��
		intialize_cluster(m_z,n_zv,n_z,documentSet);
	}
	//���ĵ���ÿ�����ʽ��м���
	private int sampleCluster(int d, Document document)
	{ 
		double[] prob = new double[K+1];
		//�����������дصĸ���
		for(int k = 0; k < K; k++){
			//��һ��
			prob[k] = (m_z[k]) / (D - 1 + alpha);
			double valueOfRule2 = 1.0;
			int i = 0;
			//�������˻�
			for(int w=0; w < document.wordNum; w++){
				int wordNo = document.wordIdArray[w];
				int wordFre = document.wordFreArray[w];
				//���ݹ�ʽ���м���
				for(int j = 0; j < wordFre; j++){
					
					valueOfRule2 *= (n_zv[k][wordNo] + beta + j) / (n_z[k] + V*beta + i);
					i++;
				}
			}
			prob[k] = prob[k] * valueOfRule2 ; 
		}
		//���������´صĸ���	
		prob[K]= (alpha) / (D - 1 + alpha);
		double valueOfRule3 = 1.0;
		int i = 0;
		//������Խ��н��Ƽ����
		for(int w=0; w < document.wordNum; w++){
			int wordFre = document.wordFreArray[w];
			for(int j = 0; j < wordFre; j++){
				valueOfRule3 *= (beta + j) /( beta*V + i);
				i++;
			}
			
		}
		prob[K] = prob[K] * valueOfRule3 ;
		//�������̶�ѡ�������еĴػ��ǾɵĴ�
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
