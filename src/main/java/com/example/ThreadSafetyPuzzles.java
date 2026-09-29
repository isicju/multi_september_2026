package com.example;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class  ThreadSafetyPuzzles {

    static class ConfigService {
        private static ConfigService instance;
        private final Map<String, String> settings = new HashMap<>();

        private ConfigService() {}

        public static ConfigService getInstance() {
            if (instance == null) {
                synchronized (ConfigService.class) {
                    if (instance == null) {
                        instance = new ConfigService();
                    }
                }
            }
            return instance;
        }

        public void set(String key, String value) {
            settings.put(key, value);
        }

        public String get(String key) {
            return settings.get(key);
        }
    }


    static class RequestCounterService {
        private volatile int count = 0;

        public void recordRequest() {
            count++;
        }

        public int getCount() {
            return count;
        }
    }

    static class StatsService {
        private final AtomicInteger total = new AtomicInteger(0);

        public void add(int value) {
            total.addAndGet(value);
        }

        public int getTotal() {
            return total.get();
        }
    }

    static class BankAccountService {
        private double balance;
        private final Object lock = new Object();

        public void deposit(double amount) {
            synchronized (this) {
                balance += amount;
            }
        }

        public void withdraw(double amount) {
            synchronized (lock) {
                if (balance >= amount) {
                    balance -= amount;
                }
            }
        }

        public double getBalance() {
            return balance;
        }
    }

    static class FeatureFlagsService {
        private volatile Map<String, Boolean> flags = Collections.emptyMap();

        public void update(String key, boolean value) {
            Map<String, Boolean> copy = new HashMap<>(flags);
            copy.put(key, value);
            flags = copy;
        }

        public boolean isEnabled(String key) {
            return flags.getOrDefault(key, false);
        }
    }

    static class AuditLogService {
        private final List<String> entries = Collections.synchronizedList(new ArrayList<>());

        public void log(String entry) {
            entries.add(entry);
        }

        public List<String> snapshot() {
            List<String> copy = new ArrayList<>();
            for (String e : entries) {
                copy.add(e);
            }
            return copy;
        }
    }

    static class ExpensiveLookupCacheService {
        private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

        public String get(String key) {
            if (!cache.containsKey(key)) {
                cache.put(key, computeExpensive(key));
            }
            return cache.get(key);
        }

        private String computeExpensive(String key) {
            return key.toUpperCase() + "_RESULT";
        }
    }

    static class CoordinatesService {
        private final int[] point;

        public CoordinatesService(int[] point) {
            this.point = point;
        }

        public int[] getPoint() {
            return point;
        }
    }

    static class RequestIdService {
        private final ThreadLocal<Integer> requestId = ThreadLocal.withInitial(() -> 0);

        public void newRequest() {
            requestId.set(requestId.get() + 1);
        }

        public int currentId() {
            return requestId.get();
        }
    }

    static class GreetingService {
        private final StringBuilder log = new StringBuilder();

        public void greet(String name) {
            log.append("Hello, ").append(name).append("!\n");
        }

        public String getLog() {
            return log.toString();
        }
    }

    public static class TrickyCache {
        private final Map<String, String> cache = new ConcurrentHashMap<>();

        public void putIfAbsent(String key, String value) {
//            cache.putIfAbsent(key, value);
            if (!cache.containsKey(key)) {
                cache.put(key, value);
            }
        }

        public String get(String key) {
            return cache.get(key);
        }
    }
}