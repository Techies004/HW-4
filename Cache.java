/**
 * @author THE ORIGINAL
 */
import java.util.ArrayList;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.LRUMap;


/**
 * @author THE ORIGINAL
 */

public class Cache<K, T> {
    private LRUMap lruMap;
    private long TTL;
    protected class CacheValue {
        public long lastAccessTime;
        public T value;

        protected CacheValue(T value) {
            this.lastAccessTime = System.currentTimeMillis();
            this.value = value;
        }
    }

    public Cache(long TTL, final long timerInterval, int maxItems) {
        lruMap = new LRUMap(maxItems);
        this.TTL = TTL * 1000;
        if (this.TTL > 0 && timerInterval > 0) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(timerInterval * 1000);
                            cleanup();
                        } catch (InterruptedException ex) {
                        }
                    }
                }
            });
            t.setDaemon(true);
            t.start();
        }
    }

    public void put(K key, T value) {
        synchronized (lruMap) {
            lruMap.put(key, new CacheValue(value));
        }
    }

    @SuppressWarnings("unchecked")
    public T get(K key) {
        synchronized (lruMap) {
            CacheValue c = (CacheValue) lruMap.get(key);

            if (c == null)
                return null;
            else {
                c.lastAccessTime = System.currentTimeMillis();
                return c.value;
            }
        }
    }

    public void remove(K key) {
        synchronized (lruMap) {
            lruMap.remove(key);
        }
    }

    public int size() {
        synchronized (lruMap) {
            return lruMap.size();
        }
    }

    @SuppressWarnings("unchecked")
    public void cleanup() {

        long now = System.currentTimeMillis();
        ArrayList<K> deleteKey = null;

        synchronized (lruMap) {
            MapIterator itr = lruMap.mapIterator();

            deleteKey = new ArrayList<K>((lruMap.size() / 2) + 1);
            K key = null;
            CacheValue c = null;

            while (itr.hasNext()) {
                key = (K) itr.next();
                c = (CacheValue) itr.getValue();

                if (c != null && (now > (TTL + c.lastAccessTime))) {
                    deleteKey.add(key);
                }
            }
        }

        for (K key : deleteKey) {
            synchronized (lruMap) {
                lruMap.remove(key);
            }

            Thread.yield();
        }
    }
}