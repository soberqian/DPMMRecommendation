package clouddataprocesscosine1;

public class Test {

	 public static void main(String[] args) {
	        //���л�����
	        testParallel();

	    }
	    public static void testParallel()
	    {

	    	int number = 100;
	        Parallel.loop(number, new Parallel.LoopInt()
	        {
	            //��Ҫ���л��ĳ���
	            public void compute(int i)
	            {
	                System.out.println("i:"+i);
	            }
	        });
	    }
	}
