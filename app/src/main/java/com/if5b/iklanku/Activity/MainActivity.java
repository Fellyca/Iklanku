package com.if5b.iklanku.Activity;

import android.app.appsearch.StorageInfo;
import  android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.if5b.iklanku.API.APIServices;
import com.if5b.iklanku.IklanViewHolder;
import com.if5b.iklanku.Model.ChatMessage;
import com.if5b.iklanku.Model.ValueData;
import com.if5b.iklanku.Model.ValueNoData;
import com.if5b.iklanku.Adapter.PostViewAdapter;
import com.if5b.iklanku.Model.Post;
import com.if5b.iklanku.R;
import com.if5b.iklanku.User;
import com.if5b.iklanku.Utils.Utilities;
import com.if5b.iklanku.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements PostViewAdapter.OnItemLongClickListener {
    private ActivityMainBinding binding;
    private PostViewAdapter postViewAdapter;
    private List<Post> data = new ArrayList<>();

    private FirebaseAuth mAuth;
    private static final String TAG = MainActivity.class.getSimpleName();

    private String mUsername;
    private static final int REQUEST_IMAGE = 2;
    private static final String LOADING_IMAGE_URL = "";

    private DatabaseReference mRoot, mRef;
    private FirebaseRecyclerAdapter<ChatMessage, IklanViewHolder> mFirebaseAdapter;
    private String userId;

    private RecyclerView rvChat;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar progressBar;
    private Button btnSend;
    private EditText etMessage;
    private ImageView ivAddMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        if (!Utilities.checkValue(MainActivity.this, "xUsername")) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        if(mAuth.getCurrentUser() == null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        String userId = mAuth.getCurrentUser().getUid();
        mRoot = FirebaseDatabase.getInstance().getReference();
        mRef = mRoot.child("users_id").child(userId);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                mUsername = user.getUsername();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mUsername = "Anonymous";
            }
        });

