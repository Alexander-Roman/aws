package com.alexander.aws.runner;

import com.alexander.aws.config.property.AwsS3Properties;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(2)
public class CleaningCommandLineRunner implements CommandLineRunner {

    private final AwsS3Properties awsS3Properties;
    private final AmazonS3 amazonS3;

    @Override
    public void run(String... args) {
        String bucketName = awsS3Properties.getBucketName();

        // Delete all objects from the bucket. This is sufficient
        // for unversioned buckets. For versioned buckets, when you attempt to delete objects, Amazon S3 inserts
        // delete markers for all objects, but doesn't delete the object versions.
        // To delete objects from versioned buckets, delete all the object versions before deleting
        // the bucket (see below for an example).
        ObjectListing objectListing = amazonS3.listObjects(bucketName);
        this.deleteAllObjects(objectListing);

        // If the bucket contains many objects, the listObjects() call
        // might not return all the objects in the first listing. Check to
        // see whether the listing was truncated. If so, retrieve the next page of objects
        // and delete them.
        while (objectListing.isTruncated()) {
            objectListing = amazonS3.listNextBatchOfObjects(objectListing);
            this.deleteAllObjects(objectListing);
        }

        // Delete all object versions (required for versioned buckets).
        ListVersionsRequest listVersionsRequest = new ListVersionsRequest().withBucketName(bucketName);
        VersionListing versionList = amazonS3.listVersions(listVersionsRequest);
        this.deleteAllObjectVersions(versionList);
        while (versionList.isTruncated()) {
            versionList = amazonS3.listNextBatchOfVersions(versionList);
            this.deleteAllObjectVersions(versionList);
        }

        // After all objects and object versions are deleted, delete the bucket.
        amazonS3.deleteBucket(bucketName);
    }

    private void deleteAllObjects(ObjectListing objectListing) {
        objectListing.getObjectSummaries()
                .stream()
                .map(S3ObjectSummary::getKey)
                .forEach(key -> amazonS3.deleteObject(awsS3Properties.getBucketName(), key));
    }

    private void deleteAllObjectVersions(VersionListing versionList) {
        String bucketName = awsS3Properties.getBucketName();
        for (S3VersionSummary s3VersionSummary : versionList.getVersionSummaries()) {
            String key = s3VersionSummary.getKey();
            String versionId = s3VersionSummary.getVersionId();
            amazonS3.deleteVersion(bucketName, key, versionId);
        }
    }

}