package com.arduino.mando_brazo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    EditText Email, Password;
    Button Acceder;
    FirebaseAuth Auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        verificarSesion();
    }

    private void initView() {
        Email = findViewById(R.id.editTextEmail);
        Password = findViewById(R.id.editTextPassword);
        Acceder = findViewById(R.id.loginBtn);

        Acceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceder();
            }
        });
    }

    private void acceder() {
        String txtEmail = Email.getText().toString().trim();
        String txtPassword = Password.getText().toString().trim();

        if (TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)) {
            Toast.makeText(this, "Complete los datos", Toast.LENGTH_SHORT).show();
            return;
        }
        Auth.signInWithEmailAndPassword(txtEmail, txtPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(MainActivity.this, ControllerActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Error al acceder", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void verificarSesion() {
        FirebaseAuth.AuthStateListener AuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //Sesion activa
                    Intent intent = new Intent(MainActivity.this, ControllerActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        Auth.addAuthStateListener(AuthListener);
    }
}