package com.underiteration.lwms.example;

import com.underiteration.lwms.service.RemoteServiceRegister;
import com.underiteration.lwms.service.Runner;

import static com.underiteration.lwms.service.ServiceCaller.call;

public class ExampleCaller extends Runner {

	public static void main(String[] args) {

		start();

		RemoteServiceRegister.subscribeToServices(name -> {
			if ("capitalize".equals(name)) {

				call("capitalize", "Mixed Case!")
						.thenApply(s -> "1. " + s)
						.thenAccept(System.out::println)
						.thenRun(() -> {
							System.out.println("Post 1");
						});

				call("capitalize", "Something else")
						.thenApply(s -> "2. " + s)
						.thenAccept(System.out::println)
						.thenRun(() -> {
							System.out.println("Post 2");
						});

				call("capitalize", "altogether")
						.thenApply(s -> "3. " + s)
						.thenAccept(System.out::println)
						.thenRun(() -> {
							System.out.println("Post 3");
						});

				call("capitalize", "Mixed Case!")
						.thenApply(s -> "4. " + s)
						.thenAccept(System.out::println)
						.thenRun(() -> {
							System.out.println("Post 4");
						});

				call("capitalize", "Something else")
						.thenApply(s -> "5. " + s)
						.thenAccept(System.out::println)
						.thenRun(() -> {
							System.out.println("Post 5");
						});

				call("capitalize", "altogether")
						.thenApply(s -> "6. " + s)
						.thenAccept(System.out::println)
						.thenRun(() -> {
							System.out.println("Post 6");
						});
			}
		});
	}

}
