package com.android.mhike;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements HikeAdapter.ItemListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RadioGroup radioGroup;
    private EditText edtSearch;
    private RecyclerView recyclerView;
    private HikeAdapter adapter;

    private ActivityResultLauncher<Intent> resultLauncher;

    private DatabaseHelper databaseHelper;
    private List<Hike> hikes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::onHikeResult);
        databaseHelper = new DatabaseHelper(this);
        hikes = databaseHelper.getAllHikes();
        if (hikes == null) {
            Toast.makeText(this, "An error occurred. Please try again", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        recyclerView = findViewById(R.id.recycler_view);
        adapter = new HikeAdapter(this, hikes, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btn_add).setOnClickListener(v -> resultLauncher.launch(new Intent(MainActivity.this, HikeActivity.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_delete) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Delete hikes");
            alert.setMessage("Are you sure you want to delete all hikes?");
            alert.setPositiveButton(R.string.string_yes, (dialog, which) -> deleteAll());
            alert.setNegativeButton(R.string.string_no, (dialog, which) -> dialog.cancel());
            alert.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(Hike hike) {
        Log.d(TAG, "onItemClick: " + hike);
        String finalMessage = "Name : " + hike.getName() + "\n" +
                "Location : " + hike.getLocation() + "\n" +
                "Date : " + hike.getDate() + "\n" +
                "Parking : " + (hike.getParkingAvailable() == 1 ? "Yes" : "No") + "\n" +
                "Length : " + hike.getLength() + "\n" +
                "Level : " + hike.getLevel() + "\n" +
                "Description : " + hike.getDescription();

        new AlertDialog.Builder(this)
                .setMessage(finalMessage)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onItemEdit(Hike hike) {
        Log.d(TAG, "onItemEdit: " + hike);

        Intent intent = new Intent(this, HikeActivity.class);
        intent.putExtra("Hike", hike);
        resultLauncher.launch(intent);
    }

    @Override
    public void onItemDelete(Hike hike) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete Hike");
        alert.setMessage("Are you sure you want to delete this item?");
        alert.setPositiveButton(R.string.string_yes, (dialog, which) -> delete(hike));
        alert.setNegativeButton(R.string.string_no, (dialog, which) -> dialog.cancel());
        alert.show();
    }

    private void onHikeResult(ActivityResult result) {
        Log.d(TAG, "onHikeResult: " + result);

        if (result != null && result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Hike hike = (Hike) result.getData().getSerializableExtra("Hike");
            Log.d(TAG, "onHikeResult: " + hike);
            if (hike == null) {
                return;
            }

            int index = hikes.indexOf(hike);
            if (index != -1) {
                Log.d(TAG, "onHikeResult: update " + hike);

                hikes.set(index, hike);
                Toast.makeText(this, "Update Success!", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "onHikeResult: add " + hike);

                hikes.add(hike);
                Toast.makeText(this, "Add Success!", Toast.LENGTH_SHORT).show();
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void deleteAll() {
        if (databaseHelper.deleteAllHikes()) {
            hikes.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Delete Success!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "An error occurred. Please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void delete(Hike hike) {
        Log.d(TAG, "delete: " + hike);

        if (databaseHelper.deleteHike(hike)) {
            int index = hikes.indexOf(hike);
            if (index != -1) {
                hikes.remove(index);
                adapter.notifyDataSetChanged();
            }
            Toast.makeText(this, "Delete Success!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "An error occurred. Please try again", Toast.LENGTH_SHORT).show();
        }
    }
}
