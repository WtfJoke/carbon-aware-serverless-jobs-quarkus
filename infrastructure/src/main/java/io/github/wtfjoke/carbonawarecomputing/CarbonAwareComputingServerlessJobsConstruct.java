package io.github.wtfjoke.carbonawarecomputing;

import software.amazon.awscdk.Duration;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.Tracing;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.ssm.IStringParameter;
import software.amazon.awscdk.services.ssm.SecureStringParameterAttributes;
import software.amazon.awscdk.services.ssm.StringParameter;
import software.constructs.Construct;

public class CarbonAwareComputingServerlessJobsConstruct extends Construct {

	public CarbonAwareComputingServerlessJobsConstruct(@NotNull Construct scope, @NotNull String id, CarbonAwareComputingServerlessJobsConstructProps props) {
		super(scope, id);

		// Quarkus handler Must be hardcoded to this string, See https://quarkus.io/guides/aws-lambda
		Function getBestRenewableEnergyTimeWindowLambda = createBestRenewableEnergyTimeWindowLambda(props);
		IStringParameter apiKey = StringParameter.fromSecureStringParameterAttributes(this, "CarbonAwareComputingApiKeyString",
				SecureStringParameterAttributes.builder()
				.parameterName("/carbon-aware-computing/api-key").build());
		apiKey.grantRead(getBestRenewableEnergyTimeWindowLambda);

	}

	private Function createBestRenewableEnergyTimeWindowLambda(CarbonAwareComputingServerlessJobsConstructProps props) {
		String QUARKUS_LAMBDA_HANDLER = "io.quarkus.amazon.lambda.runtime.QuarkusStreamHandler::handleRequest";

		if (props.useNativeImage()) {
			return Function.Builder.create(this, "GetBestRenewableEnergyTimeWindowLambda")
					.description("Native - Get the best time window to run a job based on the carbon intensity of the grid using the API of https://www.carbon-aware-computing.com/.")
					.runtime(Runtime.PROVIDED) // Taken from ../lambda/build/manage.sh
					.code(Code.fromAsset("../lambda/build/function.zip"))
					.handler(QUARKUS_LAMBDA_HANDLER)
					.tracing(Tracing.ACTIVE)
					.memorySize(512)
					.architecture(Architecture.X86_64)
					.timeout(Duration.seconds(30))
					.logRetention(RetentionDays.ONE_MONTH)
					.environment(Map.of(
							"DISABLE_SIGNAL_HANDLERS", "true" // See https://quarkus.io/guides/aws-lambda#deploy-to-aws-lambda-custom-native-runtime
					))
					.build();
		}
		else {
			return Function.Builder.create(this, "GetBestRenewableEnergyTimeWindowLambda")
					.description("Get the best time window to run a job based on the carbon intensity of the grid using the API of https://www.carbon-aware-computing.com/.")
					.runtime(Runtime.JAVA_17)
					.code(Code.fromAsset("../lambda/build/function.zip"))
					.handler(QUARKUS_LAMBDA_HANDLER)
					.tracing(Tracing.ACTIVE)
					.memorySize(512)
					.architecture(Architecture.ARM_64)
					.timeout(Duration.seconds(30))
					.logRetention(RetentionDays.ONE_MONTH)
					.build();
		}
	}
}
