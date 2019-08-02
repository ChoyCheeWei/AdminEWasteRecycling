package com.example.adminewasterecycling;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;

public class Login extends AppCompatActivity {

    private static final int REQUEST_SIGNUP = 0;
    private static final String PREFS_NAME = "PreshFile";
    EditText emailText, passwordText;
    Button loginButton;
    TextView forget;
    private FirebaseAuth mAuth;
    private CheckBox saveLoginCheckBox;
    private SharedPreferences sharedPreferences;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);
        emailText = (EditText) findViewById(R.id.input_email);
        passwordText = (EditText) findViewById(R.id.input_password);
        loginButton = (Button) findViewById(R.id.btn_login);

        forget = (TextView) findViewById(R.id.forget_pass);

        saveLoginCheckBox = (CheckBox) findViewById(R.id.saveLoginCheckBox);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        getPreferencesData();

        mAuth = FirebaseAuth.getInstance();

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgetPassword();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loginUser(v);
            }
        });

    }

    private void getPreferencesData() {

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (sharedPreferences.contains("pref_email")) {
            String email = sharedPreferences.getString("pref_email", "not found");
            emailText.setText(email.toString());
        }
        if (sharedPreferences.contains("pref_pass")) {
            String pass = sharedPreferences.getString("pref_pass", "not found");
            passwordText.setText(pass.toString());
        }
        if (sharedPreferences.contains("pref_checked")) {
            Boolean boo = sharedPreferences.getBoolean("pref_checked", false);
            saveLoginCheckBox.setChecked(boo);
        }
    }

    public void loginUser(View v) {
        final String Email = emailText.getText().toString();
        final String password = passwordText.getText().toString();

        relativeLayout.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(Email)) {
            emailText.setError("Email is required");
            relativeLayout.setVisibility(View.INVISIBLE);
        }
        if (TextUtils.isEmpty(password)) {
            passwordText.setError("password is required");
            relativeLayout.setVisibility(View.INVISIBLE);
        }
        if ((!TextUtils.isEmpty(Email)) && (!TextUtils.isEmpty(password))) {
            mAuth.signInWithEmailAndPassword(Email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                relativeLayout.setVisibility(View.INVISIBLE);
                                Toasty.error(getApplicationContext(), "Login Failed",
                                        Toast.LENGTH_SHORT, true).show();
                            } else {
                                relativeLayout.setVisibility(View.INVISIBLE);
                                Toasty.success(getApplicationContext(), "Login Success",
                                        Toast.LENGTH_SHORT, true).show();
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.putExtra("Email", emailText.getText().toString());
                                startActivity(intent);


                                if (saveLoginCheckBox.isChecked()) {
                                    Boolean boo = saveLoginCheckBox.isChecked();
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("pref_email", emailText.getText().toString());
                                    editor.putString("pref_pass", passwordText.getText().toString());
                                    editor.putBoolean("pref_checked", boo);
                                    editor.apply();

                                } else {
                                    sharedPreferences.edit().clear().apply();
                                }

                            }

                        }
                    });
        }
    }

    public void ForgetPassword() {

        final String Email = emailText.getText().toString();

        if (TextUtils.isEmpty(Email)) {
            Toasty.warning(getApplicationContext(), "Please enter your email address for reset password", Toast.LENGTH_SHORT).show();
        }
        if (!TextUtils.isEmpty(Email)) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(Email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toasty.info(getApplicationContext(), "Kindly Check Your Email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

}

