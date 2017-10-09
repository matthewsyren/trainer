/*
 * Author: Matthew Syrén
 *
 * Date:   10 October 2017
 *
 * Description: Class provides a basis for a User object
 */

package a15008377.opsc7312_assign2_15008377;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@SuppressWarnings("WeakerAccess")
public class User {
    //Declarations
    private String userFullName;
    private String userEmailAddress;
    private String userKey;
    private String userPassword;
    private boolean userAdminRights;

    //Constructor (used when the user is creating an account)
    public User(String userFullName, String userEmailAddress, String userPassword, boolean userAdminRights){
        this.userFullName = userFullName;
        this.userEmailAddress = userEmailAddress;
        this.userPassword = userPassword;
        this.userAdminRights = userAdminRights;
    }

    //Constructor (used when logging in)
    public User(String userEmailAddress, String userPassword) {
        this.userEmailAddress = userEmailAddress;
        this.userPassword = userPassword;
    }

    //Default constructor
    public User(){

    }

    //Default constructor (used when the user has already signed in, context is used to get the user's key from SharedPreferences)
    public User(Context context){
        //Fetches the user's email address and key from SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences("", Context.MODE_PRIVATE);
        userEmailAddress = preferences.getString("userEmail", null);
        userKey = preferences.getString("userKey", null);
        userAdminRights = preferences.getBoolean("userAdminRights", false);
    }

    //Getter methods
    public String getUserFullName() {
        return userFullName;
    }

    public String getUserEmailAddress() {
        return userEmailAddress;
    }

    public String getUserKey() {
        return userKey;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public boolean isUserAdminRights() {
        return userAdminRights;
    }

    //Setter method
    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    //Method gets the unique key used by Firebase to store information about the user signed in
    public void setKey(final LoginActivity context){
        //Gets reference to the Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference().child("Users");

        //Adds Listeners for when the data is changed
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Loops through all children of the Users key in the Firebase database, and fetches the correct key for the user that is signed in (using the user's email address)
                Iterable<DataSnapshot> lstSnapshots = dataSnapshot.getChildren();
                for(DataSnapshot snapshot : lstSnapshots){
                    String key = snapshot.getKey();
                    String email = (String) snapshot.child("userEmailAddress").getValue();
                    String adminRights = (String) snapshot.child("userAdminRights").getValue();

                    //Sets the user's key once the key has been located, and calls the method to log the user in
                    if(email.equals(userEmailAddress)){
                        userKey = key;
                        userAdminRights = Boolean.valueOf(adminRights);
                        context.writeDataToSharedPreferences(userEmailAddress, userKey, userAdminRights);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.i("Data", "Failed to read data");
            }
        });
    }

    //Requests Users from the Firebase Database
    public void requestUsers(String searchTerm, Context context, ResultReceiver resultReceiver){
        try{
            //Requests User information from the FirebaseService class
            Intent intent = new Intent(context, FirebaseService.class);
            intent.setAction(FirebaseService.ACTION_FETCH_USER);
            intent.putExtra(FirebaseService.USER_NAME, searchTerm);
            intent.putExtra(FirebaseService.RECEIVER, resultReceiver);
            context.startService(intent);
        }
        catch(Exception exc){
            Toast.makeText(context, exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
