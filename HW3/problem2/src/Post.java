import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Post{
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    private int id;
    private final static int ID_NOT_INITIATED = -1;
    private LocalDateTime dateTime;
    private String title, content;
    private int occurenceCount = -1;

    Post(String title, String content) {
        this(ID_NOT_INITIATED, LocalDateTime.now(), title, content);
    }

    Post(int id, LocalDateTime dateTime, String title, String content) {
        this.id = id;
        this.dateTime = dateTime;
        this.title = title;
        this.content = content.trim();
    }

    String getSummary() {
        return String.format("id: %d, created at: %s, title: %s", id, getDate(), title);
    }
//    @Override
//    public int compareTo(Post post){
//        if(this.dateTime.isAfter(post.dateTime)) return 1;
//        if(this.dateTime.isBefore(post.dateTime)) return -1;
//        return 0;
//    }

    @Override
    public String toString() {
        return String.format(
            "-----------------------------------\n" +
            "id: %d\n" +
            "created at: %s\n" +
            "title: %s\n" +
            "content: %s"
            , id, getDate(), title, content);
}

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    String getDate() {
        return dateTime.format(formatter);
    }

    void setDateTime(LocalDateTime dateTime){
        this.dateTime = dateTime;
    }

    String getTitle() {
        return title;
    }

    String getContent() {
        return content;
    }

    static LocalDateTime parseDateTimeString(String dateString, DateTimeFormatter dateTimeFormatter) {
        return LocalDateTime.parse(dateString,dateTimeFormatter);
    }
    public void setOccurenceCount(int count){
        this.occurenceCount = count;
    }
    public int getOccurenceCount(){
        return this.occurenceCount;
    }
    public LocalDateTime getDateTime(){
        return this.dateTime;
    }
}
