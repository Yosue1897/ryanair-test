package com.rynair.model.deserialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.rynair.model.Route;

public class RouteDeserializer extends StdDeserializer<Route> {

	private static final long serialVersionUID = 1L;
	
	private static final String OPERATOR = "RYANAIR";
	
	private Route route = Route.builder().build();

	public RouteDeserializer() { 
        this(null); 
    } 
    
	protected RouteDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public Route deserialize(JsonParser jp, DeserializationContext desContext) throws IOException {

		JsonNode node = jp.getCodec().readTree(jp);
        String operator = node.get("operator").asText();
        String connectingAirport = node.get("connectingAirport").asText();
        if(operator.equals(OPERATOR) && connectingAirport.equals("null")) {

        	String airportFrom = node.get("airportFrom").asText();
        	String airportTo = node.get("airportTo").asText();
        	Boolean newRoute = node.get("newRoute").asBoolean();
        	Boolean seasonalRoute = node.get("seasonalRoute").asBoolean();
        	String group = node.get("group").asText();
        	List<String> similarArrivalAirportCodes = new ArrayList<>();
            List<String> tags = new ArrayList<>();
        	
            for(JsonNode element: node.get("similarArrivalAirportCodes")) {
            	similarArrivalAirportCodes.add(element.asText());
            }
        	
            for(JsonNode element: node.get("tags")) {
            	tags.add(element.asText());
            }

            route = Route.builder()
					.departureAirport(airportFrom)
					.arrivalAirport(airportTo)
        			.operator(operator)
        			.connectingAirport(connectingAirport)
        			.newRoute(newRoute)
        			.seasonalRoute(seasonalRoute)
        			.group(group)
        			.similarArrivalAirportCodes(similarArrivalAirportCodes)
        			.tags(tags)
        			.build();
        }
        
        return route;
	}

}
