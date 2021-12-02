package com.nuryadincjr.merdekabelanja.adminacitvity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.SpinnersAdapter;
import com.nuryadincjr.merdekabelanja.databinding.ActivityAddBookBinding;
import com.nuryadincjr.merdekabelanja.models.Books;
import com.nuryadincjr.merdekabelanja.pojo.Constaint;
import com.nuryadincjr.merdekabelanja.pojo.ImagesPreference;
import com.nuryadincjr.merdekabelanja.pojo.ProductsPreference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddBookActivity extends AppCompatActivity {

    private ActivityAddBookBinding binding;
    private StorageReference storageReference;
    private ProductsPreference productsPreference;
    private ImagesPreference imagesPreference;
    private SpinnersAdapter spinnersAdapter;
    private List<Uri> uriImageList;
    private ProgressDialog dialog;
    private Books books;
    private boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding  = ActivityAddBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageReference = FirebaseStorage.getInstance().getReference().child("product");
        productsPreference = ProductsPreference.getInstance(this);
        imagesPreference = ImagesPreference.getInstance(this);
        spinnersAdapter = SpinnersAdapter.getInstance(this);

        dialog = new ProgressDialog(this);
        uriImageList = new ArrayList<>();
        books = new Books();
        isEdit = getIntent().getBooleanExtra("ISEDIT", false);

        binding.btnAddProduct.setOnClickListener(v -> getInputValidations());
        binding.btnAddPhoto.setOnClickListener(v -> imagesPreference.getMultipleImage(this));

        books.setCategory(getIntent().getStringExtra("PRODUCT"));
        String titleBar = "Add ";

        if(isEdit) {
            books = getIntent().getParcelableExtra("DATA");
            titleBar = "Edit ";
            onDataSet(books);
            binding.btnAddProduct.setText("Save Product");
        }

        spinnersAdapter.getSpinnerAdapter(binding.actBookType, R.array.book_type, books.getBook_type());
        getSupportActionBar().setTitle(titleBar + books.getCategory());
    }

    private void onDataSet(Books books) {
        binding.etTitle.setText(books.getName());
        binding.etAuthor.setText(books.getAuthor());
        binding.etPublisher.setText(books.getPublisher());
        binding.etPublisherYear.setText(String.valueOf(books.getPublisher_year()));
        binding.etNumberOfPage.setText(String.valueOf(books.getNumber_of_page()));
        binding.etDescriptions.setText(books.getDescriptions());
        binding.etPiece.setText(books.getPiece());
        binding.etQuantity.setText(books.getQuantity());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getInputValidations() {
        String id = UUID.randomUUID().toString();
        if(isEdit) id = books.getId();

        String title = binding.etTitle.getText().toString();
        String author = binding.etAuthor.getText().toString();
        String publisher = binding.etPublisher.getText().toString();
        String publisherYear = binding.etPublisherYear.getText().toString();
        String numberOfPage = binding.etNumberOfPage.getText().toString();
        String descriptions = binding.etDescriptions.getText().toString();
        String piece = binding.etPiece.getText().toString();
        String quantity = binding.etQuantity.getText().toString();
        String bookType = binding.actBookType.getText().toString();

        if(!title.isEmpty() && !piece.isEmpty() && !quantity.isEmpty() && !bookType.isEmpty()) {
            books = new Books(id, title, descriptions, null, piece, quantity,
                    this.books.getCategory(), author, Constaint.time(), publisher, publisherYear,
                    bookType, Integer.parseInt(numberOfPage));
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

                            if(isEdit) productsPreference.onUpdateData(books, dialog);
                            else productsPreference.onCreateData(books, this);
                        }
                    }
                });
            }
        }  else {
            if(isEdit) productsPreference.onUpdateData(books, dialog);
            else productsPreference.onCreateData(books, this);
        }
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
}