package com.rynair.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@JsonInclude(Include.NON_NULL) 
@JsonIgnoreProperties(ignoreUnknown=true)
public class ResultFlight {

	private Integer stops;
	private List<Airport> legs;
}
