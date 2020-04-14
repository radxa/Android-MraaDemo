package com.radxa.mraademo;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import mraa.*;

public class AioAction {

    /* 创建保存对象的变量 */
    private Button buttonRefresh;
    private Button buttonInt;
    private Button buttonFloat;
    private TextView textAio;
    private TextView textDisplay;

    /* 在此修改为自己要控制的aio引脚 */
    Aio aio_0 = new Aio(0);
    long valueLong;
    double valueDouble;

    /* 定义值的类型的标记 */
    private final int INT = 1;
    private final int FLOAT = 2;
    private int typeFlag = 0;



    public View setAioLayoutWidget(final View view) {

        /* 实例化组件(widget) */
        buttonRefresh = (Button) view.findViewById(R.id.aio_button_Refresh);
        buttonInt = (Button) view.findViewById(R.id.aio_button_Int);
        buttonFloat = (Button) view.findViewById(R.id.aio_button_Float);
        textAio = (TextView) view.findViewById(R.id.aio_text1);
        textDisplay = (TextView) view.findViewById(R.id.aio_text_display);



        /* 设置Int类型值的按钮功能 */
        setIntTypeButton();

        /* 设置Float类型值的按钮功能 */
        setFloatTypeButton();

        /* 设置aio值刷新显示的功能按钮 */
        setRefreshPinValueButton();



        return view;
    }


    /* 设置Int类型值的按钮功能 */
    private void setIntTypeButton() {

        buttonInt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* 显示隐藏的组件 */
                textDisplay.setVisibility(View.VISIBLE);
                buttonRefresh.setVisibility(View.VISIBLE);
                textAio.setVisibility(View.VISIBLE);

                /* 设置标记位 */
                typeFlag = INT;
            }
        });
    }


    /* 设置Float类型值的按钮功能 */
    private void setFloatTypeButton() {

        buttonFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* 显示隐藏的组件 */
                textDisplay.setVisibility(View.VISIBLE);
                buttonRefresh.setVisibility(View.VISIBLE);
                textAio.setVisibility(View.VISIBLE);

                /* 设置标记位 */
                typeFlag = FLOAT;
            }
        });
    }


    /* 设置aio值刷新显示的功能按钮 */
    private void setRefreshPinValueButton() {

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* 判断选择的值类型是哪个 */
                if (typeFlag == INT) {
                    /* 整形类型值的读取和显示 */
                    /* 读取aio的值并显示到textView上 */
                    /* 1.读出aio引脚中的值 */
                    valueLong = readAioInt();

                    /* 2.将读取出来的值，显示到textView上 */
                    textDisplay.setText(String.valueOf(valueLong));

                } else {
                    /* 浮点类型值的读取和显示 */
                    /* 读取aio的值并显示到textView上 */
                    /* 1.读出aio引脚中的值 */
                    valueDouble = readAioFloat();

                    /* 2.将读取出来的值，显示到textView上 */
                    textDisplay.setText(String.valueOf(valueDouble));
                }


            }
        });
    }



    /* 实现读取aio引脚的值, 读出整数值 */
    private long readAioInt(){

        return aio_0.read();
    }


    /* 实现读取aio引脚的值, 读出浮点数值 */
    private double readAioFloat(){

        return aio_0.readFloat();
    }






}  /* AioAction.class */
