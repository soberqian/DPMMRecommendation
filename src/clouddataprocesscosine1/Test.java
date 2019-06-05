package clouddataprocesscosine1;

public class Test {

	 public static void main(String[] args) {
	        //并行化运行
	        testParallel();

	    }
	    public static void testParallel()
	    {

	    	int number = 100;
	        Parallel.loop(number, new Parallel.LoopInt()
	        {
	            //需要并行化的程序
	            public void compute(int i)
	            {
	                System.out.println("i:"+i);
	            }
	        });
	    }
	}
