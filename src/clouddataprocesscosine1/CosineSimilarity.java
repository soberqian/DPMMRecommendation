package clouddataprocesscosine1;

public class CosineSimilarity {
    private String sentence1;
    private String sentence2;

    CosineSimilarity(String s1, String s2)
    {
        this.sentence1 = s1;
        this.sentence2 = s2;
    }

    public double calcCosineSimilarity()
    {
        EnglishTextProcessing.getWordFrequencyVector(sentence1, sentence2);
        return Utils.calcVectorCosineIncludedAngle(
                EnglishTextProcessing.getVector1(),
                EnglishTextProcessing.getVector2()
        );
    }
}