package info.curtbinder.reefangel.phone;

import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HistoryFragment extends ListFragment {

	@Override
	public View onCreateView (
			LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState ) {
		View root = inflater.inflate( R.layout.frag_history, container, false );
		return root;
	}

}
