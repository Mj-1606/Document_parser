package com.dataparser;

import static java.lang.Double.parseDouble;

import com.amazonaws.services.textract.model.AnalyzeDocumentResult;
import com.amazonaws.services.textract.model.DocumentLocation;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.AnalyzeDocumentRequest;
import software.amazon.awssdk.services.textract.model.AnalyzeDocumentResponse;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.Document;
import software.amazon.awssdk.services.textract.model.EntityType;
import software.amazon.awssdk.services.textract.model.FeatureType;
import software.amazon.awssdk.services.textract.model.Relationship;
import software.amazon.awssdk.services.textract.model.StartDocumentAnalysisRequest;
import software.amazon.awssdk.services.textract.model.TextractException;

public class FormExtract {

  String consumerNumber;
  String billNumber;
  String invoiceDate;
  String previousReading;
  String currentReading;
  double unitsConsumed=-1;
  String billAmount;

  public Map<String,String> analyzeDocument(TextractClient textractClient, String sourceDoc)
      throws FileNotFoundException {

    try {
      InputStream sourceStream = new FileInputStream(sourceDoc);
      SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);

      // Get the input Document object as bytes
      Document myDoc = Document.builder()
          .bytes(sourceBytes)
          .build();
          
      // Creating request with custom feautures like FeatureType.FORMS or FeautureTYPES.TABLES or pass both in form of list
      AnalyzeDocumentRequest req = AnalyzeDocumentRequest.builder()
          .featureTypes(FeatureType.FORMS)
          .document(myDoc)
          .build();
//       Getting response in JSON format in which elements are in form of blocks
      AnalyzeDocumentResponse res = textractClient.analyzeDocument(
          req);
//      DocumentLocation doc= DocumentLocation.builder().build();
//      StartDocumentAnalysisRequest req=StartDocumentAnalysisRequest.builder()
//          .featureTypes(FeatureType.FORMS)
//          .documentLocation(new DocumentLocation().)
//          .build();
      // storing all blocks in list
      List<Block> blocks = res.blocks();
      
      
      Map<String, Block> blockMap = new LinkedHashMap<>();
      Map<String, Block> keyMap = new LinkedHashMap<>();
      Map<String, Block> valueMap = new LinkedHashMap<>();
      
      // Iterating over each blocks and adding required blocks(i.e., KEY_VALUE_SET) to respective list with their block id.  
      for (Block b : blocks) {
        String block_id = b.id();
        blockMap.put(block_id, b);
        if (b.blockTypeAsString().equals("KEY_VALUE_SET")) {
          for (EntityType entityType : b.entityTypes()) {
            if (entityType.toString().equals("KEY")) {
              keyMap.put(block_id, b);
            } else {
              valueMap.put(block_id, b);
            }
          }
        }
      }
      
      // Merging keyMap<key,block> and valueMap<value,block> into one map<key,value>
      Map<String, String> ans = getRelationships(blockMap, keyMap, valueMap);
      
      // If you want to see all key value pair directly print(ans)
      // Filtering required data
//      for (Map.Entry<String, String> entry : ans.entrySet()) {
//        String s = entry.getKey();
//        s = s.replaceAll("[^a-zA-z]", "");  // Removing all character and spaces other than a-z/A-Z and
//        s = s.toLowerCase();                // converting each string to lowercase so that it will become CASE-INSENSITIVE
//        if (s.equals("billdate")) {          //and then storing them in variables.
//          invoiceDate = entry.getValue();
//        }
//        if (s.equals("currentreading")) {
//          currentReading = entry.getValue();
//        }
//        if (s.equals("previousreading")) {
//          previousReading = entry.getValue();
//        }if (s.equals("totalunit") || s.equals("billedunits")|| s.equals("totalbilledunits")|| s.equals("totalunits")||
//            s.equals("billedunit")||s.equals("totalbilledunit")) {
//          if(entry.getValue()!=null) unitsConsumed = parseDouble(entry.getValue());
//        }
//        if (s.equals("billno") || s.equals("billnumber")) {
//          billNumber=entry.getValue();
//        }
//        if( s.equals("consumerno") ||  s.equals("consumernumber") || s.equals("consumerid")||
//            s.equals("customerno")|| s.equals("customerid")|| s.equals("customernumber")){
//          consumerNumber = entry.getValue();
//        }
//        if(s.equals("address")){
//
//        }
//      }
//      if( (currentReading!=null&&previousReading!=null )|| unitsConsumed==-1)
//      unitsConsumed = parseDouble(currentReading) - parseDouble(previousReading);
//      System.out.println(ans);
//      System.out.println("total units :"+unitsConsumed);
//      System.out.println("Invoice Date : "+invoiceDate);
//      System.out.println("consumer no: "+consumerNumber);
//      System.out.println("Bill no: "+billNumber);
//      System.out.println("The number of pages in the document is " + res.documentMetadata().pages());
        return ans;
    } catch (TextractException | FileNotFoundException e) {

      System.err.println(e.getMessage());
      return null;
    }
  } public Map<String,String> analyzeDocument(TextractClient textractClient, InputStream sourceStream)
      throws FileNotFoundException {

    try {
//      InputStream sourceStream = new FileInputStream(sourceDoc);
      SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);

      // Get the input Document object as bytes
      Document myDoc = Document.builder()
          .bytes(sourceBytes)
          .build();

      // Creating request with custom feautures like FeatureType.FORMS or FeautureTYPES.TABLES or pass both in form of list
      AnalyzeDocumentRequest req = AnalyzeDocumentRequest.builder()
          .featureTypes(FeatureType.FORMS)
          .document(myDoc)
          .build();
//       Getting response in JSON format in which elements are in form of blocks
      AnalyzeDocumentResponse res = textractClient.analyzeDocument(
          req);
//      DocumentLocation doc= DocumentLocation.builder().build();
//      StartDocumentAnalysisRequest req=StartDocumentAnalysisRequest.builder()
//          .featureTypes(FeatureType.FORMS)
//          .documentLocation(new DocumentLocation().)
//          .build();
      // storing all blocks in list
      List<Block> blocks = res.blocks();


      Map<String, Block> blockMap = new LinkedHashMap<>();
      Map<String, Block> keyMap = new LinkedHashMap<>();
      Map<String, Block> valueMap = new LinkedHashMap<>();

      // Iterating over each blocks and adding required blocks(i.e., KEY_VALUE_SET) to respective list with their block id.
      for (Block b : blocks) {
        String block_id = b.id();
        blockMap.put(block_id, b);
        if (b.blockTypeAsString().equals("KEY_VALUE_SET")) {
          for (EntityType entityType : b.entityTypes()) {
            if (entityType.toString().equals("KEY")) {
              keyMap.put(block_id, b);
            } else {
              valueMap.put(block_id, b);
            }
          }
        }
      }

      // Merging keyMap<key,block> and valueMap<value,block> into one map<key,value>
      Map<String, String> ans = getRelationships(blockMap, keyMap, valueMap);

      // If you want to see all key value pair directly print(ans)
      // Filtering required data
//      for (Map.Entry<String, String> entry : ans.entrySet()) {
//        String s = entry.getKey();
//        s = s.replaceAll("[^a-zA-z]", "");  // Removing all character and spaces other than a-z/A-Z and
//        s = s.toLowerCase();                // converting each string to lowercase so that it will become CASE-INSENSITIVE
//        if (s.equals("billdate")) {          //and then storing them in variables.
//          invoiceDate = entry.getValue();
//        }
//        if (s.equals("currentreading")) {
//          currentReading = entry.getValue();
//        }
//        if (s.equals("previousreading")) {
//          previousReading = entry.getValue();
//        }if (s.equals("totalunit") || s.equals("billedunits")|| s.equals("totalbilledunits")|| s.equals("totalunits")||
//            s.equals("billedunit")||s.equals("totalbilledunit")) {
//          if(entry.getValue()!=null) unitsConsumed = parseDouble(entry.getValue());
//        }
//        if (s.equals("billno") || s.equals("billnumber")) {
//          billNumber=entry.getValue();
//        }
//        if( s.equals("consumerno") ||  s.equals("consumernumber") || s.equals("consumerid")||
//            s.equals("customerno")|| s.equals("customerid")|| s.equals("customernumber")){
//          consumerNumber = entry.getValue();
//        }
//        if(s.equals("address")){
//
//        }
//      }
//      if( (currentReading!=null&&previousReading!=null )|| unitsConsumed==-1)
//      unitsConsumed = parseDouble(currentReading) - parseDouble(previousReading);
//      System.out.println(ans);
//      System.out.println("total units :"+unitsConsumed);
//      System.out.println("Invoice Date : "+invoiceDate);
//      System.out.println("consumer no: "+consumerNumber);
//      System.out.println("Bill no: "+billNumber);
//      System.out.println("The number of pages in the document is " + res.documentMetadata().pages());
        return ans;
    } catch (TextractException e) {

      System.err.println(e.getMessage());
      return null;
    }
  }

  public static Map<String, String> getRelationships(Map<String, Block> blockMap,
      Map<String, Block> keyMap, Map<String, Block> valueMap) {
    Map<String, String> result = new LinkedHashMap<>();
    for (Map.Entry<String, Block> itr : keyMap.entrySet()) {
      Block valueBlock = findValue(itr.getValue(), valueMap);
      String key = getText(itr.getValue(), blockMap);
      String value = getText(valueBlock, blockMap);
      result.put(key, value);
    }
    return result;
  }

  public static Block findValue(Block keyBlock, Map<String, Block> valueMap) {
    Block b = null;
    for (Relationship relationship : keyBlock.relationships()) {
      if (relationship.type().toString().equals("VALUE")) {
        for (String id : relationship.ids()) {
          b = valueMap.get(id);
        }
      }
    }
    return b;
  }

  public static String getText(Block result, Map<String, Block> blockMap) {
    StringBuilder stringBuilder = new StringBuilder();
    for (Relationship relationship : result.relationships()) {
      if (relationship.type().toString().equals("CHILD")) {
        for (String id : relationship.ids()) {
          Block b = blockMap.get(id);
          if (b.blockTypeAsString().equals("WORD")) {
            stringBuilder.append(b.text()).append(" ");
          }
        }
      }
    }
    return stringBuilder.toString();
  }

}
