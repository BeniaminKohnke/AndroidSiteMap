package com.example.androidforeversource;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public final class DataAccess {
    private Firestore database;

    public DataAccess(){
        try {
            var serviceAccount =
                    new FileInputStream("androidestimate-firebase-adminsdk-7lpkh-a2bac8880c.json");

            var firestoreOptions =
                    FirestoreOptions.getDefaultInstance().toBuilder()
                            .setProjectId("androidestimate")
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .build();

            database = firestoreOptions.getService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveOrUpdateProduct(Product product){
        if(product != null) {
            var values = new HashMap<String, Object>();
            values.put("name", product.name);
            values.put("url", product.url);
            values.put("oldPrice", product.oldPrice);
            values.put("currentPrice", product.currentPrice);
            values.put("category", product.category);
            values.put("imageUrl", product.imageUrl);
            ApiFuture<WriteResult> writeResult = database.collection("Products").document(product.sku).set(values);

            try {
                System.out.println("Update time : " + writeResult.get().getUpdateTime());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
