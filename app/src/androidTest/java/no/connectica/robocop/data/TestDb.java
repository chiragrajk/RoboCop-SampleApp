package no.connectica.robocop.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

import no.connectica.robocop.data.database.AgendaDatabase;
import no.connectica.robocop.data.database.table.AgendaItemGroupTable;
import no.connectica.robocop.data.database.table.AgendaItemTable;
import no.connectica.robocop.data.database.table.AgendaTable;
import no.connectica.robocop.data.database.table.CalendarItemTable;
import no.connectica.robocop.data.model.Agenda;
import no.connectica.robocop.data.model.AgendaItem;

/**
 * Created by Chirag on 12/22/15.
 */
public class TestDb extends AndroidTestCase {

    void deleteTheDatabase() {
        mContext.deleteDatabase(AgendaDatabase.DATABASE_NAME);
    }

    @Override
    protected void setUp() throws Exception {
        deleteTheDatabase();
    }

    public void testCreateDb() {

        final HashSet<String> tableNames = new HashSet<>();
        tableNames.add(AgendaItemGroupTable.TABLE_NAME);
        tableNames.add(AgendaItemTable.TABLE_NAME);
        tableNames.add(AgendaTable.TABLE_NAME);
        tableNames.add(CalendarItemTable.TABLE_NAME);

        SQLiteDatabase db = new AgendaDatabase(mContext).getWritableDatabase();
        assertTrue("TestError: Database is not open", db.isOpen());

        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("TestError: This means that the database has not been created correctly",
                cursor.moveToFirst());

        do {
            tableNames.remove(cursor.getString(0));
        } while( cursor.moveToNext() );

        assertTrue("TestError: Your database was created without both the location entry and weather entry tables",
                tableNames.isEmpty());

    }
}
