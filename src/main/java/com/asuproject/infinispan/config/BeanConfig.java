package com.asuproject.infinispan.config;

import com.asuproject.infinispan.cache.Authorization;
import com.asuproject.infinispan.cache.SerializationContextInitializerImpl;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class BeanConfig {

	@Bean
	public EmbeddedCacheManager defaultCacheManager() throws Exception {
		return new DefaultCacheManager(
				GlobalConfigurationBuilder.defaultClusteredBuilder()
						.transport()
						.defaultTransport()
						.clusterName("qa-cluster")
						.addProperty("configurationFile", "jgroups.xml")
						.build()
		);
	}

	@Bean
	public Cache<String, Authorization> authorizationCache(EmbeddedCacheManager cacheManager) {
		ConfigurationBuilder config = new ConfigurationBuilder();
		config
				.expiration().lifespan(5, TimeUnit.SECONDS)
				.clustering().cacheMode(CacheMode.DIST_SYNC)
				.hash();
		cacheManager.defineConfiguration("authorizationCache", config.build());
		return cacheManager.getCache("authorizationCache");
	}
}
