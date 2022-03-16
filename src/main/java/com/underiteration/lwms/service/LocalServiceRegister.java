package com.underiteration.lwms.service;

import com.underiteration.lwms.api.RemoteServiceDescription;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LocalServiceRegister {

	private static final LocalServiceRegister singleton = new LocalServiceRegister();

	private final Map<String, Service> services = new HashMap<>();
	private Function<Integer, Integer> calculateBackPressureInMilliseconds = i -> 0;

	private LocalServiceRegister() {

	}

	public static void registerService(Service service) {

		singleton.services.put(service.getName(), service);
	}

	public static Optional<Service> getService(String serviceName) {
		return Optional.ofNullable(singleton.services.get(serviceName));
	}

	public static Collection<RemoteServiceDescription> buildServiceDescription() {
		return singleton.services.values().stream()
				.map(service -> {
					RemoteServiceDescription description = new RemoteServiceDescription();
					description.setServiceName(service.getName());
					description.setInputTypeHint(service.getInputType().getCanonicalName());
					description.setOutputTypeHint(service.getOutputType().getCanonicalName());

					return description;
				})
				.collect(Collectors.toSet());
	}

	public static void setBackpressureFunction(Function<Integer, Integer> calculateBackPressureInMilliseconds) {
		singleton.calculateBackPressureInMilliseconds = calculateBackPressureInMilliseconds;
	}

	public static Integer backPressure(Integer queueSize) {
		return singleton.calculateBackPressureInMilliseconds.apply(queueSize);
	}

}
