package net.clov3r.elig.fragment;

import net.clov3r.elig.R;
import net.clov3r.elig.R.layout;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

	

/**
 *  It will load graphic with the result of the election
 *  
 * */

public class TrendFragment extends Fragment implements LoaderCallbacks<Cursor>{
	
	private final static String LOG= TrendFragment.class.getName();
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view=inflater.inflate(R.layout.trend,container,false);
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		//getLoaderManager().restartLoader(0, null, this);
	}
	@Override
	public void onPause(){
		super.onPause();
	}

	public static TrendFragment newInstance() {
		return new TrendFragment();
	}

	public void refresh() {
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursor) {
		
	}

}
