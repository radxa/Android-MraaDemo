package com.radxa.mraademo;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.radxa.mraademo.dummy.DummyContent;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    /* 将每次进来get得到的Activity保存下来 */
    private Activity activity;

    /* 创建保存layout值的数组 */
    int[] ArrayLayoutValue = new int[]{
            R.layout.iomap, R.layout.gpio, R.layout.aio,
            R.layout.i2c,   R.layout.pwm,  R.layout.spi,
            R.layout.uart,
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.item_detail, container, false);
//
//        //Show the dummy content as text in a TextView.
//        if (mItem != null) {
//            ((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem.details);
//        }

        /* 实例化一个View对象 */
        View rootView = null;

        /* 调用选择并创建layout的方法 */
        rootView = Selectlayout(inflater, container, mItem.content);

        return rootView;
    }






    /* 将选项栏中的字符串转换成对应的数字 */
    private int Selectfunc(String select){

        DummyContent dummycontent = new DummyContent();

        for (int i=0; i < dummycontent.getcount(); i++){

            if ( select.equals(dummycontent.namearr[i]) ) {
                return i;
            }

        }

        return -1;
    }


    /*选择layout*/
    private final View Selectlayout(LayoutInflater inflater, ViewGroup container, String layoutname){

        return SetUpwidget(inflater.inflate(ArrayLayoutValue[Selectfunc(layoutname)], container, false), Selectfunc(layoutname));
    }


    /* 设置layout 中的 widget 功能 */
    private View SetUpwidget(View view, int select){

        /* 选择layout, 调用对应layout的设置方法 */
        switch (select){

            case 0:
                return view;
            case 1:
                GpioAction gpioAction = new GpioAction();
                return  gpioAction.setGpioLayoutWidget(view, activity);
            case 2:
                AioAction aioAction = new AioAction();
                return aioAction.setAioLayoutWidget(view);
            case 3:
                I2cAction i2cAction = new I2cAction();
                return i2cAction.setI2cLayoutWidget(view, activity);
            case 4:
                PwmAction pwmAction = new PwmAction();
                return pwmAction.setPwmLayoutWidget(view);
            case 5:
                SpiAction spiAction = new SpiAction();
                return spiAction.setSpiLayoutWidget(view, activity);
            case 6:
                UartAction uartAction = new UartAction();
                return uartAction.setUartLayoutWidget(view, activity);

            default:
                return null;
        }
    }






}
