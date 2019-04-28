package com.rynair.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@AllArgsConstructor
@SuperBuilder(toBuilder = true, builderMethodName = "Builder")
@JsonInclude(Include.NON_NULL)
public class Airport {

	@JsonProperty("airportFrom")
	@JsonAlias("departureAirport")
	private String departureAirport;
	
	@JsonProperty("airportTo")
	@JsonAlias("arrivalAirport")
	private String arrivalAirport;
	
	@JsonProperty("departureTime")
	@JsonAlias("departureDateTime")
	private LocalDateTime departureDateTime;
	
	@JsonProperty("arrivalTime")
	@JsonAlias("arrivalDateTime")
	private LocalDateTime arrivalDateTime;
}
