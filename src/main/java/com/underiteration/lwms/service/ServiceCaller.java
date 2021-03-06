package com.underiteration.lwms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.underiteration.lwms.api.exception.JsonMarshallingException;
import com.underiteration.lwms.jobpool.PendingRemoteJobs;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.underiteration.lwms.networking.TcpServiceRequest.sendAsyncRemoteServiceRequest;

public class ServiceCaller {

	private static Logger logger = Logger.getLogger(ServiceCaller.class.getCanonicalName());

	private static ObjectMapper mapper = new ObjectMapper();

	public static <T> CompletableFuture<T> call(String serviceName, Object input) {

		// TODO: this assumes service is remote - check for local first

		RemoteServiceRegister remoteServiceRegister = RemoteServiceRegister.instance();

		Node serviceProvider = remoteServiceRegister.getServiceProvider(serviceName).orElseThrow();
		if (! remoteServiceRegister.checkServiceInputType(serviceName, input).orElse(true)) {
			throw new CompletionException(new IllegalArgumentException(String.format("Wrong input type for %s", serviceName)));
		}

		String jobReference = UUID.randomUUID().toString();

		CompletableFuture<T> job = PendingRemoteJobs.instance().recordPendingJob(jobReference)
				.thenApply(json -> {
					logger.info(String.format("Future completed with JSON: %s", json));

					try {
						RemoteServiceRegister.Marshaller marshaller = remoteServiceRegister
								.marshaller(serviceName)
								.orElseThrow(() -> new JsonMarshallingException("Could not get marshaller"));

						return marshaller.marshall(json);
					} catch (JsonMarshallingException e) {
						logger.log(Level.SEVERE, e.getLocalizedMessage());
						logger.warning(String.format("Could not parse JSON %s", json));
						throw new CompletionException(e);
					}
				});

		try {
			sendAsyncRemoteServiceRequest(serviceProvider.getAddress(), serviceProvider.getServicePort(), serviceName, jobReference, Server.getListeningPort(), mapper.writeValueAsString(input));
		} catch (IOException e) {
			throw new CompletionException(e);
		}

		return job;
	}
}
