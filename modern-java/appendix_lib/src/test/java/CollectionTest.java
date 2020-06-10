import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CollectionTest {

    @Test
    public void getOrDefault_비교(){
        Map<String, Integer> carInventory = new HashMap<String, Integer>();
        final String key = "Aston Martin";

        // 기존 방식
        Integer count = 0;
        if(carInventory.containsKey(key)){
            count = carInventory.get(key);
        }

        // 새로운 방식
        Integer newCount = carInventory.getOrDefault(key, 0);

        System.out.println(count + " vs " + newCount);
        Assert.assertEquals(count, newCount);
    }

    @Test
    public void getOrDefault_value_null(){
        Map<String, Integer> carInventory = new HashMap<String, Integer>();
        final String key = "Aston Martin";

        carInventory.put(key, null);

        Integer count = carInventory.getOrDefault(key, 0);

        System.out.println(count);
        Assert.assertNull(count);
    }

    @Test
    public void computeIfAbsent_비교(){
        final String url = "http://localhost:8080";

        // 기존 로직
        ComputeIfAbsentCache.cacheClear();
        String data = ComputeIfAbsentCache.getDataOld(url);

        // 새로운 로직
        ComputeIfAbsentCache.cacheClear();
        String newData = ComputeIfAbsentCache.getDataNew(url);

        System.out.println(data + " vs " + newData);
        Assert.assertEquals(data, newData);
    }

    @Test
    public void removeIf_비교(){
        // 기존 방식
        List<Integer> baseList = IntStream.range(1, 10).boxed().collect(Collectors.toList());
        Iterator<Integer> iterator = baseList.iterator();
        while(iterator.hasNext()){
            Integer value = iterator.next();
            if(value % 3 == 0){
                iterator.remove();
            }
        }
        System.out.println("base: " + baseList.toString());

        // 새로운 방식
        List<Integer> newList = IntStream.range(1, 10).boxed().collect(Collectors.toList());
        newList.removeIf(val -> (val % 3 == 0));

        System.out.println("new: " + newList);

        Assert.assertArrayEquals(baseList.toArray(), newList.toArray());
    }

    @Test
    public void replaceAll_비교(){
        // 기존 방식
        List<Integer> oldNumbers = Arrays.asList(1, 2, 3, 4, 5);
        for(int i = 0; i < oldNumbers.size(); i++){
            Integer num = oldNumbers.get(i);
            oldNumbers.set(i, num + 2);
        }
        System.out.println(oldNumbers);

        // 새로운 방식
        List<Integer> newNumbers = Arrays.asList(1, 2, 3, 4, 5);
        newNumbers.replaceAll(n -> n + 2);
        System.out.println(newNumbers);
    }
}
