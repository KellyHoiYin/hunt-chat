package com.kelly.hunt_chat;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by User on 1/20/2018.
 */

public class ImageHandler {

    //            Uri selectedImage = data.getData();
//            String[] filePathColumn = {MediaStore.Images.Media.DATA };
//
//            Cursor selectedCursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
//            selectedCursor.moveToFirst();
//
//            int columnIndex = selectedCursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = selectedCursor.getString(columnIndex);
//            selectedCursor.close();
//
//            Bitmap bmp = BitmapFactory.decodeFile(picturePath);
//
//            ExifInterface exif = null;
//            try {
//                File pictureFile = new File(picturePath);
//                exif = new ExifInterface(pictureFile.getAbsolutePath());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            int orientation = ExifInterface.ORIENTATION_NORMAL;
//
//            if (exif != null)
//                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//
//            switch (orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    bmp = rotateBitmap(bmp, 90);
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    bmp = rotateBitmap(bmp, 180);
//                    break;
//
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    bmp = rotateBitmap(bmp, 270);
//                    break;
//            }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public Bitmap getResizedBitmap(Bitmap image) {
        int maxSize = 300;
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
