
import java.util.*;

public class Movie implements Comparable<Movie>{
    private String title;
    private Set<String> tagSet = new HashSet<String>();
    private Integer RatingSum = 0;
    private Integer RatingCount = 0;
    public Movie(String title) { this.title = title; }
    @Override
    public String toString() {
        return title;
    }
    @Override
    public int compareTo(Movie movie){
        if(this.avgRate()>movie.avgRate()) return 1;
        else if(this.avgRate()<movie.avgRate()) return -1;
        else{
            return -this.title.compareTo(movie.toString());
        }
    }
    public void addTags(String[] tags){
        for(String tag: tags){
            tagSet.add(tag);
        }
    }
    public boolean isTagMatched(String[] tags){

        if(tagSet.containsAll(Arrays.asList(tags))){return true;}
        return false;
    }
    public void Rate(int rating){
        RatingSum+=rating;
        RatingCount++;
    }
    public void editRate(int newRating, int oldRating){
        RatingSum += newRating - oldRating;
    }
    public double avgRate(){
        if(RatingCount==0) return 0;
        return (double)RatingSum/RatingCount;
    }
//    public void resetRate(){
//        RatingSum = 0;
//        RatingCount = 0;
//    }

}
