package com.salesbundle.service;

import io.awspring.cloud.s3.S3Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class S3Service {

    private final S3Template s3Template;
    private final S3Client s3Client;
    private final String bucketName;

    public S3Service(S3Template s3Template, S3Client s3Client, @Value("${spring.cloud.aws.s3.bucket}") String bucketName) {
        this.s3Template = s3Template;
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public Optional<String> getLatestBundleFile() {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix("bundle-data-")
                .build();

        ListObjectsV2Response response = s3Client.listObjectsV2(request);
        List<S3Object> objects = response.contents();

        return objects.stream()
                .filter(o -> o.key().endsWith(".json"))
                .max(Comparator.comparing(S3Object::lastModified))
                .map(S3Object::key);
    }

    public InputStream readFile(String key) throws IOException {
        return s3Template.download(bucketName, key).getInputStream();
    }
}