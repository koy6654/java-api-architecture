package com.example.koy.util.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {
	@Value("${spring.cloud.aws.s3.region}")
	private String region;

	@Value("${spring.cloud.aws.s3.endpoint}")
	private String endpoint;

	@Value("${spring.cloud.aws.credentials.access-key}")
	private String accessKey;

	@Value("${spring.cloud.aws.credentials.secret-key}")
	private String secretKey;

	@Bean
	public AmazonS3Client amazonS3Client() {
		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

		AWSStaticCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);

		if (!endpoint.isEmpty()) {
			EndpointConfiguration endpointConfig = new EndpointConfiguration(endpoint, region);
			return (AmazonS3Client)AmazonS3ClientBuilder
				.standard()
				.withEndpointConfiguration(endpointConfig)
				.withCredentials(awsCredentialsProvider)
				.build();
		}

		return (AmazonS3Client)AmazonS3ClientBuilder
			.standard()
			.withCredentials(awsCredentialsProvider)
			.withRegion(region)
			.build();
	}
}
