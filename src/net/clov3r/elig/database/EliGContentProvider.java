package net.clov3r.elig.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class EliGContentProvider extends ContentProvider {

	public static final String CONTENT_AUTHORITY = "net.clov3r.elig";
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://"
			+ CONTENT_AUTHORITY);
	
	private static final String PATH_HASHTAG = "hashtag";
	private static final String PATH_TWEET = "tweet";
	private static final String PATH_TWEET_USER = "tweet_user";
	private static final String PATH_ID_INFORMATION = "id_information";
	
	public static final Uri HASHTAG_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
			.appendPath(PATH_HASHTAG).build();
	public static final Uri TWEET_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
			.appendPath(PATH_TWEET).build();
	public static final Uri ID_INFORMATION_CONTENT_URI =BASE_CONTENT_URI.buildUpon().appendPath(PATH_ID_INFORMATION).build();
	
	public static final Uri TWEET_USER_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
			.appendPath(PATH_TWEET_USER).build();

	private static final String LOG = EliGContentProvider.class.getName();
	private static final UriMatcher uriMatcher = buildMatcher();

	private EliGDatabase databaseHelper;

	private static final int HASHTAG = 100;
	private static final int HASHTAG_ID = 101;
	private static final int TWEET = 200;
	private static final int TWEET_ID = 201;
	private static final int TWEET_USER = 300;
	private static final int TWEET_USER_ID = 301;
	private static final int ID_INFORMATION = 400;
	private static final int ID_INFORMATION_ID = 401;

	public static final String CONTENT_TYPE_HASHTAG = "vnd.android.cursor.dir/vnd.net.clov3r.elig.hashtag";
	public static final String CONTENT_ITEM_TYPE_HASHTAG = "vnd.android.cursor.item/vnd.net.clov3r.elig.hashtag";
	public static final String CONTENT_TYPE_TWEET = "vnd.android.cursor.dir/vnd.net.clov3r.elig.tweet";
	public static final String CONTENT_ITEM_TYPE_TWEET = "vnd.android.cursor.item/vnd.net.clov3r.elig.tweet";
	public static final String CONTENT_TYPE_TWEET_USER = "vnd.android.cursor.dir/vnd.net.clov3r.elig.tweet_user";
	public static final String CONTENT_ITEM_TYPE_TWEET_USER = "vnd.android.cursor.item/vnd.net.clov3r.elig.tweet_user";
	public static final String CONTENT_TYPE_ID_INFORMATION = "vnd.android.cursor.dir/vnd.net.clov3r.elig.id_information";
	public static final String CONTENT_ITEM_TYPE_ID_INFORMATION = "vnd.android.cursor.item/vnd.net.clov3r.elig.id_information";
	
	

	private static UriMatcher buildMatcher() {
		UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

		matcher.addURI(CONTENT_AUTHORITY, "hashtag", HASHTAG);
		matcher.addURI(CONTENT_AUTHORITY, "hashtag/*", HASHTAG_ID);
		matcher.addURI(CONTENT_AUTHORITY, "tweet", TWEET);
		matcher.addURI(CONTENT_AUTHORITY, "tweet/*", TWEET_ID);
		matcher.addURI(CONTENT_AUTHORITY, "tweet_user", TWEET_USER);
		matcher.addURI(CONTENT_AUTHORITY, "tweet_user/*", TWEET_USER_ID);
		matcher.addURI(CONTENT_AUTHORITY, "id_information", ID_INFORMATION);
		matcher.addURI(CONTENT_AUTHORITY, "id_information/*", ID_INFORMATION_ID);

		return matcher;

	}

	@Override
	public String getType(Uri uri) {

		final int match = uriMatcher.match(uri);
		switch (match) {

		case HASHTAG:
			return CONTENT_TYPE_HASHTAG;
		case HASHTAG_ID:
			return CONTENT_ITEM_TYPE_HASHTAG;
		case TWEET:
			return CONTENT_TYPE_TWEET;
		case TWEET_ID:
			return CONTENT_ITEM_TYPE_TWEET;
		case ID_INFORMATION:
			return CONTENT_TYPE_ID_INFORMATION;
		case ID_INFORMATION_ID:
			return CONTENT_ITEM_TYPE_ID_INFORMATION;
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		String finalWhere=null;
		int count;
		String table=null;
		switch (uriMatcher.match(uri)) {

		case HASHTAG:
			table=EliGDatabase.HASHTAG_TABLE;
			break;
		case HASHTAG_ID:
			finalWhere = BaseColumns._ID + " = " + getId(uri);
			if (selection != null) {
				finalWhere = finalWhere + " AND " + selection;
			}
			table=EliGDatabase.HASHTAG_TABLE;
			break;
		case TWEET:
			table=EliGDatabase.TWEET_TABLE;
			break;
		case TWEET_ID:
			finalWhere = BaseColumns._ID + " = " + getId(uri);
			if (selection != null) {
				finalWhere = finalWhere + " AND " + selection;
			}
			table=EliGDatabase.TWEET_TABLE;
			break;
		case TWEET_USER:
			table=EliGDatabase.TWEET_USER_TABLE;
			break;
		case TWEET_USER_ID:
			finalWhere = BaseColumns._ID + " = " + getId(uri);
			if (selection != null) {
				finalWhere = finalWhere + " AND " + selection;
			}
			table=EliGDatabase.TWEET_USER_TABLE;
			break;
		case ID_INFORMATION:
			table=EliGDatabase.ID_INFORMATION_TABLE;
			break;
		case ID_INFORMATION_ID:
			finalWhere = BaseColumns._ID + " = " + getId(uri);
			if (selection != null) {
				finalWhere = finalWhere + " AND " + selection;
			}
			table=EliGDatabase.ID_INFORMATION_TABLE;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		count = db.delete(table, finalWhere,selectionArgs);
		
		getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	public String getId(Uri uri) {
		return uri.getPathSegments().get(1);
	}

	public static Uri buildUri(Uri uri, String id) {
		return uri.buildUpon().appendPath(id).build();
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		final SQLiteDatabase db = databaseHelper.getWritableDatabase();
		final int match = uriMatcher.match(uri);
		
		switch (match) {
		case HASHTAG: {
			long _id = db.insertOrThrow(EliGDatabase.HASHTAG_TABLE, null,
					values);
			getContext().getContentResolver().notifyChange(uri, null);
			return buildUri(HASHTAG_CONTENT_URI, String.valueOf(_id));
		}
		case TWEET: {
			long _id = db.insertOrThrow(EliGDatabase.TWEET_TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return buildUri(TWEET_CONTENT_URI, String.valueOf(_id));
		}
		case TWEET_USER: {
			long _id = db.insertOrThrow(EliGDatabase.TWEET_USER_TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return buildUri(TWEET_USER_CONTENT_URI, String.valueOf(_id));
		}
		case ID_INFORMATION: {
			long _id = db.insertOrThrow(EliGDatabase.ID_INFORMATION_TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return buildUri(ID_INFORMATION_CONTENT_URI, String.valueOf(_id));
		}
		default: {
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		}
	}

	@Override
	public boolean onCreate() {
		databaseHelper = new EliGDatabase(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		final int match = uriMatcher.match(uri);

		final SQLiteDatabase db = databaseHelper.getReadableDatabase();
		
		final SQLiteQueryBuilder builder = buildExpandedSelection(uri, match);
		Log.d(LOG,"Sort order "+ sortOrder);
		Cursor c = builder.query(db, projection, selection, selectionArgs,
				null, null, sortOrder);

		return c;

	}

	private SQLiteQueryBuilder buildExpandedSelection(Uri uri, int match) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

		String id = null;
		switch (match) {
		case HASHTAG:
			builder.setTables(EliGDatabase.HASHTAG_TABLE);
			break;

		case HASHTAG_ID:
			id = getId(uri);
			builder.setTables(EliGDatabase.HASHTAG_TABLE);
			builder.appendWhere(BaseColumns._ID + "=" + id);
			break;
		case TWEET_USER:
			builder.setTables(EliGDatabase.TWEET_USER_TABLE);
			break;

		case TWEET_USER_ID:
			id = getId(uri);
			builder.setTables(EliGDatabase.TWEET_USER_TABLE);
			builder.appendWhere(BaseColumns._ID + "=" + id);
			break;
		case TWEET:
			builder.setTables(EliGDatabase.TWEET_TABLE);
			break;

		case TWEET_ID:
			id = getId(uri);
			builder.setTables(EliGDatabase.TWEET_TABLE);
			builder.appendWhere(BaseColumns._ID + "=" + id);
			break;
		case ID_INFORMATION:
			builder.setTables(EliGDatabase.ID_INFORMATION_TABLE);
			break;

		case ID_INFORMATION_ID:
			id = getId(uri);
			builder.setTables(EliGDatabase.ID_INFORMATION_TABLE);
			builder.appendWhere(BaseColumns._ID + "=" + id);
			break;
		default:
			// If the URI doesn't match any of the known patterns, throw an
			// exception.
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		return builder;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		// Opens the database object in "write" mode.
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		int count;
		String id;
		String finalWhere = null;
		switch (uriMatcher.match(uri)) {

		case HASHTAG:
			count = db.update(EliGDatabase.HASHTAG_TABLE, values, selection,
					selectionArgs);

			break;
		case HASHTAG_ID:
			id = getId(uri);
			finalWhere = BaseColumns._ID + " = " + id;
			if (selection != null)
				finalWhere = finalWhere + " AND " + selection;

			count = db.update(EliGDatabase.HASHTAG_TABLE, values, finalWhere,
					selectionArgs);

			break;
		case TWEET_USER:
			count = db.update(EliGDatabase.TWEET_USER_TABLE, values, selection,
					selectionArgs);

			break;
		case TWEET_USER_ID:
			id = getId(uri);

			if (selection != null)
				finalWhere = finalWhere + " AND " + selection;

			count = db.update(EliGDatabase.TWEET_USER_TABLE, values, finalWhere,
					selectionArgs);

			break;
		case TWEET:
			count = db.update(EliGDatabase.TWEET_TABLE, values, selection,
					selectionArgs);

			break;
		case TWEET_ID:
			id = getId(uri);

			if (selection != null)
				finalWhere = finalWhere + " AND " + selection;

			count = db.update(EliGDatabase.TWEET_TABLE, values, finalWhere,
					selectionArgs);

			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		return count;

	}

}
