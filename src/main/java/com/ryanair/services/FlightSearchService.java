package com.ryanair.services;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rynair.model.ResultFlight;

public interface FlightSearchService {
	
	public List<ResultFlight> searchFlight(String departure, String arrival, 
			LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) throws JsonProcessingException;

}