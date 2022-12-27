package com.if5b.iklanku.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.if5b.iklanku.API.APIServices;
import com.if5b.iklanku.Model.ValueData;
import com.if5b.iklanku.Model.ValueNoData;
import com.if5b.iklanku.Adapter.PostViewAdapter;
import com.if5b.iklanku.Model.Post;
import com.if5b.iklanku.R;
import com.if5b.iklanku.Utils.Utilities;
import com.if5b.iklanku.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private PostViewAdapter postViewAdapter;
    private List<Post> data = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Utilities.checkValue(MainActivity.this, "xUsername")){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        postViewAdapter = new PostViewAdapter();
        binding.rvPost.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPost.setAdapter(postViewAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();
        getAllPost();
    }
    private void  showProgressBar(){binding.progressBar.setVisibility(View.GONE);
    }
    private void  hideProggressBar(){
        binding.progressBar.setVisibility(View.GONE);
    }

    private void getAllPost(){
        showProgressBar();
        APIServices api = Utilities.getRetrofit().create(APIServices.class);
        api.getPost(Utilities.API_KEY).enqueue(new Callback<ValueData<Post>>()
        {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call<ValueData<Post>> call, Response<ValueData<Post>> response)
            {
                if (response.code() == 200)
                {
                    int success = response.body().getSuccess();
                    if (success == 1)
                    {
                        data = response.body().getData();
                        postViewAdapter.setData(data, MainActivity.this::onItemPostLongClick);
                        Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Response Code : " + response.code(), Toast.LENGTH_SHORT).show();
                }
                hideProggressBar();
            }

            @Override
            public void onFailure(Call<ValueData<Post>> call, Throwable t)
            {
                hideProggressBar();
                Toast.makeText(MainActivity.this, "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void onItemPostLongClick(View view, Post post, int position) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.menu_popup);
        popupMenu.setGravity(Gravity.RIGHT);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_edit:
                        Intent intent = new Intent(MainActivity.this, UpdatePostActivity.class);
                        intent.putExtra("EXTRA_DATA", post);
                        startActivity(intent);
                        return true;
                    case R.id.action_delete:
                        int id = post.getId();
                        AlertDialog.Builder alerDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        alerDialogBuilder.setTitle("");
                        alerDialogBuilder.setMessage("");
                        alerDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                deletePost(id);
                            }
                        });
                        alerDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                dialogInterface.cancel();
                            }
                        });
                        AlertDialog alertDialog = alerDialogBuilder.create();
                        alertDialog.show();
                        return true;
                    default:
                        return false;

                }
            }
        });
        popupMenu.show();;

    public boolean onOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return  true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_logout){
            Utilities.clearUser(this);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}