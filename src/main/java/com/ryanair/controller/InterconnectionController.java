package com.ryanair.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ryanair.services.FlightSearchService;
import com.rynair.model.ResultFlight;

@RestController
@RequestMapping(value = "/api/flights", produces = MediaType.APPLICATION_JSON_VALUE)
public class InterconnectionController {
	
	@Autowired
	private FlightSearchService flightSearchService;
	
	@RequestMapping(value = "/interconnections", method = RequestMethod.GET)
    public ResponseEntity<List<ResultFlight>> getFlights(@RequestParam(required = true) String departure, @RequestParam(required = true) String arrival,
    												@RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDateTime, 
    												@RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime arrivalDateTime) throws JsonProcessingException {
		return new ResponseEntity<>(flightSearchService.searchFlight(departure, arrival, departureDateTime, arrivalDateTime), HttpStatus.OK);
    }

}
