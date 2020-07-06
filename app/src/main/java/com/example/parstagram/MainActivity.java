package com.example.parstagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.parstagram.fragments.ComposeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);

        // When a different Nav item is selected, replace the current frag with an instance
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment frag;
                switch(menuItem.getItemId()){
                    case R.id.home:
                        frag = new Fragment();
                        System.out.println("yo");
                        break;
                    case R.id.create_post:
                        frag = new ComposeFragment();
                        break;
                    default:
                        frag = new Fragment();
                        break;
                }
                // Update fragment by replacing
                fragmentManager.beginTransaction().replace(R.id.frag_container, frag).commit();
                return true;
            }
        });
        bottomNav.setSelectedItemId(R.id.create_post);
    }

    // Retrieve a list of all the posts
    public void queryPosts() {
        ParseQuery<Post> q = ParseQuery.getQuery(Post.class);
        q.include(Post.KEY_USER);
        q.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e != null) {
                    Toast.makeText(MainActivity.this, "Error getting posts", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Post p : objects) {
                    System.out.println(p.getDescription() + " " + p.getUser().getUsername());
                }
            }
        });
    }
}