//package com.wegame.capi.infrastructure.config;
//
//import com.amazonaws.ClientConfiguration;
//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//
///**
// * @author apple
// */
//@Slf4j
//@Configuration
//public class S3Config {
//    @Value("${amazon.aws.access-key-id}")
//    private String accessKeyId;
//    @Value("${amazon.aws.access-key-secret}")
//    private String accessKeySecret;
//    @Value("${amazon.s3.region}")
//    private String region;
//
//    @Bean
//    public AmazonS3 s3Client() {
//        ClientConfiguration config = new ClientConfiguration();
//        config.setMaxConnections(100);
//        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId, accessKeySecret);
//        return AmazonS3ClientBuilder.standard()
//                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
//                .withRegion(region)
//                .build();
//    }
//
//}
