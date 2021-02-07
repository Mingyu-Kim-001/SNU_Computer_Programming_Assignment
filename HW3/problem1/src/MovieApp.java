
import java.util.*;

public class MovieApp {
    ArrayList<Movie> movieArrayList = new ArrayList<Movie>();
    ArrayList<User> userArrayList = new ArrayList<User>();
    HashMap<User,HashMap> ratingMap = new HashMap<>();
    HashMap<User,Set<Movie>> searchHistory = new HashMap<>();
    HashMap<String,Integer> RatingSumByTitle = new HashMap<>();
    HashMap<String,Integer> RatingCountByTitle = new HashMap<>();
    public boolean addMovie(String title, String[] tags) {
        // TODO sub-problem 1

        if(findMovie(title)!=null) return false;
        Movie movie = new Movie(title);
        movie.addTags(tags);
        movieArrayList.add(movie);
        return true;
    }

    public boolean addUser(String name) {
        // TODO sub-problem 1
        if(findUser(name)!=null) return false;
        User user = new User(name);
        userArrayList.add(user);
        return true;
    }

    public Movie findMovie(String title) {
        // TODO sub-problem 1
        for(Movie movie: movieArrayList){
            if(movie.toString().equals(title)){
                return movie;
            }
        }
        return null;
    }

    public User findUser(String username) {
        // TODO sub-problem 1
        for(User user: userArrayList){
            if(user.toString().equals(username)){
                return user;
            }
        }
        return null;
    }

    public List<Movie> findMoviesWithTags(String[] tags) {
        // TODO sub-problem 2
        if(tags==null||tags.length==0) return new ArrayList<>();
        List<String> titleList = new LinkedList<>();
        HashMap<String,Movie> movieWithTitle = new HashMap<>();
        List<Movie> result = new LinkedList<>();
        for(Movie movie:movieArrayList){
            if(movie.isTagMatched(tags)){
                titleList.add(movie.toString());
                movieWithTitle.put(movie.toString(),movie);
            }
        }
        Collections.sort(titleList);
        Iterator iter = titleList.listIterator();
        while(iter.hasNext()){
            result.add(movieWithTitle.get(iter.next()));
        }
        return result;
    }
    private boolean isRegistered(User user){
        for(User user1: userArrayList){
            if(user==user1) return true;
        }
        return false;
    }

    public boolean rateMovie(User user, String title, int rating) {
        // TODO sub-problem 3
        if(user==null || findUser(user.toString())==null|| !isRegistered(user)) return false;
        if(title==null || findMovie(title)==null) return false;
        if(rating<1 || rating>10) return false;
        if(!ratingMap.containsKey(user)){
            HashMap<String,Integer> TitleRatingMapByUser = new HashMap<>();
            TitleRatingMapByUser.put(title,rating);
            ratingMap.put(user,TitleRatingMapByUser);
            findMovie(title).Rate(rating);
        }
        else{
            HashMap<String,Integer> TitleRatingMap = ratingMap.get(user);
            if(!TitleRatingMap.containsKey(title)){
                findMovie(title).Rate(rating);
            }
            else{
                int oldRating = TitleRatingMap.get(title);
                findMovie(title).editRate(rating,oldRating);
            }
            TitleRatingMap.put(title,rating);

        }

//        if(!RatingSumByTitle.containsKey(title)){
//            RatingSumByTitle.put(title,rating);
//            RatingCountByTitle.put(title,1);
//        }
//        else{
//            RatingSumByTitle.put(title,RatingSumByTitle.get(title)+rating);
//            RatingCountByTitle.put(title,RatingCountByTitle.get(title)+1);
//        }


        return true;
    }

    public int getUserRating(User user, String title) {
        // TODO sub-problem 3
        if(user==null || findUser(user.toString())==null|| !isRegistered(user)) return -1;
        if(title==null || findMovie(title)==null) return -1;
        if(!ratingMap.containsKey(user)) return 0;
        HashMap<String,Integer> TitleRatingMap = ratingMap.get(user);
        if(!TitleRatingMap.containsKey(title)) return 0;
        return TitleRatingMap.get(title);
    }

    public List<Movie> findUserMoviesWithTags(User user, String[] tags) {
        // TODO sub-problem 4
        if(user==null || findUser(user.toString())==null|| !isRegistered(user)) return new LinkedList<>();
        List<Movie> movieMatched = findMoviesWithTags(tags);
        if(!searchHistory.containsKey(user)){
            searchHistory.put(user,new HashSet<>(movieMatched));
        }
        else{
            searchHistory.get(user).addAll(movieMatched);
        }
        return movieMatched;
    }

    public List<Movie> recommend(User user) {
        // TODO sub-problem 4
        if(user==null || findUser(user.toString())==null|| !isRegistered(user)) return new LinkedList<>();
        if(!searchHistory.containsKey(user)||searchHistory.get(user).isEmpty()) return new LinkedList<>();
        List<Movie> candidates = new ArrayList<Movie>(searchHistory.get(user));
        Collections.sort(candidates,Collections.reverseOrder());
        if(candidates.size()<3) {
            //System.out.println(candidates.toString());
            return candidates;
        }

        List<Movie> result = new ArrayList(candidates.subList(0,3));
        //System.out.println(result.toString());
        return result;
    }
//    public List<Movie> findALlMovie(){
//        return movieArrayList;
//    }
}
