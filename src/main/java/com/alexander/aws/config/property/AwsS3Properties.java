package com.alexander.aws.config.property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties("aws.s3")
@ConstructorBinding
@AllArgsConstructor
@Getter
public class AwsS3Properties {

    private final String bucketName;
    private final String stringObjectKey;
    private final String fileObjectKey;

}
