import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        List<Integer> tempResultList = new ArrayList<>();
        List<Integer> tempListA = new ArrayList<Integer>(){{
            add(1);
            add(2);
            add(3);
            add(4);
            add(5);
            add(6);
        }};
        List<Integer> tempListB = new ArrayList<Integer>(){{
            add(2);
            add(3);
            add(4);
            add(5);
        }};
        tempResultList.addAll(tempListA);
        tempResultList.retainAll(tempListB);
        System.out.println(tempResultList);
    }
}
