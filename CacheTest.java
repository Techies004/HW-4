/**
 * @author THE ORIGINALS
 */

public class CacheTest {

    public static void main(String[] args) throws InterruptedException {
        testAddRemoveObjects();
        testExpiredCacheObjects();
        testObjectsCleanupTime();
    }

    private static void testAddRemoveObjects() {

        System.out.println("\n\nTesting Adding and Removing Objects from Cache");
        Cache<String, String> cache = new Cache<String, String>(200, 500, 6);

        System.out.println("Initial cache size: " + cache.size());
        cache.put("1", "1");
        cache.put("2", "2");
        cache.put("3", "3");
        cache.put("4", "4");
        cache.put("5", "5");
        cache.put("6", "6");

        System.out.println("Cache size after adding 6 items: " + cache.size());
        cache.remove("1");
        System.out.println("Cache size after removing 1 item1: " + cache.size());

        cache.put("7", "7");
        cache.put("8", "8");
        System.out.println("Cache size after adding two items (max size is 6): " + cache.size());
    }

    private static void testExpiredCacheObjects() throws InterruptedException {

        System.out.println("\n\nTesting Expired Cache Objects");
        Cache<String, String> cache = new Cache<String, String>(1, 1, 10);
        System.out.println("Initial cache size: " + cache.size());
        cache.put("1", "1");
        cache.put("2", "2");
        System.out.println("Cache size after adding two items: " + cache.size());
        // Adding 3 seconds sleep.. Both above objects will be removed from
        // Cache because of timeToLiveInSeconds value
        System.out.println("Sleeping for 3 seconds");
        Thread.sleep(3000);
        System.out.println("Cache size after sleeping: " + cache.size());
    }

    private static void testObjectsCleanupTime() throws InterruptedException {
        System.out.println("\n\nTesting Clean ups");
        int size = 500000;
        Cache<String, String> cache = new Cache<String, String>(100, 100, 500000);
        System.out.println("Initial cache size: " + cache.size());
        for (int i = 0; i < size; i++) {
            String value = Integer.toString(i);
            cache.put(value, value);
        }
        System.out.println("Cache size after adding 500000 items: " + cache.size());
        Thread.sleep(200);

        long start = System.currentTimeMillis();
        System.out.println("Calling clean up");
        cache.cleanup();
        double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
        System.out.println("Time taken to clean up 50000 items is " + finish + " s");
    }
}