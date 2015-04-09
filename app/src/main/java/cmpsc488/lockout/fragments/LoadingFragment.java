package cmpsc488.lockout.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cmpsc488.lockout.R;

/**
* A placeholder fragment containing a simple view.
*/
public class LoadingFragment extends BaseFragment {
   /**
    * The fragment argument representing the section number for this
    * fragment.
    */
   private static final String ARG_SECTION_NUMBER = "section_number";

   /**
    * Returns a new instance of this fragment for the given section
    * number.
    */
   public static LoadingFragment newInstance(int sectionNumber) {
       LoadingFragment fragment = new LoadingFragment();
       Bundle args = new Bundle();
       args.putInt(ARG_SECTION_NUMBER, sectionNumber);
       fragment.setArguments(args);
       return fragment;
   }

   public LoadingFragment() {
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       return inflater.inflate(R.layout.main_activity, container, false);
   }
}