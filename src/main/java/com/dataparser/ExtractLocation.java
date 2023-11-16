package com.dataparser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextRequest;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextResponse;
import software.amazon.awssdk.services.textract.model.Document;

public class ExtractLocation {
String state=null;
  public String getState(TextractClient textractClient, String sourceDoc) {
    try {
      InputStream sourceStream = new FileInputStream(sourceDoc);
      SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);

      // Get the input Document object as bytes
      Document myDoc = Document.builder()
          .bytes(sourceBytes)
          .build();
      long t1 = System.currentTimeMillis();
      DetectDocumentTextRequest req= DetectDocumentTextRequest.builder()
          .document(myDoc)
          .build();
      DetectDocumentTextResponse res=textractClient.detectDocumentText(req);
      List<Block> blocks= res.blocks();
      for(Block b:blocks){
        if(b.blockTypeAsString().equals("WORD")){
          String s=b.text();
          s = s.replaceAll("[^a-zA-z]", "");  // Removing all character and spaces other than a-z/A-Z and
          s = s.toLowerCase();
//          System.out.println(s);
          if(s.equals("maharashtra")){
            state=s;
            System.out.println(System.currentTimeMillis()-t1);
            break;
          }
        };
      }
//      System.out.println(res);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    return state;
  }

}
