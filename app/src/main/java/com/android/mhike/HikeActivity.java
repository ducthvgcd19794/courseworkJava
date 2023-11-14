package com.android.mhike;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;

public class HikeActivity extends AppCompatActivity {

    private static final String TAG = HikeActivity.class.getSimpleName();

    private EditText edtName, edtLocation, edtDescription, edtLength;
    private AppCompatSpinner spinnerLevel;
    private TextView edtDate;
    private RadioButton radioYes, radioNo;

    private DatabaseHelper mDatabaseHelper;
    private Hike mHike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike);

        mDatabaseHelper = new DatabaseHelper(this);

        edtName = findViewById(R.id.edt_name);
        edtLocation = findViewById(R.id.edt_location);
        edtDate = findViewById(R.id.edt_date);
        edtDescription = findViewById(R.id.edt_description);
        edtLength = findViewById(R.id.edt_length);
        spinnerLevel = findViewById(R.id.spinner_level);
        radioYes = findViewById(R.id.radio_yes);
        radioNo = findViewById(R.id.radio_no);

        edtDate.setOnClickListener(v->{
            MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("Chọn ngày");
            MaterialDatePicker materialDatePicker = builder.build();
            materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                String format = "dd/MM/yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                edtDate.setText(simpleDateFormat.format(materialDatePicker.getSelection()));
            });
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.level_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLevel.setAdapter(adapter);

        loadData();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        } else if (item.getItemId() == R.id.menu_save) {
            save();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_option_save, menu);
        return true;
    }

    private void loadData() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mHike = (Hike) getIntent().getSerializableExtra("Hike");
        if (mHike == null) {
            if (actionBar != null) {
                actionBar.setTitle(R.string.add_hike);
            }
        } else {
            if (actionBar != null) {
                actionBar.setTitle(R.string.edit_hike);
            }

            edtName.setText(mHike.getName());
            edtLocation.setText(mHike.getLocation());
            edtDate.setText(mHike.getDate());
            edtDescription.setText(mHike.getDescription());
            edtLength.setText(String.valueOf(mHike.getLength()));
            spinnerLevel.setSelection(mHike.getLevel());
            if (mHike.getParkingAvailable() == 1) {
                radioYes.setChecked(true);
                radioNo.setChecked(false);
            } else {
                radioYes.setChecked(false);
                radioNo.setChecked(true);
            }
        }
    }

    private Hike createHike() {
        String name = edtName.getText().toString().trim();
        String location = edtLocation.getText().toString().trim();
        String date = edtDate.getText().toString().trim();
        int parking = radioYes.isChecked() ? 1 : 0;
        int length;
        try {
            length = Integer.parseUnsignedInt(edtLength.getText().toString());
        } catch (NumberFormatException e) {
            return null;
        }
        int level = spinnerLevel.getSelectedItemPosition();
        String description = edtDescription.getText().toString();

        if (name.isEmpty() || location.isEmpty() || date.isEmpty()) {
            return null;
        }

        return new Hike(0, name, location, date, parking, length, level, description);
    }

    private void save() {
        Hike hike = createHike();
        if (hike == null) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String finalMessage = "Name : " + hike.getName() + "\n" +
                "Location : " + hike.getLocation() + "\n" +
                "Date : " + hike.getDate() + "\n" +
                "Parking : " + (hike.getParkingAvailable() == 1 ? "Yes" : "No") + "\n" +
                "Length : " + hike.getLength() + "\n" +
                "Level : " + hike.getLevel() + "\n" +
                "Description : " + hike.getDescription();

        new AlertDialog.Builder(this)
            .setMessage(finalMessage)
            .setPositiveButton("OK", (dialog, which) -> onOkClick(hike))
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void onOkClick(Hike hike) {
        if (mHike == null) {
            long id = mDatabaseHelper.insertHike(hike);
            if (id == -1) {
                Toast.makeText(this, "An error occurred. Please try again", Toast.LENGTH_SHORT).show();
                return;
            } else {
                hike.setId((int) id);
            }
        } else {
            hike.setId(mHike.getId());
            if (!mDatabaseHelper.updateHike(hike)) {
                Toast.makeText(this, "An error occurred. Please try again", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Intent result = new Intent();
        result.putExtra("Hike", hike);
        setResult(RESULT_OK, result);
        finish();
    }
}