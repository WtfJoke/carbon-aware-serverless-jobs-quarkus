package io.github.wtfjoke.carbonawarecomputing;

import java.nio.file.Path;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.Tracing;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.ssm.IStringParameter;
import software.amazon.awscdk.services.ssm.SecureStringParameterAttributes;
import software.amazon.awscdk.services.ssm.StringParameter;
import software.amazon.awscdk.services.stepfunctions.DefinitionBody;
import software.amazon.awscdk.services.stepfunctions.IStateMachine;
import software.amazon.awscdk.services.stepfunctions.StateMachine;
import software.amazon.awscdk.services.stepfunctions.Wait;
import software.amazon.awscdk.services.stepfunctions.WaitTime;
import software.amazon.awscdk.services.stepfunctions.tasks.LambdaInvoke;
import software.constructs.Construct;

public class CarbonAwareComputingServerlessJobsConstruct extends Construct {

	final IStateMachine stateMachine;

	public CarbonAwareComputingServerlessJobsConstruct(@NotNull Construct scope, @NotNull String id, CarbonAwareComputingServerlessJobsConstructProps props) {
		super(scope, id);

		Function getBestRenewableEnergyTimeWindowLambda = createBestRenewableEnergyTimeWindowLambda(props);
		IStringParameter apiKey = StringParameter.fromSecureStringParameterAttributes(this, "CarbonAwareComputingApiKeyString",
				SecureStringParameterAttributes.builder()
						.parameterName("/carbon-aware-computing/api-key").build());
		apiKey.grantRead(getBestRenewableEnergyTimeWindowLambda);

		var getStatus = LambdaInvoke.Builder.create(this, "Get time window for best energy mix from grid")
				.lambdaFunction(getBestRenewableEnergyTimeWindowLambda)
				.resultPath("$.bestTimeWindowOutput")
				.build();

		var waitStep = Wait.Builder.create(this, "Wait for time Window")
				.time(WaitTime.secondsPath("$.bestTimeWindowOutput.Payload.waitTimeInSecondsForOptimalExecution"))
				.build();

		var stateMachineDefinition = getStatus.next(waitStep).next(props.batchJobTask());

		this.stateMachine = StateMachine.Builder.create(this, "Scheduler")
				.stateMachineName("CarbonAwareServerlessCACBatchJobsSchedulerQuarkus")
				.definitionBody(DefinitionBody.fromChainable(stateMachineDefinition))
//				.logs(LogOptions.builder()
//						.level(LogLevel.ALL)
//						.includeExecutionData(true)
//						.build())
				.tracingEnabled(true)
				.build();
	}

	private Function createBestRenewableEnergyTimeWindowLambda(CarbonAwareComputingServerlessJobsConstructProps props) {
		// Quarkus handler Must be hardcoded to this string, See https://quarkus.io/guides/aws-lambda
		String QUARKUS_LAMBDA_HANDLER = "io.quarkus.amazon.lambda.runtime.QuarkusStreamHandler::handleRequest";

		var functionBuilder = Function.Builder.create(this, "GetBestRenewableEnergyTimeWindowLambda")
				.code(Code.fromAsset(Path.of("..", "lambda", "build", "function.zip").toString()))
				.handler(QUARKUS_LAMBDA_HANDLER)
				.tracing(Tracing.ACTIVE)
				.memorySize(512)
				.timeout(Duration.seconds(30))
				.logRetention(RetentionDays.ONE_MONTH);

		if (props.useNativeImage()) {
			return functionBuilder
					.description("Native - Get the best time window to run a job based on the carbon intensity of the grid using the API of https://www.carbon-aware-computing.com/.")
					.runtime(Runtime.PROVIDED_AL2023)
					.environment(Map.of(
							"DISABLE_SIGNAL_HANDLERS", "true" // See https://quarkus.io/guides/aws-lambda#deploy-to-aws-lambda-custom-native-runtime
					))
					.build();
		}
		else {
			return functionBuilder
					.description("Get the best time window to run a job based on the carbon intensity of the grid using the API of https://www.carbon-aware-computing.com/.")
					.runtime(Runtime.JAVA_21)
					.architecture(Architecture.ARM_64)
					.build();
		}
	}
}
