package com.dataparser;

import static java.lang.Double.parseDouble;

import java.util.HashMap;
import java.util.Map;

public class ElectricityParser {

  Map<String, String> parsedFields = null;
  String amount = null;
  String billDate = null;
  String billNo = null;
  String consumerNo = null;
  String currentUnit = null;
  String previousUnit = null;

  public Map<String, String> getRequiredFields(Map<String, String> res, String state) {
    for (Map.Entry<String, String> entry : res.entrySet()) {
      String s = entry.getKey();
      s = s.replaceAll("[^a-zA-z]", "");  // Removing all character and spaces other than a-z/A-Z/0-9 and
      s = s.toLowerCase();                // converting each string to lowercase so that it will become CASE-INSENSITIVE
      if (billDate == null && s.equals("billdate")) {          //and then storing them in variables.
        billDate = entry.getValue();
      }
      if (currentUnit == null && s.equals("currentreading")) {
        currentUnit = entry.getValue();
      }
      if (previousUnit == null && s.equals("previousreading")) {
        previousUnit = entry.getValue();
      }
      if (amount == null && (s.equals("totalunit") || s.equals("billedunits") || s.equals(
          "totalbilledunits") || s.equals("totalunits") ||
          s.equals("billedunit") || s.equals("totalbilledunit"))) {
        amount = entry.getValue();
      }
      if (billNo == null && (s.equals("billno") || s.equals("billnumber"))) {
        billNo = entry.getValue();
      }
      if (consumerNo == null && (s.equals("consumerno") || s.equals("consumernumber") || s.equals(
          "consumerid") ||
          s.equals("customerno") || s.equals("customerid") || s.equals("customernumber"))) {
        consumerNo = entry.getValue();
      }
    }
    if (currentUnit != null && previousUnit != null && amount == null) {
      amount = String.valueOf(parseDouble(currentUnit) - parseDouble(previousUnit));
    }
    return parseByState(state);

  }

  public Map<String, String> parseByState(String state) {
    parsedFields = new HashMap<>();
    parsedFields.put("billDate", billDate);
    parsedFields.put("amount", amount);
    parsedFields.put("currentUnit", currentUnit);
    parsedFields.put("previousUnit", previousUnit);
    if (state.equals("maharashtra")) {
      parsedFields.put("proof", consumerNo);
    } else if (state.equals("madhyapradesh")) {
      parsedFields.put("proof", billNo);
    } else {
      parsedFields.put("proof", consumerNo != null ? consumerNo : billNo);
    }
    return parsedFields;
  }

}

