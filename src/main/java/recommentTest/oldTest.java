package recommentTest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class oldTest {
    private static Comparator<Integer> valueComparator;
    static {
        //排序规则
        valueComparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        };
    }
    public static void main(String[] args) throws SQLException, IOException {
        RecommentAlgorithmTest test = new RecommentAlgorithmTest();

//        List<String> selectMovieKindByUserId = test.selectMovieKindByUserId(1);
//        System.out.println(selectMovieKindByUserId.toString());

//        Map<Integer,Map<Integer,Integer>> buildUserAllMovieRatingTable = test.buildUserAllMovieRatingTable();
//        System.out.println(buildUserAllMovieRatingTable.toString());

//        Map<Integer,Map<Integer,Integer>> buildUserMovieRatingTable = test.buildUserMovieRatingTable();
//        System.out.println(buildUserMovieRatingTable.toString());

        Map<Integer,Map<String,Double>> buildUserKindRatingTable = test.buildUserKindRatingTable();
        System.out.println(buildUserKindRatingTable.toString());
//
//        double averageRating = test.averageKindRating(3);
//        System.out.println(averageRating);

        //Map<Integer, List<Integer>> buildUserMovieTable = test.buildUserMovieTable();

//        List<Integer> buildUserUserMovie = test.buildUserUserMovie(1,2);
//        System.out.println(buildUserUserMovie.toString());

        //------------------Map<Integer,List<Integer>> buildsimUserList
        //-----------------buildRecommendList
        //------------------Map<Integer,Map<Integer,Double>> buildUserUserSim1Table
        //------------------Map<Integer,Map<Integer,Double>> buildUserUserSim2Table

//        double countKindLike = test.countKindLike(1);
//        System.out.println(countKindLike);

        //Double countSim1BySameMovie = test.countSim1BySameMovie(1,2);
        Double countSim2BySameMovie = test.countSim2BySameMovie(1,2);
        System.out.println(countSim2BySameMovie);


    }
}
