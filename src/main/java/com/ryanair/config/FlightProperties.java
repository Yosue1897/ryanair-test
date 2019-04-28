package com.ryanair.config;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ConfigurationProperties(prefix = "endpoint")
@Getter
@Setter
@NoArgsConstructor
public class FlightProperties {
	
	@NestedConfigurationProperty
	private AvailableRoute availableRoute;
	
	@NestedConfigurationProperty
	private ScheduleRoute scheduleRoute;
	
    @Getter
    @Setter
    @NoArgsConstructor
	public static class AvailableRoute {

    	@NotNull
		private String url;
	}
    
    @Getter
    @Setter
    @NoArgsConstructor
	public static class ScheduleRoute {

    	@NotNull
		private String url;
	}

}
