package com.ryanair.services.impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.ryanair.config.FlightProperties;
import com.ryanair.services.ScheduleService;
import com.rynair.model.Route;
import com.rynair.model.Schedule;

@Service
@Configuration
@EnableConfigurationProperties(FlightProperties.class)
public class ScheduleServiceImpl implements ScheduleService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleServiceImpl.class);
	private RestTemplate restTemplate;
	private FlightProperties flightProperties;
	
	@Autowired
	public ScheduleServiceImpl(RestTemplateBuilder builder, FlightProperties flightProperties) {
		restTemplate = builder.build();
		this.flightProperties = flightProperties;
	}
	
	@Override
	@Cacheable(value = "schedule")
	public Schedule getSchedulesByDepartureAndArrival(Route route) {

		Map<String, String> uriParams = new HashMap<>();
		uriParams.put("departure", route.getDepartureAirport());
		uriParams.put("arrival", route.getArrivalAirport());
		uriParams.put("year", String.valueOf(LocalDate.now().getYear()));
		uriParams.put("month", String.valueOf(LocalDate.now().getMonthValue()));
		
		try {
			ResponseEntity<Schedule> schedules = restTemplate.exchange(flightProperties.getScheduleRoute().getUrl(), HttpMethod.GET, 
					null, Schedule.class, uriParams);

			LOGGER.info("There are flights from the airport {} to {}", route.getDepartureAirport(), route.getArrivalAirport());
			return schedules.getBody();
		} catch (HttpStatusCodeException e) {
			LOGGER.info("There's no flight from the airport {} to {}", route.getDepartureAirport(), route.getArrivalAirport());
		}
		return null;
	}

}
