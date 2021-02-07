import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.time.LocalDateTime;

public class FrontEnd {
    private UserInterface ui;
    private BackEnd backend;
    private User user;
    private String DATA_DIRECTORY;

    public FrontEnd(UserInterface ui, BackEnd backend) {
        this.ui = ui;
        this.backend = backend;
        this.DATA_DIRECTORY = backend.getServerStorageDir();
    }

    public boolean auth(String authInfo){
        // TODO sub-problem 1
        String[] idPassword = authInfo.split("\\n");
        String passwordPath = DATA_DIRECTORY + idPassword[0] + "/" + "password.txt";
        String passwordInput = idPassword[1];
        //System.out.println(Arrays.toString(idPassword));
        if(backend.auth(passwordPath,passwordInput)){
            this.user = new User(idPassword[0],idPassword[1]);
            return true;
        }
        return false;
    }

    public void post(Pair<String, String> titleContentPair) {
        // TODO sub-problem 2
        String storePath = DATA_DIRECTORY + user.id + "/post/";
        backend.post(storePath,titleContentPair.key,titleContentPair.value);
    }

    public void recommend(){
        // TODO sub-problem 3
        final int topN = 10; // top 10
        String friendListPath = DATA_DIRECTORY  + user.id + "/friend.txt";
        List<String> friendNameList = backend.readFriends(friendListPath);
        //String frontPathOfFriend = DATA_DIRECTORY + "/";
        List<String> pathOfFriendsPost = new ArrayList<>();
        for(String friendName:friendNameList){
            pathOfFriendsPost.add(DATA_DIRECTORY + friendName + "/post/");
        }
        List<Post> posts = backend.sortedPost(pathOfFriendsPost);
        int count = 0;
        for(Post post:posts){
            if(count>=topN) break; // print only top N
            ui.println(post.toString());
            count++;
        }
    }

    public void search(String command) {
        // TODO sub-problem 4
        final int topN = 10;
        String[] wordSplit = command.split("\\s+");
        //System.out.println(Arrays.toString(wordSplit));
        List<String> keywords = Arrays.asList(wordSplit).subList(1,wordSplit.length); // remove first element(the word "search")
        Set<String> s = new LinkedHashSet<>(keywords);
        List<String> keywordsRemoveDuplicate = new ArrayList<String>(s);
        //System.out.println(keywordsRemoveDuplicate.toString());
        //String keyword = String.join(" ",keywordsRemoveDuplicate);
        List<Post> posts = backend.searchPost(DATA_DIRECTORY,keywordsRemoveDuplicate);
        int count = 0;
        for(Post post:posts){
            if(count>=topN) break; // print only top N
            ui.println(post.getSummary());
            count++;
        }
    }

    User getUser(){
        return user;
    }
}
