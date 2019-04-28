package com.ryanair.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ryanair.config.FlightProperties;
import com.ryanair.services.AvailableRouteService;
import com.rynair.model.Route;

@Service
@Configuration
@EnableConfigurationProperties(FlightProperties.class)
public class AvailableRouteServiceImpl implements AvailableRouteService {
	
	private RestTemplate restTemplate;
	private FlightProperties flightProperties;
	
	@Autowired
	public AvailableRouteServiceImpl(RestTemplateBuilder builder, FlightProperties flightProperties) {
		restTemplate = builder.build();
		this.flightProperties = flightProperties;
	}

	@Override
	@Cacheable(value = "routeIATA")
	public List<Route> getAvailableRoutesIATACodes(String departure, String arrival, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {
		
		ResponseEntity<List<Route>> routes = restTemplate.exchange(flightProperties.getAvailableRoute().getUrl(), HttpMethod.GET, 
				null, new ParameterizedTypeReference<List<Route>>(){});

		return routes.getBody();
	}

}
