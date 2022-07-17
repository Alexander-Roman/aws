package com.alexander.aws.runner;

import com.alexander.aws.config.property.AwsS3Properties;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
@Order(1)
public class ObjectsUploadCommandLineRunner implements CommandLineRunner {

    public static final String STRING_OBJECT = "Uploaded String Object";
    public static final String FILE_OBJECT_PATH = "src/main/resources/objects/uploaded-file-object.jpg";
    private final AwsS3Properties awsS3Properties;
    private final AmazonS3 amazonS3;

    @Override
    public void run(String... args) {
        // Upload a text string as a new object.
        this.uploadString();

        // Upload a file as a new object with ContentType and title specified.
        this.uploadFile();
    }

    private void uploadString() {
        String bucketName = awsS3Properties.getBucketName();
        String stringObjectKey = awsS3Properties.getStringObjectKey();
        amazonS3.putObject(bucketName, stringObjectKey, STRING_OBJECT);
    }

    private void uploadFile() {
        String bucketName = awsS3Properties.getBucketName();
        String fileObjectKey = awsS3Properties.getFileObjectKey();

        Path fileObjectPath = Path.of(FILE_OBJECT_PATH);
        PutObjectRequest request = new PutObjectRequest(bucketName, fileObjectKey, fileObjectPath.toFile());
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("plain/text");
        metadata.addUserMetadata("x-amz-meta-title", "someTitle");
        request.setMetadata(metadata);
        amazonS3.putObject(request);
    }

}
