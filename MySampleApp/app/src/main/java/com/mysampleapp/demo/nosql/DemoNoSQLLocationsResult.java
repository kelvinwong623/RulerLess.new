package com.mysampleapp.demo.nosql;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.models.nosql.LocationsDO;

import java.util.Set;

public class DemoNoSQLLocationsResult implements DemoNoSQLResult {
    private static final int KEY_TEXT_COLOR = 0xFF333333;
    private final LocationsDO result;

    DemoNoSQLLocationsResult(final LocationsDO result) {
        this.result = result;
    }
    @Override
    public void updateItem() {
        final DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        final double originalValue = result.getElevation();
        result.setElevation(DemoSampleDataGenerator.getRandomSampleNumber());
        try {
            mapper.save(result);
        } catch (final AmazonClientException ex) {
            // Restore original data if save fails, and re-throw.
            result.setElevation(originalValue);
            throw ex;
        }
    }

    @Override
    public void deleteItem() {
        final DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        mapper.delete(result);
    }

    private void setKeyTextViewStyle(final TextView textView) {
        textView.setTextColor(KEY_TEXT_COLOR);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(dp(5), dp(2), dp(5), 0);
        textView.setLayoutParams(layoutParams);
    }

    /**
     * @param dp number of design pixels.
     * @return number of pixels corresponding to the desired design pixels.
     */
    private int dp(int dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    private void setValueTextViewStyle(final TextView textView) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(dp(15), 0, dp(15), dp(2));
        textView.setLayoutParams(layoutParams);
    }

    private void setKeyAndValueTextViewStyles(final TextView keyTextView, final TextView valueTextView) {
        setKeyTextViewStyle(keyTextView);
        setValueTextViewStyle(valueTextView);
    }

    private static String bytesToHexString(byte[] bytes) {
        final StringBuilder builder = new StringBuilder();
        builder.append(String.format("%02X", bytes[0]));
        for(int index = 1; index < bytes.length; index++) {
            builder.append(String.format(" %02X", bytes[index]));
        }
        return builder.toString();
    }

    private static String byteSetsToHexStrings(Set<byte[]> bytesSet) {
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (byte[] bytes : bytesSet) {
            builder.append(String.format("%d: ", ++index));
            builder.append(bytesToHexString(bytes));
            if (index < bytesSet.size()) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    @Override
    public View getView(final Context context, final View convertView, int position) {
        final LinearLayout layout;
        final TextView resultNumberTextView;
        final TextView userIdKeyTextView;
        final TextView userIdValueTextView;
        final TextView elevationKeyTextView;
        final TextView elevationValueTextView;
        final TextView latitudeKeyTextView;
        final TextView latitudeValueTextView;
        final TextView longitudeKeyTextView;
        final TextView longitudeValueTextView;
        final TextView timeKeyTextView;
        final TextView timeValueTextView;
        final TextView usernameKeyTextView;
        final TextView usernameValueTextView;
        if (convertView == null) {
            layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            resultNumberTextView = new TextView(context);
            resultNumberTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            layout.addView(resultNumberTextView);


            userIdKeyTextView = new TextView(context);
            userIdValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(userIdKeyTextView, userIdValueTextView);
            layout.addView(userIdKeyTextView);
            layout.addView(userIdValueTextView);

            elevationKeyTextView = new TextView(context);
            elevationValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(elevationKeyTextView, elevationValueTextView);
            layout.addView(elevationKeyTextView);
            layout.addView(elevationValueTextView);

            latitudeKeyTextView = new TextView(context);
            latitudeValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(latitudeKeyTextView, latitudeValueTextView);
            layout.addView(latitudeKeyTextView);
            layout.addView(latitudeValueTextView);

            longitudeKeyTextView = new TextView(context);
            longitudeValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(longitudeKeyTextView, longitudeValueTextView);
            layout.addView(longitudeKeyTextView);
            layout.addView(longitudeValueTextView);

            timeKeyTextView = new TextView(context);
            timeValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(timeKeyTextView, timeValueTextView);
            layout.addView(timeKeyTextView);
            layout.addView(timeValueTextView);

            usernameKeyTextView = new TextView(context);
            usernameValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(usernameKeyTextView, usernameValueTextView);
            layout.addView(usernameKeyTextView);
            layout.addView(usernameValueTextView);
        } else {
            layout = (LinearLayout) convertView;
            resultNumberTextView = (TextView) layout.getChildAt(0);

            userIdKeyTextView = (TextView) layout.getChildAt(1);
            userIdValueTextView = (TextView) layout.getChildAt(2);

            elevationKeyTextView = (TextView) layout.getChildAt(3);
            elevationValueTextView = (TextView) layout.getChildAt(4);

            latitudeKeyTextView = (TextView) layout.getChildAt(5);
            latitudeValueTextView = (TextView) layout.getChildAt(6);

            longitudeKeyTextView = (TextView) layout.getChildAt(7);
            longitudeValueTextView = (TextView) layout.getChildAt(8);

            timeKeyTextView = (TextView) layout.getChildAt(9);
            timeValueTextView = (TextView) layout.getChildAt(10);

            usernameKeyTextView = (TextView) layout.getChildAt(11);
            usernameValueTextView = (TextView) layout.getChildAt(12);
        }

        resultNumberTextView.setText(String.format("#%d", + position+1));
        userIdKeyTextView.setText("userId");
        userIdValueTextView.setText(result.getUserId());
        elevationKeyTextView.setText("elevation");
        elevationValueTextView.setText("" + result.getElevation().longValue());
        latitudeKeyTextView.setText("latitude");
        latitudeValueTextView.setText("" + result.getLatitude().longValue());
        longitudeKeyTextView.setText("longitude");
        longitudeValueTextView.setText("" + result.getLongitude().longValue());
        timeKeyTextView.setText("time");
        timeValueTextView.setText(result.getTime());
        usernameKeyTextView.setText("username");
        usernameValueTextView.setText(result.getUsername());
        return layout;
    }
}
