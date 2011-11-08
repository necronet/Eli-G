package net.clov3r.elig.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class EliGDatabase extends SQLiteOpenHelper{
	
	private static final String LOG=EliGDatabase.class.getName(); 
	public static final String DATABASE="elig";
	private static final int VERSION_CODE=4;
	
	public static final String HASHTAG_TABLE="HashTag";//HashTags Table
	public static final String TWEET_TABLE="Tweet";//Twitter Table
	public static final String HASHTAG_DESCRIPTION_COL="Description";
	
	public static final String TWEET_COL="Tweet";
	public static final String TWEET_USER_COL="User";//@NECRONET
	public static final String TWEET_ID_COL="TweetId";
	public static final String TWEET_IMAGE_URL_COL="ImageUrl";
	public static final String TWEET_CREATED_AT_COL="CreatedAt";
	public static final String TWEET_USER_NAME_COL = "UserNameTweet";//Jose Ayerdis
	public static final String TWEET_USER_TABLE = "UserTweet";
	
	public static final String ID_INFORMATION_TABLE = "IdInformation";
	public static final String CEDULA_COL="Cedula";//Cedula
	public static final String NOMBRE_COMPLETO_COL="FullName";//NOMBRE completo
	public static final String JRV_COL="JRV";//Junta receptora de voto
	public static final String CV_COL="CV";//Centro de votacion
	public static final String DEPARTAMENTO_COL="Departamento";//Departamento
	public static final String MUNICIPIO_COL="Municipio";//Municipio
	public static final String DISTRITO_COL="Distrito";//DISTRITO
	public static final String UBICACION_COL="Ubicacion";//Ubicacion
	public static final String DIRECCION_COL="Direccion";//Direccion
	
	
	public EliGDatabase(Context context){
		super(context,DATABASE,null,VERSION_CODE);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE "+ HASHTAG_TABLE +" (" + 
				BaseColumns._ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
				HASHTAG_DESCRIPTION_COL +" TEXT NOT NULL, "+
				"UNIQUE (" + HASHTAG_DESCRIPTION_COL + ") ON CONFLICT REPLACE )");
		
		db.execSQL("CREATE TABLE "+ TWEET_TABLE +" (" + 
				BaseColumns._ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
				TWEET_COL +" TEXT NOT NULL,"+ 
				TWEET_USER_COL +" TEXT NOT NULL," +
				TWEET_ID_COL +" INTEGER NOT NULL,"+
				TWEET_IMAGE_URL_COL +" TEXT NOT NULL,"+
				TWEET_CREATED_AT_COL +" INTEGER NOT NULL, "+
				"UNIQUE (" + TWEET_ID_COL + ") ON CONFLICT REPLACE )");
		db.execSQL("CREATE TABLE "+ TWEET_USER_TABLE +" (" + 
				BaseColumns._ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
				TWEET_COL +" TEXT NOT NULL,"+ 
				TWEET_USER_COL +" TEXT NOT NULL," +
				TWEET_USER_NAME_COL +" TEXT NOT NULL," +
				TWEET_ID_COL +" INTEGER NOT NULL,"+
				TWEET_CREATED_AT_COL +" INTEGER NOT NULL, "+
				"UNIQUE (" + TWEET_ID_COL + ") ON CONFLICT REPLACE )");
		
		db.execSQL("CREATE TABLE "+ ID_INFORMATION_TABLE +" (" + 
				BaseColumns._ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
				CEDULA_COL +" TEXT NOT NULL,"+ 
				NOMBRE_COMPLETO_COL +" TEXT NOT NULL," +
				JRV_COL +" TEXT NOT NULL," +
				CV_COL +" TEXT NOT NULL,"+
				DEPARTAMENTO_COL +" TEXT NOT NULL, "+
				MUNICIPIO_COL +" TEXT NOT NULL, "+
				DISTRITO_COL +" TEXT NOT NULL, "+
				UBICACION_COL +" TEXT NOT NULL, "+
				DIRECCION_COL +" TEXT NOT NULL, "+
				"UNIQUE (" + CEDULA_COL + ") ON CONFLICT REPLACE )");
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion<newVersion){
			Log.d(LOG,"updateing database schema");
			db.execSQL("DROP TABLE IF EXISTS "+TWEET_TABLE);
			db.execSQL("DROP TABLE IF EXISTS "+HASHTAG_TABLE);
			db.execSQL("DROP TABLE IF EXISTS "+TWEET_USER_TABLE);
			onCreate(db);
		}
	}
	

}
