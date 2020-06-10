import java.util.HashMap;
import java.util.Map;

public class ComputeIfAbsentCache {
    private static Map<String, String> db = new HashMap<String, String>(){
        {
            put("http://localhost:8080", "health_check");
        }
    };
    private static Map<String, String> cache = new HashMap<>();

    public static void cacheClear(){
        cache.clear();
    }

    public static String getDataOld(String key){
        String data = cache.get(key);
        if(data == null){
            data = db.get(key);
            cache.put(key, data);
        }
        return data;
    }

    public static String getDataNew(String key){
        return cache.computeIfAbsent(key, (k) -> db.get(k));
    }
}
