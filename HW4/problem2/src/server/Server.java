package server;

import course.*;
import utils.Config;
import utils.ErrorCode;
import utils.Pair;

import java.io.*;
import java.util.*;

public class Server {
    String courseDataDir = "data/Courses/2020_Spring/";
    String biddingDir = "data/Users/";
    public List<Course> search(Map<String,Object> searchConditions, String sortCriteria){
        // TODO Problem 2-1
        List<Course> courseList = getCourseList(courseDataDir);
        if(searchConditions==null || searchConditions.isEmpty()){
            return sortCourse(courseList,sortCriteria);
        }
        if(searchConditions.containsKey("dept")){
            List<Course> searchedList = new LinkedList<>();
            String deptValue = (String) searchConditions.get("dept");
            for(Course course:courseList){
                if(course.department.equals(deptValue)){
                    searchedList.add(course);
                }
            }
            courseList = searchedList;
        }
        if(searchConditions.containsKey("ay")){
            List<Course> searchedList = new LinkedList<>();
            int ayValue = (int) searchConditions.get("ay");
            for(Course course:courseList){
                if(course.academicYear == ayValue){
                    searchedList.add(course);
                }
            }
            courseList = searchedList;
        }
        if(searchConditions.containsKey("name")){
            String nameValue = (String) searchConditions.get("name");
            courseList = searchByName(courseList,nameValue);
        }
        return sortCourse(courseList,sortCriteria);
    }
    private List<Course> getCourseList(String courseDataDir){
        List<Course> courseList = new LinkedList<>();
        File[] collegeDirList = new File(courseDataDir).listFiles(File::isDirectory);
        for(File collegeFile:collegeDirList){
            String collegeDir = collegeFile.getPath();
            String collegeName = collegeFile.getName();
            File[] courseForEachCollege = new File(collegeDir).listFiles();
            for(File courseFile:courseForEachCollege){
                try(BufferedReader br = new BufferedReader(new FileReader(courseFile))){
                    String courseInfo = br.readLine();
                    String[] courseInfoContent = courseInfo.split("\\|");
                    int courseID = Integer.parseInt(courseFile.getName().substring(0,courseFile.getName().length()-4));
                    Course course = new Course(courseID,
                            collegeName,
                            courseInfoContent[0],
                            courseInfoContent[1],
                            Integer.parseInt(courseInfoContent[2]),
                            courseInfoContent[3],
                            Integer.parseInt(courseInfoContent[4]),
                            courseInfoContent[5],
                            courseInfoContent[6],
                            Integer.parseInt(courseInfoContent[7]));
                    courseList.add(course);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return courseList;
    }
    class CourseIdComparator implements Comparator<Course>{
        @Override
        public int compare(Course c1, Course c2){
            if(c1.courseId>c2.courseId) return 1;
            if(c1.courseId<c2.courseId) return -1;
            return 0;
        }
    }
    class CourseNameComparator implements Comparator<Course>{
        @Override
        public int compare(Course c1, Course c2){
            int nameCompare = c1.courseName.compareTo(c2.courseName);
            if(nameCompare!=0) return nameCompare;
            CourseIdComparator courseIdComparator = new CourseIdComparator();
            return courseIdComparator.compare(c1,c2);
        }
    }
    class CourseDeptComparator implements Comparator<Course>{
        @Override
        public int compare(Course c1, Course c2){
            int deptCompare = c1.department.compareTo(c2.department);
            if(deptCompare!=0) return deptCompare;
            CourseIdComparator courseIdComparator = new CourseIdComparator();
            return courseIdComparator.compare(c1,c2);
        }
    }
    class CourseAyComparator implements Comparator<Course>{
        @Override
        public int compare(Course c1, Course c2){
            if(c1.academicYear>c2.academicYear) return 1;
            if(c1.academicYear<c2.academicYear) return -1;
            CourseIdComparator courseIdComparator = new CourseIdComparator();
            return courseIdComparator.compare(c1,c2);
        }
    }
    private List<Course> sortCourse(List<Course> courseList,String sortCriteria){
        if(sortCriteria==null||sortCriteria.isEmpty()){
            CourseIdComparator courseIdComparator = new CourseIdComparator();
            Collections.sort(courseList,courseIdComparator);
            return courseList;
        }
        switch(sortCriteria){
            case "id":
                CourseIdComparator courseIdComparator = new CourseIdComparator();
                Collections.sort(courseList,courseIdComparator);
                break;
            case "name":
                CourseNameComparator courseNameComparator = new CourseNameComparator();
                Collections.sort(courseList,courseNameComparator);
                break;
            case "dept":
                CourseDeptComparator courseDeptComparator = new CourseDeptComparator();
                Collections.sort(courseList,courseDeptComparator);
                break;
            case "ay":
                CourseAyComparator courseAyComparator = new CourseAyComparator();
                Collections.sort(courseList,courseAyComparator);
                break;
        }
        return courseList;
    }
    private List<Course> searchByName(List<Course> courseList,String searchKey){
        String[] keywordSplit= searchKey.split(" ");
        List<Course> searchedList = new LinkedList<>();
        for(Course course:courseList){
            boolean isSearched = true;
            String[] courseNameSplit = course.courseName.split(" ");
            for(String keyword:keywordSplit){
                boolean isEachKeywordSearched = false;
                for(String courseNameWord:courseNameSplit) {
                    if (keyword.equals(courseNameWord)) {
                        isEachKeywordSearched = true;
                        break;
                    }
                }
                if(!isEachKeywordSearched){
                    isSearched = false;
                    break;
                }
            }
            if(isSearched) searchedList.add(course);
        }
        return searchedList;
    }
    public int bid(int courseId, int mileage, String userId){
        // TODO Problem 2-2
        List<Course> courseList = getCourseList(courseDataDir);

        String userBiddingDir = biddingDir+userId+"/bid.txt";
        File bidFile = new File(userBiddingDir);
        Map<Integer,Integer> IdToMileage = new HashMap<>();
        int totalBidding = 0;
        try(BufferedReader br = new BufferedReader(new FileReader(bidFile))){
            String biddingForEachCourse;
            //List<Bidding> biddingList = new LinkedList<>();
            while((biddingForEachCourse=br.readLine())!=null){
                String[] IdMileage = biddingForEachCourse.split("\\|");
                //biddingList.add(new Bidding(Integer.parseInt(IdMileage[0]),Integer.parseInt(IdMileage[1])));
                IdToMileage.put(Integer.parseInt(IdMileage[0]),Integer.parseInt(IdMileage[1]));
                totalBidding+= Integer.parseInt(IdMileage[1]);
            }
        } catch (FileNotFoundException e) {
            return ErrorCode.USERID_NOT_FOUND;
        } catch (IOException e) {
            return ErrorCode.IO_ERROR;
        }
        boolean isFound = false;
        for(Course course:courseList){
            if(course.courseId==courseId){
                isFound = true;
                break;
            }
        }
        if(!isFound) return ErrorCode.NO_COURSE_ID;
        if(mileage<0) return ErrorCode.NEGATIVE_MILEAGE;
        if(mileage> Config.MAX_MILEAGE_PER_COURSE) return ErrorCode.OVER_MAX_COURSE_MILEAGE;
        if(mileage==0){
            if(IdToMileage.containsKey(courseId)){
                IdToMileage.remove(courseId);
                return storeMapToFile(bidFile,IdToMileage);
            }
            return ErrorCode.SUCCESS;
        }
        else{
            totalBidding+= IdToMileage.containsKey(courseId) ?
                    mileage - IdToMileage.get(courseId) : mileage;
            if(totalBidding>Config.MAX_MILEAGE) return ErrorCode.OVER_MAX_MILEAGE;
            IdToMileage.put(courseId,mileage);
            return storeMapToFile(bidFile,IdToMileage);
        }
    }
    private int storeMapToFile(File bidFile,Map<Integer,Integer> IdToMileage){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(bidFile))){
            for(Map.Entry<Integer, Integer> elem : IdToMileage.entrySet()) {
                writer.write(Integer.toString(elem.getKey()) + "|" + Integer.toString(elem.getValue()) + "\n");
            }
        } catch (IOException e) {
            return ErrorCode.IO_ERROR;
        }
        return ErrorCode.SUCCESS;
    }

    public Pair<Integer,List<Bidding>> retrieveBids(String userId){
        // TODO Problem 2-2
        boolean doesUserIdExist = false;
        File[] users = new File(biddingDir).listFiles(File::isDirectory);
        for(File user:users){
            if(user.getName().equals(userId)){
                doesUserIdExist = true;
                break;
            }
        }
        if(!doesUserIdExist){
            return new Pair<>(ErrorCode.USERID_NOT_FOUND,new ArrayList<>());
        }
        //List<Course> courseList = getCourseList(courseDataDir);
        String userBiddingDir = biddingDir+userId+"/bid.txt";
        File bidFile = new File(userBiddingDir);
        List<Bidding> biddingList = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(bidFile))){
            String biddingForEachCourse;
            while((biddingForEachCourse=br.readLine())!=null){
                String[] IdMileage = biddingForEachCourse.split("\\|");
                Bidding bidding = new Bidding(Integer.parseInt(IdMileage[0]),Integer.parseInt(IdMileage[1]));
                biddingList.add(bidding);
            }
        } catch (FileNotFoundException e) {
            return new Pair<>(ErrorCode.USERID_NOT_FOUND,new ArrayList<>());
        } catch (IOException e) {
            return new Pair<>(ErrorCode.IO_ERROR,new ArrayList<>());
        }
        return new Pair<>(ErrorCode.SUCCESS,biddingList);
    }
    class BiddingInfo{
        public String studentId;
        public int bidding;
        public int totalBidding;
        public BiddingInfo(String studentId,int bidding,int totalBidding){
            this.studentId = studentId;
            this.totalBidding = totalBidding;
            this.bidding = bidding;
        }
    }
    public boolean confirmBids(){
        // TODO Problem 2-3

        Map<Integer,List<BiddingInfo>> biddingInfoListToCourseId = getBiddingInfoMapToCourseId(biddingDir);
        if(biddingInfoListToCourseId==null) return false;
        List<Course> courseList = getCourseList(courseDataDir);
        Map<String,List<Integer>> registeredCourseIdToStudentId = new HashMap<>();
        for(Course course:courseList){
            List<BiddingInfo> biddingInfoList = biddingInfoListToCourseId.get(course.courseId);
//            System.out.println(course.quota);
//            System.out.println(biddingInfoList.size());
            if(biddingInfoList==null || biddingInfoList.isEmpty()) continue;
            if(biddingInfoList.size()<course.quota){
                for(BiddingInfo biddingInfo:biddingInfoList){
                    if(!registeredCourseIdToStudentId.containsKey(biddingInfo.studentId)){
                        registeredCourseIdToStudentId.put(biddingInfo.studentId, new ArrayList<Integer>());
                    }
                    registeredCourseIdToStudentId.get(biddingInfo.studentId).add(course.courseId);
                }
            }
            else{
                BiddingInfoComparator biddingInfoComparator = new BiddingInfoComparator();
                Collections.sort(biddingInfoList,biddingInfoComparator);
                List<BiddingInfo> registeredBiddingInfoList = biddingInfoList.subList(0,course.quota);
                for(BiddingInfo registeredBiddingInfo:registeredBiddingInfoList) {
                    if (!registeredCourseIdToStudentId.containsKey(registeredBiddingInfo.studentId)) {
                        registeredCourseIdToStudentId.put(registeredBiddingInfo.studentId, new ArrayList<Integer>());
                    }
                    registeredCourseIdToStudentId.get(registeredBiddingInfo.studentId).add(course.courseId);
                }
            }
        }
        return saveResult(registeredCourseIdToStudentId);
    }

    private boolean saveResult(Map<String,List<Integer>> registeredCourseIdToStudentId){
        for(Map.Entry<String,List<Integer>> elem : registeredCourseIdToStudentId.entrySet()) {
            String studentId = elem.getKey();
            String saveDir = biddingDir + "/" + studentId + "/result.txt";
            String deleteDir = biddingDir + "/" + studentId + "/bid.txt";
            List<Integer> registeredCourse = elem.getValue();
            String registeredCourseToStringWithDelimCommaAndSpace = registeredCourse.toString();
            registeredCourseToStringWithDelimCommaAndSpace = registeredCourseToStringWithDelimCommaAndSpace.substring(1,registeredCourseToStringWithDelimCommaAndSpace.length()-1);
            try(BufferedWriter br = new BufferedWriter(new FileWriter(new File(deleteDir)))){
                br.write("");
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            try(BufferedWriter br = new BufferedWriter(new FileWriter(new File(saveDir)))){
                br.write(registeredCourseToStringWithDelimCommaAndSpace);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    class BiddingInfoComparator implements Comparator<BiddingInfo>{
        @Override
        public int compare(BiddingInfo b1, BiddingInfo b2){
            if(b1.bidding<b2.bidding) return 1;
            if(b1.bidding>b2.bidding) return -1;
            if(b1.totalBidding>b2.totalBidding) return 1;
            if(b1.totalBidding<b2.totalBidding) return -1;
            return b1.studentId.compareTo(b2.studentId);
        }
    }
    private Map<Integer,List<BiddingInfo>> getBiddingInfoMapToCourseId(String biddingDir){
        Map<Integer,List<BiddingInfo>> biddingInfoListToCourseId = new HashMap<>();
        Map<String,Integer> totalBiddingAmountToStudentId = new HashMap<>();
        File[] users = new File(biddingDir).listFiles(File::isDirectory);
        List<BiddingInfo> biddingInfoList = new ArrayList<>();
        for(File user:users){
            String userBiddingDir = biddingDir+user.getName()+"/bid.txt";
            File bidFile = new File(userBiddingDir);
            List<Integer> biddingList = new ArrayList<>();
            int totalBidding = 0;
            try(BufferedReader br = new BufferedReader(new FileReader(bidFile))){
                String biddingForEachCourse;
                while((biddingForEachCourse=br.readLine())!=null){
                    String[] IdMileage = biddingForEachCourse.split("\\|");
                    int courseId = Integer.parseInt(IdMileage[0]);
                    int biddingAmount = Integer.parseInt(IdMileage[1]);
                    if(!biddingInfoListToCourseId.containsKey(courseId)){
                        biddingInfoListToCourseId.put(courseId,new ArrayList<BiddingInfo>());
                    }
                    BiddingInfo biddingInfo = new BiddingInfo(user.getName(),biddingAmount,-1);
                    biddingInfoListToCourseId.get(courseId).add(biddingInfo);
                    totalBidding+= Integer.parseInt(IdMileage[1]);
                }
                totalBiddingAmountToStudentId.put(user.getName(), totalBidding);
            } catch (FileNotFoundException e) {
                return null;
            } catch (IOException e) {
                return null;
            }
        }
        for(Map.Entry<Integer, List<BiddingInfo>> elem : biddingInfoListToCourseId.entrySet()) {
            List<BiddingInfo> biddingInfoList2 = elem.getValue();

            for(BiddingInfo biddingInfo: biddingInfoList2){
                biddingInfo.totalBidding = totalBiddingAmountToStudentId.get(biddingInfo.studentId);
            }
        }
        return biddingInfoListToCourseId;
    }

    public Pair<Integer,List<Course>> retrieveRegisteredCourse(String userId){
        // TODO Problem 2-3
        String resultDir = biddingDir + "/" + userId + "/result.txt";
        Map<Integer,Course> courseToCourseIdMap = getCourseToCourseIdMap(courseDataDir);
        List<Integer> resultCourseIdList = new ArrayList<>();
        List<Course> resultCourseList = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(new File(resultDir)))){
            String result = br.readLine();

            for(String eachResult : result.split(", ")){
                resultCourseIdList.add(Integer.parseInt(eachResult));
            }
        } catch (FileNotFoundException e) {
            return new Pair<>(ErrorCode.USERID_NOT_FOUND,new ArrayList<>());
        } catch (IOException e) {
            return new Pair<>(ErrorCode.IO_ERROR,new ArrayList<>());
        }
        for(Integer Id:resultCourseIdList){
            resultCourseList.add(courseToCourseIdMap.get(Id));
        }
        //System.out.println(resultCourseIdList.toString());
        return new Pair<>(ErrorCode.SUCCESS,resultCourseList);
    }
    private Map<Integer,Course> getCourseToCourseIdMap(String courseDataDir){
        Map<Integer,Course> courseToCourseIdMap = new HashMap<>();
        File[] collegeDirList = new File(courseDataDir).listFiles(File::isDirectory);
        for(File collegeFile:collegeDirList){
            String collegeDir = collegeFile.getPath();
            String collegeName = collegeFile.getName();
            File[] courseForEachCollege = new File(collegeDir).listFiles();
            for(File courseFile:courseForEachCollege){
                try(BufferedReader br = new BufferedReader(new FileReader(courseFile))){
                    String courseInfo = br.readLine();
                    String[] courseInfoContent = courseInfo.split("\\|");
                    int courseID = Integer.parseInt(courseFile.getName().substring(0,courseFile.getName().length()-4));
                    Course course = new Course(courseID,
                            collegeName,
                            courseInfoContent[0],
                            courseInfoContent[1],
                            Integer.parseInt(courseInfoContent[2]),
                            courseInfoContent[3],
                            Integer.parseInt(courseInfoContent[4]),
                            courseInfoContent[5],
                            courseInfoContent[6],
                            Integer.parseInt(courseInfoContent[7]));
                    courseToCourseIdMap.put(course.courseId,course);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return courseToCourseIdMap;
    }
}