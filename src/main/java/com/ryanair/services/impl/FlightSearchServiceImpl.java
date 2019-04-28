package com.ryanair.services.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
	
	
	@Autowired
	public FlightSearchServiceImpl(AvailableRouteService availableRouteService, ScheduleService scheduleService) {
		this.availableRouteService = availableRouteService;
		this.scheduleService = scheduleService;
	}

	@Override
	@Cacheable(value = "result")
	public List<ResultFlight> searchFlight(String departure, String arrival, 
			LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) throws JsonProcessingException {
		
		List<Route> routeList = availableRouteService.getAvailableRoutesIATACodes(departure, arrival, departureDateTime, arrivalDateTime).stream()
				.filter(item -> item.getDepartureAirport().equals(departure))
				.collect(Collectors.toList());
		
		List<ResultFlight> listResult = new ArrayList<>();
		routeList.parallelStream().forEach(item -> listResult.add(buildResultFlight(arrival, departureDateTime, arrivalDateTime, item)));
		
		Route routeDirect = Route.builder()
				.departureAirport(departure)
				.departureDateTime(departureDateTime)
				.arrivalAirport(arrival)
				.arrivalDateTime(arrivalDateTime)
				.build();
		
		listResult.addAll(directFlight(routeDirect));
		
		return listResult.parallelStream()
				.filter(Objects::nonNull)
				.sorted(Comparator.comparing(ResultFlight::getStops))
				.collect(Collectors.toList());
	}
	
	private ResultFlight buildResultFlight(String arrival, 
			LocalDateTime departureDateTime, LocalDateTime arrivalDateTime, Route route) {
		
		Boolean interconnected = Boolean.FALSE;
		ResultFlight resultFlight = null;
		Schedule sd = logic(scheduleService.getSchedulesByDepartureAndArrival(route), departureDateTime, arrivalDateTime, interconnected);
		
		Airport airportFrom = null;
		Airport airportTo = null;
		
		if(sd != null) {
			LOGGER.info("There's flight from the airport {} to {}", route.getDepartureAirport(), route.getArrivalAirport());
			airportFrom = Airport.Builder()
					.departureAirport(route.getDepartureAirport())
					.arrivalAirport(route.getArrivalAirport())
					.departureDateTime(LocalDateTime.of(LocalDate.now(), sd.getDays().get(0).getFlights().get(0).getDepartureTime()))
					.arrivalDateTime(LocalDateTime.of(LocalDate.now(), sd.getDays().get(0).getFlights().get(0).getArrivalTime()))
					.build();
			
			String airportAux = route.getArrivalAirport();
			route.setDepartureAirport(airportAux);
			route.setArrivalAirport(arrival);
			
			Schedule aux = logic(scheduleService.getSchedulesByDepartureAndArrival(route), airportFrom.getArrivalDateTime(), arrivalDateTime, Boolean.TRUE);
			
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
	
	private Schedule logic(Schedule schedule, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime, Boolean interconnected) {
		
		if (schedule != null) {
			schedule.getDays().removeIf(day -> day.getDay() != departureDateTime.getDayOfMonth());

			if (!schedule.getDays().isEmpty()) {
				
				if(interconnected) {
					schedule.getDays().get(0).getFlights().removeIf(time -> !time.getDepartureTime()
							.isAfter(LocalTime.of(departureDateTime.getHour(), departureDateTime.getMinute()).plusHours(2L)));
					
					schedule.getDays().get(0).getFlights().removeIf(time -> !time.getArrivalTime()
							.isBefore(LocalTime.of(arrivalDateTime.getHour(), arrivalDateTime.getMinute())));
					
				}else {
					schedule.getDays().get(0).getFlights().removeIf(time -> time.getDepartureTime()
							.isBefore(LocalTime.of(departureDateTime.getHour(), departureDateTime.getMinute())));
				}

				if (!schedule.getDays().get(0).getFlights().isEmpty()) {
					return schedule;
				}
			}
		}
		return null;
	}
	
	private List<ResultFlight> directFlight(Route routeDirect) {
		
		List<ResultFlight> directFlight = new ArrayList<>();
		Schedule scheDirect = scheduleService.getSchedulesByDepartureAndArrival(routeDirect);
		
		if(scheDirect != null) {
			scheDirect.getDays().stream()
				.filter(day -> day.getDay().equals(routeDirect.getDepartureDateTime().getDayOfMonth()))
				.forEach(item -> {
					item.getFlights().stream().filter(time -> 
					time.getDepartureTime()
					.equals(LocalTime.of(routeDirect.getDepartureDateTime().getHour(), routeDirect.getDepartureDateTime().getMinute()))
					|| time.getDepartureTime().isAfter(LocalTime.of(routeDirect.getDepartureDateTime().getHour(), routeDirect.getDepartureDateTime().getMinute())))
					.forEach(flight -> {
						directFlight.add(
						ResultFlight.builder()
								.stops(0)
								.legs(Arrays.asList(Airport.Builder()
										.departureAirport(routeDirect.getDepartureAirport())
										.departureDateTime(LocalDateTime.of(LocalDate.now(), flight.getDepartureTime()))
										.arrivalAirport(routeDirect.getArrivalAirport())
										.arrivalDateTime(LocalDateTime.of(LocalDate.now(), flight.getArrivalTime()))
										.build()))
								.build());
					});
				});
			
			return directFlight;
		}
		return new ArrayList<>();
	}

}
