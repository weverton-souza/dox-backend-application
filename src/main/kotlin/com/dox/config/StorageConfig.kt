package com.dox.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI

@ConfigurationProperties(prefix = "dox.storage")
data class StorageProperties(
    val enabled: Boolean = true,
    val endpoint: String,
    val region: String,
    val bucket: String,
    val accessKey: String,
    val secretKey: String,
    val presignedUrlTtlMinutes: Long = 60,
    val maxFileSizeMb: Long = 10,
    val allowedMimeTypes: String = "application/pdf,image/jpeg,image/png,image/webp",
) {
    fun allowedMimeTypesList(): List<String> = allowedMimeTypes.split(",").map { it.trim() }.filter { it.isNotBlank() }
}

@Configuration
@org.springframework.boot.context.properties.EnableConfigurationProperties(StorageProperties::class)
class StorageConfig(
    private val props: StorageProperties,
) {
    private fun credentials() =
        StaticCredentialsProvider.create(
            AwsBasicCredentials.create(props.accessKey, props.secretKey),
        )

    private fun serviceConfig(): S3Configuration =
        S3Configuration.builder()
            .pathStyleAccessEnabled(true)
            .build()

    @Bean(destroyMethod = "close")
    fun s3Client(): S3Client =
        S3Client.builder()
            .endpointOverride(URI.create(props.endpoint))
            .region(Region.of(props.region))
            .credentialsProvider(credentials())
            .serviceConfiguration(serviceConfig())
            .build()

    @Bean(destroyMethod = "close")
    fun s3Presigner(): S3Presigner =
        S3Presigner.builder()
            .endpointOverride(URI.create(props.endpoint))
            .region(Region.of(props.region))
            .credentialsProvider(credentials())
            .serviceConfiguration(serviceConfig())
            .build()
}
