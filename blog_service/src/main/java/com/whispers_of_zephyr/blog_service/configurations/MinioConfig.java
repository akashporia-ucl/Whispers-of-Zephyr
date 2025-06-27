package com.whispers_of_zephyr.blog_service.configurations;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.createBucket:false}")
    private boolean createBucket;

    @Value("${minio.external.public-url}")
    private String publicUrl;

    // @Value("${minio.external.path:/minio-api}")
    // private String externalPath;

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();

        if (createBucket) {
            try {
                boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
                if (!isExist) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                }
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException
                    | ServerException | XmlParserException | IOException | IllegalArgumentException
                    | InvalidKeyException | NoSuchAlgorithmException e) {
                throw new RuntimeException("Error creating bucket: " + bucketName, e);
            }
        }

        return minioClient;
    }

    @Bean
    @Qualifier("presignedUrlClient")
    public MinioClient presignedUrlClient() {
        // Use only the base URL without path for MinioClient
        log.info("Creating presigned URL client with endpoint: {}", publicUrl);
        return MinioClient.builder()
                .endpoint(publicUrl)
                .credentials(accessKey, secretKey)
                .build();
    }

    // @Bean
    // @Qualifier("externalPath")
    // public String externalPath() {
    // return externalPath;
    // }
}