package com.ryanair.services;

import com.rynair.model.Route;
import com.rynair.model.Schedule;

public interface ScheduleService {

	public Schedule getSchedulesByDepartureAndArrival(Route route);
}
