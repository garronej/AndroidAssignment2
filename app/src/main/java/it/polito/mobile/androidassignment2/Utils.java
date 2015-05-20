package it.polito.mobile.androidassignment2;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

/**
 * Created by 3v0k4 on 20/05/15.
 */
public class Utils {
    public static boolean isPdf(Context context, Uri uri) {
        String type = getExtensionOfFile(context, uri);
        return type.indexOf("pdf") != -1;
    }

    public static boolean isPicture(Context context, Uri uri) {
        String type = getExtensionOfFile(context, uri);
        return type.indexOf("png") != -1 ||
                type.indexOf("jpeg") != -1 ||
                type.indexOf("jpg") != -1;
    }

    private static String getExtensionOfFile(Context context, Uri uri) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        if (extension == null || extension.equals("")) {
            ContentResolver cR = context.getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(cR.getType(uri));
        }
        return extension;
    }
}
