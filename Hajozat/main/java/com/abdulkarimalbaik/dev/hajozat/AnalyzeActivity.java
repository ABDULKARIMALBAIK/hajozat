package com.abdulkarimalbaik.dev.hajozat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.abdulkarimalbaik.dev.hajozat.Adapter.AnalyzeFragmentAdapter;
import com.abdulkarimalbaik.dev.hajozat.Common.Common;
import com.abdulkarimalbaik.dev.hajozat.Fragments.BookingFragment;
import com.abdulkarimalbaik.dev.hajozat.Fragments.PaymentsFragment;
import com.abdulkarimalbaik.dev.hajozat.Fragments.RatingFragment;
import com.abdulkarimalbaik.dev.hajozat.Fragments.TopBookingFragment;
import com.abdulkarimalbaik.dev.hajozat.Fragments.TopPriceFragment;
import com.abdulkarimalbaik.dev.hajozat.Fragments.TopRatingFragment;
import com.abdulkarimalbaik.dev.hajozat.Model.AnalyzeDataPercent;
import com.abdulkarimalbaik.dev.hajozat.Model.AnalyzeDataYear;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AnalyzeActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    AnalyzeFragmentAdapter adapter;

    int positionTab = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Note: add this code before setContentView method
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/indie_flower.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_analyze);

        initViews();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Analyzes");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);

        adapter = new AnalyzeFragmentAdapter(getSupportFragmentManager() , AnalyzeActivity.this);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        View viewPayments = getLayoutInflater().inflate(R.layout.action_bar_payments , null);
        View viewBookings = getLayoutInflater().inflate(R.layout.action_bar_bookings , null);
        View viewRatings = getLayoutInflater().inflate(R.layout.action_bar_ratings , null);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_payments_on_white_24dp);
        tabLayout.getTabAt(1).setCustomView(viewPayments);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_booking_white_24dp);
        tabLayout.getTabAt(3).setCustomView(viewBookings);
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_ratings_white_24dp);
        tabLayout.getTabAt(5).setCustomView(viewRatings);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                positionTab = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.analyze_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_export_csv){

            switch (positionTab){

                case 0:{

                    exportPayments();
                    break;
                }
                case 1:{

                    exportTopPayments();
                    break;
                }
                case 2:{

                    exportBookings();
                    break;
                }
                case 3:{

                    exportTopBookings();

                    break;
                }
                case 4:{

                    exportRatings();
                    break;
                }
                case 5:{

                    exportTopRating();
                    break;
                }
            }
        }


        return super.onOptionsItemSelected(item);
    }

    private void exportTopRating() {

        Dexter.withActivity(AnalyzeActivity.this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()){

                            TopRatingFragment topRatingsFragment = (TopRatingFragment)adapter.getItem(positionTab);

                            List<AnalyzeDataPercent> topRatings = topRatingsFragment.getAnalyzes();
                            List<String[]> data = new ArrayList<String[]>();

                            Workbook wb = new HSSFWorkbook();
                            Cell cell=null;
                            CellStyle cellStyle = wb.createCellStyle();
                            cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
                            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

                            //Now we are creating sheet
                            Sheet sheet = null;
                            sheet = wb.createSheet("Payments Sheet");
                            //Width of cells
                            sheet.setColumnWidth(0,(10*200));
                            sheet.setColumnWidth(1,(10*200));
                            Log.d("1-" , "OK");

                            if (topRatings.size() > 1){

                                for (int i = 0; i < topRatings.size(); i++) {

                                    //Now column and row
                                    Row row = sheet.createRow(i);

                                    //First Cell
                                    cell=row.createCell(0);
                                    cell.setCellValue(topRatings.get(i).getName());
                                    cell.setCellStyle(cellStyle);
                                    //Second Cell
                                    cell=row.createCell(1);
                                    cell.setCellValue(topRatings.get(i).getPercent());
                                    cell.setCellStyle(cellStyle);

                                }

                                Log.d("2-" , "OK");

                                Common.createCSVFile("_TopRatings" , AnalyzeActivity.this , wb);

                            }
                            else
                                Toast.makeText(AnalyzeActivity.this, "Data is null", Toast.LENGTH_SHORT).show();

                        }
                        else
                            Toast.makeText(AnalyzeActivity.this, "You can't export as CSV file !", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
                .check();

    }

    private void exportRatings() {

        Dexter.withActivity(AnalyzeActivity.this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()){

                            RatingFragment ratingFragment = (RatingFragment)adapter.getItem(positionTab);

                            List<AnalyzeDataYear> ratings = ratingFragment.getAnalyzes();
                            List<String[]> data = new ArrayList<String[]>();

                            Workbook wb = new HSSFWorkbook();
                            Cell cell=null;
                            CellStyle cellStyle = wb.createCellStyle();
                            cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
                            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

                            //Now we are creating sheet
                            Sheet sheet = null;
                            sheet = wb.createSheet("Payments Sheet");
                            //Width of cells
                            sheet.setColumnWidth(0,(10*200));
                            sheet.setColumnWidth(1,(10*200));
                            Log.d("1-" , "OK");

                            if (ratings.size() > 1){

                                for (int i = 0; i < ratings.size(); i++) {

                                    //Now column and row
                                    Row row = sheet.createRow(i);

                                    //First Cell
                                    cell=row.createCell(0);
                                    cell.setCellValue(ratings.get(i).getMonth());
                                    cell.setCellStyle(cellStyle);
                                    //Second Cell
                                    cell=row.createCell(1);
                                    cell.setCellValue(ratings.get(i).getData());
                                    cell.setCellStyle(cellStyle);

                                }

                                Log.d("2-" , "OK");

                                Common.createCSVFile("_Ratings" , AnalyzeActivity.this , wb);

                            }
                            else
                                Toast.makeText(AnalyzeActivity.this, "Data is null", Toast.LENGTH_SHORT).show();

                        }
                        else
                            Toast.makeText(AnalyzeActivity.this, "", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
                .check();


    }

    private void exportTopBookings() {

        Dexter.withActivity(AnalyzeActivity.this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()){

                            TopBookingFragment topBookingsFragment = (TopBookingFragment)adapter.getItem(positionTab);

                            List<AnalyzeDataPercent> topBookings = topBookingsFragment.getAnalyzes();
                            List<String[]> data = new ArrayList<String[]>();

                            Workbook wb = new HSSFWorkbook();
                            Cell cell=null;
                            CellStyle cellStyle = wb.createCellStyle();
                            cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
                            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

                            //Now we are creating sheet
                            Sheet sheet = null;
                            sheet = wb.createSheet("Payments Sheet");
                            //Width of cells
                            sheet.setColumnWidth(0,(10*200));
                            sheet.setColumnWidth(1,(10*200));
                            Log.d("1-" , "OK");

                            if (topBookings.size() > 1){

                                for (int i = 0; i < topBookings.size(); i++) {

                                    //Now column and row
                                    Row row = sheet.createRow(i);

                                    //First Cell
                                    cell=row.createCell(0);
                                    cell.setCellValue(topBookings.get(i).getName());
                                    cell.setCellStyle(cellStyle);
                                    //Second Cell
                                    cell=row.createCell(1);
                                    cell.setCellValue(topBookings.get(i).getPercent());
                                    cell.setCellStyle(cellStyle);

                                }

                                Log.d("2-" , "OK");

                                Common.createCSVFile("_TopBookings" , AnalyzeActivity.this , wb);

                            }
                            else
                                Toast.makeText(AnalyzeActivity.this, "Data is null", Toast.LENGTH_SHORT).show();

                        }
                        else
                            Toast.makeText(AnalyzeActivity.this, "You can't export as CSV file !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
                .check();

    }

    private void exportBookings() {

        Dexter.withActivity(AnalyzeActivity.this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()){

                            BookingFragment bookingsFragment = (BookingFragment)adapter.getItem(positionTab);

                            List<AnalyzeDataYear> bookings = bookingsFragment.getAnalyzes();
                            List<String[]> data = new ArrayList<String[]>();

                            Workbook wb = new HSSFWorkbook();
                            Cell cell=null;
                            CellStyle cellStyle = wb.createCellStyle();
                            cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
                            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

                            //Now we are creating sheet
                            Sheet sheet = null;
                            sheet = wb.createSheet("Payments Sheet");
                            //Width of cells
                            sheet.setColumnWidth(0,(10*200));
                            sheet.setColumnWidth(1,(10*200));
                            Log.d("1-" , "OK");

                            if (bookings.size() > 1){

                                for (int i = 0; i < bookings.size(); i++) {

                                    //Now column and row
                                    Row row = sheet.createRow(i);

                                    //First Cell
                                    cell=row.createCell(0);
                                    cell.setCellValue(bookings.get(i).getMonth());
                                    cell.setCellStyle(cellStyle);
                                    //Second Cell
                                    cell=row.createCell(1);
                                    cell.setCellValue(bookings.get(i).getData());
                                    cell.setCellStyle(cellStyle);
                                }

                                Log.d("2-" , "OK");

                                Common.createCSVFile("_Bookings" , AnalyzeActivity.this , wb);

                            }
                            else
                                Toast.makeText(AnalyzeActivity.this, "Data is null", Toast.LENGTH_SHORT).show();

                        }
                        else
                            Toast.makeText(AnalyzeActivity.this, "You can't export as CSV file !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
                .check();






    }

    private void exportTopPayments() {

        Dexter.withActivity(AnalyzeActivity.this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()){

                            TopPriceFragment topPaymentsFragment = (TopPriceFragment)adapter.getItem(positionTab);

                            List<AnalyzeDataPercent> topPayments = topPaymentsFragment.getAnalyzes();
                            List<String[]> data = new ArrayList<String[]>();

                            Workbook wb = new HSSFWorkbook();
                            Cell cell=null;
                            CellStyle cellStyle = wb.createCellStyle();
                            cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
                            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

                            //Now we are creating sheet
                            Sheet sheet = null;
                            sheet = wb.createSheet("Payments Sheet");
                            //Width of cells
                            sheet.setColumnWidth(0,(10*200));
                            sheet.setColumnWidth(1,(10*200));
                            Log.d("1-" , "OK");

                            if (topPayments.size() > 1){

                                for (int i = 0; i < topPayments.size(); i++) {

                                    //Now column and row
                                    Row row = sheet.createRow(i);

                                    //First Cell
                                    cell=row.createCell(0);
                                    cell.setCellValue(topPayments.get(i).getName());
                                    cell.setCellStyle(cellStyle);
                                    //Second Cell
                                    cell=row.createCell(1);
                                    cell.setCellValue(topPayments.get(i).getPercent());
                                    cell.setCellStyle(cellStyle);
                                }

                                Log.d("2-" , "OK");

                                Common.createCSVFile("_TopPayments" , AnalyzeActivity.this , wb);

                            }
                            else
                                Toast.makeText(AnalyzeActivity.this, "Data is null", Toast.LENGTH_SHORT).show();

                        }
                        else
                            Toast.makeText(AnalyzeActivity.this, "You can't export as CSV file !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
                .check();


    }

    private void exportPayments() {

        Dexter.withActivity(AnalyzeActivity.this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()){

                            PaymentsFragment paymentsFragment = (PaymentsFragment)adapter.getItem(positionTab);

                            List<AnalyzeDataYear> payments = paymentsFragment.getAnalyzes();
                            List<String[]> data = new ArrayList<String[]>();

                            Workbook wb = new HSSFWorkbook();
                            Cell cell=null;
                            CellStyle cellStyle = wb.createCellStyle();
                            cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
                            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

                            //Now we are creating sheet
                            Sheet sheet = null;
                            sheet = wb.createSheet("Payments Sheet");
                            //Width of cells
                            sheet.setColumnWidth(0,(10*200));
                            sheet.setColumnWidth(1,(10*200));
                            Log.d("1-" , "OK");

                            if (payments.size() > 1){

                                for (int i = 0; i < payments.size(); i++) {

                                    //Now column and row
                                    Row row = sheet.createRow(i);

                                    //First Cell
                                    cell=row.createCell(0);
                                    cell.setCellValue(payments.get(i).getMonth());
                                    cell.setCellStyle(cellStyle);
                                    //Second Cell
                                    cell=row.createCell(1);
                                    cell.setCellValue(payments.get(i).getData());
                                    cell.setCellStyle(cellStyle);

                                }
                                Log.d("2-" , "OK");

                                Common.createCSVFile("_Payments" , AnalyzeActivity.this , wb);

                            }
                            else
                                Toast.makeText(AnalyzeActivity.this, "The Data is empty !", Toast.LENGTH_SHORT).show();

                        }
                        else
                            Toast.makeText(AnalyzeActivity.this, "You can't export as CSV file !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
        .check();


    }

    private void initViews() {

        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        viewPager = (ViewPager)findViewById(R.id.viewPager);
    }
}
