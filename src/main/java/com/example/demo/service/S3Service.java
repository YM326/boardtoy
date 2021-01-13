package com.example.demo.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.util.IOUtils;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URLEncoder;


@Service
@NoArgsConstructor
public class S3Service {
    private AmazonS3 s3Client;
    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    @Value("${spring.cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @PostConstruct
    public void setS3Client() throws IOException{
        try {
            AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

            s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion(this.region)
                    .build();
        }catch(Exception e){

        }
    }

    public String upload(MultipartFile file, String fileName, String sseKey) throws IOException{
        try {
            SSECustomerKey customerKey = new SSECustomerKey(sseKey);
            TransferManager tm = TransferManagerBuilder.standard().withS3Client(s3Client).build();
            Upload upload = null;
            PutObjectRequest putRequest = new PutObjectRequest(bucket, fileName, file.getInputStream(), null).withSSECustomerKey(customerKey)
                    .withCannedAcl(CannedAccessControlList.Private);
            upload = tm.upload(putRequest);
        } catch(RuntimeException var){
            logger.info(var.getMessage(), var);

            return "";
        }

        return s3Client.getUrl(bucket, fileName).toString();
    }

    @SneakyThrows
    public ResponseEntity<byte[]> download(String storedFileName, String fileName, String sseKey) throws Exception {
        try {
            SSECustomerKey customerKey = new SSECustomerKey(sseKey);
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, storedFileName).withSSECustomerKey(customerKey);
            S3Object s3Object = s3Client.getObject(getObjectRequest);
            S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(objectInputStream);
            String downFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            httpHeaders.setContentLength(bytes.length);
            httpHeaders.setContentDispositionFormData("attachment", downFileName);
            return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
        }catch(Exception e){
            String eMessage = e.getMessage();
            String message = "Access Denied";
            return new ResponseEntity<byte[]>(message.getBytes("UTF-8"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean deleteObject(String storedFile) throws IOException{
        boolean isDone = false;
        try{
            s3Client.deleteObject(new DeleteObjectRequest(bucket, storedFile));
            isDone = true;
            logger.debug("파일 제거");
        }catch(Exception e){
            logger.info("파일 제거 실패");
        }

        return isDone;
    }
}
