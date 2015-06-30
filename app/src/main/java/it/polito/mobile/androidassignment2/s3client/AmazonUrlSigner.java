package it.polito.mobile.androidassignment2.s3client;

import android.content.Context;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import java.io.IOException;
import java.net.URL;


/**
 * Created by mark9 on 15/06/15.
 */
public final class AmazonUrlSigner {

    public static String[] getUrls(Context context, String[] urls) {
        String[] ret = new String[urls.length];
        int i = 0;
        for (String url : urls) {
            try {
                ret[i++] = AmazonUrlSigner.getPreSignedUrl(context, url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    private static String getPreSignedUrl(Context context, String objectKey) throws IOException {
        AmazonS3 s3client = Util.getS3Client(context);
        URL url = null;
        try {
            java.util.Date expiration = new java.util.Date();
            long milliSeconds = expiration.getTime();
            milliSeconds += 1000 * 60 * 60; // Add 1 hour.
            expiration.setTime(milliSeconds);

            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(Constants.BUCKET_NAME, objectKey);
            generatePresignedUrlRequest.setMethod(HttpMethod.GET);
            generatePresignedUrlRequest.setExpiration(expiration);

            url = s3client.generatePresignedUrl(generatePresignedUrlRequest);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return url.toString();
    }

}
