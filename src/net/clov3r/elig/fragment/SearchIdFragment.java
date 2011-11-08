package net.clov3r.elig.fragment;

import net.clov3r.elig.R;
import net.clov3r.elig.ServiceHelper;
import net.clov3r.elig.database.EliGContentProvider;
import net.clov3r.elig.database.EliGDatabase;
import net.clov3r.elig.processor.EliGActions;
import net.clov3r.elig.util.ValidarCedula;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Render all the crawler result from clov3r servers
 * 
 * */

public class SearchIdFragment extends Fragment {

	private final static String LOG = SearchIdFragment.class.getName();
	private EditText editTextSearch;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.searchid, container, false);

		editTextSearch = (EditText) view.findViewById(R.id.editTextSearch);
		editTextSearch.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					// buscar Cedula or Search for Person Id
					String cedula = editTextSearch.getText().toString().toUpperCase();
					searchId(cedula);

					return true;
				}
				return false;
			}
		});
		return view;
	}

	public void searchId(String cedula) {
		ValidarCedula vc=new ValidarCedula();
		vc.setCedula(cedula);
		if(!vc.isCedulaValida()){
			Toast.makeText(getActivity(), R.string.cedula_no_valida, Toast.LENGTH_LONG).show();
			return;
		}
		ContentResolver cr = getActivity().getContentResolver();
		Cursor cursor = cr.query(
				EliGContentProvider.ID_INFORMATION_CONTENT_URI, null,
				EliGDatabase.CEDULA_COL + " = ?", new String[] { cedula }, null);
		
		if (cursor.getCount() > 0) {
			
			cursor.moveToNext();
			String nombre=cursor.getString(cursor.getColumnIndex(EliGDatabase.NOMBRE_COMPLETO_COL));
			String id=cursor.getString(cursor.getColumnIndex(EliGDatabase.CEDULA_COL));
			String jrv="Jrv." + cursor.getString(cursor.getColumnIndex(EliGDatabase.JRV_COL));
			String cv="CV. "+cursor.getString(cursor.getColumnIndex(EliGDatabase.CV_COL));
			String departamento=cursor.getString(cursor.getColumnIndex(EliGDatabase.DEPARTAMENTO_COL));
			String municipio=cursor.getString(cursor.getColumnIndex(EliGDatabase.MUNICIPIO_COL));
			
			LinearLayout linearLayoutInformation=(LinearLayout)getView().findViewById(R.id.linearLayoutInformation);
			linearLayoutInformation.setVisibility(View.VISIBLE);
			TextView textName=(TextView)getView().findViewById(R.id.textName);
			textName.setText(nombre);
			TextView textId=(TextView)getView().findViewById(R.id.textId);
			textId.setText(id);
			
			TextView textJrv=(TextView)getView().findViewById(R.id.textJrv);
			textJrv.setText(jrv);
			TextView textCv=(TextView)getView().findViewById(R.id.textCv);
			textCv.setText(cv);
			
			TextView textDepartamento=(TextView)getView().findViewById(R.id.textDepartamento);
			textDepartamento.setText(departamento);
			TextView textMunicipio=(TextView)getView().findViewById(R.id.textMunicipio);
			textMunicipio.setText(municipio);
			
			cursor.close();
		} else {
			cursor.close();
			Bundle extras = new Bundle();
			extras.putString("id", cedula);
			ServiceHelper.getInstance(getActivity()).startService(
					EliGActions.SEARCH_ID_ACTION, extras);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public static SearchIdFragment newInstance() {
		return new SearchIdFragment();
	}

	public void refresh(String cedula) {
		
		searchId(cedula);
	}

}
