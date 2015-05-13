/*
 * Copyright 2010-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package it.polito.mobile.androidassignment2.s3client;

public class Constants {
    // You should replace these values with your own
    // See the readme for details on what to fill in
    public static final String AWS_ACCOUNT_ID = "165580128231";
    public static final String COGNITO_POOL_ID =
            "eu-west-1:c8b54270-122d-40d3-8dd0-feca9e314480";
    public static final String COGNITO_ROLE_UNAUTH =
            "arn:aws:iam::165580128231:role/Cognito_polijobUnauth_Role";
    // Note, the bucket will be created in all lower case letters
    // If you don't enter an all lower case title, any references you add
    // will need to be sanitized
    public static final String BUCKET_NAME = "polijobs";
}
