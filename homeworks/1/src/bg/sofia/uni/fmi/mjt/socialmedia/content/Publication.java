package bg.sofia.uni.fmi.mjt.socialmedia.content;

import bg.sofia.uni.fmi.mjt.socialmedia.enums.ContentType;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Publication implements Content {
    private int numberOfLikes;
    private int numberOfComments;
    private String id;
    private String description;
    private LocalDateTime publishedOn;
    private Collection<String> tags;
    private Collection<String> mentions;
    private ContentType type;

    public Publication(String id, ContentType type, LocalDateTime publishedOn, String description) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.publishedOn = publishedOn;
        numberOfLikes = 0;
        numberOfComments = 0;
        tags = new HashSet<>();
        mentions = new HashSet<>();
        findMentions();
        findTags();
    }

    private void findMentions() {
        Pattern mention = Pattern.compile("@\\w+");
        Matcher m = mention.matcher(description);
        while (m.find()) {
            mentions.add(m.group());
        }
    }

    private void findTags() {
        Pattern mention = Pattern.compile("#\\w+");
        Matcher m = mention.matcher(description);
        while (m.find()) {
            tags.add(m.group());
        }
    }

    public boolean isExpired() {
        return publishedOn.plusDays(type.getDaysToExpire()).isBefore(LocalDateTime.now());
    }

    public void addLike() {
        numberOfLikes++;
    }

    public void addComment(String comment) {
        numberOfComments++;
    }

    public LocalDateTime getPublishedOn() {
        return publishedOn;
    }

    public String getPublisher() {
        return id.split("-")[0];
    }

    @Override
    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    @Override
    public int getNumberOfComments() {
        return numberOfComments;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Collection<String> getTags() {
        return tags;
    }

    @Override
    public Collection<String> getMentions() {
        return mentions;
    }
}
