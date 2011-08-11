package com.timefortabata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseWrapper {

	private static final String DB_NAME = "timefortabata.db";
	private static final int DB_VERSION = 1;
	private static final String TABLE_NAME = "interval_table";
	private static final class TableDescription{
		private static final String NAME_FIELD = "name";
		private static final String INTERVAL_LENGTH_FIELD = "intervalLength";
		private static final String REST_LENGTH_FIELD = "restLength";
		private static final String INTERVAL_NUMBER_FIELD = "intervalNumber";
		private static final String SET_REST_LENGTH_FIELD = "setRestLength";
		private static final String SET_NUMBER_FIELD = "setNumber";
		private static final String ENABLE_SOUND_FX_FIELD = "enableSoundFx";
		private static final String ENABLE_SCREEN_AWAKE_FIELD = "enableScreenAwake";
		private static final String WORK_MUSIC_FIELD = "workMusic";
		private static final String REST_MUSIC_FIELD = "restMusic";
	}
	
	private Context context;
	private SQLiteDatabase db;

	public DataBaseWrapper(Context c){
		context = c;
		OpenHelper oh = new OpenHelper(context);
		db = oh.getWritableDatabase();
	}
	
	public void insertField(String name,
							IntervalSessionDescription isd,
							boolean enableSoundFx,
							boolean enableAwakeScreen,
							String workMusic,
							String restMusic){
		ContentValues cv = new ContentValues();
		cv.put(TableDescription.NAME_FIELD, name);
		cv.put(TableDescription.INTERVAL_LENGTH_FIELD, isd.workPeriod);
		cv.put(TableDescription.INTERVAL_NUMBER_FIELD, isd.intervalNo);
		cv.put(TableDescription.REST_LENGTH_FIELD, isd.interIntervalRestPeriod);
		cv.put(TableDescription.SET_NUMBER_FIELD, isd.setNo);
		cv.put(TableDescription.SET_REST_LENGTH_FIELD, isd.interSetRestPeriod);
		cv.put(TableDescription.ENABLE_SOUND_FX_FIELD, enableSoundFx);
		cv.put(TableDescription.ENABLE_SCREEN_AWAKE_FIELD, enableAwakeScreen);
		cv.put(TableDescription.WORK_MUSIC_FIELD, workMusic);
		cv.put(TableDescription.REST_MUSIC_FIELD, restMusic);
		db.insert(TABLE_NAME, null, cv);
	}
	
	public Cursor getAllIntervalSessions(){
		Cursor cursor = db.query(TABLE_NAME, new String[] { "name" }, null, null, null, null, "name desc");
		return cursor;
	}
	
	public void deleteInterval(String name){
		db.delete(TABLE_NAME,
				  TableDescription.NAME_FIELD + "=?",
				  new String[] {name});
	}
	
	private static class OpenHelper extends SQLiteOpenHelper{
		OpenHelper(Context c){
			super(c, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db){
			db.execSQL("CREATE TABLE " + TABLE_NAME + 
					"(id INTEGER PRIMARY KEY, " +
					TableDescription.NAME_FIELD + " TEXT UNIQUE, " +
					TableDescription.INTERVAL_LENGTH_FIELD + " INTEGER, " +
					TableDescription.REST_LENGTH_FIELD + " INTEGER, " +
					TableDescription.INTERVAL_NUMBER_FIELD + " INTEGER, " +
					TableDescription.SET_REST_LENGTH_FIELD + " INTEGER, " +
					TableDescription.SET_NUMBER_FIELD + " INTEGER, " +
					TableDescription.ENABLE_SOUND_FX_FIELD + " BOOLEAN, " +
					TableDescription.ENABLE_SCREEN_AWAKE_FIELD + " BOOLEAN, " +
					TableDescription.WORK_MUSIC_FIELD + " TEXT, " +
					TableDescription.REST_MUSIC_FIELD + " TEXT)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db,
							  int oldVesion,
							  int newVersion) {
			// TODO Auto-generated method stub
			
		}
	}
}
