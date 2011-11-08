package net.clov3r.elig.fragment;

import static net.clov3r.elig.processor.EliGActions.TWITTER_HASHTAG_ACTION;

import java.util.Calendar;

import net.clov3r.elig.R;
import net.clov3r.elig.ServiceHelper;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItem;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

public class BarChoiceFragment extends Fragment implements
		RadioGroup.OnCheckedChangeListener {

	private static final String LOG = BarChoiceFragment.class.getName();
	private TextView textCountDown;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.barchoice, container);
		setHasOptionsMenu(true);
		return view;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		long id = item.getItemId();

		if (id == R.id.mnuCountDown) {
			showDialog();
		}
		return super.onOptionsItemSelected(item);
	}

	public static long calculateTimeLeft() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 6);
		calendar.set(Calendar.YEAR, 2011);
		calendar.set(Calendar.HOUR_OF_DAY, 7);
		Calendar today = Calendar.getInstance();

		long timeLeft = (calendar.getTimeInMillis() - today.getTimeInMillis());
		return timeLeft;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		RadioGroup radioGroupOptions = (RadioGroup) getActivity().findViewById(
				R.id.radioGroupOptions);
		radioGroupOptions.setOnCheckedChangeListener(this);
		renderFragment(radioGroupOptions.getCheckedRadioButtonId());

		long timeLeft = calculateTimeLeft();

		textCountDown = (TextView) getView().findViewById(R.id.textCountDown);

		new CountDownTimer(timeLeft, 1000) {
			public void onTick(long millisUntilFinished) {
				millisUntilFinished = millisUntilFinished / 1000;
				int days = (int) (millisUntilFinished / (60 * 60 * 24));
				if (days == 1) {
					textCountDown.setText(days + " Dia ");
				} else
					textCountDown.setText(days + " Dias ");
			}

			public void onFinish() {
				textCountDown.setText("Elecciones 2011");
			}
		}.start();

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		Log.d(LOG, "Entering the checked");
		renderFragment(checkedId);
	}

	private void renderFragment(int checkedId) {
		if (checkedId == R.id.radioButtonTwitter) {
			
			TwitterListFragment twitterList = TwitterListFragment.newInstance();
			FragmentTransaction tr = getFragmentManager().beginTransaction();
			tr.replace(R.id.content, twitterList, "twitterList");
			tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			tr.commit();
		} else if (checkedId == R.id.radioButtonTrend) {
			TrendFragment trendFragment = TrendFragment.newInstance();
			FragmentTransaction tr = getFragmentManager().beginTransaction();
			tr.replace(R.id.content, trendFragment, "trend");
			tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			tr.commit();
		} else if (checkedId == R.id.radioSearchId) {
			SearchIdFragment newsFragment = SearchIdFragment.newInstance();
			FragmentTransaction tr = getFragmentManager().beginTransaction();
			tr.replace(R.id.content, newsFragment, "searchId");
			tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			tr.commit();
		}
	}

	public boolean refresh() {

		RadioGroup radioGroupOptions = (RadioGroup) getActivity().findViewById(
				R.id.radioGroupOptions);

		if (radioGroupOptions.getCheckedRadioButtonId() == R.id.radioButtonTwitter) {
			// called the Twitter Service
			ServiceHelper serviceHelper = ServiceHelper
					.getInstance(getActivity());
			return serviceHelper.startService(TWITTER_HASHTAG_ACTION);
		}

		return false;
	}

	public static class CountDownDialogFragment extends DialogFragment {

		static CountDownDialogFragment newInstance() {
			CountDownDialogFragment f = new CountDownDialogFragment();
			f.setCancelable(true);
			f.setStyle(STYLE_NO_TITLE, 0);
			return f;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.countdown, container, false);
			final TextView textDias = (TextView) v.findViewById(R.id.textDias);
			final TextView textHoras = (TextView) v
					.findViewById(R.id.textHoras);

			new CountDownTimer(calculateTimeLeft(), 1000) {
				public void onTick(long millisUntilFinished) {
					if(isVisible()){
					millisUntilFinished = millisUntilFinished / 1000;

					int days = (int) (millisUntilFinished / (60 * 60 * 24));
					int hours = (Math.round(millisUntilFinished) / 3600)
							- (days * 24);
					int minutes = (Math.round(millisUntilFinished) / 60)
							- (days * 1440) - (hours * 60);
					int seconds = Math.round(millisUntilFinished) % 60;

					if (days == 1) {
						textDias.setText(Html.fromHtml(days + " D\u00EDa "));
					}
					if (days == 0 ) {
						textDias.setText(getString(R.string.almost_now));
					} else
						textDias.setText(Html.fromHtml(days + " D\u00EDas "));

					textHoras.setText(hours + ":" + minutes + ":" + seconds);
					}
				}

				public void onFinish() {
					textDias.setText("Elecciones 2011, A Votar!!");
				}
			}.start();

			return v;
		}
	}

	public void showDialog() {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment prev = getSupportFragmentManager().findFragmentByTag(
				"countdown");
		if (prev != null) {
			ft.remove(prev);
		}

		ft.addToBackStack(null);

		// Create and show the dialog.
		CountDownDialogFragment newFragment = CountDownDialogFragment
				.newInstance();
		newFragment.show(ft, "countdown");

	}

}
