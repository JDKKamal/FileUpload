package com.jdkgroup.fileupload;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kamlesh on 7/27/2017.
 */

public class ChunkFileUpload {

    public static void main(String args[]) {
        String text = "Lakhani Kamlesh";
        //Store the temp list
        List<String> tempimg = new ArrayList<>();

        //Sub String Data
        int index = 0;
        while (index < text.length()) {
            tempimg.add(text.substring(index, Math.min(index + 3, text.length())));
            index += 3;
        }

        if (text.length() > 3) //Segment size
        {
            int lastsize = tempimg.size() - 1;
            for (int i = 0; i < tempimg.size(); i++) {
                if (i == 0) {
                    System.out.println("add String :: " + tempimg.get(i));
                } else if (lastsize == i) {
                    System.out.println("finish data :: " + tempimg.get(i));
                } else {
                    System.out.println("call every time :: " + tempimg.get(i));
                }
            }
        } else {
            System.out.println("call first to finish");
        }
    }
}
