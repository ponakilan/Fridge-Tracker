package com.example.fridgeapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;

public class RecipeGeneratorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ai_recipe);

        TextView recipeText = findViewById(R.id.recipeText);
        final Markwon markwon = Markwon.builder(this).build();

        String markdownContent = "# Hello Markwon!\n\nThis is **bold** text and *italic* text.\n\n- List item 1\n- List item 2\n\n```java\nSystem.out.println(\\\"Code block\\\");\n```";
        markwon.setMarkdown(recipeText, markdownContent);
    }
}
