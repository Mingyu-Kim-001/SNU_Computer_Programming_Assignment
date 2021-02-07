import course.Course;
import server.Server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class myTest {
    public static void main(String args[]){
        Server server = new Server();
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("name", "I");
        List<Course>  searchResult = server.search(map, null);
        for(Course course:searchResult){
            System.out.println(course.courseName);
        }
    }
}
