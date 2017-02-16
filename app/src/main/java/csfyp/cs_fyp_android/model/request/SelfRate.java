package csfyp.cs_fyp_android.model.request;

public class SelfRate {

    float extraversion;
    float agreeableness;
    float conscientiousness;
    float neuroticism;
    float openness;
    int userId;

    public SelfRate(float extraversion, float agreeableness, float conscientiousness, float neuroticism, float openness, int userId) {
        this.extraversion = extraversion;
        this.agreeableness = agreeableness;
        this.conscientiousness = conscientiousness;
        this.neuroticism = neuroticism;
        this.openness = openness;
        this.userId = userId;
    }
}
