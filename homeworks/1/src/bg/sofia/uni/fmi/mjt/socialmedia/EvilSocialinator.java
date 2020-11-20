package bg.sofia.uni.fmi.mjt.socialmedia;

import bg.sofia.uni.fmi.mjt.socialmedia.content.Content;
import bg.sofia.uni.fmi.mjt.socialmedia.content.Publication;
import bg.sofia.uni.fmi.mjt.socialmedia.enums.ContentType;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.ContentNotFoundException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.NoUsersException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.UsernameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.UsernameNotFoundException;

import java.util.Collections;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collection;
import java.util.Iterator;

public class EvilSocialinator implements SocialMediaInator {
    private Map<String, List<String>> usersLogs;
    private Map<String, Integer> usersPopularity;
    private Map<String, List<String>> contentsByTag;
    private Map<String, Publication> contentsByID;
    private Set<String> mostRecentContent;
    private static int uniqueNumber = 0;

    public EvilSocialinator() {
        usersLogs = new HashMap<>();
        usersPopularity = new HashMap<>();
        contentsByTag = new HashMap<>();
        contentsByID = new HashMap<>();
        mostRecentContent = new TreeSet<>(byRecentlyAdded);
    }

    private Comparator<String> byRecentlyAdded = new Comparator<>() {

        @Override
        public int compare(String o1, String o2) {
            return contentsByID.get(o2).getPublishedOn().compareTo(contentsByID.get(o1).getPublishedOn());
        }
    };

    private Comparator<Content> byPopularityContent = new Comparator<>() {
        @Override
        public int compare(Content o1, Content o2) {
            return (o2.getNumberOfComments() + o2.getNumberOfLikes())
                    - (o1.getNumberOfComments() + o1.getNumberOfLikes());
        }
    };

    private String generateID(String username) {
        return username + "-" + uniqueNumber++;

    }

    @Override
    public void register(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username is null.");
        }
        if (usersLogs.containsKey(username)) {
            throw new UsernameAlreadyExistsException("User is already registered.");
        }
        usersLogs.put(username, new LinkedList<String>());
        usersPopularity.put(username, 0);
    }

    private String addPublication(String username, LocalDateTime publishedOn, String description, ContentType type) {
        if (username == null || publishedOn == null || description == null) {
            throw new IllegalArgumentException("Some of args. are null");
        }
        if (!usersLogs.containsKey(username)) {
            throw new UsernameNotFoundException("User does not exist");
        }
        String id = generateID(username);
        Publication p = new Publication(id, type, publishedOn, description);

        for (String tag :
                p.getTags()) {
            if (!contentsByTag.containsKey(tag)) {
                contentsByTag.put(tag, new LinkedList<>());
            }
            contentsByTag.get(tag).add(id);
        }
        for (String m :
                p.getMentions()) {
            String mention = m.substring(1);
            usersPopularity.put(mention, usersPopularity.get(mention) + 1);
        }
        contentsByID.put(id, p);
        mostRecentContent.add(id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yy");
        usersLogs.get(username).add(LocalDateTime.now().format(formatter)
                + ": Created a " + type.toString() + " with id " + id);
        return id;
    }

    @Override
    public String publishPost(String username, LocalDateTime publishedOn, String description) {
        return addPublication(username, publishedOn, description, ContentType.Post);
    }

    @Override
    public String publishStory(String username, LocalDateTime publishedOn, String description) {
        return addPublication(username, publishedOn, description, ContentType.Story);
    }

    @Override
    public void like(String username, String id) {
        if (username == null || id == null) {
            throw new IllegalArgumentException("Some of arguments are null.");
        }
        if (!usersLogs.containsKey(username)) {
            throw new UsernameNotFoundException("This user does not exist");
        }
        if (!contentsByID.containsKey(id)) {
            throw new ContentNotFoundException("This content does not exits");
        }
        contentsByID.get(id).addLike();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yy");
        usersLogs.get(username).add(LocalDateTime.now().format(formatter)
                + ": Liked a content with id " + id);
    }

    @Override
    public void comment(String username, String text, String id) {
        if (username == null || text == null || id == null) {
            throw new IllegalArgumentException("Some of arguments are null.");
        }
        if (!usersLogs.containsKey(username)) {
            throw new UsernameNotFoundException("This user does not exist");
        }
        if (!contentsByID.containsKey(id)) {
            throw new ContentNotFoundException("This content does not exits");
        }
        contentsByID.get(id).addComment(text);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yy");
        usersLogs.get(username).add(LocalDateTime.now().format(formatter)
                + ": Commented \"" + text + "\" on a content with id " + id);
    }

    @Override
    public Collection<Content> getNMostPopularContent(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n is negative");
        }
        List<Content> toSort = new ArrayList<>();
        for (Publication value : contentsByID.values()) {
            if (!value.isExpired()) {
                toSort.add(value);
            }
        }
        Collections.sort(toSort, byPopularityContent);
        Iterator<Content> it = toSort.iterator();
        Collection<Content> toReturn = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (it.hasNext()) {
                Content c = it.next();
                System.out.println(c.getId());
                toReturn.add(c);
            } else {
                break;
            }
        }
        return Collections.unmodifiableCollection(toReturn);
    }

    @Override
    public Collection<Content> getNMostRecentContent(String username, int n) {
        if (username == null || n < 0) {
            throw new IllegalArgumentException("Username is null, or n is negative.");
        }
        if (!usersLogs.containsKey(username)) {
            throw new UsernameNotFoundException("This user do not exist.");
        }
        Collection<Content> toReturn = new LinkedList<>();
        Iterator<String> it = mostRecentContent.iterator();
        int i = 0;
        while (i < n) {
            if (it.hasNext()) {
                String next = it.next();
                if (!contentsByID.get(next).isExpired()
                        && contentsByID.get(next).getPublisher().compareTo(username) == 0) {
                    System.out.println(next);
                    toReturn.add(contentsByID.get(next));
                }
            } else {
                break;
            }
        }
        return Collections.unmodifiableCollection(toReturn);
    }

    @Override
    public String getMostPopularUser() {
        if (usersLogs.size() == 0) {
            throw new NoUsersException("There are no users in the platform.");
        }
        int maxPopularity = Collections.max(usersPopularity.values());
        for (String user :
                usersPopularity.keySet()) {
            if (usersPopularity.get(user) == maxPopularity) {
                return user;
            }
        }
        return null;
    }

    @Override
    public Collection<Content> findContentByTag(String tag) {
        if (tag == null) {
            throw new IllegalArgumentException();
        }
        System.out.println(tag);
        LinkedList<Content> toReturn = new LinkedList<>();
        for (String content :
                contentsByTag.get(tag)) {
            if (!contentsByID.get(content).isExpired()) {
                System.out.println(content);
                toReturn.add(contentsByID.get(content));
            }
        }
        return Collections.unmodifiableCollection(toReturn);
    }

    @Override
    public List<String> getActivityLog(String username) {
        return usersLogs.get(username);
    }
}
