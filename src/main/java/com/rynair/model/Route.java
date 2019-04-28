package com.rynair.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.rynair.model.deserialization.RouteDeserializer;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@JsonDeserialize(using = RouteDeserializer.class)
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class Route extends Airport {
	
	private String connectingAirport;
	private Boolean newRoute;
	private Boolean seasonalRoute;
	
	private String operator;
	private String group;
	private List<String> similarArrivalAirportCodes;
	private List<String> tags;
	
}
