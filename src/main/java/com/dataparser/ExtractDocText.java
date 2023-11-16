package com.dataparser;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;
import java.io.FileNotFoundException;

public class ExtractDocText {
  public static void main(String[] args) throws IOException {
// Before using this setup AWS credentials, for more information go to https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html


// change "pathfile" below with  your local file/image path"
      String docPath = "C:\\Users\\mehul\\Downloads\\proofs.zip";
//      String docPath = "C:\\Users\\mehul\\Downloads\\delhi_bill.jpg";
    Region region = Region.US_EAST_1; // Set Region as per your credential
    TextractClient textractClient = TextractClient.builder()
        .region(region)
        .credentialsProvider(ProfileCredentialsProvider.create())
        .build();
        
// for zip of proofs of a particular ObjectType
    ZipFile zipFile=new ZipFile(docPath);
    Enumeration<? extends ZipEntry> entries = zipFile.entries();

    // Upload each file in the ZIP file in parallel
    while (entries.hasMoreElements()) {
      ZipEntry entry = entries.nextElement();
      // Skip directories
      if (entry.isDirectory()) {
        continue;
      }

      // Create an input stream for the file
      InputStream inputStream = zipFile.getInputStream(entry);

      Map<String,String>res=new FormExtract().analyzeDocument(textractClient, inputStream);
      System.out.println(new ElectricityParser().getRequiredFields(res,"delhi"));

      // Close the input stream
//      inputStream.close();
    }

//    Map<String,String> res=new FormExtract().analyzeDocument(textractClient, docPath);
////    System.out.println(res);
//    System.out.println(new ElectricityParser().getRequiredFields(res,"delhi"));
//    double d=Double.parseDouble("");
//    System.out.println(d);
    textractClient.close();
  }



}
