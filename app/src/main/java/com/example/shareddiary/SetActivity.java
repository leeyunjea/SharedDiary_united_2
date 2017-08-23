package com.example.shareddiary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.R.attr.data;

public class SetActivity extends AppCompatActivity {

    final int REQ_CODE_SELECT_IMAGE=100;
    Bitmap choiceImage = null;
    Bitmap image_bitmap_copy = null;
    Bitmap image_bitmap = null;

    String imageName = null;
    String img_path = new String();
    String serverURL = "http://192.168.0.33/AndroidProject_SharedDiary/setting.jsp";

    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());


        Button profileChangeButton = (Button) findViewById(R.id.profileChange);
        Button profileUpdateButton = (Button)findViewById(R.id.profileUpdate);
        ImageView profile = (ImageView) findViewById(R.id.profileImage);

        profileChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mediaScanIntent = new Intent(Intent.ACTION_PICK);
                mediaScanIntent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                mediaScanIntent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(mediaScanIntent, REQ_CODE_SELECT_IMAGE);
            }
        });

        // 사진을 JSP 로 커리 전달
        profileUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoFileUpLoad(serverURL , img_path);
                Toast.makeText(getApplicationContext(), "이미지 전송 성공", Toast.LENGTH_SHORT).show();
                Log.d("seongwon", "Success");

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Toast.makeText(getBaseContext(), "resultCode : " + data, Toast.LENGTH_SHORT).show();

        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    img_path = getImagePathToUri(data.getData()); //이미지의 URI를 얻어 경로값으로 반환.
                    Toast.makeText(getBaseContext(), "img_path : " + img_path, Toast.LENGTH_SHORT).show();
                    //이미지를 비트맵형식으로 반환
                    image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    int reWidth = (int) (getWindowManager().getDefaultDisplay().getWidth());
                    int reHeight = (int) (getWindowManager().getDefaultDisplay().getHeight());

                    //image_bitmap 으로 받아온 이미지의 사이즈를 임의적으로 조절함. width: 400 , height: 300
                    image_bitmap_copy = Bitmap.createScaledBitmap(image_bitmap, 400, 300, true);
                    ImageView image = (ImageView) findViewById(R.id.profileImage);  //이미지를 띄울 위젯 ID값
                    image.setImageBitmap(image_bitmap_copy);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /* onActivityResult 버전 1
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            if(requestCode == REQ_CODE_SELECT_IMAGE)
            {
                if(resultCode== Activity.RESULT_OK)
                {
                    try {
                        //Uri에서 이미지 이름을 얻어온다.
                        //String name_Str = getImageNameToUri(data.getData());

                        //이미지 데이터를 비트맵으로 받아온다.
                        Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        ImageView image = (ImageView)findViewById(R.id.profileImage);

                        //배치해놓은 ImageView에 set
                        image.setImageBitmap(image_bitmap);


                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        } // end of onActivityResult()

        */
    public String getImagePathToUri(Uri data) {
        //사용자가 선택한 이미지의 정보를 받아옴
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        //이미지의 경로 값
        String imgPath = cursor.getString(column_index);
        Log.d("seongwon", imgPath);

        //이미지의 이름 값
        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);
        //Toast.makeText(setting.this, "이미지 이름 : " + imgName, Toast.LENGTH_SHORT).show();
        this.imageName = imgName;

        return imgPath;
    }//end of getImagePathToUri()


    public void DoFileUpLoad(String apiUrl, String absolutePath){
        HttpFileUpload(apiUrl , "" , absolutePath);
    } // end of DoFileUpLoad()

    public void HttpFileUpload(String urlString , String params, String fileName){
        try{
            FileInputStream mFileInputStream = new FileInputStream(fileName);
            URL connectUrl = new URL(urlString);
            Log.d("seongwon" , "mFileInputStream is" + mFileInputStream );

            // HttpURLConnection 통신 시작
            // URL 을 연결 , conn 은 웹서버와 연결할때의 객체
            HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection" , "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            Log.d("seongwon" , "conn 성공");

            //write data 하는 과정
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            Log.d("seongwon" , "write data 성공1");
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            Log.d("seongwon" , "write data 성공2");
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\"" + fileName + "\"" + lineEnd );
            Log.d("seongwon" , "write data 성공3");
            dos.writeBytes(lineEnd);
            Log.d("seongwon" , "write data 성공4");

            int bytesAvailable = mFileInputStream.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            byte[] buffer = new byte[bufferSize];
            int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

            Log.d("seongwon", "image byte is " + bytesRead);

            // read image
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = mFileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            Log.e("seongwon", "File is written");
            mFileInputStream.close();
            dos.flush();
            // finish upload...

            // get response
            InputStream is = conn.getInputStream();

            StringBuffer b = new StringBuffer();
            for (int ch = 0; (ch = is.read()) != -1; ) {
                b.append((char) ch);
            }
            is.close();
            Log.e("seongwon", b.toString());


        }catch (Exception e){
            Log.d("seongwon" , "Exception " + e.getMessage());
        }

    }// end of HttpFileUpload()


}
