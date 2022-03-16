package com.underiteration.lwms.jobpool;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class PendingRemoteJobs {

	private static Logger logger = Logger.getLogger(PendingRemoteJobs.class.getCanonicalName());

	private static PendingRemoteJobs singleton = new PendingRemoteJobs();

	private final Map<String, CompletableFuture<String>> jobs = new ConcurrentHashMap<>();

	private PendingRemoteJobs() {

	}

	public static PendingRemoteJobs instance() {
		return singleton;
	}

	public CompletableFuture<String> recordPendingJob(String jobReference) {
		CompletableFuture<String> future = new CompletableFuture<>();
		jobs.put(jobReference, future);

		logger.info(String.format("Added job %s, queue now at %s", jobReference, jobs.size()));

		return future;
	}

	public void resolveFuture(String jobReference, String result) {

		jobs.remove(jobReference).complete(result);

		logger.info(String.format("Resolved job %s, queue now at %s", jobReference, jobs.size()));
	}

}