//        rvChat = findViewById(R.id.rv_chat);
//        progressBar = findViewById(R.id.progressBar);
//        etMessage = findViewById(R.id.et_message);
//        ivAddMessage = findViewById(R.id.iv_add_message);
//        btnSend = findViewById(R.id.btn_send);

        mLinearLayoutManager = new LinearLayoutManager(MainActivity.this);
        mLinearLayoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(mLinearLayoutManager);

        SnapshotParser<ChatMessage> parser = new SnapshotParser<ChatMessage>() {
            @NonNull
            @Override
            public ChatMessage parseSnapshot(@NonNull DataSnapshot snapshot) {
                ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                if (chatMessage != null) {
                    chatMessage.setId(snapshot.getKey());
                }
                return chatMessage;
            }
        };
        mRef = mRoot.child("message");
        FirebaseRecyclerOptions<ChatMessage> options = new FirebaseRecyclerOptions.Builder<ChatMessage>()
                .setQuery(mRef, parser)
                .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage, IklanViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull IklanViewHolder holder, int position, @NonNull ChatMessage model) {
                progressBar.setVisibility(View.INVISIBLE);
                if (model.getText() != null) {
                    holder.tvMessage.setText(model.getText());
                    holder.tvMessage.setVisibility(View.VISIBLE);
                    holder.tvMessage.setVisibility(View.GONE);
                } else if (model.getImageUrl() != null) {
                    String imageURL = model.getImageUrl();
                    if (imageURL.startsWith("gs://")) {
                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageURL);
                        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    String downloadUrl = task.getResult().toString();
                                    Glide.with(holder.ivMessage.getContext())
                                            .load(downloadUrl)
                                            .into(holder.ivMessage);
                                } else {
                                    Log.w(TAG, "Getting Download url failed !", task.getException());
                                }

                            }
                        });
                    } else {
                        Glide.with(holder.ivMessage.getContext())
                                .load(model.getImageUrl())
                                .into(holder.ivMessage);
                    }
                    holder.ivMessage.setVisibility(View.VISIBLE);
                    holder.tvMessage.setVisibility(View.GONE);

                }
                holder.tvMes.setText(model.getName());

                ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
                getInitialName(mUsername.toUpperCase());
                TextDrawable textDrawable = TextDrawable.builder()
                        .beginConfig()
                        .width(50)
                        .height(50)
                        .endConfig()
                        .buildRound(getInitialName(mUsername.toUpperCase()), colorGenerator.getColor(mAuth.getCurrentUser().getEmail()));

                holder.ivMes.setImageDrawable(textDrawable);
            }

            @NonNull
            @Override
            public IklanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                return new IklanViewHolder(inflater.inflate(R.layout.item_, parent, false));
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                int chatMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 ||
                        (positionStart >= (chatMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    rvChat.scrollToPosition(positionStart);
                }
            }
        });

        rvChat.setAdapter(mFirebaseAdapter);

        etMessage.addTextChangedListener(new TextWatcher() {  // NAHHHHHHHHHHHH 1.12.52
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().length() > 0){
                    btnSend.setEnabled(true);
                }else {
                    btnSend.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatMessage chatMessage = new ChatMessage(etMessage.getText().toString(),
                        mUsername,
                        null);
                mRoot.child("messages").push().setValue(chatMessage);

            }
        });

        postViewAdapter = new PostViewAdapter();
        binding.rvPost.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPost.setAdapter(postViewAdapter);

        

        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddPostActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllPost();
    }

    private void showProgressBar() {
        binding.progressBar.setVisibility(View.GONE);
    }

    private void hideProggressBar() {
        binding.progressBar.setVisibility(View.GONE);
    }

    private void getAllPost() {
        showProgressBar();
        APIServices api = Utilities.getRetrofit().create(APIServices.class);
        api.getPost(Utilities.API_KEY).enqueue(new Callback<ValueData<Post>>() {
            @Override
            public void onResponse(Call<ValueData<Post>> call, Response<ValueData<Post>> response) {
                if (response.code() == 200) {
                    int success = response.body().getSuccess();
                    if (success == 1) {
                        data = response.body().getData();
                        postViewAdapter.setData(data, MainActivity.this);
                        Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Response Code : " + response.code(), Toast.LENGTH_SHORT).show();
                }
                hideProggressBar();
            }



            @Override
            public void onFailure(Call<ValueData<Post>> call, Throwable t) {
                hideProggressBar();
                Toast.makeText(MainActivity.this, "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_logout){
            Utilities.clearUser(this);
            mAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemLongClick(View v, int position) {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
        popupMenu.inflate(R.menu.menu_popup);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popupMenu.setGravity(Gravity.RIGHT);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_edit:
                        Intent intent = new Intent(MainActivity.this, UpdatePostActivity.class);
                        intent.putExtra("EXTRA_DATA", String.valueOf(data.get(position)));
                        startActivity(intent);
                        return true;
                    case R.id.action_delete:
                        int id = (data.get(position).getId());
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        alertDialogBuilder.setTitle("Konfirmasi");
                        alertDialogBuilder.setMessage("Yakin ingin menghapus post '" + data.get(position).getJudul() + "' ?");
                        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletePost(id);
                            }
                        });
                        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();

    }

    private void deletePost(int id) {
        APIServices api = Utilities.getRetrofit().create(APIServices.class);
        Call<ValueNoData> call = api.deletePost(Utilities.API_KEY, id);
        call.enqueue(new Callback<ValueNoData>() {
            @Override
            public void onResponse(Call<ValueNoData> call, Response<ValueNoData> response) {
                if(response.code() == 200){
                    int success = response.body().getSuccess();
                    String  message = response.body().getMessage();
                    if (success == 1){
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        getAllPost();
                    }else{
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Response "+ response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ValueNoData> call, Throwable t) {
                System.out.println("Retrofit Error : "+ t.getMessage());
                Toast.makeText(MainActivity.this, "Retrofit Error : "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getInitialName(String fullname){
        int firstSpace = fullname.indexOf(" ");
        String firstName = fullname.substring(0, firstSpace);
        int lastSpace = fullname.lastIndexOf(" ");
        String middleName = fullname.substring(firstSpace + 1,  lastSpace);
        String lastName = fullname.substring(lastSpace + 1);

        return "" + firstName.charAt(0) + lastName.charAt(0);
    }
}