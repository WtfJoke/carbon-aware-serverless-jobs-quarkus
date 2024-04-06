package io.github.wtfjoke.lambda.carbon.aware.computing;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "https://forecast.carbon-aware-computing.com")
@RegisterClientHeaders(CarbonAwareComputingServiceHeaderFactory.class)
public interface CarbonAwareComputingService {


	@GET
	@Path("/emissions/forecasts/current")
	@HeaderParam("x-api-key")
	ForecastResponse[] getCurrentEmissionForecasts(
			@QueryParam("location")
			Location location,
			@QueryParam("dataStartAt")
			String dataStartAt,
			@QueryParam("dataEndAt")
			String dataEndAt,
			@DefaultValue("")
			@QueryParam("windowSize")
			Integer windowSize
	);
}
