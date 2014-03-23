package info.curtbinder.reefangel.phone;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.curtbinder.reefangel.db.StatusProvider;
import info.curtbinder.reefangel.db.StatusTable;
import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.UpdateService;

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
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // example used mPagerAdapter.instantiateItem(mPager, position)
                PageRefreshInterface page = (PageRefreshInterface) mPagerAdapter.getItem(position);
                if ( page != null ) {
                    page.refreshData();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mPager.setCurrentItem(0, true);
        // enable the options menu
        setHasOptionsMenu(true);
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

//    public Cursor getLatestDataCursor() {
//        Uri uri = Uri.parse( StatusProvider.CONTENT_URI + "/" + StatusProvider.PATH_LATEST );
//        Cursor c = getActivity().getContentResolver().query( uri, null, null, null,
//                StatusTable.COL_ID + " DESC" );
//        return c;
//    }

    public String[] getNeverValues ( int qty ) {
        String[] s = new String[qty];
        for ( int i = 0; i < qty; i++ ) {
            s[i] = getString( R.string.defaultStatusText );
        }
        return s;
    }

    @Override
    public void onPause ( ) {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    private void launchStatusTask ( ) {
        Intent i = new Intent( getActivity(), UpdateService.class );
        i.setAction( MessageCommands.QUERY_STATUS_INTENT );
        getActivity().startService(i);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.frag_status, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_refresh:
                Log.d(TAG, "Refresh Data");
                launchStatusTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
						mAppPages[0] = PageControllerFragment.newInstance();
					}
					break;
				case 1:
					if ( mAppPages[1] == null ) {
						mAppPages[1] = PageRelayFragment.newInstance(0);
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
            // todo add in other page names
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
