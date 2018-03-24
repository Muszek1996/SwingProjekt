import java.util.AbstractMap;
import java.util.Map;

public class Pair {
    Map.Entry<String,String> stringsPair;
    public Pair(String stringNo1,String stringNo2){
        stringsPair = new AbstractMap.SimpleEntry<>(stringNo1,stringNo2);
    }
    public String toString(){
        return stringsPair.getKey();
    }
    public String getValue(){
        return stringsPair.getValue();
    }
}
