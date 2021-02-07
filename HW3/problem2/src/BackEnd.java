import java.io.*;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BackEnd extends ServerResourceAccessible {
    // Use getServerStorageDir() as a default directory
    // TODO sub-program 1 ~ 4 :
    // Create helper funtions to support FrontEnd class
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    //private int postNum;
    public boolean auth(String passwordPath,String passwordInput){
        File file = new File(passwordPath);
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line = br.readLine();
            //System.out.println("fileread : "+line);
            //System.out.println("passwordInput : "+passwordInput);
            if(passwordInput.equals(line)){
                return true;
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    private File[] getSubFile(String dirPath){
        File file = new File(dirPath);
        return file.listFiles();
    }
    private int largestPostID(String dirPath){
        File[] files = getSubFile(dirPath);
        String postName;
        int ID;
        int largeID = -1;
        if(files!=null){
            for(int i=0;i<files.length;i++){
                File postFile = files[i];
                postName = postFile.getName();
                ID = Integer.parseInt(postName.substring(0,postName.length()-4));
                if(largeID<ID) largeID = ID;
            }
        }
        return largeID;
    }
    public void post(String dirPath,String title, String content){
        String fileName = Integer.toString(largestPostID(dirPath)+1) + ".txt";
        String filePath = dirPath + fileName;
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));;
        try {
            File post = new File(filePath);
            if (post.createNewFile()) {
                //System.out.println("File created: " + post.getName());
                FileWriter postWriter = new FileWriter(filePath);
                //System.out.println(title);
                //System.out.println(content);
                postWriter.write(currentDate + "\n" + title + "\n\n" + content);
                postWriter.close();
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public List<String> readFriends(String friendPath){
        File file = new File(friendPath);
        List<String> friendNameList = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String friendName;
            while((friendName=br.readLine())!=null){
                friendNameList.add(friendName);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return friendNameList;
    }

    private List<Integer> getPostIDs(String dirPath){
        File[] files = getSubFile(dirPath);
        List<Integer> postIDs = new ArrayList<>();
        String postName;
        if(files!=null){
            for(int i=0;i<files.length;i++) {
                File postFile = files[i];
                postName = postFile.getName();
                postIDs.add(Integer.parseInt(postName.substring(0, postName.length() - 4)));
            }
        }
        return postIDs;
    }

    private Post parsePostFromFile(File file){
        //File file = new File(path);
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            List<String> contentLines = new ArrayList<>();
            String line,title = "";
            LocalDateTime time;
            line = br.readLine();
            time = Post.parseDateTimeString(line,formatter);
            title = br.readLine();
            br.readLine();//for newline
            while((line=br.readLine())!=null){
                contentLines.add(line);
            }
            String content = String.join("\n",contentLines);
            String fileName = file.getName();//fileName : 123.txt when id == 123
            int id = Integer.parseInt(fileName.substring(0,fileName.length()-4));
            Post post = new Post(id,time,title,content);
            return post;
        }catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    class PostDateComparator implements Comparator<Post>{
        @Override
        public int compare(Post p1, Post p2){
            if(p1.getDateTime().isAfter(p2.getDateTime())) return 1;
            if(p1.getDateTime().isBefore(p2.getDateTime())) return -1;
            return 0;
        }
    }
    public List<Post> sortedPost(List<String> pathOfFreiendsPost){
        String postPath;
        List<Post> posts = new LinkedList<>();
        for(String path: pathOfFreiendsPost){
            List<Integer> IDs = getPostIDs(path);
            for(Integer id:IDs){
                postPath = path + id + ".txt";
                posts.add(parsePostFromFile(new File(postPath)));
            }
        }
        PostDateComparator postDateComparator = new PostDateComparator();
        Collections.sort(posts,postDateComparator.reversed());
        //posts = posts.subList(0,topN);
        return posts;
    }
    private List <File> findAllPostFile(String dirPath){
        List <File> postFiles = new ArrayList<>();
        for(File subFile:getSubFile(dirPath)){
            String subName = subFile.getName();
            //if(subName.equals(".DS_Store")) continue;
            postFiles.addAll(Arrays.asList(getSubFile(dirPath + subName + "/post")));
        }
        return postFiles;
    }
    private int findOccurenceCount(String str, List<String> findStrs){
//        int lastIndex = 0;
//        int count = 0;
//        while(lastIndex != -1){
//            lastIndex = str.indexOf(findStr,lastIndex);
//            if(lastIndex != -1){
//                count ++;
//                lastIndex += findStr.length();
//            }
//        }
//        return count;
        int totalCount = 0;
        List<String> strSplit = Arrays.asList((str.split("\\s")));
        for(String findStr: findStrs){
//            int oneCount = Collections.frequency(strSplit,findStr);
//            if(oneCount==0) return 0;
//            totalCount+=oneCount;
            totalCount+= Collections.frequency(strSplit,findStr);
        }
        return totalCount;
    }
    class SearchComparator implements Comparator<Post>{
        @Override
        public int compare(Post p1, Post p2){
            if(p1.getOccurenceCount()>p2.getOccurenceCount()) return 1;
            if(p1.getOccurenceCount()<p2.getOccurenceCount()) return -1;
            if(p1.getDateTime().isAfter(p2.getDateTime())) return 1;
            if(p1.getDateTime().isBefore(p2.getDateTime())) return -1;
            return 0;
        }
    }
    public List<Post> searchPost(String path,List<String> keyword){
        List <File> postFiles = findAllPostFile(path);
        List <Post> posts = new ArrayList<>();
        int count;
        for(File postFile:postFiles){
            Post post = parsePostFromFile(postFile);
            if((count = findOccurenceCount(post.getContent(),keyword) + findOccurenceCount(post.getTitle(),keyword))!=0){
                post.setOccurenceCount(count);
                posts.add(post);
            }
        }
        SearchComparator searchComparator = new SearchComparator();
        Collections.sort(posts,searchComparator.reversed());
        //posts = posts.subList(0,topN);
        return posts;
    }
    //    private List<String> TopNPostPath(String dirPath,int topN){// get path of top N latest post for one person
//        File file = new File(dirPath);
//        File[] files = file.listFiles();
//        List<Integer> fileIDs = new LinkedList<>();
//        String postName;
//        List<String> topNpath = new ArrayList<>();
//        if(files!=null){
//            for(int i=0;i<files.length;i++){
//                File postFile = files[i];
//                postName = postFile.getName();
//                fileIDs.add(Integer.parseInt(postName.substring(0,postName.length()-4)));
//            }
//            Collections.sort(fileIDs,Collections.reverseOrder());
//            for(int i=0;i<topN && i<fileIDs.size();i++){
//                topNpath.add(dirPath + Integer.toString(fileIDs.get(i)) + ".txt");
//            }
//        }
//        return topNpath;
//    }
//    private Pair<List<String>,List<Integer>> PathAndIDs(String dirPath){//topN == infinity for TopNPostPath
//        File file = new File(dirPath);
//        File[] files = file.listFiles();
//        List<Integer> fileIDs = new LinkedList<>();
//        String postName;
//        List<String> postpath = new ArrayList<>();
//        if(files!=null){
//            for(int i=0;i<files.length;i++){
//                File postFile = files[i];
//                postName = postFile.getName();
//                fileIDs.add(Integer.parseInt(postName.substring(0,postName.length()-4)));
//            }
//            //Collections.sort(fileIDs,Collections.reverseOrder());
//            for(int i=0;i<fileIDs.size();i++){
//                postpath.add(dirPath + Integer.toString(fileIDs.get(i)) + ".txt");
//            }
//        }
//        return new Pair<List<String>,List<Integer>>(postpath,fileIDs);
//    }
    //    private LocalDateTime parsePostTime(String path){
//        File file = new File(path);
//        try(BufferedReader br = new BufferedReader(new FileReader(file))){
//            String line = br.readLine();
//            return Post.parseDateTimeString(line,formatter);
//        }catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
