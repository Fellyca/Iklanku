package com.if5b.iklanku.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.if5b.iklanku.databinding.ActivityUploadImageBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UploadImageActivity extends AppCompatActivity {
    private ActivityUploadImageBinding binding;
    Uri imageUri;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    public static String image = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }

    private void uploadImage() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Upload File...");
        progressDialog.show();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String fileName = formatter.format(now);
        storageReference = FirebaseStorage.getInstance().getReference(fileName);



        storageReference.putFile(imageUri).addOnCompleteListener(UploadImageActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            task.getResult().getMetadata().getReference().getDownloadUrl()
                                    .addOnCompleteListener(UploadImageActivity.this, new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()){
                                                Glide.with(UploadImageActivity.this)
                                                        .load(task.getResult().toString())
                                                        .into(binding.ivImage);
                                                image = task.getResult().toString();
                                                Toast.makeText(UploadImageActivity.this, "Upload Berhasil", Toast.LENGTH_SHORT).show();
                                                System.out.println("gambar 2:" + image);
                                                if(progressDialog.isShowing()){
                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent(UploadImageActivity.this, AddPostActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                });
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        binding.ivImage.setImageURI(null);
//                        Toast.makeText(UploadImageActivity.this, "Upload Berhasil", Toast.LENGTH_SHORT).show();
//                        if(progressDialog.isShowing())
//                            progressDialog.dismiss();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        if (progressDialog.isShowing())
//                            progressDialog.dismiss();
//
//                        Toast.makeText(UploadImageActivity.this, "Upload gagal", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
    }

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && data!= null && data.getData()!= null){
            imageUri = data.getData();
            binding.ivImage.setImageURI(imageUri);
        }
    }
}