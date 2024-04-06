package io.github.wtfjoke.lambda.carbon.aware.computing;


import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;

@ApplicationScoped
public class CarbonAwareComputingServiceHeaderFactory implements ClientHeadersFactory {

	private static final String API_KEY_PARAMETER_NAME = "/carbon-aware-computing/api-key";

	@Inject
	SsmClient ssmClient;

	@Override
	public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders, MultivaluedMap<String, String> clientOutgoingHeaders) {
		var headers = new MultivaluedHashMap<String, String>();
		headers.add("x-api-key", getApiKey());
		return headers;
	}

	private String getApiKey() {
		return getApiKeyParameter().orElseThrow();
	}

	private Optional<String> getApiKeyParameter() {
		var request = GetParameterRequest.builder()
				.name(CarbonAwareComputingServiceHeaderFactory.API_KEY_PARAMETER_NAME)
				.withDecryption(true)
				.build();
		var response = ssmClient.getParameter(request);
		return Optional.of(response.parameter().value());
	}

}
