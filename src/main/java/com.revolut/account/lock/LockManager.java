package com.revolut.account.lock;

import com.google.common.util.concurrent.Striped;

import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LockManager {

    public static final int NUMBER_OF_STRIPES = 1000;
    Striped<Lock> sripedLock = Striped.lock(NUMBER_OF_STRIPES);
    WeakHashMap<Long, ReadWriteLock> lockMap = new WeakHashMap<>();

    public ReadWriteLock getLockForAccount(Long accountId) {
        ReadWriteLock readWriteLock = lockMap.get(accountId);
        if (readWriteLock == null) {
            /*synchronized (this) {
                lock = locks.get(accountId);
                if(lock==null) {
                    lock =  new ReentrantReadWriteLock();
                    locks.put(accountId, lock);
                }
                //lock =  new ReentrantReadWriteLock();
                //locks.putIfAbsent(accountId,lock);
            }*/
            Lock lock = sripedLock.getAt(accountId.hashCode());
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
