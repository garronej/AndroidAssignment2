package it.polito.mobile.laboratory3.Picasso;

import android.content.Context;
import android.util.Log;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import java.io.IOException;
import java.net.URL;

import it.polito.mobile.laboratory3.s3client.Util;

final class Data {
    private final static String TAG = "S3Util";
//    static final String[] urls = {
//            "eu-west-1:190d69d0-4193-45db-870f-6a39d26209d1/photo/student3/17098.jpeg",
//            "eu-west-1:190d69d0-4193-45db-870f-6a39d26209d1/photo/student3/17099.jpeg",
//            "eu-west-1:190d69d0-4193-45db-870f-6a39d26209d1/photo/student3/17091.jpeg",
//            "eu-west-1:190d69d0-4193-45db-870f-6a39d26209d1/photo/student3/17095.jpeg",
//            "eu-west-1:190d69d0-4193-45db-870f-6a39d26209d1/photo/student3/17097.jpeg",
//    };

    private Data() {
        // No instances.
    }

    public static String[] getUrls(Context context, String[] urls) {
        String[] ret = new String[urls.length];
        int i = 0;
        for (String url : urls) {
            try {
                ret[i++] = GeneratePreSignedUrl.daje(context, url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    private static class GeneratePreSignedUrl {
        private static String bucketName = "polijobs";
        //private String objectKey  =  "eu-west-1:190d69d0-4193-45db-870f-6a39d26209d1/photo/student3/17099.jpeg";

        public static String daje(Context context, String objectKey) throws IOException {
            AmazonS3 s3client = Util.getS3Client(context);
            URL url = null;
            try {
                Log.d(TAG, "Generating pre-signed URL.");
                java.util.Date expiration = new java.util.Date();
                long milliSeconds = expiration.getTime();
                milliSeconds += 1000 * 60 * 60; // Add 1 hour.
                expiration.setTime(milliSeconds);

                GeneratePresignedUrlRequest generatePresignedUrlRequest =
                        new GeneratePresignedUrlRequest(bucketName, objectKey);
                generatePresignedUrlRequest.setMethod(HttpMethod.GET);
                generatePresignedUrlRequest.setExpiration(expiration);

                url = s3client.generatePresignedUrl(generatePresignedUrlRequest);

                Log.d(TAG, "Pre-Signed URL = " + url.toString());

            } catch (AmazonServiceException exception) {
                Log.e(TAG, "Caught an AmazonServiceException, " +
                        "which means your request made it " +
                        "to Amazon S3, but was rejected with an error response " +
                        "for some reason.");
                Log.e(TAG, "Error Message: " + exception.getMessage());
                Log.e(TAG, "HTTP  Code: " + exception.getStatusCode());
                Log.e(TAG, "AWS Error Code:" + exception.getErrorCode());
                Log.e(TAG, "Error Type:    " + exception.getErrorType());
                Log.e(TAG, "Request ID:    " + exception.getRequestId());
            } catch (AmazonClientException ace) {
                Log.e(TAG, "Caught an AmazonClientException, " +
                        "which means the client encountered " +
                        "an internal error while trying to communicate" +
                        " with S3, " +
                        "such as not being able to access the network.");
                Log.e(TAG, "Error Message: " + ace.getMessage());
            }
            return url.toString();
        }
    }
}
