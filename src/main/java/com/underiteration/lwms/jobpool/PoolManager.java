package com.underiteration.lwms.jobpool;

import com.underiteration.lwms.config.ConfigAddresses;
import com.underiteration.lwms.config.ConfigManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class PoolManager {

	public static final int DEFAULT_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
	public static final String DEFAULT_POOL_NAME = "_DEFAULT";
	private static Map<String, ExecutorService> pools = new HashMap<>();

	public static ExecutorService getPool(String poolName, int poolSize) {
		return pools.computeIfAbsent(poolName, __ -> Executors.newFixedThreadPool(poolSize));
	}

	public static ExecutorService getDefaultPool() {

		Integer poolSize = ConfigManager.instance().getInteger(ConfigAddresses.DEFAULT_THREAD_POOL_SIZE).orElse(DEFAULT_POOL_SIZE);
		return getPool(DEFAULT_POOL_NAME, poolSize);
	}

	public static Integer getPoolSize(String poolName) throws IllegalArgumentException {

		ExecutorService pool = pools.get(poolName);

		if (pool == null) throw new IllegalArgumentException("No such pool");

		if (ThreadPoolExecutor.class.isInstance(pool)) {
			return ((ThreadPoolExecutor) pool).getPoolSize();
		} else {
			return DEFAULT_POOL_SIZE;
		}
	}

	public static Integer getDefaultPoolSize() throws IllegalArgumentException {

		return getPoolSize(DEFAULT_POOL_NAME);
	}

}
