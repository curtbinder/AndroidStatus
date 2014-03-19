package info.curtbinder.reefangel.phone;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StatusFragment extends Fragment {

	private static final String TAG = StatusFragment.class.getSimpleName();

    public static final int PAGES = 2;

    // display views
    private TextView mUpdateTime;
	private ViewPager mPager;
	private SectionsPagerAdapter mPagerAdapter;
	private Fragment[] mAppPages;
    private String[] mVortechModes;

    // minimum number of pages: status, main relay
    private static final int MIN_PAGES = 3;

    private static final int POS_START = 0;

    private static final int POS_COMMANDS = POS_START;
    private static final int POS_CONTROLLER = POS_START + 1;

    private static final int POS_MODULES = POS_CONTROLLER + 10;
    private static final int POS_DIMMING = POS_MODULES;
    private static final int POS_RADION = POS_MODULES + 1;
    private static final int POS_VORTECH = POS_MODULES + 2;
    private static final int POS_AI = POS_MODULES + 3;
    private static final int POS_IO = POS_MODULES + 4;
    private static final int POS_CUSTOM = POS_MODULES + 5;

    private static final int POS_MAIN_RELAY = POS_CONTROLLER + 1;
    private static final int POS_EXP1_RELAY = POS_MAIN_RELAY + 1;
    private static final int POS_EXP2_RELAY = POS_MAIN_RELAY + 2;
    private static final int POS_EXP3_RELAY = POS_MAIN_RELAY + 3;
    private static final int POS_EXP4_RELAY = POS_MAIN_RELAY + 4;
    private static final int POS_EXP5_RELAY = POS_MAIN_RELAY + 5;
    private static final int POS_EXP6_RELAY = POS_MAIN_RELAY + 6;
    private static final int POS_EXP7_RELAY = POS_MAIN_RELAY + 7;
    private static final int POS_EXP8_RELAY = POS_MAIN_RELAY + 8;

    private static final int POS_END = POS_CUSTOM + 1;


	public static StatusFragment newInstance() {
		StatusFragment f = new StatusFragment();
		return f;
	}

	@Override
	public View onCreateView (
			LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState ) {
		Log.d(TAG, "onCreateView");
		View root = inflater.inflate( R.layout.frag_status, container, false );
        mUpdateTime = (TextView) root.findViewById(R.id.textUpdate);

        // set the maximum number of pages we can have
		mAppPages = new Fragment[PAGES];
		
		// Set up the ViewPager with the sections adapter.
		mPager = (ViewPager) root.findViewById( R.id.pager );
		/* we are using Nested Fragments inside the pager adapter to allow the 
		 * fragment manager to manage them properly, instead of using the
		 * getSupportFragmentManager or getFragmentManager, you must use 
		 * getChildFragmentManager to allow for proper management
		 */
		mPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		
		return root;
	}

    @Override
    public void onResume ( ) {
        super.onResume();
        Log.d(TAG, "onResume");
        // update the Display Text
        updateDisplayText("");
    }

    public void updateDisplayText(String text) {
        mUpdateTime.setText(text);
    }

    @Override
    public void onPause ( ) {
        super.onPause();
        Log.d(TAG, "onPause");
    }

	private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

		public SectionsPagerAdapter ( FragmentManager fm ) {
			super( fm );
		}

		@Override
		public Fragment getItem ( int position ) {
			Log.d(TAG, "getItem: " + position);
			switch ( position ) {
				case 0:
					if ( mAppPages[0] == null ) {
						mAppPages[0] = ControllerPage.newInstance();
					}
					break;
				case 1:
					if ( mAppPages[1] == null ) {
						mAppPages[1] = RelayPage.newInstance(0);
					}
					break;
			}
			return mAppPages[position];
		}

		@Override
		public int getCount ( ) {
			return PAGES;
		}

		@Override
		public CharSequence getPageTitle ( int position ) {
            // this gets called before the getItem function gets called
			switch ( position ) {
				case 0:
					return "Controller";
				case 1:
					return "Main Relay";
			}
			return null;
		}
		
		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
		
	}
}
