package ie.ul.ulthrift;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {
    EditText name, email, password;
    private FirebaseAuth auth;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        auth = FirebaseAuth.getInstance();
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        sharedPreferences = getSharedPreferences("onBoardingScreen",MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean("firstTime",true);

        if(isFirstTime){
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putBoolean("firstTime",false);
            editor.commit();

            Intent intent = new Intent(RegistrationActivity.this, OnBoardingActivity.class);
            startActivity(intent);
            finish();
        }

    }

    public void signup(View view) {

        String userName = name.getText().toString();
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        //Check if username is empty
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

            if (!matcher.matches()) {
                Toast.makeText(this, "Password must contain a capital letter and a special character", Toast.LENGTH_SHORT).show();
            }

        }

        auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if (task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Registration Failed"+task.getException(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }


        public void signin (View view){
            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
        }

}