package com.asuproject.infinispan.web;

import com.asuproject.infinispan.cache.StorageCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class Controller {
	private final StorageCache cache;

	@Autowired
	public Controller(StorageCache cache) {
		this.cache = cache;
	}

	@PutMapping("/value")
	public void putKey(@RequestParam("key") String key, @RequestParam("value") String value) {
		cache.put(key, value);
	}

	@DeleteMapping("/value")
	public void removeKey(@RequestParam("key") String key, @RequestParam("value") String value) {
		cache.remove(key, value);
	}

	@GetMapping("/state")
	public Map<String, Set<String>> state() {
		return cache.state();
	}

	@PostMapping("/clear")
	public void clear() {
		cache.clear();
	}

}
