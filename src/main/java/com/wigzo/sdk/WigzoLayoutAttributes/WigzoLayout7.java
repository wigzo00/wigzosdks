package com.wigzo.sdk.WigzoLayoutAttributes;

import android.content.Context;

import com.wigzo.sdk.R;
import com.wigzo.sdk.helpers.WigzoTypeFace;

/**
 * Created by wigzo on 4/8/17.
 */

public class WigzoLayout7 extends WigzoLayoutProperties {
    private WigzoLayout7(Context context) {
        hasImage = true;
        wigzoTitleFace = WigzoTypeFace.getFontFromRes(context, R.raw.wigzo_brown_bold);
        wigzoBodyFace = WigzoTypeFace.getFontFromRes(context, R.raw.wigzo_brown_light);
    }

    public static WigzoLayout7 getInstance(Context context) {
        return new WigzoLayout7(context);
    }
}
