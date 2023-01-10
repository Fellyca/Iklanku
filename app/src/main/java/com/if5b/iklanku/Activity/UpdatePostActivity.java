package com.if5b.iklanku.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.if5b.iklanku.API.APIServices;
import com.if5b.iklanku.Model.Post;
import com.if5b.iklanku.Model.ValueNoData;
import com.if5b.iklanku.Utils.Utilities;
import com.if5b.iklanku.databinding.ActivityUpdatePostBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePostActivity extends AppCompatActivity {
    private ActivityUpdatePostBinding binding;
    private Post mPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUpdatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mPost = getIntent().getParcelableExtra("EXTRA_DATA");
        int id = mPost.getId();

        binding.progressBar.setVisibility(View.GONE);

        binding.etJudul.setText(mPost.getJudul());

        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String judul = binding.etJudul.getText().toString();
                boolean bolehUpdatePost = true;

                if(TextUtils.isEmpty(judul)){
                    bolehUpdatePost = false;
                    binding.etJudul.setError("judul tidak boleh kosong!");
                }

                if (bolehUpdatePost){
                    updatePost(id, judul, UploadImageActivity.image);
                }
            }
        });
    }

    private void updatePost(int id, String judul, String image) {
        binding.progressBar.setVisibility(View.VISIBLE);
        APIServices api = Utilities.getRetrofit().create(APIServices.class);
        api.updatePost(Utilities.API_KEY, id, judul).enqueue(new Callback<ValueNoData>(){
//        Call<ValueNoData> call = api.updatePost(Utilities.API_KEY, id, judul);
//        call.enqueue(new Callback<ValueNoData>() {
            @Override
            public void onResponse(Call<ValueNoData> call, Response<ValueNoData> response) {
                if (response.code() == 200){
                    binding.progressBar.setVisibility(View.GONE);
                    int success = response.body().getSuccess();
                    String message = response.body().getMessage();

                    if (success == 1){
                        Toast.makeText(UpdatePostActivity.this, message, Toast.LENGTH_SHORT).show();
                        finish();
                    } else{
                        Toast.makeText(UpdatePostActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else{
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(UpdatePostActivity.this, "Response "+response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ValueNoData> call, Throwable t) {
                System.out.println("Retrofit Error : "+t.getMessage());
                Toast.makeText(UpdatePostActivity.this, "Retrofit Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

