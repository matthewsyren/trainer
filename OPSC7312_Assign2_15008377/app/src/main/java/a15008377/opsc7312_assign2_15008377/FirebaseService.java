/*
 * Author: Matthew Syrén
 *
 * Date:   29 August 2017
 *
 * Description: Class defines methods that the user can use to read/write data from/to the Firebase Database
 */

package a15008377.opsc7312_assign2_15008377;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class FirebaseService extends IntentService {
    //Declarations
    public static final String RECEIVER = "a15008377.opsc7312assign2_15008377.RECEIVER";
    public static final String QUIZ_ID = "a15008377.opsc7312assign2_15008377.QUIZ_ID";
    public static final String USER_KEY = "a15008377.opsc7312assign2_15008377.USER_KEY";


    //Action Declarations
    public static final String ACTION_FETCH_QUIZ =  "a15008377.opsc7312assign2_15008377.action.FETCH_QUIZ";
    public static final String ACTION_WRITE_QUIZ =  "a15008377.opsc7312assign2_15008377.action.WRITE_QUIZ";
    public static final String ACTION_WRITE_QUIZ_INFORMATION = "a15008377.opsc7312assign2_15008377.action.WRITE_QUIZ_INFORMATION";
    public static final String ACTION_FETCH_STATISTIC =  "a15008377.opsc7312assign2_15008377.action.FETCH_STATISTIC";
    public static final String ACTION_WRITE_STATISTIC =  "a15008377.opsc7312assign2_15008377.action.WRITE_STATISTIC";
    public static final String ACTION_FETCH_USER =  "a15008377.opsc7312assign2_15008377.action.FETCH_USER";

    //Result Codes and ResultReceiver
    public static final int ACTION_FETCH_QUIZ_RESULT_CODE = 1;
    public static final int ACTION_WRITE_QUIZ_RESULT_CODE = 2;
    public static final int ACTION_FETCH_STATISTIC_RESULT_CODE = 3;
    public static final int ACTION_WRITE_STATISTIC_RESULT_CODE = 4;
    public static final int ACTION_FETCH_USER_RESULT_CODE = 5;
    private ResultReceiver resultReceiver;

    //Constructor
    public FirebaseService() {
        super("FirebaseService");
    }

    //Method calls the appropriate method based on the action sent to this IntentService
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            //Gets data from the intent
            resultReceiver = intent.getParcelableExtra(RECEIVER);
            final String action = intent.getAction();

            //Calls the appropriate method based on the retrieved data
            if (action.equals(ACTION_FETCH_QUIZ)) {
                String quizID = intent.getStringExtra(QUIZ_ID);
                startActionFetchQuiz(quizID);
            }
            else if(action.equals(ACTION_WRITE_QUIZ)){
                Quiz quiz = (Quiz) intent.getSerializableExtra(ACTION_WRITE_QUIZ);
                String writeInformation = intent.getStringExtra(ACTION_WRITE_QUIZ_INFORMATION);
                startActionWriteQuiz(quiz, writeInformation);
            }
            else if(action.equals(ACTION_FETCH_STATISTIC)){
                String userKey = intent.getStringExtra(USER_KEY);
                startActionFetchStatistic(userKey);
            }
            else if(action.equals(ACTION_WRITE_STATISTIC)){
                Statistic statistic = (Statistic) intent.getSerializableExtra(ACTION_WRITE_STATISTIC);
                startActionWriteStatistic(statistic);
            }
            else if(action.equals(ACTION_FETCH_USER)){
                startActionFetchUser();
            }
        }
    }

    //Method fetches the Quiz data from the Firebase Database
    private void startActionFetchQuiz(final String searchTerm){
        //Gets reference to Firebase
        final ArrayList<Quiz> lstQuizzes = new ArrayList<>();
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference().child("Quizzes");

        //Adds Listeners for when the data is changed
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Loops through all Quizzes and adds them to the lstQuiz ArrayList
                Iterable<DataSnapshot> lstSnapshots = dataSnapshot.getChildren();
                for(DataSnapshot snapshot : lstSnapshots){
                    //Retrieves the Quiz from Firebase and adds the Quiz to an ArrayList of Quiz objects
                    Quiz quiz = snapshot.getValue(Quiz.class);
                    quiz.setKey(snapshot.getKey());
                    if(searchTerm == null || quiz.getName().contains(searchTerm)) {
                        lstQuizzes.add(quiz);
                    }
                }
                //Removes the EventListener
                databaseReference.removeEventListener(this);

                //Returns result
                returnFetchQuizResult(lstQuizzes);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.i("Data", "An error occurred while reading the data from Firebase");
            }
        });
    }

    //Method writes a Quiz object to the Firebase database
    private void startActionWriteQuiz(final Quiz quiz, final String writeInformation){
        //Gets reference to Firebase
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference().child("Quizzes");

        //Adds Listeners for when the data is changed
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String quizKey = databaseReference.push().getKey();
                databaseReference.child(quizKey).setValue(quiz);

                //Removes the EventListener
                databaseReference.removeEventListener(this);

                //Returns the result
                returnWriteQuizResult(true);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.i("Data", "An error occurred while writing the data to Firebase");
            }
        });
    }

    //Method fetches the Statistic data from the Firebase Database
    private void startActionFetchStatistic(final String userKey){
        //Gets reference to Firebase
        final ArrayList<Statistic> lstStatistics = new ArrayList<>();
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference().child("Results");

        //Adds Listeners for when the data is changed
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Loops through all Quizzes and adds them to the lstQuiz ArrayList
                Iterable<DataSnapshot> lstSnapshots = dataSnapshot.getChildren();
                for(DataSnapshot snapshot : lstSnapshots){
                    if(userKey == null || snapshot.getKey().equals(userKey)) {
                        for(DataSnapshot statisticSnapshot : snapshot.getChildren()){
                            //Retrieves the Statistic from Firebase and adds the Statistic to an ArrayList of Statistic objects
                            Statistic statistic = statisticSnapshot.getValue(Statistic.class);
                            statistic.setQuizKey(statisticSnapshot.getKey());
                            statistic.setUserKey(snapshot.getKey());
                            lstStatistics.add(statistic);
                        }
                    }
                }
                //Removes the EventListener
                databaseReference.removeEventListener(this);

                //Returns result
                returnFetchStatisticResult(lstStatistics);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.i("Data", "An error occurred while reading the data from Firebase");
            }
        });
    }

    //Method writes a Statistic object to the Firebase database
    private void startActionWriteStatistic(final Statistic statistic){
        //Gets reference to Firebase
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference().child("Results").child(new User(this).getUserKey());

        //Adds Listeners for when the data is changed
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String quizKey = statistic.getQuizKey();
                Statistic statisticNoKey = new Statistic(null, statistic.getResult());
                databaseReference.child(quizKey).setValue(statisticNoKey);

                //Removes the EventListener
                databaseReference.removeEventListener(this);

                //Returns the result
                returnWriteStatisticResult(true);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.i("Data", "An error occurred while writing the data to Firebase");
            }
        });
    }

    //Method fetches the User data from the Firebase Database
    private void startActionFetchUser(){
        //Gets reference to Firebase
        final ArrayList<User> lstUsers = new ArrayList<>();
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference().child("Users");

        //Adds Listeners for when the data is changed
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Loops through all Quizzes and adds them to the lstQuiz ArrayList
                Iterable<DataSnapshot> lstSnapshots = dataSnapshot.getChildren();
                for(DataSnapshot snapshot : lstSnapshots){
                    //Retrieves the Quiz from Firebase and adds the Quiz to an ArrayList of Quiz objects
                    String fullName = (String) snapshot.child("userFullName").getValue();
                    String adminRights = (String) snapshot.child("userAdminRights").getValue();
                    String emailAddress = (String) snapshot.child("userEmailAddress").getValue();
                    User user = new User(fullName, emailAddress, "", Boolean.valueOf(adminRights));
                    user.setUserKey(snapshot.getKey());
                    lstUsers.add(user);
                }
                //Removes the EventListener
                databaseReference.removeEventListener(this);

                //Returns result
                returnFetchUserResult(lstUsers);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.i("Data", "An error occurred while reading the data from Firebase");
            }
        });
    }

    //Returns the result of fetching Quiz data
    private void returnFetchQuizResult(ArrayList<Quiz> lstQuizzes){
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTION_FETCH_QUIZ, lstQuizzes);
        resultReceiver.send(ACTION_FETCH_QUIZ_RESULT_CODE, bundle);
    }

    //Returns the result of writing Quiz data
    private void returnWriteQuizResult(boolean success){
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTION_WRITE_QUIZ, success);
        resultReceiver.send(ACTION_WRITE_QUIZ_RESULT_CODE, bundle);
    }

    //Returns the result of fetching Statistic data
    private void returnFetchStatisticResult(ArrayList<Statistic> lstStatistics){
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTION_FETCH_STATISTIC, lstStatistics);
        resultReceiver.send(ACTION_FETCH_STATISTIC_RESULT_CODE, bundle);
    }

    //Returns the result of writing Statistic data
    private void returnWriteStatisticResult(boolean success){
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTION_WRITE_STATISTIC, success);
        resultReceiver.send(ACTION_WRITE_STATISTIC_RESULT_CODE, bundle);
    }

    //Returns the result of fetching User data
    private void returnFetchUserResult(ArrayList<User> lstUsers){
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTION_FETCH_USER, lstUsers);
        resultReceiver.send(ACTION_FETCH_USER_RESULT_CODE, bundle);
    }
}