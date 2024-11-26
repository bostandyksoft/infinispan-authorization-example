package com.asuproject.infinispan.cache;

import org.infinispan.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@EnableScheduling
public class StorageCache {
	private static final Logger logger = LoggerFactory.getLogger(StorageCache.class);

	private final Map<String, Lock> locks = new ConcurrentHashMap<>();
	private final Cache<String, Authorization> cache;

	@Autowired
	public StorageCache(Cache<String, Authorization> cache) {
		this.cache = cache;
	}

	public void put(String key, String value) {
		Lock lock = locks.computeIfAbsent(key, k -> new ReentrantLock());
		lock.lock();
		try {
			Authorization authorization = cache.get(key);
			if (authorization == null) {
				authorization = new Authorization(key, new HashSet<>());
			}
			authorization.add(value);
			cache.put(key, authorization);
		} finally {
			lock.unlock();
		}
	}

	public void remove(String key, String value) {
		Lock lock = locks.computeIfAbsent(key, k -> new ReentrantLock());
		lock.lock();
		try {
			Authorization authorization = cache.get(key);
			if (authorization == null) {
				authorization = new Authorization(key, new HashSet<>());
			}
			authorization.remove(value);
			cache.put(key, authorization);
		} finally {
			lock.unlock();
		}
	}

	@Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
	public void outStorage() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Authorization> entry : cache.entrySet()) {
			sb.append(entry.getKey()).append(": ");
			for (String value : entry.getValue().keys) {
				sb.append(value).append(", ");
			}
			sb.append("\n");
		}
		logger.info(sb.toString());
	}

	public Map<String, Set<String>> state() {
		Map<String, Set<String>> result = new HashMap<>();
		for (Map.Entry<String, Authorization> entry : cache.entrySet()) {
			result.put(entry.getKey(), new HashSet<>(entry.getValue().keys));
		}
		return result;
	}

	public void clear() {
		cache.clear();
	}
}
