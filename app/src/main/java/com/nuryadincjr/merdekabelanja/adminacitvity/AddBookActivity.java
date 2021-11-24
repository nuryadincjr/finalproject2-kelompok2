package com.nuryadincjr.merdekabelanja.adminacitvity;

import static android.widget.AdapterView.OnItemSelectedListener;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.api.ProductsRepository;
import com.nuryadincjr.merdekabelanja.databinding.ActivityAddBookBinding;
import com.nuryadincjr.merdekabelanja.models.Books;
import com.nuryadincjr.merdekabelanja.pojo.Constaint;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddBookActivity extends AppCompatActivity implements OnItemSelectedListener{

    private ActivityAddBookBinding binding;
    private StorageReference storageReference;
    private List<Uri> uriImageList;
    private ProgressDialog dialog;
    private Books books;
    private final String TAG = "LIA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding  = ActivityAddBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageReference = FirebaseStorage.getInstance().getReference().child("product");
        dialog = new ProgressDialog(this);
        uriImageList = new ArrayList<>();
        books = new Books();

        books.setCategory(getIntent().getStringExtra("PRODUCT"));
        getSupportActionBar().setTitle("Add " + books.getCategory());


        binding.btnAddProduct.setOnClickListener(v -> getInputValidations());
        binding.btnAddPhoto.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 25);
        });

        getBookTypeAdapter();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getBookTypeAdapter() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.book_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spBookType.setOnItemSelectedListener(this);
        binding.spBookType.setAdapter(adapter);
    }

    private void getInputValidations() {
        String id = UUID.randomUUID().toString();
        String title = binding.etTitle.getText().toString();
        String author = binding.etAuthor.getText().toString();
        String publisher = binding.etPublisher.getText().toString();
        String publisherYear = binding.etPublisherYear.getText().toString();
        String numberOfPage = binding.etNumberOfPage.getText().toString();
        String descriptions = binding.etDescriptions.getText().toString();
        String piece = binding.etPiece.getText().toString();
        String quantity = binding.etQuantity.getText().toString();

        if(!title.isEmpty() && !piece.isEmpty() &&
                !quantity.isEmpty() && books.getBook_type() != null) {
            books = new Books(id, title, descriptions, null, piece, quantity,
                    this.books.getCategory(), author, publisher, publisherYear,
                    this.books.getBook_type(), Integer.parseInt(numberOfPage));
            onCreateProduct(books);

        } else Toast.makeText(this,"Empty credentials!", Toast.LENGTH_SHORT).show();
    }

    private void onCreateProduct(Books books) {
        dialog.setMessage("Createing Data..");
        dialog.setCancelable(false);
        dialog.show();

        List<String> photo = new ArrayList<>();
        books.setPhoto(photo);

        if (!uriImageList.isEmpty()) {
            dialog.setMessage("Setuping data..");

            for (int i = 0; i < uriImageList.size(); i++) {
                StorageReference filePath = storageReference
                        .child(books.getCategory())
                        .child(books.getId())
                        .child("preview" + i + "." + Constaint.getFileExtension(uriImageList.get(i), this));
                int finalI = i;

                filePath.putFile(uriImageList.get(i)).continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        dialog.dismiss();
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        photo.add(finalI, task.getResult().toString());
                        if ((finalI + 1) == uriImageList.size()) {
                            books.setPhoto(photo);
                            onCreateData(books);
                        }
                    }
                });
            }
        } else onCreateData(books);
    }

    private void onCreateData(Books books) {
        dialog.setMessage("Setuping data..");
        new ProductsRepository().insertProducts(books).addOnSuccessListener(documentReference -> {
            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference);
            Toast.makeText(getApplicationContext(),
                    "Success.", Toast.LENGTH_SHORT).show();

            dialog.dismiss();
            finish();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Log.w(TAG, "Error adding document", e);
            Toast.makeText(getApplicationContext(),
                    "Error adding document.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 25 || resultCode != -1) {
            this.binding.btnAddPhoto.setChecked(false);
        } else if (data.getClipData() != null) {
            int count = data.getClipData().getItemCount();
            for (int i = 0; i < count; i++) {
                uriImageList.add(i, data.getClipData().getItemAt(i).getUri());
            }
        } else if (data.getData() != null) {
            uriImageList.add(0, data.getData());
        } else binding.btnAddPhoto.setChecked(false);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position !=0 ){
            books.setBook_type(parent.getSelectedItem().toString());
        }else {
            view.setEnabled(false);
            books.setBook_type(null);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}