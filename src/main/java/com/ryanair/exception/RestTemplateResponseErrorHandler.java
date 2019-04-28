package com.ryanair.exception;

import java.io.IOException;

import javax.ws.rs.NotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return (
				response.getStatusCode().series() == Series.CLIENT_ERROR 
		          || response.getStatusCode().series() == Series.SERVER_ERROR);
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		if (response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
			// handle SERVER_ERROR
		} else if (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
			// handle CLIENT_ERROR
			if(response.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new NotFoundException();
			}
		}
	}

}
