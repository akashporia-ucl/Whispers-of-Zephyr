package com.whispers_of_zephyr.blog_service.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.minio.http.Method;

import com.whispers_of_zephyr.blog_service.model.BlogImage;
import com.whispers_of_zephyr.blog_service.model.Image;
import com.whispers_of_zephyr.blog_service.repository.ImageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.whispers_of_zephyr.blog_service.dto.BlogResponse;
import com.whispers_of_zephyr.blog_service.dto.ImageResponse;
import com.whispers_of_zephyr.blog_service.repository.BlogImageRepository;

import io.minio.GetPresignedObjectUrlArgs;
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
    private BlogImageRepository blogImageRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Value("${minio.bucketName}")
    private String bucketName;

    // public MinioService(MinioClient minioClient) {
    // this.minioClient = minioClient;
    // }

    @Autowired
    @Qualifier("presignedUrlClient")
    private MinioClient presignedUrlClient;

    public UUID uploadFile(MultipartFile file, UUID userId) {
        try {
            if (file == null || file.isEmpty()) {
                return null; // or throw an exception if you prefer

            }
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
            // Generate presigned URL using the client configured with public-facing URL
            String presignedUrl = presignedUrlClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(fileName)
                            .expiry(1, TimeUnit.HOURS)
                            .build());

            log.info("Generated raw presigned URL: {}", presignedUrl);

            // Replace the Docker internal hostname with localhost that browsers can access
            presignedUrl = presignedUrl.replace("http://minio:9000", "http://localhost/minio-api");
            log.info("Rewrote URL for external access: {}", presignedUrl);

            return presignedUrl;
        } catch (Exception e) {
            log.error("Error generating presigned URL: {}", e.getMessage(), e);
            throw new RuntimeException("Error generating presigned URL", e);
        }
    }

    public ImageResponse getFileURLByBlogId(UUID blogId) {
        try {
            BlogImage blogImage = blogImageRepository.findByBlogId(blogId)
                    .orElseThrow(() -> new IllegalArgumentException("BlogImage not found for blog id: " + blogId));
            log.info("BlogImage found with blogId {} and imageId {}", blogImage.getBlogId(), blogImage.getImageId());
            Image image = imageRepository.findById(blogImage.getImageId())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Image not found with id: " + blogImage.getImageId()));
            log.info("Image found with filename: {}", image.getFileName());
            String url = getFileURL(image.getFileName());
            log.info("File URL generated: {}", url);
            ImageResponse response = new ImageResponse(
                    url,
                    image.getMimeType(),
                    image.getFileName(),
                    image.getFileSize());
            return response;
        } catch (IllegalArgumentException e) {
            log.error("Error fetching image by ID: {}", e.getMessage());
            return null; // or throw an exception if you prefer
        } catch (RuntimeException e) {
            log.error("Error generating file URL from Minio: {}", e.getMessage());
            return null; // or throw an exception if you prefer
        }
    }

    public InputStream getFileAsStream(UUID BlogId) {
        try {
            BlogImage blogImage = blogImageRepository.findByBlogId(BlogId)
                    .orElseThrow(() -> new IllegalArgumentException("BlogImage not found for blog id: " + BlogId));
            log.info("BlogImage found with blogId {} and imageId {}", blogImage.getBlogId(), blogImage.getImageId());
            Image image = imageRepository.findById(blogImage.getImageId())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Image not found with id: " + blogImage.getImageId()));
            log.info("Image found with filename: {}", image.getFileName());

            InputStream fileStream = getFile(image.getFileName());
            log.info("File stream recived {}", fileStream);

            log.info("ImageResponse created with fileName: {}, mimeType: {}, fileSize: {}", image.getFileName(),
                    image.getMimeType(), image.getFileSize());

            return fileStream;

        } catch (IllegalArgumentException e) {
            log.error("Error fetching image by Blog ID: {}", e.getMessage());
            return null; // or throw an exception if you prefer
        } catch (RuntimeException e) {
            log.error("Error retrieving file from Minio: {}", e.getMessage());
            return null; // or throw an exception if you prefer
        }
    }

    private InputStream getFile(String fileName) {
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
