package com.dataparser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.AnalyzeExpenseRequest;
import software.amazon.awssdk.services.textract.model.AnalyzeExpenseResponse;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.Document;
import software.amazon.awssdk.services.textract.model.ExpenseDocument;
import software.amazon.awssdk.services.textract.model.ExpenseField;
import software.amazon.awssdk.services.textract.model.LineItemFields;
import software.amazon.awssdk.services.textract.model.LineItemGroup;
import software.amazon.awssdk.services.textract.model.Relationship;

public class InvoiceExtract {

  public static void analyzeInvoice(TextractClient textractClient, String sourceDoc)
      throws FileNotFoundException {
    InputStream sourceStream = new FileInputStream(sourceDoc);
    SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);

    // Get the input Document object as bytes
    Document myDoc = Document.builder()
        .bytes(sourceBytes)
        .build();
    AnalyzeExpenseRequest req = AnalyzeExpenseRequest.builder()
        .document(myDoc)
        .build();

    // Invoke the Detect operation
    AnalyzeExpenseResponse res = textractClient.analyzeExpense(
        req);
//    System.out.println(res);
    List<ExpenseDocument> ans = res.expenseDocuments();
    for (ExpenseDocument e : ans) {
      DisplayAnalyzeExpenseLineItemGroupsInfo(e);
      if (e.hasLineItemGroups()) {

        List<LineItemGroup> lineitemgroups = e.lineItemGroups();
        extract_lineitems(lineitemgroups);
      }
    }
//    System.out.println(res);
//    ExpenseDocument output= res.expenseDocuments().get(0);
//    expenseDocuments();
//    System.out.println(output);
  }

  private static void DisplayAnalyzeExpenseLineItemGroupsInfo(ExpenseDocument expensedocument) {

//    System.out.println("	ExpenseId : " + expensedocument.expenseIndex());
//    System.out.println("    Expense LineItemGroups information:");

    if (expensedocument.hasLineItemGroups()) {

      List<LineItemGroup> lineitemgroups = expensedocument.lineItemGroups();

      for (LineItemGroup lineitemgroup : lineitemgroups) {

        System.out.println(
           + lineitemgroup.lineItemGroupIndex());

        if (lineitemgroup.hasLineItems()) {

          List<LineItemFields> lineItems = lineitemgroup.lineItems();

          for (LineItemFields lineitemfield : lineItems) {

            if (lineitemfield.hasLineItemExpenseFields()) {

              List<ExpenseField> expensefields = lineitemfield.lineItemExpenseFields();
              for (ExpenseField expensefield : expensefields) {

//                if (expensefield.type() != null) {
//                  System.out.println(
//                       expensefield.type().text());
//
//                }

                if (expensefield.valueDetection() != null) {
                  System.out.println(
                      expensefield.valueDetection().text());

                }

                if (expensefield.labelDetection() != null) {
                  System.out.println(
                       expensefield.labelDetection().text());

                }

              }
            }

          }

        }
      }
    }

  }
  public static void extract_lineitems(List<LineItemGroup> lineitemgroups) {
    List<String> items = new ArrayList<>();
    List<String> price = new ArrayList<>();
    List<String> row = new ArrayList<>();
    List<String> qty = new ArrayList<>();
    String t_items = null;
    String t_price = null;
    String t_row = null;
    String t_qty = null;
    for (LineItemGroup lines : lineitemgroups) {
      for (LineItemFields item : lines.lineItems()) {
        for (ExpenseField line : item.lineItemExpenseFields()) {
          if (line.type().text().equals("ITEM")) {
            t_items = line.valueDetection().text();
          }
          if (line.type().text().equals("PRICE")) {
            t_price = line.valueDetection().text();
          }
          if (line.type().text().equals("QUANTITY")) {
            t_qty = line.valueDetection().text();
          }
          if (line.type().text().equals("EXPENSE_ROW")) {
            t_row = line.valueDetection().text();
          }
        }
        if (t_items != null) {
          items.add(t_items);
        } else {
          items.add("");
        }
        if (t_price != null) {
          price.add(t_price);
        } else {
          price.add("");
        }
        if (t_row != null) {
          row.add(t_row);
        } else {
          row.add("");
        }
        if (t_qty != null) {
          qty.add(t_qty);
        } else {
          qty.add("");
        }
        t_items = null;
        t_price = null;
        t_row = null;
        t_qty = null;
      }
    }
    Iterator<String> a= items.iterator();
    Iterator<String> b= price.iterator();
    Iterator<String> c= row.iterator();
    Iterator<String> d= qty.iterator();
    while(b.hasNext()){
      System.out.println(a.next()+" ");
//      +b.next()+" "+c.next()+" "+d.next());
    }
  }

  public static void extract_kv(List<ExpenseField> summaryfields) {
    List<String> field_type = new ArrayList<>();
    List<String> label = new ArrayList<>();
    List<String> value = new ArrayList<>();
    for (ExpenseField item : summaryfields) {
      try {
        field_type.add(item.type().text());
      } catch (Exception e) {
        field_type.add("");
      }
      try {
        label.add(item.labelDetection().text());
      } catch (Exception e) {
        label.add("");
      }
      try {
        value.add(item.valueDetection().text());
      } catch (Exception e) {
        value.add("");
      }
    }
  }

}
