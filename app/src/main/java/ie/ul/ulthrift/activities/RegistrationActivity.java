package ie.ul.ulthrift.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ie.ul.ulthrift.R;
import ie.ul.ulthrift.models.UserModel;

public class RegistrationActivity extends AppCompatActivity {
    EditText name, email, password;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        auth = FirebaseAuth.getInstance();
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        sharedPreferences = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean("firstTime", true);

        if (isFirstTime) {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putBoolean("firstTime", false);
            editor.commit();

            Intent intent = new Intent(RegistrationActivity.this, OnBoardingActivity.class);
            startActivity(intent);
            finish();
        }

    }

    public void signup(View view) {

        // Declares the name, email and password variables so we can put inputs to them
        String userName = name.getText().toString();
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        // Checks if email format is correct and ends with @studentmail.ul.ie
        if (!userEmail.endsWith("@studentmail.ul.ie")) {
            Toast.makeText(this, "Email must be a UL student email (are you sure you're a UL stundet?", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if username is empty
        if (TextUtils.isEmpty(userName)) {

            Toast.makeText(this, "Enter Name!", Toast.LENGTH_SHORT).show();
            return;

        }

        //Check if email is empty
        if (TextUtils.isEmpty(userEmail)) {

            Toast.makeText(this, "Enter Email Address!", Toast.LENGTH_SHORT).show();
            return;

        }

        //Check if password is empty
        if (TextUtils.isEmpty(userPassword)) {

            Toast.makeText(this, "Enter Password!", Toast.LENGTH_SHORT).show();
            return;

        }

        //Check if password too short
        if (userPassword.length() < 8) {
            Toast.makeText(this, "Password is too short, minimum 8 characters", Toast.LENGTH_SHORT).show();
        } else if (userPassword.length() > 20) {
            // Check if the password is too long
            Toast.makeText(this, "Password is too long, maximum 20 characters!", Toast.LENGTH_SHORT).show();
        } else {
            // Check if the password contains a capital letter and a special character
            Pattern pattern = Pattern.compile("^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\",.<>?]).*$");
            Matcher matcher = pattern.matcher(userPassword);

            //Error message for if password does not meet criteria
            if (!matcher.matches()) {
                Toast.makeText(this, "Password must contain a capital letter and a special character", Toast.LENGTH_SHORT).show();
            }

        }


        // Attempts to create a user account with Firebase Auth using the provided email and password
        auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // if the registration is successful, display that they registered, otherwise display it failed
                        if (task.isSuccessful()) {

                            // Get the UID of the newly registered user
                            String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            // Create a user object
                            final UserModel user = new UserModel(userName, userEmail, userPassword, userUid);

                            // Add the user to the users collection in the Firestore database
                            firestore.collection("users").document(userUid).set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(RegistrationActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegistrationActivity.this, "Registration Failed" + task.getException(), Toast.LENGTH_SHORT).show();

                                        }
                                    });

                        } else {
                            Toast.makeText(RegistrationActivity.this, "Registration Failed" + task.getException(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }


    // Redirects to LoginActivity when the user clicks the sign in button
    public void signin(View view) {
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
    }

}