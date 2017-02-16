package csfyp.cs_fyp_android.model.request;

public class Rate {
    float extraversion;
    float agreeableness;
    float conscientiousness;
    float neuroticism;
    float openness;
    int userId;
    int otherUserId;
    int eventId;

    public Rate(float extraversion, float agreeableness, float conscientiousness, float neuroticism, float openness, int userId, int otherUserId, int eventId) {
        this.extraversion = extraversion;
        this.agreeableness = agreeableness;
        this.conscientiousness = conscientiousness;
        this.neuroticism = neuroticism;
        this.openness = openness;
        this.userId = userId;
        this.otherUserId = otherUserId;
        this.eventId = eventId;
    }

    public int getUserId() {
        return userId;
    }

    public int getOtherUserId() {
        return otherUserId;
    }

    public int getEventId() {
        return eventId;
    }
}
