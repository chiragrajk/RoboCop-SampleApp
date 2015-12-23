package no.connectica.robocop.data;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import no.connectica.robocop.data.database.table.AgendaTable;
import no.connectica.robocop.data.provider.AgendaProvider;

/**
 * Created by Chirag on 12/22/15.
 */
public class TestProvider extends AndroidTestCase {

    private static String LOG_TAG = ">> " + TestProvider.class.getSimpleName();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                AgendaProvider.class.getName());

        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            assertEquals("TestError: AgendaProvider registered with authority: " + providerInfo.authority
                    + " instead of authority: " + AgendaProvider.AUTHORITY,
                    providerInfo.authority, AgendaProvider.AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            assertTrue("TestError: AgendaProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {
        Uri uri;
        String expectedType, actualType;

        Log.d(LOG_TAG, "    ****    ****");
        uri = AgendaProvider.AGENDA_CONTENT_URI;
        Log.d(LOG_TAG, "agenda_content_uri: " + uri);
        actualType = mContext.getContentResolver().getType(uri);
        Log.d(LOG_TAG, "actual type:   " + actualType);
        expectedType = AgendaProvider.AgendaContent.CONTENT_TYPE;
        Log.d(LOG_TAG, "expected type: " + expectedType);
        assertEquals("TestError: the Agenda CONTENT_URI should return AgendaProvider.AgendaContent.CONTENT_TYPE",
                expectedType, actualType);


        Log.d(LOG_TAG, "    ****    ****");
        uri = AgendaProvider.AGENDA_ITEM_CONTENT_URI;
        Log.d(LOG_TAG, "agenda_content_uri: " + uri);
        actualType = mContext.getContentResolver().getType(AgendaProvider.AGENDA_ITEM_CONTENT_URI);
        Log.d(LOG_TAG, "actual type:   " + actualType);
        expectedType = AgendaProvider.AgendaItemContent.CONTENT_TYPE;
        Log.d(LOG_TAG, "expected type: " + expectedType);
        assertEquals("TestError: the Agenda CONTENT_URI should return AgendaProvider.AgendaContent.CONTENT_TYPE",
                expectedType, actualType);


        Log.d(LOG_TAG, "    ****    ****");
        uri = AgendaProvider.AGENDA_ITEM_GROUP_CONTENT_URI.buildUpon().appendPath("23").build();
        Log.d(LOG_TAG, "agenda_content_uri: " + uri);
        actualType =  mContext.getContentResolver().getType(uri);
        Log.d(LOG_TAG, "actual_type:   " + actualType);
        expectedType = AgendaProvider.AgendaItemGroupContent.CONTENT_ITEM_TYPE;
        Log.d(LOG_TAG, "expected_type: " + expectedType);
        assertEquals("TestError: the Agenda CONTENT_URI should return AgendaProvider.AgendaContent.CONTENT_TYPE",
                expectedType, actualType);

    }
}
