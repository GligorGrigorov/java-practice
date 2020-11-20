package bg.sofia.uni.fmi.mjt.socialmedia.enums;

public enum ContentType {

    Post(30),
    Story(1);
    private final int daysToExpire;

    ContentType(int daysToExpire) {
        this.daysToExpire = daysToExpire;
    }

    public int getDaysToExpire() {
        return daysToExpire;
    }
}
