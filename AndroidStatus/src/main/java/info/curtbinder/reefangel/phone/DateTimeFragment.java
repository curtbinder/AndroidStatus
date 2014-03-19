package info.curtbinder.reefangel.phone;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DateTimeFragment extends Fragment {

	@Override
	public View onCreateView (
			LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState ) {
		View root = inflater.inflate( R.layout.frag_datetime, container, false );
		return root;
	}

}
