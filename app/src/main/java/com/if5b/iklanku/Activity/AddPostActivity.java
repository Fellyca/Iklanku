package com.if5b.iklanku.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.if5b.iklanku.API.APIServices;
import com.if5b.iklanku.Model.ValueNoData;
import com.if5b.iklanku.Utils.Utilities;
import com.if5b.iklanku.databinding.ActivityAddPostBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPostActivity extends AppCompatActivity {
    private ActivityAddPostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.progressBar.setVisibility(View.GONE);

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String judul = binding.etJudul.getText().toString();
                boolean bolehPost = true;

                if(TextUtils.isEmpty(judul)){
                    bolehPost = false;
                    binding.etJudul.setError("judul tidak boleh kosong!");
                }

                if (bolehPost){
                    addPost(Utilities.getValue(AddPostActivity.this, "xUsername"), judul, "");
                }

                Intent intent = new Intent(AddPostActivity.this, UploadImageActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void addPost(String username, String judul, String image) {
        binding.progressBar.setVisibility(View.VISIBLE);
        APIServices api = Utilities.getRetrofit().create(APIServices.class);
        Call<ValueNoData> call = api.insertPost(Utilities.API_KEY, username, judul, image);
        call.enqueue(new Callback<ValueNoData>() {
            @Override
            public void onResponse(Call<ValueNoData> call, Response<ValueNoData> response) {
                if (response.code() == 200){
                    binding.progressBar.setVisibility(View.GONE);
                    int success = response.body().getSuccess();
                    String message = response.body().getMessage();

                    if (success == 1){
                        Toast.makeText(AddPostActivity.this, message, Toast.LENGTH_SHORT).show();
                        finish();
                    } else{
                        Toast.makeText(AddPostActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else{
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddPostActivity.this, "Response "+response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ValueNoData> call, Throwable t) {
                System.out.println("Retrofit Error : "+t.getMessage());
                Toast.makeText(AddPostActivity.this, "Retrofit Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}