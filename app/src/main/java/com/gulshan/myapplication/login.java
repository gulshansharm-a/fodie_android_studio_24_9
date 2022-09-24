package com.gulshan.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class login extends AppCompatActivity {
FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.buttontosubmit).setVisibility(View.INVISIBLE);

        EditText etnumber =  findViewById(R.id.editText);
        getSupportActionBar().hide();
        if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
            Intent i = new Intent(login.this,HomeActivity.class);
            startActivity(i);
            return;
        }

            PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+918899111508")       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                                super.onCodeAutoRetrievalTimeOut(s);
                                Toast.makeText(login.this, s, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(String verificationId,
                                                   PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                findViewById(R.id.buttontosubmit).setVisibility(View.VISIBLE);
                                findViewById(R.id.buttontosubmit).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                      EditText et =  findViewById(R.id.editText);

                                      String otp = et.getText().toString();
                                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);

                                        signInWithPhoneAuthCredential(credential);
                                    }
                                });

                                // The corresponding whitelisted code above should be used to complete sign-in.
                                login.this.enableUserManuallyInputCode();
                            }

                            @Override
                            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                                // Sign in with the credential
                                // ...
                            }

                            @Override
                            public void onVerificationFailed(FirebaseException e) {
                                // ...
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private void enableUserManuallyInputCode() {

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = task.getResult().getUser();
                            FirebaseDatabase fDB = FirebaseDatabase.getInstance();
                            DatabaseReference UserDBRef = fDB.getReference().child("users");

                            UserDBRef.child(FirebaseAuth.getInstance().getUid().toString())
                                    .child("number")
                                    .setValue(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

                            Intent i = new Intent(login.this,HomeActivity.class);
                            startActivity(i);

                        } else {
                            // Sign in failed, display a message and update the UI
                             if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
}