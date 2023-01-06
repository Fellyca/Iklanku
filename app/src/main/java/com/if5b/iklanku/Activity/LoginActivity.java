package com.if5b.iklanku.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.if5b.iklanku.API.APIServices;
import com.if5b.iklanku.Model.ValueNoData;
import com.if5b.iklanku.Utils.Utilities;
import com.if5b.iklanku.databinding.ActivityLoginBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding b;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRoot, mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRoot = mDatabase.getReference();

        b.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = b.etUsername.getText().toString();
                String email = b.etEmail.getText().toString();
                String password = b.etPassword.getText().toString();

                boolean bolehLogin = true;

                if (TextUtils.isEmpty(email)){
                    bolehLogin = false;
                    b.etEmail.setError("Email tidak boleh kosong!");
                }
                if (TextUtils.isEmpty(username)){
                    bolehLogin = false;
                    b.etUsername.setError("Username tidak boleh kosong!");
                }
                if (TextUtils.isEmpty(password)){
                    bolehLogin = false;
                    b.etPassword.setError("Password tidak boleh kosong!");
                }

                hideProgressBar();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                hideProgressBar();
                                if(task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this, "Login berhasil!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast.makeText(LoginActivity.this, "Login gagal!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                if (bolehLogin) {
                    login(username, password);
                }
            }
        });
        b.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        b.btnResetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void login(String username ,String password){
        showProgressBar();
        APIServices api = Utilities.getRetrofit().create(APIServices.class);
        Call<ValueNoData> call = api.login(Utilities.API_KEY, username, password);
        call.enqueue(new Callback<ValueNoData>() {
            @Override
            public void onResponse(Call<ValueNoData> call, Response<ValueNoData> response) {
                if(response.code() == 200 ) {
                    hideProgressBar();
                    int succes = response.body().getSuccess();
                    String message = response.body().getMessage();
                    if (succes == 1){
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        Utilities.setValue(LoginActivity.this, "xUsername", username);
                    }else {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    hideProgressBar();
                    Toast.makeText(LoginActivity.this, "Response " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ValueNoData> call, Throwable t) {
                hideProgressBar();
                System.out.println("Retrofit Error : " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Retrofit Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showProgressBar() {
        b.progessBar.setVisibility(View.VISIBLE);
    }
    private void hideProgressBar() {
        b.progessBar.setVisibility(View.INVISIBLE);
        b.progessBar.setVisibility(View.GONE);
    }
}