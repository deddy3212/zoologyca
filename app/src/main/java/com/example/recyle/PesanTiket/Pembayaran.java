package com.example.recyle.PesanTiket;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.recyle.Admin.Dashboard_Admin;
import com.example.recyle.HomeFragment;
import com.example.recyle.MainActivity;
import com.example.recyle.R;
import com.example.recyle.TambahData.TambahDataFasilitas;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static android.content.DialogInterface.BUTTON_NEGATIVE;

public class Pembayaran extends AppCompatActivity {

    ProgressDialog progressDialog;
    ActionBar actionBar;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReferenceuserDb;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    //image pick constans
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;



    //persmission array
    String [] cameraPermissions;
    String [] storagePermissions;

    ProgressDialog pd;


    EditText ed_atas_nama;
    ImageView im_gambar_upload;
    Button upload_btn;

    //user info
    String name,email,uid,dp;

    Uri image_uri = null;

    //progres bar

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_pembayaran);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Upload Bukti");
        //enable tombol kembali di action bar
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        pd = new ProgressDialog(this);
        //ini permissions array
        cameraPermissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        firebaseAuth = FirebaseAuth.getInstance();
        checkuserStatus();
        actionBar.setSubtitle(email);
        //get some info of current user to include in a post
        databaseReferenceuserDb = FirebaseDatabase.getInstance().getReference("Users");
        Query query = databaseReferenceuserDb.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    name = ""+ ds.child("name").getValue();
                    email = ""+ ds.child("email").getValue();
                    dp = ""+ ds.child("image").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //init viewes
        ed_atas_nama = findViewById(R.id.edt_atas_nama);
        im_gambar_upload = findViewById(R.id.tv_imageBukti);
        //ambil camera dan galeri dari image view
        im_gambar_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //tampilkan image pick dialog
                showImagePickDialog();

            }
        });

        // upload klik listener
        upload_btn = findViewById(R.id.btn_upload_bukti);
        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String atas_nama = ed_atas_nama.getText().toString().trim();
                if (TextUtils.isEmpty(atas_nama)){
                    Toast.makeText(Pembayaran.this, "Wajib di isi..",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    new android.app.AlertDialog.Builder(com.example.recyle.PesanTiket.Pembayaran.this)
                            .setTitle("Warning !!!")
                            .setMessage("Apakah Anda Yakin?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    uploadData(atas_nama, String.valueOf(image_uri));
                                    Intent intent = new Intent(Pembayaran.this, TiketTerbit.class);
                                    startActivity(intent);
                                    finish();
                                    //com.example.recyle.PesanTiket.Tambah_Pesanan.this.saving();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();



                    //intent

                }

            }
        });


    }

    private void uploadData(final String atas_nama, final String uri) {
        final String timeStamp = String.valueOf(System.currentTimeMillis());
        String filpathAndName = "BuktiPembayaran/" + "bukti" + timeStamp;
        if (!uri.equals("noImage")){
            //postingan dengan gambar
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filpathAndName);
            ref.putFile(Uri.parse(uri))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());

                            String downloadUri = uriTask.getResult().toString();

                            if (uriTask.isSuccessful()){

                                HashMap<Object, String> hashMap = new HashMap<>();
                                //put post info
                                hashMap.put("uid", uid);
                                hashMap.put("uName", name);
                                hashMap.put("uEmail", email);
                                hashMap.put("uDp", dp);
                                hashMap.put("pId", timeStamp);
                                hashMap.put("pAtas_nama", atas_nama);
                                hashMap.put("pImage", downloadUri);
                                hashMap.put("pTime", timeStamp);

                                //path to store post data
                                pd.dismiss();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Bukti Pembayaran");
                                //put data in this refe
                                //oke
                                databaseReference.child(timeStamp).setValue(hashMap)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //add to database
                                                pd.dismiss();
                                                Toast.makeText(Pembayaran.this, "Bukti di Sudah di Upload.." ,
                                                        Toast.LENGTH_SHORT).show();
                                                //reset views
                                                ed_atas_nama.setText("");
                                                im_gambar_upload.setImageURI(null);
                                                image_uri = null;


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failde add
                                                pd.dismiss();
                                                Toast.makeText(Pembayaran.this, "" +e.getMessage(),
                                                        Toast.LENGTH_SHORT).show();

                                            }
                                        });



                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(Pembayaran.this, ""+e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            //postingan tidak dengan gambar

            HashMap<Object, String> hashMap = new HashMap<>();
            //put post info
            hashMap.put("uid", uid);
            hashMap.put("uName", name);
            hashMap.put("uEmail", email);
            hashMap.put("uDp", dp);
            hashMap.put("pId", timeStamp);
            hashMap.put("pAtas_nama", atas_nama);
            hashMap.put("pImage", "noImage");
            hashMap.put("pTime", timeStamp);

            //path to store post data
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Bukti Pembayaran");
            //put data in this refe
            databaseReference.child(timeStamp).setValue(hashMap)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //add to database
                            pd.dismiss();
                            Toast.makeText(Pembayaran.this, "Bukti di upload.." ,
                                    Toast.LENGTH_SHORT).show();
                            //reset views
                            ed_atas_nama.setText("");
                            im_gambar_upload.setImageURI(null);
                            image_uri = null;



                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failde add
                            pd.dismiss();
                            Toast.makeText(Pembayaran.this, "" +e.getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }
                    });

        }
    }




    private void checkuserStatus() {
        //get current user
        FirebaseUser user =  firebaseAuth.getCurrentUser();
        if (user != null){
            //user is signed
            //set email of logged in user
            email = user.getEmail();
            uid = user.getUid();

            //
        }
        else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void showImagePickDialog() {
        String [] options = {"Kamera","Gallery"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Gambar Dari");
        //set options to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int pilih) {
                //item click handle

                if (pilih==0){
                    //clicked camera
                    if (!checkCameraPermissions()){
                        requestCameraPermissions();
                    }
                    else {
                        pickFromCamera();
                    }
                }
                if (pilih==1){
                    //gallery checked
                    if (!checkStoragePermissions()){
                        requestStoragePermissions();
                    }
                    else {
                        pickFromGallery();
                    }

                }
            }
        });
        builder.create().show();
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);

    }

    private void pickFromCamera() {
        //intent to pick image from camera
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Temp Descr");
        image_uri = getContentResolver().insert( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermissions(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermissions(){
        //request runtime storage permission
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermissions(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermissions(){
        //request runtime storage permission
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onBackPressed() {
        konfir();
    }

    private void konfir(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Warning !!!");
        builder.setMessage("Do you want to Logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
        Intent intent = new Intent(Pembayaran.this, UploadBukti.class);
        startActivity(intent);
                }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    //handle permissions

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if ( cameraAccepted && writeStorageAccepted){
                        pickFromCamera();
                    }
                    else {
                        Toast.makeText(this, "Camera & Storage both permissions are neccessary...",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else {

                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        pickFromGallery();
                    }
                    else {
                        Toast.makeText(this,"Please enable Storage permissions ...",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else {

                }

            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri = data.getData();

                //set to imageview
                im_gambar_upload.setImageURI(image_uri);
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                im_gambar_upload.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
