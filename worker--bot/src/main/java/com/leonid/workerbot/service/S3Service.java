package com.leonid.workerbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    /**
     * @param bucket name of bucket s3://my-bucket
     * @param key    path to file in bucket some/path/to/file.txt
     * @return true if everything is ok
     */
    public boolean checkFileExists(String bucket, String key) {
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());
            return true;
        } catch (S3Exception e) {
            return false;
        }
    }

    public void bucketList(String bucketName) {
//        ListBucketsResponse listBucketsResponse = s3Client.listBuckets();
//        for (Bucket bucket : listBucketsResponse.buckets()) {
//            System.out.println(bucket.name());
//        }
//
        System.out.println("=================");

        ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response listRes = s3Client.listObjectsV2(listReq);

        for (S3Object s3Object : listRes.contents()) {
            System.out.println("Key: " + s3Object.key() + "  Size: " + s3Object.size());
        }
    }

    /**
     * @param bucket name of bucket s3://my-bucket
     * @param key    path to file in bucket some/path/to/file.txt
     */
    public void deleteFile(String bucket, String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
    }


}
