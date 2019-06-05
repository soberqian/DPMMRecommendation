package clouddataprocesscosine1;

import java.util.ArrayList;

public class Utils {

    /**
     * �������ڻ�
     * */
    private static double calcVectorInnerProduct(ArrayList<Double> a,
                                                 ArrayList<Double> b)
    {
        if (a.size() != b.size())
        {
            throw new IndexOutOfBoundsException("����ά�Ȳ�һ��, �޷����ڻ�!\n");
        }

        int size = a.size();
        double innerProduct = 0.0;
        for (int i=0; i<size; i++)
        {
            innerProduct += (a.get(i) * b.get(i));
        }
        return innerProduct;
    }

    /**
     * ������ģ
     * */
    private static double calcVectorModulusLength(ArrayList<Double> a)
    {
        double squareSum = 0.0;
        for (Double anA : a) {
            squareSum += Math.pow(anA, 2);
        }
        return Math.sqrt(squareSum);
    }

    /**
     * �������н�����ֵ
     * */
    public static double calcVectorCosineIncludedAngle(ArrayList<Double> a,
                                                        ArrayList<Double> b)
    {
        double innerProduct = calcVectorInnerProduct(a, b);
        return innerProduct / (calcVectorModulusLength(a) * calcVectorModulusLength(b));
    }
}
