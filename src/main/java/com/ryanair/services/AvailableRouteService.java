package com.ryanair.services;

import java.time.LocalDateTime;
import java.util.List;

import com.rynair.model.Route;

public interface AvailableRouteService {

	public List<Route> getAvailableRoutesIATACodes(String departure, String arrival, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime);
}
