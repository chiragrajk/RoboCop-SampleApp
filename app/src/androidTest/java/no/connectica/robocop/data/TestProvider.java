package no.connectica.robocop.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import junit.framework.Test;

import no.connectica.robocop.data.database.table.AgendaTable;
import no.connectica.robocop.data.model.Agenda;
import no.connectica.robocop.data.provider.AgendaProvider;
import static no.connectica.robocop.LogUtils.LOGD;
import static no.connectica.robocop.LogUtils.makeLogTag;

/**
 * Created by Chirag on 12/22/15.
 */
public class TestProvider extends AndroidTestCase {

    private static String LOG_TAG = makeLogTag(TestProvider.class);

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

//        LOGD(LOG_TAG, "    ****    ****");
        uri = AgendaProvider.AGENDA_CONTENT_URI;
//        LOGD(LOG_TAG, "agenda_content_uri: " + uri);
        actualType = mContext.getContentResolver().getType(uri);
//        LOGD(LOG_TAG, "actual type:   " + actualType);
        expectedType = AgendaProvider.AgendaContent.CONTENT_TYPE;
//        LOGD(LOG_TAG, "expected type: " + expectedType);
        assertEquals("TestError: the Agenda CONTENT_URI should return AgendaProvider.AgendaContent.CONTENT_TYPE",
                expectedType, actualType);


//        LOGD(LOG_TAG, "    ****    ****");
        uri = AgendaProvider.AGENDA_ITEM_CONTENT_URI;
//        LOGD(LOG_TAG, "agenda_content_uri: " + uri);
        actualType = mContext.getContentResolver().getType(uri);
//        LOGD(LOG_TAG, "actual type:   " + actualType);
        expectedType = AgendaProvider.AgendaItemContent.CONTENT_TYPE;
//        LOGD(LOG_TAG, "expected type: " + expectedType);
        assertEquals("TestError: the Agenda CONTENT_URI should return AgendaProvider.AgendaContent.CONTENT_TYPE",
                expectedType, actualType);


//        LOGD(LOG_TAG, "    ****    ****");
        uri = AgendaProvider.AGENDA_ITEM_GROUP_CONTENT_URI.buildUpon().appendPath("23").build();
//        LOGD(LOG_TAG, "agenda_content_uri: " + uri);
        actualType =  mContext.getContentResolver().getType(uri);
//        LOGD(LOG_TAG, "actual_type:   " + actualType);
        expectedType = AgendaProvider.AgendaItemGroupContent.CONTENT_ITEM_TYPE;
//        LOGD(LOG_TAG, "expected_type: " + expectedType);
        assertEquals("TestError: the Agenda CONTENT_URI should return AgendaProvider.AgendaContent.CONTENT_TYPE",
                expectedType, actualType);


