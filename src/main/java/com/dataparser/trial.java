package com.dataparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class trial {
  static void compare() {
//    if(("Bill Date").toLowerCase()==("Bill date").toLowerCase()){
//      System.out.println("same");
//    }
    String s = "Bill Date :";
    s = s.replaceAll("[^a-zA-z]", "");
    System.out.println(s);
    s=s.toLowerCase();
    System.out.println(s);
    String s2 = "billdate";
    if (s.equals(s2)) {
      System.out.println("same");
    } else {
      System.out.println("not same");
    }
  }

}
