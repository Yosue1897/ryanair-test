package com.rynair.model;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class Flight {

	private String carrierCode;
	private Integer number;
	
//	@DateTimeFormat(iso = ISO.TIME)
	@JsonFormat(pattern = "HH:mm")
	private LocalTime departureTime;
	
	@JsonFormat(pattern = "HH:mm")
	private LocalTime arrivalTime;
}
