package com.if5b.iklanku.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.if5b.iklanku.API.APIServices;
import com.if5b.iklanku.Model.Post;
import com.if5b.iklanku.Model.ValueNoData;
import com.if5b.iklanku.R;
import com.if5b.iklanku.Utils.Utilities;
import com.if5b.iklanku.databinding.ActivityUpdatePostBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePostActivity extends AppCompatActivity {
    private ActivityUpdatePostBinding binding;
    private Post mPost;

    ImageView imageView;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filepath;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUpdatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mPost = getIntent().getParcelableExtra("EXTRA_DATA");
        int id = mPost.getId();

        binding.progressBar.setVisibility(View.GONE);

        binding.etJudul.setText(mPost.getJudul());
        binding.tv4.setText(mPost.getImage());

        binding.btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowFileChooser();
            }
        });

        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String judul = binding.etJudul.getText().toString();
                String tv4 = binding.tv4.getText().toString();
                String path = getPath(filepath);
                boolean bolehUpdatePost = true;

                if(TextUtils.isEmpty(judul)){
                    bolehUpdatePost = false;
                    binding.etJudul.setError("Konten tidak boleh kosong!");
                }

                if(TextUtils.isEmpty(tv4)){
                    bolehUpdatePost = false;
                    binding.tv4.setError("Konten tidak boleh kosong!");
                }

                if (bolehUpdatePost){
                    updatePost(id, judul, path);
                }
            }
        });
    }

    private void updatePost(int id, String judul, String image) {
        binding.progressBar.setVisibility(View.VISIBLE);
        APIServices api = Utilities.getRetrofit().create(APIServices.class);
        Call<ValueNoData> call = api.updatePost(Utilities.API_KEY, id, judul, image);
        call.enqueue(new Callback<ValueNoData>() {
            //untuk 2 baris diatas
            //api.insertPost(Utilities.API_KEY, username, content).enqueue(new Callback<ValueNoData>(){
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
    private void ShowFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {

            filepath = data.getData();
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                imageView.setImageBitmap(bitmap);
                binding.tv4.setText(filepath.toString());
                // Toast.makeText(getApplicationContext(),getPath(filepath),Toast.LENGTH_LONG).show();
            } catch (Exception ex) {

            }
        }
    }

    private String getPath(Uri uri) {

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + "=?", new String[]{document_id}, null
        );
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }
}