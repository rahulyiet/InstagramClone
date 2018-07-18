package com.example.rahulyiet.instagramclone;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import de.hdodenhof.circleimageview.CircleImageView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.fxn.pix.Pix;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {



    ImageView profile,profile2,profile3,profile4,profile5,profile6,img1;


    private static final int RC_SIGN_IN = 123;
    private static final int RC_PIX = 101;
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.GoogleBuilder().build()

    );

    //authenticator
    private FirebaseAuth firebaseAuth;


    private FirebaseUser user;
    private TextView Email;
    RecyclerView recyclerView;
    //auth satate Listener
    private FirebaseAuth.AuthStateListener authStateListener;
    //Realtime database
    FirebaseDatabase database;

    FirebaseStorage storage;

    StorageReference storageReference, imageRef;

    //database reference
    DatabaseReference userRef, postRef;


    RecyclerView recyclerview;
    PostAdapter adapter;
    ArrayList<PostModel> postModelArrayList;

     ImageView Create,Share,img2,img4,img5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        profile=findViewById(R.id.profile);
        profile2=findViewById(R.id.profile2);
        profile3=findViewById(R.id.profile3);
        profile4=findViewById(R.id.profile4);
        profile5=findViewById(R.id.profile5);
        profile6=findViewById(R.id.profile6);


       Create=findViewById(R.id.create);
       Share=findViewById(R.id.share);
       img2=findViewById(R.id.img2);
       img4=findViewById(R.id.img4);
       img5=findViewById(R.id.img5);

        img5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,Story.class);
                startActivity(intent);
            }
        });

       img4.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Intent intent = new Intent(MainActivity.this,Follow.class);
               startActivity(intent);
           }
       });

       img2.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Intent intent = new Intent(MainActivity.this,Search.class);
               startActivity(intent);
           }
       });

       Share.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Intent intent = new Intent(MainActivity.this,Chat.class);
               startActivity(intent);
           }
       });

       Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,PostStatus.class);
                startActivity(intent);
            }
        });



        Email = findViewById(R.id.email);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter=new PostAdapter(this);
        recyclerView.setAdapter(adapter);
        postModelArrayList=new ArrayList<>();

        //get instance
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        //reference to database reference
        userRef = database.getReference("users");
        postRef = database.getReference("posts");


        storageReference = storage.getReference();

        postRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                PostModel post = dataSnapshot.getValue(PostModel.class);
                postModelArrayList.add(post);
                adapter.swap(postModelArrayList);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //signed in



                    //get user details;
                    String id = user.getUid();
                    String name = user.getDisplayName();
                    String email = user.getEmail();
                    String imgUrl = user.getPhotoUrl().toString();

                    //create model
                    userModel userModel1 = new userModel(id, name, email, imgUrl);

                    //add to firebase database
                    userRef.child(userModel1.getId()).setValue(userModel1);


                } else {
                    //not sign in
                    startSignIn();

                }
            }
        };


    }


    private void startSignIn() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);


    }

    private void startSignOut() {

        AuthUI.getInstance()
                .signOut(MainActivity.this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "successfully sign out", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                // ...
            } else {
                Toast.makeText(MainActivity.this, response.getError().getErrorCode(), Toast.LENGTH_SHORT).show();
                startSignIn();
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == RC_PIX) {
            ArrayList<String> images = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            uploadPost(images.get(0));
        }
    }

    private void uploadPost(String imageLocation) {

        Toast.makeText(MainActivity.this, "processing image", Toast.LENGTH_SHORT).show();
        Uri file = Uri.fromFile(new File(imageLocation));
        imageRef = storageReference.child(file.getLastPathSegment());
        UploadTask uploadTask = imageRef.putFile(file);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Toast.makeText(MainActivity.this, "processing finished", Toast.LENGTH_SHORT).show();

                    String userName = user.getDisplayName();
                    String userImgUrl = user.getPhotoUrl().toString();
                    String userEmail = user.getEmail();
                    String postImgUrl = downloadUri.toString();
                    String postTime = String.valueOf(System.currentTimeMillis() / 1000);

                    PostModel postModel = new PostModel(userName, userImgUrl, userEmail, postImgUrl, postTime);
                    postRef.push().setValue(postModel);
                    Toast.makeText(MainActivity.this, "Uploading finished", Toast.LENGTH_SHORT).show();


                } else {

                    Toast.makeText(MainActivity.this, "Uploading failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.android_menu, menu);

        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signOut:
                startSignOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        firebaseAuth.addAuthStateListener(authStateListener);
    }


    public void post(View view) {
        Pix.start(MainActivity.this, RC_PIX, 1);

    }

    public void statrStory(View view) {

        switch (view.getId()){

            case R.id.profile:
                startIntent(0);
                break;
            case R.id.profile2:
                startIntent(1);
                break;
            case R.id.profile3:
                startIntent(2);
                break;
            case R.id.profile4:
                startIntent(3);
                break;
            case R.id.profile5:
                startIntent(4);
                break;
            case R.id.profile6:
                startIntent(5);
        }

    }

    private void startIntent(int i) {

        Intent intent = new Intent(MainActivity.this, StoryClass.class);
        intent.putExtra("position",i);
        startActivity(intent);

    }
}
