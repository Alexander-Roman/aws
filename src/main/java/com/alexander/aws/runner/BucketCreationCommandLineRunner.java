package com.alexander.aws.runner;

import com.alexander.aws.config.property.AwsS3Properties;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(0)
public class BucketCreationCommandLineRunner implements CommandLineRunner {

    private final AwsS3Properties awsS3Properties;
    private final AmazonS3 amazonS3;

    @Override
    public void run(String... args) {
        String bucketName = awsS3Properties.getBucketName();
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            // Because the CreateBucketRequest object doesn't specify a region, the
            // bucket is created in the region specified in the client.
            amazonS3.createBucket(new CreateBucketRequest(bucketName));
            // Verify that the bucket was created by retrieving it and checking its location.
            String bucketLocation = amazonS3.getBucketLocation(new
                    GetBucketLocationRequest(bucketName));
            System.out.println("Bucket location: " + bucketLocation);
        }
    }

}
