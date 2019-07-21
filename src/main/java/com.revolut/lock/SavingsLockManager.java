package com.revolut.lock;

import com.google.common.util.concurrent.Striped;

import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SavingsLockManager {

    public static final int NUMBER_OF_STRIPES = 1000;
    private Striped<Lock> stripedLock = Striped.lock(NUMBER_OF_STRIPES);
    private WeakHashMap<Long, ReadWriteLock> lockMap = new WeakHashMap<>();

    public ReadWriteLock getLockForAccount(Long accountId) {
        ReadWriteLock readWriteLock = lockMap.get(accountId);
        if (readWriteLock == null) {
            Lock lock = stripedLock.getAt(accountId.hashCode());
            try {
                lock.lock();
                readWriteLock = new ReentrantReadWriteLock();
                lockMap.putIfAbsent(accountId, readWriteLock);
            } finally {
                lock.unlock();
            }
        }
        return readWriteLock;
    }
}
