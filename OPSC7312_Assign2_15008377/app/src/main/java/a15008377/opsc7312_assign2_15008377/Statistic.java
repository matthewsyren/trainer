package a15008377.opsc7312_assign2_15008377;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;
import android.widget.Toast;

import java.io.Serializable;

/**
 * Created by Matthew Syrén on 2017/09/30.
 */

public class Statistic implements Serializable{
    private String quizKey;
    private double result;

    public Statistic(String quizKey, double result) {
        this.quizKey = quizKey;
        this.result = result;
    }

    public Statistic(){

    }

    public String getQuizKey() {
        return quizKey;
    }

    public double getResult() {
        return result;
    }

    //Method calls the FirebaseService class and passes in a Statistic object that must be written to the Firebase Database
    public void requestWriteOfStatistic(Context context, ResultReceiver resultReceiver){
        try{
            //Requests location information from the LocationService class
            Intent intent = new Intent(context, FirebaseService.class);
            intent.setAction(FirebaseService.ACTION_WRITE_STATISTIC);
            intent.putExtra(FirebaseService.ACTION_WRITE_STATISTIC, this);
            intent.putExtra(FirebaseService.RECEIVER, resultReceiver);
            context.startService(intent);
        }
        catch(Exception exc){
            Toast.makeText(context, exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}