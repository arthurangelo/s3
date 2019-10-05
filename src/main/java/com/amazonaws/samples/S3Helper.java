package com.amazonaws.samples;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;

public class S3Helper {
	
	private AmazonS3Client s3;
	
	public S3Helper() {}
	
	@SuppressWarnings("deprecation")
	public void connect() throws IOException {
		AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider("s3-desenvolvedores").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (C:\\Users\\Boaventura\\.aws\\credentials), and is in valid format.",
                    e);
        }
		s3 = (AmazonS3Client) AmazonS3ClientBuilder.standard()
	            .withCredentials(new AWSStaticCredentialsProvider(credentials))
	            .withRegion("sa-east-1")
	            .build();
	}
	
	public void listBuckets() {
		System.out.println("Listando buckets");
		for(Bucket bucket : s3.listBuckets()) {
			System.out.println(" - " + bucket.getName());
		}
		System.out.println();
	}
	
	public void listObjects(String bucketName) {
		System.out.println("Listando objetos: " + bucketName);
		ObjectListing obl = s3.listObjects(new ListObjectsRequest().withBucketName(bucketName));
		for(S3ObjectSummary obs : obl.getObjectSummaries()) {
			System.out.println(" - " + obs.getKey() + " " + "(tamanho = " + obs.getSize() + ")");
		}
	}
	
	public void createBucket(String bucketName) {
		s3.createBucket(bucketName);
	}
	
	public void deleteBucket(String bucketName) {
		s3.deleteBucket(bucketName);
	}
	
	public void deleteObject(String bucketName, String key) {
		s3.deleteObject(bucketName, key);
	}
	
	public void putFile(String bukcetName, String dir, String fileName, String contentType, byte[] bytes) throws IOException {
		String path = dir + "/" + fileName;
		
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(contentType);
		metadata.setContentLength(bytes.length);
		s3.putObject(bukcetName, path, new ByteArrayInputStream(bytes), metadata);
		s3.setObjectAcl(bukcetName, path, CannedAccessControlList.PublicRead);
	}
	
	public String getFile(String bukcetName, String key) throws IOException {
		StringBuffer sb = new StringBuffer();
		S3Object object = s3.getObject(new GetObjectRequest(bukcetName, key));
		S3ObjectInputStream input = object.getObjectContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		while (true) {
			String line = reader.readLine();
			if(line == null)
				break;
			sb.append(line);
		}
		return sb.toString();
	}
	
	public static void main(String[] args) throws IOException {
		S3Helper s3 = new S3Helper();
		s3.connect();
		s3.listBuckets();
		s3.listObjects("arthurangelocarvalho");
		//s3.createBucket("livros - " + UUID.randomUUID());
		s3.listBuckets();
		
		byte[] bytes = new String("Arthur").getBytes();
		s3.putFile("arthurangelocarvalho", "Teste", "nome.txt", "plain/text", bytes);
		s3.listObjects("arthurangelocarvalho");
		
		String s = s3.getFile("arthurangelocarvalho", "Teste/nome.txt");
		System.out.println(s);
		
		//s3.deleteBucket("livro-cloud-computing");
		//s3.listBuckets();
		
		
		
	}

}