package io.github.wtfjoke;

import java.util.Map;

import io.github.wtfjoke.carbonawarecomputing.CarbonAwareComputingServerlessJobsConstruct;
import io.github.wtfjoke.carbonawarecomputing.CarbonAwareComputingServerlessJobsConstructProps;
import software.amazon.awscdk.services.ssm.SecureStringParameterAttributes;
import software.amazon.awscdk.services.ssm.StringParameter;
import software.amazon.awscdk.services.stepfunctions.Pass;
import software.amazon.awscdk.services.stepfunctions.Result;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

public class CarbonAwareServerlessJobsQuarkusStack extends Stack {

	public CarbonAwareServerlessJobsQuarkusStack(final Construct scope, final String id, final StackProps props) {
		super(scope, id, props);

		var fakeLongRunningBatchJob = Pass.Builder.create(this, "My long running batch job")
				.comment("This is my long running batch job").inputPath("$.batchJobInput")
				.result(Result.fromObject(Map.of("success", true))).build();

		var carbonAwareComputingApiKey = StringParameter.fromSecureStringParameterAttributes(this, "CarbonAwareComputingApiKeyString",
				SecureStringParameterAttributes
						.builder()
						.parameterName("/carbon-aware-computing/api-key")
						.build()
		);

		new CarbonAwareComputingServerlessJobsConstruct(this, "Computing",
				new CarbonAwareComputingServerlessJobsConstructProps(carbonAwareComputingApiKey, fakeLongRunningBatchJob, true));
	}
}
