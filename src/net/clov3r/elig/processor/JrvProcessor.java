package net.clov3r.elig.processor;

import net.clov3r.elig.database.EliGContentProvider;
import net.clov3r.elig.database.EliGDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

public class JrvProcessor {

	private static final String LOG = JrvProcessor.class.getName();
	private Context context;

	public JrvProcessor(Context context) {
		this.context = context;
	}

	public void parse(String htmlResponse) {

		Document doc = Jsoup.parse(htmlResponse);
		Elements datas = doc.select("b");
		if (datas.size() > 0) {

			ContentResolver cr = context.getContentResolver();
			ContentValues contentValue = new ContentValues();
			// nombre:
			String nombre = rtrim(datas.get(0).nextSibling().toString());
			String cedula = datas.get(1).nextSibling().toString()
					.replaceAll(" ", "");
			String jrv = rtrim(datas.get(4).nextSibling().toString());
			String cv = rtrim(datas.get(5).nextSibling().toString());
			String departamento = rtrim(datas.get(6).nextSibling().toString());
					
			String municipio = rtrim(datas.get(7).nextSibling().toString());
					
			String distrito = rtrim(datas.get(8).nextSibling().toString());
					
			String ubicacion = rtrim(datas.get(9).nextSibling().toString());
					
			String direccion = rtrim(datas.get(10).nextSibling().toString());
			
			contentValue.put(EliGDatabase.CEDULA_COL, cedula);

			contentValue.put(EliGDatabase.NOMBRE_COMPLETO_COL, nombre);

			contentValue.put(EliGDatabase.JRV_COL, jrv);
			contentValue.put(EliGDatabase.CV_COL, cv);
			contentValue.put(EliGDatabase.DEPARTAMENTO_COL, departamento);
			contentValue.put(EliGDatabase.MUNICIPIO_COL, municipio);
			contentValue.put(EliGDatabase.DISTRITO_COL, distrito);
			contentValue.put(EliGDatabase.UBICACION_COL, ubicacion);
			contentValue.put(EliGDatabase.DIRECCION_COL, direccion);

			cr.insert(EliGContentProvider.ID_INFORMATION_CONTENT_URI,
					contentValue);

		}

	}

	public static String rtrim(String s) {
		int i = s.length() - 1;
		while (i > 0 && Character.isWhitespace(s.charAt(i))) {
			i--;
		}
		return s.substring(0, i + 1);
	}

}
