package com.whispers_of_zephyr.blog_service.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.whispers_of_zephyr.blog_service.model.Image;
import com.whispers_of_zephyr.blog_service.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class MinioService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Value("${minio.bucketName}")
    private String bucketName;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public UUID uploadFile(MultipartFile file, UUID userId) {
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            minioClient.putObject(
                    io.minio.PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            // Save image metadata to the database
            UUID ImageUUID = saveImageMetadata(userId, fileName, file.getContentType(), (int) file.getSize());
            return ImageUUID;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException
                | ServerException | XmlParserException | IOException | IllegalArgumentException | InvalidKeyException
                | NoSuchAlgorithmException e) {
            throw new RuntimeException("Error uploading file to Minio", e);
        }
    }

    public String getFileURL(String fileName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    io.minio.GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .expiry(7, TimeUnit.DAYS) // URL valid for 24 hours
                            .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException
                | ServerException | XmlParserException | IOException | IllegalArgumentException | InvalidKeyException
                | NoSuchAlgorithmException e) {
            throw new RuntimeException("Error retrieving file URL from Minio", e);
        }
    }

    public InputStream getFile(String fileName) {
        try {
            return minioClient.getObject(
                    io.minio.GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException
                | ServerException | XmlParserException | IOException | IllegalArgumentException | InvalidKeyException
                | NoSuchAlgorithmException e) {
            throw new RuntimeException("Error retrieving file from Minio", e);
        }
    }

    public void deleteFile(UUID id) {
        try {
            Image image = imageRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Image not found with id: " + id));

            minioClient.removeObject(
                    io.minio.RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(image.getFileName())
                            .build());

            // Delete the image record from the database
            imageRepository.delete(image);
            log.info("Image deleted successfully with UUID: {}", id);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException
                | ServerException | XmlParserException | IOException | IllegalArgumentException | InvalidKeyException
                | NoSuchAlgorithmException e) {
            throw new RuntimeException("Error deleting file from Minio", e);
        }
    }

    private UUID saveImageMetadata(UUID userId, String fileName, String mimeType, Integer fileSize) {
        return transactionTemplate.execute(status -> {
            Image image = new Image();
            image.setUuid(UUID.randomUUID());
            image.setFileName(fileName);
            image.setBucketName(bucketName);
            image.setMimeType(mimeType);
            image.setFileSize(fileSize);
            image.setUploadedBy(userId);
            image.setUploadedAt(LocalDateTime.now());
            log.info("Saving image metadata: {}", image);
            Image savedImage = imageRepository.saveAndFlush(image); // Use saveAndFlush to ensure immediate persistence
            log.info("Image metadata saved successfully with UUID: {}", savedImage.getUuid());
            return savedImage.getUuid();
        });
    }
}
