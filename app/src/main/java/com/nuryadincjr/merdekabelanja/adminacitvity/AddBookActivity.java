package com.nuryadincjr.merdekabelanja.adminacitvity;

import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_DATA;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISEDIT;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_PRODUCT;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.getFileExtension;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.time;

import android.annotation.SuppressLint;
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
        SpinnersAdapter spinnersAdapter = SpinnersAdapter.getInstance(this);

        dialog = new ProgressDialog(this);
        uriImageList = new ArrayList<>();
        books = new Books();
        isEdit = getIntent().getBooleanExtra(NAME_ISEDIT, false);

        binding.btnAddProduct.setOnClickListener(v -> getInputValidations());
        binding.btnAddPhoto.setOnClickListener(v -> imagesPreference.getMultipleImage(this));

        books.setCategory(getIntent().getStringExtra(NAME_PRODUCT));
        String titleBar = "Add ";
        titleBar = getIsEdited(titleBar);

        spinnersAdapter.getSpinnerAdapter(binding.actBookType, R.array.book_type, books.getBook_type());
        getSupportActionBar().setTitle(titleBar + books.getCategory());
    }

    @SuppressLint("SetTextI18n")
    private String getIsEdited(String titleBar) {
        if(isEdit) {
            books = getIntent().getParcelableExtra(NAME_DATA);
            titleBar = "Edit ";
            onDataSet(books);
            binding.btnAddProduct.setText("Save Product");
        }
        return titleBar;
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

        String title = String.valueOf(binding.etTitle.getText());
        String author = String.valueOf(binding.etAuthor.getText());
        String publisher = String.valueOf(binding.etPublisher.getText());
        String publisherYear = String.valueOf(binding.etPublisherYear.getText());
        String numberOfPage = String.valueOf(binding.etNumberOfPage.getText());
        String descriptions = String.valueOf(binding.etDescriptions.getText());
        String piece = String.valueOf(binding.etPiece.getText());
        String quantity = String.valueOf(binding.etQuantity.getText());
        String bookType = String.valueOf(binding.actBookType.getText());

        if(!title.isEmpty() && !piece.isEmpty() && !quantity.isEmpty() && !bookType.isEmpty()) {
            books = new Books(id, title, descriptions, null, piece, quantity,
                    this.books.getCategory(), author, time(), publisher, publisherYear,
                    bookType, Integer.parseInt(numberOfPage));
            onCreateProduct(books);

        } else Toast.makeText(this,"Empty credentials!", Toast.LENGTH_SHORT).show();
    }

    private void onCreateProduct(Books books) {
        dialog.setMessage("Creating Data..");
        dialog.setCancelable(false);
        dialog.show();

        List<String> photo = new ArrayList<>();
        books.setPhoto(photo);

        if (!uriImageList.isEmpty()) {
            dialog.setMessage("Setup data..");

            for (int i = 0; i < uriImageList.size(); i++) {
                StorageReference filePath = storageReference
                        .child(books.getCategory())
                        .child(books.getId())
                        .child("preview" + i + "." + getFileExtension(uriImageList.get(i), this));
                int finalI = i;

                filePath.putFile(uriImageList.get(i)).continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        dialog.dismiss();
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        photo.add(finalI, String.valueOf(task.getResult()));
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
        } else {
            assert data != null;
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    uriImageList.add(i, data.getClipData().getItemAt(i).getUri());
                }
            } else if (data.getData() != null) {
                uriImageList.add(0, data.getData());
            } else binding.btnAddPhoto.setChecked(false);
        }
    }
}