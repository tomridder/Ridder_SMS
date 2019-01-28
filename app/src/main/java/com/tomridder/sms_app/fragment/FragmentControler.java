package com.tomridder.sms_app.fragment;

import java.util.HashMap;

public class FragmentControler
{
    private static HashMap<Integer,BaseFragment> sSavedFragment=new HashMap<>();

    public static BaseFragment getFragment(int position)
    {
        BaseFragment fragment=sSavedFragment.get(position);
        if(fragment ==null)
        {
            switch (position)
            {
                case 0:
                    fragment=new MessageFragment();
                    break;
                case 1:
                    fragment=new CallFragment();
                    break;
            }
            sSavedFragment.put(position,fragment);
        }
        return fragment;
    }
}
