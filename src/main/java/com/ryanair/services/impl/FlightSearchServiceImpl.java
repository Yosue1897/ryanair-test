package com.ryanair.services.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryanair.services.AvailableRouteService;
import com.ryanair.services.FlightSearchService;
import com.ryanair.services.ScheduleService;
import com.rynair.model.Airport;
import com.rynair.model.ResultFlight;
import com.rynair.model.Route;
import com.rynair.model.Schedule;

@Service
public class FlightSearchServiceImpl implements FlightSearchService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FlightSearchServiceImpl.class);
	private AvailableRouteService availableRouteService;
	private ScheduleService scheduleService;
	List<Schedule> scheduleList = new ArrayList<>();
	List<ResultFlight> listResult = new ArrayList<>();
	
	@Autowired
	public FlightSearchServiceImpl(AvailableRouteService availableRouteService, ScheduleService scheduleService) {
		this.availableRouteService = availableRouteService;
		this.scheduleService = scheduleService;
	}

	@Override
	public List<ResultFlight> searchFlight(String departure, String arrival, 
			LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) throws JsonProcessingException {
		
		List<Route> routeList = availableRouteService.getAvailableRoutesIATACodes(departure, arrival, departureDateTime, arrivalDateTime).stream()
				.filter(item -> item.getDepartureAirport().equals(departure))
				.collect(Collectors.toList());
		
		ObjectMapper objectMapper = new ObjectMapper();
		String carAsString = objectMapper.writeValueAsString(routeList);
		System.out.println(carAsString);
		
		routeList.forEach(item -> {
			listResult.add(test(departure, arrival, departureDateTime, arrivalDateTime, item));
		});
		
		return listResult;
	}
	
	private void interconnectedFlights(Route route, String originalArrival, LocalDateTime departureDateTime) {
		String airportAux = route.getArrivalAirport();
		route.setDepartureAirport(airportAux);
		route.setArrivalAirport(originalArrival);
		Schedule interconnection = scheduleService.getSchedulesByDepartureAndArrival(route);
		
		removeDays(interconnection, departureDateTime);
		
System.out.println("ee");
			
	}
	
	private void removeDays(Schedule schedule, LocalDateTime departureDateTime) {
		schedule.getDays().removeIf(x -> x.getDay() != departureDateTime.getDayOfMonth());
	}
	
	private ResultFlight test(String departure, String arrival, 
			LocalDateTime departureDateTime, LocalDateTime arrivalDateTime, Route route) {
		
		Boolean interconnected = Boolean.FALSE;
		ResultFlight resultFlight = null;
		Schedule rr = logica(scheduleService.getSchedulesByDepartureAndArrival(route), departureDateTime, interconnected);
		
		Airport airportFrom = null;
		Airport airportTo = null;
		
		if(rr != null) {
			LOGGER.info("There's flight from the airport {} to {}", route.getDepartureAirport(), route.getArrivalAirport());
			airportFrom = Airport.Builder()
					.departureAirport(route.getDepartureAirport())
					.arrivalAirport(route.getArrivalAirport())
					.departureDateTime(LocalDateTime.of(LocalDate.now(), rr.getDays().get(0).getFlights().get(0).getDepartureTime()))
					.arrivalDateTime(LocalDateTime.of(LocalDate.now(), rr.getDays().get(0).getFlights().get(0).getArrivalTime()))
					.build();
			
			String airportAux = route.getArrivalAirport();
			route.setDepartureAirport(airportAux);
			route.setArrivalAirport(arrival);
			
			Schedule aux = logica(scheduleService.getSchedulesByDepartureAndArrival(route), airportFrom.getArrivalDateTime(), Boolean.TRUE);
			
			if(aux != null) {
				airportTo = Airport.Builder()
						.departureAirport(route.getDepartureAirport())
						.arrivalAirport(route.getArrivalAirport())
						.departureDateTime(LocalDateTime.of(LocalDate.now(), aux.getDays().get(0).getFlights().get(0).getDepartureTime()))
						.arrivalDateTime(LocalDateTime.of(LocalDate.now(), aux.getDays().get(0).getFlights().get(0).getArrivalTime()))
						.build();
			}

			if(!airportFrom.getDepartureAirport().isEmpty() && airportTo != null) {
				
				resultFlight = ResultFlight.builder()
						.stops(1)
						.legs(Arrays.asList(airportFrom, airportTo))
						.build();
			}
		}
		
		return resultFlight;
	}
	
	private Schedule logica(Schedule schedule, LocalDateTime departureDateTime, Boolean interconnected) {
		
		if (schedule != null) {
			schedule.getDays().removeIf(day -> day.getDay() != departureDateTime.getDayOfMonth());

			if (!schedule.getDays().isEmpty()) {
				
				if(interconnected) {
					schedule.getDays().get(0).getFlights().removeIf(time -> !time.getDepartureTime()
							.isAfter(LocalTime.of(departureDateTime.getHour(), departureDateTime.getMinute()).plusHours(2L)));
				}else {
					schedule.getDays().get(0).getFlights().removeIf(time -> !time.getDepartureTime()
							.equals(LocalTime.of(departureDateTime.getHour(), departureDateTime.getMinute())));
				}

				if (!schedule.getDays().get(0).getFlights().isEmpty()) {
					System.out.println(schedule.getDays().get(0).getFlights().size());
					return schedule;
				}
			}
		}
		return null;
	}

}
