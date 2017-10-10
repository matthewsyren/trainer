/*
 * Author: Matthew Syrén
 *
 * Date:   10 October 2017
 *
 * Description: Class allows the user to create an account
 */

package a15008377.opsc7312_assign2_15008377;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_account);

            //Hides ProgressBar
            toggleProgressBarVisibility(View.INVISIBLE);

            //Gets an instance of FirebaseAuth, which is used to create the account
            firebaseAuth = FirebaseAuth.getInstance();
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method toggles the ProgressBar's visibility
    public void toggleProgressBarVisibility(int visibility){
        try{
            //Toggles ProgressBar visibility
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar) ;
            progressBar.setVisibility(visibility);
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method creates an account for the user
    public void createAccountOnClick(View view){
        try{
            //View assignments
            EditText txtFullName = (EditText) findViewById(R.id.text_create_account_full_name);
            EditText txtEmail = (EditText) findViewById(R.id.text_create_account_email);
            EditText txtPassword = (EditText) findViewById(R.id.text_create_account_password);
            EditText txtConfirmPassword = (EditText) findViewById(R.id.text_create_account_confirm_password);
            CheckBox chkAdminRights = (CheckBox) findViewById(R.id.check_box_admin_rights);

            //Fetches the contents of the Views
            String fullName = txtFullName.getText().toString();
            String email = txtEmail.getText().toString();
            String password = txtPassword.getText().toString();
            String confirmPassword = txtConfirmPassword.getText().toString();
            boolean adminRights = chkAdminRights.isChecked();

            //Displays ProgressBar
            toggleProgressBarVisibility(View.VISIBLE);

            //Creates an account if the user's passwords match and they have entered valid data
            final User user = new User(fullName, email, password, adminRights);

            if(user.validateUser(this)){
                if(password.equals(confirmPassword)){
                    firebaseAuth.createUserWithEmailAndPassword(user.getUserEmailAddress(), user.getUserPassword()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                String exception = task.getException().getMessage();
                                if(exception.contains("WEAK_PASSWORD")){
                                    //Displays message telling the user to choose a stronger password
                                    Toast.makeText(getApplicationContext(), "The password you have entered is too weak, please enter a password with at least 6 characters", Toast.LENGTH_LONG).show();
                                }
                                else if(exception.contains("The email address is already in use by another account.")){
                                    //Displays a message telling the user to choose another email address, as their desired email has already been used
                                    Toast.makeText(getApplicationContext(), "The email address you have entered has already been used for this app, please enter another email address", Toast.LENGTH_LONG).show();
                                }
                                else if(exception.contains("The email address is badly formatted.")){
                                    //Displays a message telling the user to enter a valid email address
                                    Toast.makeText(getApplicationContext(), "The email address you have entered is invalid, please enter a valid email address e.g. yourname@example.com", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(CreateAccountActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                                //Hides ProgressBar
                                toggleProgressBarVisibility(View.INVISIBLE);
                            }
                            else if(task.isSuccessful()){
                                //Registers the user in the Firebase authentication for this app
                                pushUser(user.getUserFullName(), user.getUserEmailAddress(), user.isUserAdminRights());
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please ensure your passwords match", Toast.LENGTH_LONG).show();

                    //Hides ProgressBar
                    toggleProgressBarVisibility(View.INVISIBLE);
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Please enter a password that is at least 6 characters long", Toast.LENGTH_LONG).show();

                //Hides ProgressBar
                toggleProgressBarVisibility(View.INVISIBLE);
            }
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method generates a unique key for the created user, and writes the key and its value (the user's email) to the 'Users' child node in the Firebase database
    public void pushUser(String fullName, String emailAddress, boolean adminRights){
        try{
            //Establishes a connection to the Firebase Database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database.getReference().child("Users");

            //Generates the user's unique key and saves the value (the user's email address, full name and whether they have admin rights) to the Firebase Database
            String key =  databaseReference.push().getKey();
            Map <String,String> hashMap = new HashMap<>();
            hashMap.put("userFullName", fullName);
            hashMap.put("userEmailAddress", emailAddress);
            hashMap.put("userAdminRights", String.valueOf(adminRights));
            databaseReference.child(key).setValue(hashMap);

            //Saves the user's email, key and admin rights to the device's SharedPreferences, and displays a message to the user
            SharedPreferences preferences = getSharedPreferences("", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("userEmail", emailAddress);
            editor.putString("userKey", key);
            editor.putBoolean("userAdminRights", adminRights);
            editor.apply();
            Toast.makeText(getApplicationContext(), "Account successfully created!", Toast.LENGTH_LONG).show();

            //Takes the user to the appropriate activity based on whether they have admin rights or not
            Intent intent;
            if(adminRights){
                intent = new Intent(CreateAccountActivity.this, QuizManagerActivity.class);
            }
            else{
                intent = new Intent(CreateAccountActivity.this, QuizFetcherActivity.class);
            }
            startActivity(intent);
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}