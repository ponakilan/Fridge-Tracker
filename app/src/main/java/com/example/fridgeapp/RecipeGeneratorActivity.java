package com.example.fridgeapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import io.noties.markwon.Markwon;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import java.util.List;
import java.util.stream.Collectors;

public class RecipeGeneratorActivity extends AppCompatActivity {

    private TextView recipeText;
    private ProgressBar progressBar;
    private FridgeViewModel fridgeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ai_recipe);

        recipeText = findViewById(R.id.recipeText);
        progressBar = findViewById(R.id.progressBar); 

        final Markwon markwon = Markwon.builder(this).build();

        fridgeViewModel = new ViewModelProvider(this).get(FridgeViewModel.class);

        fridgeViewModel.getAllItems().observe(this, items -> {
            if (items == null || items.isEmpty()) {
                recipeText.setText("No items found in your fridge. Add items to generate recipes.");
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                return;
            }

            List<String> itemNames = items.stream()
                    .map(FridgeItem::getName)
                    .collect(Collectors.toList());

            generateRecipe(itemNames, markwon);
        });
    }

    private void generateRecipe(List<String> fridgeItems, Markwon markwon) {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        recipeText.setText("Generating recipe...");

        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey("OPENAI_API_KEY")

                .build();

        String itemsString = String.join(", ", fridgeItems);
        final String prompt = "You are a recipe generator. You can create multiple recipes. Create a recipe using only the following ingredients: "
                + itemsString
                + ". Provide the recipe in Markdown format to display in an android app with headings for ingredients, instructions, and serving suggestions. Don't underline the headings. No separators.";

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage(prompt)
                .model(ChatModel.GPT_4)
                .build();

        client.async().chat().completions().create(params)
                .thenAccept(chatCompletion -> {
                    String responseContent = chatCompletion.choices().get(0).message().content().orElse("No recipe generated.");

                    runOnUiThread(() -> {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        System.out.println(responseContent);
                        markwon.setMarkdown(recipeText, responseContent);
                    });
                })
                .exceptionally(throwable -> {

                    runOnUiThread(() -> {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        recipeText.setText("Error generating recipe: " + throwable.getMessage());
                    });
                    return null;
                });
    }
}