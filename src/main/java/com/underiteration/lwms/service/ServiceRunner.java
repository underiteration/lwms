package com.underiteration.lwms.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.underiteration.lwms.networking.TcpServiceRequest.sendAsyncRemoteServiceResponse;

public class ServiceRunner implements Runnable {

	private static Logger logger = Logger.getLogger(ServiceRunner.class.getCanonicalName());
	private static ObjectMapper mapper = new ObjectMapper();

	private final InetAddress callerAddress;
	private final Integer callerPort;
	private final String target;
	private final String jobReference;
	private final Integer backPressure;
	private final String payload;
	private final Runnable callback;

	public ServiceRunner(InetAddress callerAddress, Integer callerPort, String target, String jobReference, Integer backPressure, String payload, Runnable callback) {
		this.callerAddress = callerAddress;
		this.callerPort = callerPort;
		this.target = target;
		this.jobReference = jobReference;
		this.backPressure = backPressure;
		this.payload = payload;
		this.callback = callback;
	}

	@Override
	public void run() {

		Optional<Service> optionalService = LocalServiceRegister.getService(target);
		String output = null;
		Boolean success = true;

		try {
			if (optionalService.isPresent()) {
				Service service = optionalService.get();
				Object input = mapper.readValue(payload, service.getInputType());
				output = mapper.writeValueAsString(service.getOutputType().cast(service.getFunction().apply(input)));
			}
		} catch (IOException e) {
			success = false;
			output = e.getLocalizedMessage();
		}

		try {
			sendAsyncRemoteServiceResponse(callerAddress, callerPort, jobReference, success, backPressure, output);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getLocalizedMessage());
		} finally {
			callback.run();
		}
	}
}
