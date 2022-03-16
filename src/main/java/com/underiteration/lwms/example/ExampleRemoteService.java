package com.underiteration.lwms.example;

import com.underiteration.lwms.service.Runner;
import com.underiteration.lwms.service.Service;

import org.graalvm.polyglot.Context;

import static com.underiteration.lwms.service.LocalServiceRegister.registerService;
import static com.underiteration.lwms.service.LocalServiceRegister.setBackpressureFunction;

public class ExampleRemoteService extends Runner {

	public static String capitalize(String in) {
		return in.toUpperCase();
	}

	public static String capitalizeSlowly(String in) {

		return Context.create().eval("js", "\"lol, this isn't capitalized\"").asString();
	}

	public static void main(String[] args) {

		setBackpressureFunction(i -> 1000);
		registerService(new Service<>("capitalize", String.class, String.class, ExampleRemoteService::capitalize));
		registerService(new Service<>("capitalizeSlowly", String.class, String.class, ExampleRemoteService::capitalizeSlowly));
		start();
	}

}
