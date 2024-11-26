package com.asuproject.infinispan;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class InfinispanApplicationTests {

	Map<RestClient, String> clientsMap = new HashMap<>();

	@Test
	void contextLoads() {
		List<RestClient> clients = createClients(
				"http://localhost:8080/api",
				"http://localhost:8081/api",
				"http://localhost:8083/api");
		try {
			Map<String, Set<String>> expectedState = new HashMap<>();

			Set<String> keys = generateKeys(100);

			for (int i = 0; i < 1000; i++) {
				RestClient client = clients.get((int) Math.floor(Math.random() * clients.size()));
				int action = i < 100 ? 0 : ((int) Math.floor(Math.random() * 2.0));
				switch (action) {
					case 0:
						put(client, expectedState, randomKey(keys), UUID.randomUUID().toString());
						break;
					case 1: {
						String key = null, value = null;
						while (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
							key = randomKey(expectedState.keySet());
							Set<String> values = expectedState.get(key);
							if (values.isEmpty()) {
								value = null;
							} else {
								value = expectedState.get(key).iterator().next();
								System.out.println(value);
							}
						}
						delete(client, expectedState, key, value);
						break;
					}

				}
			}
			for (RestClient client : clients) {
				Map<String, Set<String>> state = state(client);
				assertEquals(expectedState.size(), state.size());
				for (Map.Entry<String, Set<String>> entry : expectedState.entrySet()) {
					assertEquals(
							entry.getValue(),
							state.get(entry.getKey()),
							clientsMap.get(client) + ". State for " + entry.getKey());
				}
			}
		} finally {
			for (RestClient client : clients) {
				clear(client);
			}
			clientsMap.clear();
		}
	}

	private List<RestClient> createClients(String... urls) {
		List<RestClient> clients = new ArrayList<>();
		for (String url : urls) {
			RestClient client = RestClient.create(url);
			clientsMap.put(client, url);
			clients.add(client);
		}
		return clients;
	}

	private String randomKey(Set<String> keys) {
		int index = (int) (Math.random() * keys.size());
		int i = 0;
		for (String key : keys) {
			if (index == (i++)) {
				return key;
			}
		}
		return null;
	}

	@SuppressWarnings("SameParameterValue")
	private Set<String> generateKeys(int count) {
		Set<String> keys = new HashSet<>(count);
		for (int i = 0; i < count; i++) {
			keys.add("k" + i);
		}
		return keys;
	}

	private Map<String, Set<String>> state(RestClient restClient) {
		return restClient.get().
				uri("/state").
				retrieve().onStatus(HttpStatusCode::isError, (request, rsp) -> {
					fail("Fails");
				}).body(State.class);
	}

	private void put(RestClient restClient, Map<String, Set<String>> expectedState, String key, String value) {
		System.out.printf("%s. Put %s %s\n", clientsMap.get(restClient), key, value);
		expectedState.computeIfAbsent(key, k -> new HashSet<>()).add(value);
		restClient.put().
				uri("/value?key={key}&value={value}",
						Map.of("key", key, "value", value)).
				retrieve().onStatus(HttpStatusCode::isError, (request, rsp) -> {
					fail("Fails");
				}).toBodilessEntity();
	}

	private void delete(RestClient restClient, Map<String, Set<String>> expectedState, String key, String value) {
		System.out.printf("%s. Delete %s %s\n", clientsMap.get(restClient), key, value);
		expectedState.computeIfAbsent(key, k -> new HashSet<>()).remove(value);
		restClient.delete().
				uri("/value?key={key}&value={value}",
						Map.of("key", key, "value", value)).
				retrieve().onStatus(HttpStatusCode::isError, (request, rsp) -> {
					fail("Fails");
				}).toBodilessEntity();
	}

	private void clear(RestClient client) {
		client.post().
				uri("/clear").
				retrieve().onStatus(HttpStatusCode::isError, (request, rsp) -> {
					fail("Fails");
				}).toBodilessEntity();
	}


	public static class State extends HashMap<String, Set<String>> {
	}

}
