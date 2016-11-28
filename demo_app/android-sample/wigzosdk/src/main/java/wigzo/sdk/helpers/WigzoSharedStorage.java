package wigzo.sdk.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import wigzo.sdk.model.EventInfo;

/**
 * This class provides a persistence layer for the sdk.
 *
 *  @author Minaz Ali
 */

public class WigzoSharedStorage {

    public SharedPreferences getSharedStorage() {
        return sharedStorage;
    }

    private static SharedPreferences sharedStorage = null;

    /**
     * Constructs a WigzoStore object.
     * @param context used to retrieve storage meta data, must not be null.
     * @throws IllegalArgumentException if context is null
     */
    public WigzoSharedStorage(final Context context) {
        if (context == null) {
            throw new IllegalArgumentException("must provide valid context");
        }
        sharedStorage = context.getSharedPreferences(Configuration.STORAGE_KEY.value, Context.MODE_PRIVATE);
    }
}