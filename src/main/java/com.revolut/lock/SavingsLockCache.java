package com.revolut.lock;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SavingsLockCache {

    public static final int CACHE_SIZE = 1000;
    public static final int DURATION_IN_SECONDS = 60;

    static LoadingCache<Long, ReadWriteLock> lockCache = CacheBuilder.newBuilder()
            .maximumSize(CACHE_SIZE)
            .expireAfterAccess(DURATION_IN_SECONDS, TimeUnit.SECONDS)
            .weakValues()
            //.concurrencyLevel()
            .build(
                    new CacheLoader<>() {
                        public ReadWriteLock load(Long id) {
                            final ReadWriteLock lock = new ReentrantReadWriteLock();
                            return lock;
                        }
                    }
            );

    public ReadWriteLock getLockForAccount(Long accountId) throws ExecutionException {
        final ReadWriteLock toDo = lockCache.get(accountId);
        return toDo;
    }

}
