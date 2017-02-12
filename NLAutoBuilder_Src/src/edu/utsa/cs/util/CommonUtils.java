package edu.utsa.cs.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class CommonUtils {
    public static List<String> toList(Enumeration<String> enu){
        List<String> list = new ArrayList<String>();
        while(enu.hasMoreElements()){
            list.add(enu.nextElement());
        }
        return list;
    }

}