        LOGD(LOG_TAG, "    ****    ****");
        uri = AgendaProvider.AGENDA_ITEM_GROUP_CONTENT_URI;
        LOGD(LOG_TAG, "agenda_content_uri: " + uri);
        actualType =  mContext.getContentResolver().getType(uri);
        LOGD(LOG_TAG, "actual_type:   " + actualType);
        expectedType = AgendaProvider.AgendaItemGroupContent.CONTENT_TYPE;
        LOGD(LOG_TAG, "expected_type: " + expectedType);
        assertEquals("TestError: the Agenda CONTENT_URI should return AgendaProvider.AgendaContent.CONTENT_TYPE",
                expectedType, actualType);

    }

    public void testInsertReadProvider() {
        ContentValues agendaValues = TestUtilities.createAgenda();

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(AgendaProvider.AGENDA_CONTENT_URI, true, tco);
        Uri agendaUri = mContext.getContentResolver().insert(AgendaProvider.AGENDA_CONTENT_URI, agendaValues);
        assertTrue(agendaUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long agendaRowId = ContentUris.parseId(agendaUri);

        assertTrue(agendaRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(
                AgendaProvider.AGENDA_CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("TestError: error validation AgendaEntry",
                cursor, agendaValues);

        ContentValues calendarValues = TestUtilities.createCalendarItem(agendaRowId);
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(AgendaProvider.CALENDAR_ITEM_CONTENT_URI, true, tco);

        Uri calendarInsertUri = mContext.getContentResolver()
                .insert(AgendaProvider.CALENDAR_ITEM_CONTENT_URI, calendarValues);
        assertTrue(calendarInsertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        Cursor calendarCursor = mContext.getContentResolver().query(
                AgendaProvider.CALENDAR_ITEM_CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("TestError: Error validation CalendarEntry",
                calendarCursor, calendarValues);

    }

    public void testDeleteRecords() {
        testInsertReadProvider();

        TestUtilities.TestContentObserver calendarObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(AgendaProvider.CALENDAR_ITEM_CONTENT_URI, true, calendarObserver);

        TestUtilities.TestContentObserver agendaObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(AgendaProvider.AGENDA_CONTENT_URI, true, agendaObserver);

        deleteAllRecordsFromProvider();

        calendarObserver.waitForNotificationOrFail();
        agendaObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(calendarObserver);
        mContext.getContentResolver().unregisterContentObserver(agendaObserver);

    }

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(AgendaProvider.CALENDAR_ITEM_CONTENT_URI,
                null,
                null);
        mContext.getContentResolver().delete(AgendaProvider.AGENDA_CONTENT_URI,
                null,
                null);

        Cursor cursor = mContext.getContentResolver().query(
                AgendaProvider.CALENDAR_ITEM_CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        assertEquals("TestError: Records not deleted from Calendar table during delete",
                0, cursor.getCount());

        cursor = mContext.getContentResolver().query(
                AgendaProvider.AGENDA_CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );
        assertEquals("TestError: Records not deleted from Agenda table during delete",
                0, cursor.getCount());
    }

    public void testUpdateFromProvider() {
        ContentValues agendaValues = TestUtilities.createAgenda();
        Uri agendaUri = mContext.getContentResolver()
                .insert(AgendaProvider.AGENDA_CONTENT_URI, agendaValues);

        LOGD(LOG_TAG, agendaUri.toString());
        long agendaRowId = ContentUris.parseId(agendaUri);

        assertTrue(agendaRowId != -1);
        LOGD(LOG_TAG, "new agenda ID: " + agendaRowId);

        Cursor cursor = mContext.getContentResolver().query(
                AgendaProvider.AGENDA_CONTENT_URI, //.buildUpon().appendPath(agendaRowId+"").build(),
                null,
                AgendaTable.TABLE_NAME + "." + AgendaTable._ID + " = ? ",
                new String[]{agendaRowId + ""},
                null);


//        if (cursor.getCount() > 0 && cursor.moveToFirst())
//        {
//            Agenda agenda = new Agenda(cursor);
//            LOGD(LOG_TAG, agenda.getRowId() + "");
//            LOGD(LOG_TAG, agenda.getName());
//            LOGD(LOG_TAG, agenda.getDate());
//            LOGD(LOG_TAG, agenda.getPersonConducting());
//        }

        Agenda updatedAgenda = new Agenda();
        updatedAgenda.setName("NEW name");
        updatedAgenda.setDate("NEW date");
        updatedAgenda.setPersonConducting("NEW person");
        ContentValues updatedValues = updatedAgenda.getContentValues();

        Cursor agendaCursor = mContext.getContentResolver().query(AgendaProvider.AGENDA_CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        agendaCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                AgendaProvider.AGENDA_CONTENT_URI,
                updatedValues,
                AgendaTable.TABLE_NAME + "." + AgendaTable._ID + " = ? ",
                new String[]{agendaRowId + ""});
        assertTrue("TestError: More than one Agenda was updated", count == 1);

        tco.waitForNotificationOrFail();

        agendaCursor.unregisterContentObserver(tco);
        agendaCursor.close();

        Cursor cursor1 = mContext.getContentResolver().query(
                AgendaProvider.AGENDA_CONTENT_URI,
                null,
                AgendaTable.WHERE_ID_EQUALS,
                new String[]{agendaRowId+""},
                null
        );

        TestUtilities.validateCursor("TestError: test updated agenda.", cursor1, updatedValues);
        cursor.close();
    }

}
