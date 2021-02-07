import cpta.Grader;
import cpta.environment.Compiler;
import cpta.environment.Executer;
import cpta.exam.Problem;
import cpta.exam.TestCase;

import java.util.List;
import java.util.Set;

public class myTest {
    public static void main(String[] args){
        List<TestCase> testCases2 = null;
        List<TestCase> testCases3 = null;
        Set<String> judgingTypesA = null;
        Set<String> judgingTypesB = null;
        Set<String> judgingTypesC = null;
        Grader grader = new Grader(new Compiler(), new Executer());
        Problem p1 = new Problem(
                "problem1", "data/test-cases/test-exam-robust/problem1/",
                testCases2, "Problem1.sugo", null, judgingTypesA
        );
        Problem p2 = new Problem(
                "problem2", "data/test-cases/test-exam-robust/problem2/",
                testCases2, "Problem2.sugo", null, judgingTypesB
        );
        Problem p3 = new Problem(
                "problem3", "data/test-cases/test-exam-robust/problem3/",
                testCases3, "Wrapper.sugo",
                "data/test-cases/test-exam-robust/problem3/wrappers/",judgingTypesC
        );
    }
}
