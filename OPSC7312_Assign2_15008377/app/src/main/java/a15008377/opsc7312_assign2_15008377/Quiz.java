package a15008377.opsc7312_assign2_15008377;

import java.util.ArrayList;

/**
 * Created by Matthew Syrén on 2017/09/16.
 */

public class Quiz {
    //Declarations
    private String name;
    private ArrayList<Question> lstQuestions;

    public Quiz(){

    }

    public Quiz(String name, ArrayList<Question> lstQuestions) {
        this.name = name;
        this.lstQuestions = lstQuestions;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Question> getLstQuestions() {
        return lstQuestions;
    }
}
