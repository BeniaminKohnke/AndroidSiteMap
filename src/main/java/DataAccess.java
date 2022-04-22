import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public final class DataAccess {
    private DatabaseReference database;
    private DatabaseReference.CompletionListener listener = (databaseError, databaseReference) -> {
        if (databaseError != null) {
            System.out.println("Data could not be saved " + databaseError.getMessage());
        } else {
            System.out.println("Data saved successfully.");
        }
    };

    DataAccess(){
        try {
            FileInputStream serviceAccount =
                    new FileInputStream("androidestimate-firebase-adminsdk-7lpkh-a2bac8880c.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://androidestimate-default-rtdb.europe-west1.firebasedatabase.app")
                    .build();

            FirebaseApp.initializeApp(options);
            database = FirebaseDatabase.getInstance().getReference("Product");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SaveOrUpdateProduct(Product product){
        if(product != null) {
            var values = new HashMap<String, Object>();
            values.put("name", product.name);
            values.put("url", product.url);
            values.put("oldPrice", product.oldPrice);
            values.put("currentPrice", product.currentPrice);
            values.put("availability", product.availability);
            values.put("category", product.category);

            database.setValue(values, listener);
        }
    }
}
