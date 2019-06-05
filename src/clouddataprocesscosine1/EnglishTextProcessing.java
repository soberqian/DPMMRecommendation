package clouddataprocesscosine1;

import java.util.*;
import java.util.stream.Collectors;

public class EnglishTextProcessing {
    private static final List<String> symbols = Arrays.asList(",", ".", "?", "!", ";", ":");
    private static LinkedHashMap<String, Integer> text1 = new LinkedHashMap<>();
    private static LinkedHashMap<String, Integer> text2 = new LinkedHashMap<>();
    private static ArrayList<Double> vector1 = new ArrayList<>();
    private static ArrayList<Double> vector2 = new ArrayList<>();

    /**
     * ������ȥ��������, ��ת��ΪСд
     * */
    private static String symbolHandle(String sentence)
    {
        String temp = sentence;
        for (String s : symbols)
        {
            temp = temp.replace(s, "");
        }
        return temp.toLowerCase();
    }

    /**
     * ��������и����ʳ��ֵ�Ƶ��(Ƶ��), ����Ƶ
     * */
    private static void calcWordFrequency(String s1, String s2)
    {
        String _s1 = symbolHandle(s1);
        String _s2 = symbolHandle(s2);
        LinkedHashMap<String, Integer> textTemp1 = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> textTemp2 = new LinkedHashMap<>();
        List<String> w_s1 = Arrays.asList(_s1.split(" "));
        for (String word : w_s1)
        {
            textTemp1.put(word, textTemp1.getOrDefault(word, 0) + 1);
        }
        List<String> w_s2 = Arrays.asList(_s2.split(" "));
        for (String word : w_s2)
        {
            textTemp2.put(word, textTemp2.getOrDefault(word, 0) + 1);
        }
        LinkedHashSet<String> w1 = new LinkedHashSet<>();
        w1.addAll(w_s1);
        LinkedHashSet<String> w2 = new LinkedHashSet<>();
        w2.addAll(w_s2);
        w1.addAll(w2);
        List<String> w = new ArrayList<>(w1);
//        System.out.println(w);
        for (String ww : w)
        {
            text1.put(ww, textTemp1.getOrDefault(ww, 0));
            text2.put(ww, textTemp2.getOrDefault(ww, 0));
        }
//        System.out.println(text1);
//        System.out.println(text2);
    }

    /**
     * �õ���Ƶ����
     * */
    public static void getWordFrequencyVector(String s1, String s2)
    {
        calcWordFrequency(s1, s2);
        vector1.addAll(text1.entrySet().stream().
                map(entry -> (double) entry.getValue()).collect(Collectors.toList()));
//        System.out.println(vector1);
        vector2.addAll(text2.entrySet().stream().
                map(entry -> (double) entry.getValue()).collect(Collectors.toList()));
//        System.out.println(vector2);
    }

    public static LinkedHashMap<String, Integer> getText1() {
        return text1;
    }

    public static LinkedHashMap<String, Integer> getText2() {
        return text2;
    }

    public static ArrayList<Double> getVector1() {
        return vector1;
    }

    public static ArrayList<Double> getVector2() {
        return vector2;
    }
}