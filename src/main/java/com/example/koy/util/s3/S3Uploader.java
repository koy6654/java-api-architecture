package com.example.koy.util.s3;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class S3Uploader {
	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucket;

	private final AmazonS3Client amazonS3Client;

	public S3Uploader(AmazonS3Client amazonS3Client) {
		this.amazonS3Client = amazonS3Client;
	}

	public List<String> upload(List<MultipartFile> uploadFile, String directory) {
		List<String> keys = new ArrayList<>();
		List<String> filePathListForException = new ArrayList<>();

		Date now = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

		for (MultipartFile file : uploadFile) {
			try {
				String filePath = "";
				if (directory != null) {
					filePath = directory;
				}

				String random = String.valueOf(UUID.randomUUID()).replaceAll("-", "");

				// File path 구조 : /디렉토리/날짜/랜덤문자-파일이름
				filePath =
					filePath + "/" + simpleDateFormat.format(now) + "/" + random + "-" + file.getOriginalFilename();
				filePathListForException.add(filePath);

				String key = uploadToS3(file, filePath);
				keys.add(key);
			} catch (Exception err) {
				log.error("uploadToS3 failed", err);
				keys.clear();
				delete(filePathListForException);
				break;
			}
		}

		return keys;
	}

	// TODO: 차후 S3 업로드 시간이 길어진다면 병렬 업로드로 개선 필요
	public String uploadToS3(MultipartFile uploadFile, String filePath) throws IOException {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(uploadFile.getContentType());
		metadata.setContentLength(uploadFile.getResource().contentLength());

		PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, filePath, uploadFile.getInputStream(),
			metadata).withCannedAcl(CannedAccessControlList.PublicRead);

		amazonS3Client.putObject(putObjectRequest);

		return filePath;
	}

	public void delete(List<String> keys) {
		for (String key : keys) {
			try {
				DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, key);
				amazonS3Client.deleteObject(deleteObjectRequest);
			} catch (Exception err) {
				log.error("deleteFromS3 failed", err);
				break;
			}
		}
	}
}
