package com.showticket.show.config;

import lombok.Data;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {

    @Bean
    @ConfigurationProperties(prefix = "ticket.es")
    public ElasticSearchProperties elasticSearchProperties() {
        return new ElasticSearchProperties();
    }

    @Bean
    public RestHighLevelClient restHighLevelClient(ElasticSearchProperties properties) {
        return new RestHighLevelClient(RestClient.builder(HttpHost.create("http://" + properties.getHost())));
    }

    @Data
    public static class ElasticSearchProperties {
        private String host = "192.168.17.130:9200";
    }
}
