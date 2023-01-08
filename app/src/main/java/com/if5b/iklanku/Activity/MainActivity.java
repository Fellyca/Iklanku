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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";

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
        }else {
            setContentView(R.layout.activity_main2);

            FirebaseMessaging.getInstance().subscribeToTopic("messages");

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

        rvChat = findViewById(R.id.rv_chat);
//        progressBar = findViewById(R.id.progressBar);
        etMessage = findViewById(R.id.et_message);
        ivAddMessage = findViewById(R.id.iv_add_message);
        btnSend = findViewById(R.id.btn_send);

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
            mRef = mRoot.child("messages");
            FirebaseRecyclerOptions<ChatMessage> options = new FirebaseRecyclerOptions.Builder<ChatMessage>()
                    .setQuery(mRef, parser)
                    .build();

            mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage, IklanViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull IklanViewHolder holder, int position, @NonNull ChatMessage model) {
                    mRoot.child("users").child(model.getName()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            progressBar.setVisibility(View.INVISIBLE);

                            if (model.getName().equals(userId)) {
                                holder.llReceiver.setVisibility(View.GONE);
                                holder.llSender.setVisibility(View.VISIBLE);

                                if (model.getText() != null) {
                                    holder.tvMessageSender.setText(model.getText());
                                    holder.tvMessageSender.setVisibility(View.VISIBLE);
                                    holder.tvMessageSender.setVisibility(View.GONE);
                                } else if (model.getImageUrl() != null) {
                                    String imageURL = model.getImageUrl();
                                    if (imageURL.startsWith("gs://")) {
                                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageURL);
                                        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {
                                                    String downloadUrl = task.getResult().toString();
                                                    Glide.with(holder.ivMessageSender.getContext())
                                                            .load(downloadUrl)
                                                            .into(holder.ivMessageSender);
                                                } else {
                                                    Log.w(TAG, "Getting Download url failed !", task.getException());
                                                }

                                            }
                                        });
                                    } else {
                                        Glide.with(holder.ivMessageSender.getContext())
                                                .load(model.getImageUrl())
                                                .into(holder.ivMessageSender);
                                    }
                                    holder.ivMessageSender.setVisibility(View.VISIBLE);
                                    holder.tvMessageSender.setVisibility(View.GONE);

                                }
                                holder.tvSender.setText(user.getUsername());

                                ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
                                getInitialName(mUsername.toUpperCase());
                                TextDrawable textDrawable = TextDrawable.builder()
                                        .beginConfig()
                                        .width(50)
                                        .height(50)
                                        .endConfig()
                                        .buildRound(getInitialName(mUsername.toUpperCase()), colorGenerator.getColor(model.getName()));

                                holder.ivSender.setImageDrawable(textDrawable);
                            }else {
                                holder.llReceiver.setVisibility(View.GONE);
                                holder.llSender.setVisibility(View.VISIBLE);

                                if (model.getText() != null) {
                                    holder.tvMessageReceiver.setText(model.getText());
                                    holder.tvMessageReceiver.setVisibility(View.VISIBLE);
                                    holder.tvMessageReceiver.setVisibility(View.GONE);
                                } else if (model.getImageUrl() != null) {
                                    String imageURL = model.getImageUrl();
                                    if (imageURL.startsWith("gs://")) {
                                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageURL);
                                        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {
                                                    String downloadUrl = task.getResult().toString();
                                                    Glide.with(holder.ivMessageReceiver.getContext())
                                                            .load(downloadUrl)
                                                            .into(holder.ivMessageReceiver);
                                                } else {
                                                    Log.w(TAG, "Getting Download url failed !", task.getException());
                                                }

                                            }
                                        });
                                    } else {
                                        Glide.with(holder.ivMessageReceiver.getContext())
                                                .load(model.getImageUrl())
                                                .into(holder.ivMessageReceiver);
                                    }
                                    holder.ivMessageReceiver.setVisibility(View.VISIBLE);
                                    holder.tvMessageReceiver.setVisibility(View.GONE);

                                }
                                holder.tvReceiver.setText(user.getUsername());

                                ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
                                getInitialName(mUsername.toUpperCase());
                                TextDrawable textDrawable = TextDrawable.builder()
                                        .beginConfig()
                                        .width(50)
                                        .height(50)
                                        .endConfig()
                                        .buildRound(getInitialName(mUsername.toUpperCase()), colorGenerator.getColor(model.getName()));

                                holder.ivReceiver.setImageDrawable(textDrawable);


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

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
                    ChatMessage chatMessage = new ChatMessage(etMessage.getText().toString(),
                            mUsername, null);
                    mRoot.child("messages").push().setValue(chatMessage);
                    etMessage.setText("");
                }
            });

            ivAddMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_IMAGE);
                }
            });
        }

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

    protected void onPause() {
        mFirebaseAdapter.startListening();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAdapter.startListening();
        getAllPost();
    }

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    final Uri uri = result.getData().getData();
                    Log.d(TAG, "Uri : " + uri.toString());

                    ChatMessage tempMessage = new ChatMessage(null, userId, LOADING_IMAGE_URL);
                    mRoot.child("messages").push()
                            .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        String key = databaseReference.getKey();
                                        StorageReference storageReference = FirebaseStorage.getInstance()
                                                .getReference(mAuth.getCurrentUser().getUid())
                                                .child(key)
                                                .child(uri.getLastPathSegment());

                                        putImageInStorage(storageReference, uri, key);
                                    } else {
                                        Log.w(TAG, "Unable to write database", databaseError.toException());
                                    }
                                }
                            });
                }
            }

        }
    });

    private void putImageInStorage(StorageReference storageReference, Uri uri, String key) {
        storageReference.putFile(uri).addOnCompleteListener(MainActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    task.getResult().getMetadata().getReference().getDownloadUrl()
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        ChatMessage chatMessage = new ChatMessage(null, mUsername, task.getResult().toString());
                                        mRoot.child("messages").child(key).setValue(chatMessage);
                                    }
                                }
                            });
                }else {
                    Log.w(TAG, "Image Upload Task Failed!", task.getException());
                }
            }
        });
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
//        int firstSpace = fullname.indexOf(" ");
//        String firstName = fullname.substring(0, firstSpace);
//        int lastSpace = fullname.lastIndexOf(" ");
//        String middleName = fullname.substring(firstSpace + 1,  lastSpace);
//        String lastName = fullname.substring(lastSpace + 1);
//
//        return "" + firstName.charAt(0) + lastName.charAt(0);
        String splitName[] = fullname.split("\\s+");
        int splitCount = splitName.length;

        if (splitCount == 1) {
            return "" + fullname.charAt(0) + fullname.charAt(0);
        } else {
            int firstSpace = fullname.indexOf(" ");
            String firstName = fullname.substring(0, firstSpace);

            int lastSpace = fullname.lastIndexOf(" ");
            String lastName = fullname.substring(lastSpace + 1);

            return "" + firstName.charAt(0) + lastName.charAt(0);
        }
    }
}