package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class GenerateReportsProvider extends AppCompatActivity {

    TextView timeOfIncident,nameOfSeeker,numOfSeek,emailOfSeek,addressOfSeek,locationOfInci,print;
    ImageView bk;

    MapsActivityAidProvider maap = new MapsActivityAidProvider();
    AidProviderMainDash apm = new AidProviderMainDash();

    MainActivity ma = new MainActivity();//5/8/2023
    Date currentDT; //5/8/2023
    String emType = "";//5/8/2023
    //5/8/2023
    String nameOfSeekerpd = "";
    String emailOfSeeker = "";
    String addressOfSeeker = "";
    String numOfSeeker = "";
    //------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_reports_provider);

        timeOfIncident = (TextView) findViewById(R.id.timeD);
        locationOfInci = (TextView) findViewById(R.id.incident);
        addressOfSeek = (TextView) findViewById(R.id.seekeraddress);
        emailOfSeek = (TextView) findViewById(R.id.seekeremail);
        numOfSeek = (TextView) findViewById(R.id.seekernum);
        nameOfSeeker = (TextView) findViewById(R.id.seekerName);


        print = (TextView) findViewById(R.id.switchrole);

        bk = (ImageView) findViewById(R.id.back);



        //Date currentDT = Calendar.getInstance().getTime(); commented on 5/8/2023
        currentDT = Calendar.getInstance().getTime();//5/8/2023

        timeOfIncident.setText("Time & Date of Incident : "+currentDT.toString());
        locationOfInci.setText("Location of Incident : "+maap.locationOfIncident);

        gettingSeekerData();


        bk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GenerateReportsProvider.this,AidProviderMainDash.class);
                startActivity(intent);
            }
        });

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    createPdfreport();
                }catch (FileNotFoundException e)
                {

                }
            }
        });

    }

    public void gettingSeekerData()
    {
        DatabaseReference dbseek = FirebaseDatabase.getInstance().getReference("Aid-Seeker");

        dbseek.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                dbseek.child(apm.seeker_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            if(task.getResult().exists())
                            {
                                DataSnapshot snaps = task.getResult();

                                nameOfSeeker.setText("Full Name of Aid - Seeker: "+String.valueOf(snaps.child("fname").getValue())+" "+String.valueOf(snaps.child("lname").getValue()));
                                numOfSeek.setText("Number of Aid - Seeker : "+String.valueOf(snaps.child("phonenum").getValue()));
                                emailOfSeek.setText("Email of Aid - Seeker : "+String.valueOf(snaps.child("email").getValue()));
                                addressOfSeek.setText("Address of Aid - Seeker : "+String.valueOf(snaps.child("address").getValue()));

                                //5/8/2023
                                nameOfSeekerpd = String.valueOf(snaps.child("fname").getValue())+" "+String.valueOf(snaps.child("lname").getValue());
                                emailOfSeeker = String.valueOf(snaps.child("email").getValue());
                                addressOfSeeker = String.valueOf(snaps.child("address").getValue());
                                numOfSeeker = String.valueOf(snaps.child("phonenum").getValue());
                                //----

                            }
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*public void printing(View view)
    {





        //View view1 = view.getRootView();
        View view1 = getWindow().getDecorView().getRootView();

        Bitmap bitmap = Bitmap.createBitmap(view1.getWidth(), view1.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view1.draw(canvas);
        File fileScreenshot = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                Calendar.getInstance().getTime().toString()+".jpg");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileScreenshot);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Toast.makeText(GenerateReportsForAllEmergency.this,"Copy Saved!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(GenerateReportsForAllEmergency.this,AidSeekerMainDash.class);
            startActivity(intent);
        }
        catch (Exception e){
            Toast.makeText(GenerateReportsForAllEmergency.this,"Failed!",Toast.LENGTH_SHORT).show();
        }
    }*/ //commented on 5/8/2023 original

    //5/8/2023
    public void createPdfreport() throws FileNotFoundException
    {
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath,"lifeaidreport.pdf");
        OutputStream outputStream = new FileOutputStream(file);

        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        DeviceRgb grn = new DeviceRgb(51,204,51);
        DeviceRgb gry = new DeviceRgb(220,220,220);

        float columnWidth[] = {140,140,140,140};
        Table table1 = new Table(columnWidth);

        Drawable d1 = getDrawable(R.drawable.searchmap);
        Bitmap bitmap1 = ((BitmapDrawable)d1).getBitmap();
        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.PNG, 100,stream1);
        byte[] bitmapData1 = stream1.toByteArray();

        ImageData imageData1 = ImageDataFactory.create(bitmapData1);
        Image image1 = new Image(imageData1);
        image1.setWidth(100f);

        table1.addCell(new Cell(4,1).add(image1).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell(1,2).add(new Paragraph("LifeAid Transaction Report").setFontSize(26f).setFontColor(grn)).setBorder(Border.NO_BORDER));

        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("Transaction Date: ")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph(currentDT.toString())).setBorder(Border.NO_BORDER));

        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("Incident Location: ")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph(maap.locationOfIncident)).setBorder(Border.NO_BORDER));

        //table1.addCell(new Cell().add(new Paragraph("")));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));

        table1.addCell(new Cell().add(new Paragraph("\n")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));

        table1.addCell(new Cell().add(new Paragraph("To: ").setBold()).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));

        table1.addCell(new Cell().add(new Paragraph(ma.fullname + "( Aid - Provider )")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));

        table1.addCell(new Cell().add(new Paragraph(ma.address)).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));

        table1.addCell(new Cell().add(new Paragraph(ma.phonenum)).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));

        float columnWidth2[] = {280,280};

        Table table2 = new Table(columnWidth2);

        table2.addCell(new Cell().add(new Paragraph("Details").setFontColor(ColorConstants.WHITE)).setBackgroundColor(grn));
        table2.addCell(new Cell().add(new Paragraph("Contents").setFontColor(ColorConstants.WHITE)).setBackgroundColor(grn));

        table2.addCell(new Cell().add(new Paragraph("Time Aided")).setBackgroundColor(gry));
        table2.addCell(new Cell().add(new Paragraph(maap.timeAided)).setBackgroundColor(gry));


        table2.addCell(new Cell().add(new Paragraph("Name of Aid - Seeker")).setBackgroundColor(gry));
        table2.addCell(new Cell().add(new Paragraph(nameOfSeekerpd)).setBackgroundColor(gry));

        table2.addCell(new Cell().add(new Paragraph("Email of Aid - Seeker")).setBackgroundColor(gry));
        table2.addCell(new Cell().add(new Paragraph(emailOfSeeker)).setBackgroundColor(gry));

        table2.addCell(new Cell().add(new Paragraph("Address of Aid - Seeker")).setBackgroundColor(gry));
        table2.addCell(new Cell().add(new Paragraph(addressOfSeeker)).setBackgroundColor(gry));

        table2.addCell(new Cell().add(new Paragraph("Contact Num of Aid - Seeker")).setBackgroundColor(gry));
        table2.addCell(new Cell().add(new Paragraph(numOfSeeker)).setBackgroundColor(gry));



        float columnWidth3[] = {50,250,260};
        Table table3 = new Table(columnWidth3);



        Drawable d2 = getDrawable(R.drawable.phonebook);
        Bitmap bitmap2 = ((BitmapDrawable)d2).getBitmap();
        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        bitmap2.compress(Bitmap.CompressFormat.PNG, 100,stream2);
        byte[] bitmapData2 = stream2.toByteArray();

        ImageData imageData2 = ImageDataFactory.create(bitmapData2);
        Image image2 = new Image(imageData2);
        image2.setHeight(40);

        Drawable d3 = getDrawable(R.drawable.emailpdf);
        Bitmap bitmap3 = ((BitmapDrawable)d3).getBitmap();
        ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
        bitmap3.compress(Bitmap.CompressFormat.PNG, 100,stream3);
        byte[] bitmapData3 = stream3.toByteArray();

        ImageData imageData3 = ImageDataFactory.create(bitmapData3);
        Image image3 = new Image(imageData3);
        image3.setHeight(40);

        Drawable d4 = getDrawable(R.drawable.uc);
        Bitmap bitmap4 = ((BitmapDrawable)d4).getBitmap();
        ByteArrayOutputStream stream4 = new ByteArrayOutputStream();
        bitmap4.compress(Bitmap.CompressFormat.PNG, 100,stream4);
        byte[] bitmapData4 = stream4.toByteArray();

        ImageData imageData4 = ImageDataFactory.create(bitmapData4);
        Image image4 = new Image(imageData4);
        image4.setHeight(120);

        image4.setHorizontalAlignment(HorizontalAlignment.RIGHT);

        table3.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table3.addCell(new Cell().add(new Paragraph("Admin Details")).setBorder(Border.NO_BORDER));
        table3.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));

        table3.addCell(new Cell().add(image2).setBorder(Border.NO_BORDER));
        table3.addCell(new Cell().add(new Paragraph("+63 9652202568\n+63 096522025658\n+63 09622562568")).setBorder(Border.NO_BORDER));
        table3.addCell(new Cell(2,1).add(image4).setBorder(Border.NO_BORDER));

        table3.addCell(new Cell().add(image3).setBorder(Border.NO_BORDER));
        table3.addCell(new Cell().add(new Paragraph("rogerjaysering@gmail.com\nrjstesting@gmail.com")).setBorder(Border.NO_BORDER));
        table3.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));


        document.add(table1);
        document.add(new Paragraph("\n"));
        document.add(table2);
        document.add(new Paragraph("\n\n"));
        document.add(table3);


        document.close();
        Toast.makeText(GenerateReportsProvider.this,"PDF downloaded!",Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(GenerateReportsProvider.this,AidProviderMainDash.class);
        startActivity(intent);

    }


}