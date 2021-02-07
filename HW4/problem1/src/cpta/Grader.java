package cpta;

import cpta.environment.Compiler;
import cpta.environment.Executer;
import cpta.exam.ExamSpec;
import cpta.exam.Problem;
import cpta.exam.Student;
import cpta.exam.TestCase;
import cpta.exceptions.CompileErrorException;
import cpta.exceptions.RunTimeErrorException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Grader {
    Compiler compiler;
    Executer executer;
    final boolean isDebug = false;
    public Grader(Compiler compiler, Executer executer) {
        this.compiler = compiler;
        this.executer = executer;
    }

    public Map<String,Map<String, List<Double>>> gradeSimple(ExamSpec examSpec, String submissionDirPath) {
        // TODO Problem 1-1
        try {
            Map<String, Map<String, List<Double>>> studentIDtoProblemIDtoScore = new HashMap<>();
            for (Student student : examSpec.students) {
                String studentSubmissionDir = submissionDirPath + student.id + "/";
                Map<String, List<Double>> problemIDtoScore = new HashMap<>();
                for (Problem problem : examSpec.problems) {
                    String problemSubmissionDir = studentSubmissionDir + problem.id + "/";
                    String filePath = problemSubmissionDir + problem.targetFileName;
                    compiler.compile(filePath);
                    String yoFilePath = problemSubmissionDir + problem.targetFileName.substring(0, problem.targetFileName.length() - 4) + "yo";
                    String testCasesDirPath = problem.testCasesDirPath;
                    List<Double> scoreList = new ArrayList<>();
                    for (TestCase testCase : problem.testCases) {
                        String testInputPath = testCasesDirPath + testCase.inputFileName;
                        String testOutputPath = testCasesDirPath + testCase.outputFileName;
                        String resultPath = problemSubmissionDir + "result.txt";
                        executer.execute(yoFilePath, testInputPath, resultPath);
                        List<String> resultStringList = Files.readAllLines(Paths.get(resultPath));
                        String result = String.join("", resultStringList);
                        List<String> testOutputStringList = Files.readAllLines(Paths.get(testOutputPath));
                        String testOutput = String.join("", testOutputStringList);
                        scoreList.add(result.equals(testOutput) ? testCase.score : (double)0);
                    }
                    problemIDtoScore.put(problem.id, scoreList);
                    studentIDtoProblemIDtoScore.put(student.id, problemIDtoScore);
                }
            }
            return studentIDtoProblemIDtoScore;
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public Map<String,Map<String, List<Double>>> gradeRobust(ExamSpec examSpec, String submissionDirPath) {
        // TODO Problem 1-2

        Map<String, Map<String, List<Double>>> studentIDtoProblemIDtoScore = new HashMap<>();

        for (Student student : examSpec.students) {
            String studentSubmissionDir = submissionDirPath + student.id + "/";

            //Group 4. Wrong directory name - edit directory name to the wrong name
            if(!doesFileExist(studentSubmissionDir)){
                File[] everySubmissionDirList = new File(submissionDirPath).listFiles(File::isDirectory);
                for(File eachSubmissionDir:everySubmissionDirList){
                    if(eachSubmissionDir.getName().indexOf(student.id)==0){
                        studentSubmissionDir = submissionDirPath + eachSubmissionDir.getName() + "/";
                        break;
                    }
                }
            }


            Map<String, List<Double>> problemIDtoScore = new HashMap<>();

            for (Problem problem : examSpec.problems) {
                String problemSubmissionDir = studentSubmissionDir + problem.id + "/"; // /2020-12349/problem1
                String sugoFilePath = problemSubmissionDir + problem.targetFileName;
                String yoFilePath = problemSubmissionDir + problem.targetFileName.substring(0, problem.targetFileName.length() - 4) + "yo";
                String testCasesDirPath = problem.testCasesDirPath;

                //Group4. No submission for each problem
                if(!doesFileExist(problemSubmissionDir)){
                    putZeroScoreForAllTestCases(problemIDtoScore,problem);
                    continue;
                }

                //Group4. Wrong directory structure - copy all subfiles
                File[] additionalDirList = new File(problemSubmissionDir).listFiles(File::isDirectory);
                if(!(additionalDirList.length==0)){
                    for(File additionalDir:additionalDirList){
                        copyFiles(additionalDir.getPath(),problemSubmissionDir);
                    }
                }

                //Group 4. submitted only yo files
                File[] submissionFileList = new File(problemSubmissionDir).listFiles();
                List<String> nameOfYoFileList = new ArrayList<>();
                for(File submissionFile:submissionFileList){
                    if(submissionFile.getName().endsWith(".yo")) {
                        String nameOfYoFile = submissionFile.getName();
                        nameOfYoFileList.add(nameOfYoFile.substring(0,nameOfYoFile.length()-2));
                    }
                }
                boolean isAllYoMatchedToSugo = true;
                for(String nameOfYo:nameOfYoFileList) {
                    String nameOfSugo = nameOfYo + "sugo";
                    boolean isYoMatchedToSugo = false;
                    for (File submissionFile : submissionFileList) {
                        if(submissionFile.getName().equals(nameOfSugo)){
                            isYoMatchedToSugo = true;
                            break;
                        }
                    }
                    if(!isYoMatchedToSugo){
                        isAllYoMatchedToSugo = false;
                        break;
                    }
                }
//                String orgSugoFilePath = problemSubmissionDir + problem.id;
//                if(!doesFileExist(sugoFilePath)){
//                    // Group 4. No submission
//                    if(!doesFileExist(yoFilePath)) {
//                        putZeroScoreForAllTestCases(problemIDtoScore,problem);
//                        continue;
//                    }
//                    // Group4. submitted only .yo files : cut the scores by half
//                    else{
//                        isHalf = true;
//                    }
//                }


                //Group 3. copy wrapper codes
                if(problem.wrappersDirPath!=null){
                    copyFiles(problem.wrappersDirPath,problemSubmissionDir);
                    //sugoFilePath = problemSubmissionDir + "Wrapper.sugo";
                }


                //compile all .sugo files
                boolean isError = false;
                submissionFileList = new File(problemSubmissionDir).listFiles();
                for(File submissionFile:submissionFileList){
                    String fileName = submissionFile.getName();
                    if((fileName).endsWith(".sugo")){
                        if(isDebug) System.out.println(student.id + " " + problem.id + " " + fileName );
                        try {
                            String eachSugoFilePath = problemSubmissionDir + fileName;
                            //yoFilePath = sugoFilePath.substring(0,sugoFilePath.length()-5) + ".yo";
                            compiler.compile(eachSugoFilePath);
                        } catch(CompileErrorException e){ // Group1. compile errors
                            if(isDebug) System.out.println("compile error");
                            putZeroScoreForAllTestCases(problemIDtoScore,problem);
                            isError = true;
                            break;
                        } catch(Exception e){

                            e.printStackTrace();
                            putZeroScoreForAllTestCases(problemIDtoScore,problem);
                            isError = true;
                            break;
                        }
                    }
                }

                if(!isError) {
                    List<Double> scoreList = getScoreForAllTestCase(testCasesDirPath, problemSubmissionDir, yoFilePath, problem);
                    if(!isAllYoMatchedToSugo){
                        List<Double> halfScoreList = new ArrayList<>();
                        scoreList.forEach(i->halfScoreList.add(i/2));
                        problemIDtoScore.put(problem.id,halfScoreList);
                    }
                    else {
                        problemIDtoScore.put(problem.id, scoreList);
                    }
                }
            }
            studentIDtoProblemIDtoScore.put(student.id, problemIDtoScore);
        }
        return studentIDtoProblemIDtoScore;
    }
    private boolean executeFile(String yoFilePath,String testInputPath,String resultPath,List<Double> scoreList){
        try {
            executer.execute(yoFilePath, testInputPath, resultPath);
            return true;
        } catch (RunTimeErrorException e) { // Group1. Runtime error
            scoreList.add((double) 0);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            scoreList.add((double) 0);
            return false;
        }
    }
    private List<Double> getScoreForAllTestCase(String testCasesDirPath, String problemSubmissionDir,String yoFilePath,Problem problem) {
        List<Double> scoreList = new ArrayList<>();
        for (TestCase testCase : problem.testCases) {
            String testInputPath = testCasesDirPath + testCase.inputFileName;
            String testOutputPath = testCasesDirPath + testCase.outputFileName;
            String resultPath = problemSubmissionDir + "result.txt";
            if(!executeFile(yoFilePath,testInputPath,resultPath,scoreList)){
                continue;
            }
            try {
//                List<String> resultStringList = Files.readAllLines(Paths.get(resultPath));
//                String result = String.join("\n", resultStringList);
//                List<String> testOutputStringList = Files.readAllLines(Paths.get(testOutputPath));
//                String testOutput = String.join("\n", testOutputStringList);
                String result = Files.readString(Paths.get(resultPath));
                String testOutput = Files.readString(Paths.get(testOutputPath));
                if(problem.judgingTypes!=null && !problem.judgingTypes.isEmpty()) {
                    if (problem.judgingTypes.contains(Problem.TRAILING_WHITESPACES)) {
                        testOutput = trimTrailingWhiteSpaces(testOutput);
                        result = trimTrailingWhiteSpaces(result);
                    }
                    if (problem.judgingTypes.contains(Problem.IGNORE_WHITESPACES)) {
                        testOutput = removeWhiteSpace(testOutput);
                        result = removeWhiteSpace(result);
                    }
                    if (problem.judgingTypes.contains(Problem.CASE_INSENSITIVE)) {
                        testOutput = testOutput.toLowerCase();
                        result = result.toLowerCase();
                    }
                }
                scoreList.add(testOutput.equals(result) ? testCase.score : (double) 0);

            } catch (IOException e) {
                e.printStackTrace();
                scoreList.add((double) 0);
            }
        }
        return scoreList;
    }
    private void putZeroScoreForAllTestCases(Map<String, List<Double>> problemIDtoScore,Problem problem){
        List<Double> zeroScoreList = new ArrayList<>();
        for(int i=0;i<problem.testCases.size();i++){
            zeroScoreList.add((double)0);
        }
        problemIDtoScore.put(problem.id, zeroScoreList);
    }
    private void copyFiles(String source, String destination){
        try{
            for(File subFile:new File(source).listFiles()){
                Path src = Paths.get(subFile.getPath());
                //System.out.println("copy " + subFile.getPath() + " to " + destination + subFile.getName());
                Path dest = Paths.get(destination + subFile.getName());
                Files.copy(src,dest);
            }

        }catch(IOException e){
            //e.printStackTrace();
        }
    }
    private boolean doesFileExist(String path){
        File file = new File(path);
        return file.exists();
    }
    private String trimTrailingWhiteSpaces(String a){
//        int len = a.length();
//        int i;
//        List<Character> whitespaceList = new ArrayList<Character>();
//        whitespaceList.add(' ');
//        whitespaceList.add('\n');
//        whitespaceList.add('\t');
//        for(i=len-1;i>=0;i--){
//            if(!whitespaceList.contains(a.charAt(i))){
//                break;
//            }
//        }
//        return a.substring(0,i+1);
        return a.stripTrailing();
    }
    private String removeWhiteSpace(String a){
//        a = a.replaceAll(" ","");
//        a = a.replaceAll(System.getProperty("line.separator"),"");
//        a = a.replaceAll("\t","");
//        return a;
        return a.replaceAll("(\r\n|\r|\n|\n\r|\\p{Z}|\\t)","");
    }
}

