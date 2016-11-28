package wigzo.app;

import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import wigzo.sdk.WigzoSDK;
import wigzo.sdk.helpers.OrganizationEvents;
import wigzo.sdk.model.EventInfo;
import wigzo.sdk.model.UserProfile;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        WigzoSDK sdk = WigzoSDK.getInstance();
        sdk.initializeWigzoData(this, "56065c5b-db30-4b89-bd76-0a9c2938c90b");

    /*    sdk.initializeWigzoData(this, "2c271df3-713f-4802-ae4c-b0dec708c988", "1080912767729"); //, TargetActivity.class);
        sdk.onStart();*/

        EventInfo eventInfo4 = new EventInfo(OrganizationEvents.Events.LOGGEDIN.label,String.valueOf(System.currentTimeMillis()).toString());
        /*EventInfo.Metadata metadata = new EventInfo.Metadata("1","Iphone","Iphone 6SE",null);
        eventInfo4.setMetadata(metadata);*/
        eventInfo4.saveEvent();

        EventInfo eventInfo3 = new EventInfo("Bought","Bought");
        eventInfo3.saveEvent();

        EventInfo eventInfo = new EventInfo("view","viewed");
        EventInfo.Metadata metadata = new EventInfo.Metadata("1","Iphone","Iphone 6SE",null);
        eventInfo.setMetadata(metadata);
        eventInfo.saveEvent();

        EventInfo eventInfo1 = new EventInfo("addToCart","Add To Cart");
        EventInfo.Metadata metadata1 = new EventInfo.Metadata("2","Galaxy S","Galaxy S",null);
        metadata1.setTags("Phone");
        eventInfo1.setMetadata(metadata1);
        eventInfo1.saveEvent();

        EventInfo eventInfo2 = new EventInfo("Bought","Bought");
        EventInfo.Metadata metadata2 = new EventInfo.Metadata("3","Laptop","Lenovo",null);
        metadata2.setTags("Lenovo Flexi");
        metadata2.setPrice(new BigDecimal(45000));
        eventInfo2.setMetadata(metadata2);
        eventInfo2.saveEvent();
        sdk.gcmRegister();
        UserProfile user = new UserProfile("abc","abc","suyash@wigzo.com","wigzo.com");
        user.setPicturePath("/sdcard/Pictures/OGQ/pic.jpg");
        Map<String, String> customData = new HashMap<>();
        customData.put("key1","value1");
        customData.put("key2","value2");
        user.setCustomData(customData);
        user.saveUserProfile();
        sdk.onStop();
        //onDestroy();

    }

    public void onDeepLinkClick(View view) {
        String url = "http://www.wigzoes.com/deeplink";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //UserProfile.saveUserLoggedInStatus(true);
    }

    @Override
    protected void onStop() {
       // UserProfile.saveUserLoggedOutStatus(true);
        super.onStop();

    }
}