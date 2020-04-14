package com.radxa.mraademo;


import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mraa.*;

import static android.view.View.GONE;


public class GpioAction {

    /* 创建集合 */
    List<Gpio> gpioList = new ArrayList<Gpio>();

    /* 引脚对象 */
    Gpio gpio = null;

    /* 创建变量 */
    private Spinner spinnerPin;
    private Button gpioButtonI;
    private Button gpioButtonO;
    private Button gpioButtonHigh;
    private Button gpioButtonLow;
    private TextView gpioTextO;
    private TextView gpioTextI;
    private TextView gpioTextDisplay;
    private Button gpioButtonRefresh;



    /* Display Chioce */
    TextView textDisplayChoicePin;

    /* 保存View所在的Activity */
    Activity activity;

    /* 保存引脚名称 */
    final String[] PinName = {
            "Pin12", "Pin15", "Pin16", "Pin18",
            "Pin22", "Pin23", "Pin24", "Pin32",
            "Pin35", "Pin36", "Pin37", "Pin38", "Pin40"};

    /* 此方法用于设置layout's widget */
    public View setGpioLayoutWidget(View rootView, Activity act){

        /* 将传入的activity保存到类的成员中 */
        activity = act;

        /* 实例化输入输出按键 */
        gpioButtonI = (Button)rootView.findViewById(R.id.gpio_button_i);
        gpioButtonO = (Button)rootView.findViewById(R.id.gpio_button_o);

        /* 实例化隐藏的组件(widget) */
        /* 1.Button and TextView for Output */
        gpioButtonHigh = (Button)rootView.findViewById(R.id.gpio_button_high);
        gpioButtonLow  = (Button)rootView.findViewById(R.id.gpio_button_low);
        gpioTextO    = (TextView)rootView.findViewById(R.id.gpio_text_o);

        /* 2.Button and TextView for Input */
        gpioTextI       = (TextView)rootView.findViewById(R.id.gpio_text_i);
        gpioTextDisplay = (TextView)rootView.findViewById(R.id.gpio_text_display);
        gpioButtonRefresh = (Button)rootView.findViewById(R.id.gpio_button_refresh);

        /* 3.Spinner for choice pin */
        spinnerPin = (Spinner) rootView.findViewById(R.id.gpio_spinner_pin);

        /* 4.Display Chioce */
        textDisplayChoicePin = (TextView) rootView.findViewById(R.id.gpio_text_display_pin_choice);




        /* 填充集合中的引脚对象 */
        addPinObject();

        /* 添加下拉框的监听器 */
        setSpinnerforChoicePin();


        /* 设置输入功能的按键 */
        setInputButton();

        /* 设置输出功能的按键 */
        setOutputButton();


        /* 设置输出高电平的按钮 */
        setButtonOfOutputHight();

        /* 设置输出低电平的按钮 */
        setButtonOfOutputLow();


        /* 设置刷新引脚值的按钮 */
        setButtonOfRefreshPinValue();


        return rootView;
    }


    /* 实现读引脚并刷新组件的text的方法 */
    private void gpioRefreshvalue(TextView Textdisplay){

        if (gpio == null)
            return;

        /* 初始化为输入引脚 */
        gpio.dir(Dir.DIR_IN);

        /* 拉高引脚 */
        gpio.mode(Mode.MODE_PULLUP);

        /* 调用接口，实现读引脚的值 */
        Textdisplay.setText(String.valueOf(gpio.read()));

        Log.i("GPIO", "刷新完毕");
    }


    /* 实现引脚输出高电平方法 */
    private void gpioOutputHigh(){

        if (gpio == null)
            return;

        /* 初始化为输出引脚 */
        gpio.dir(Dir.DIR_OUT);

        /* 调用接口，输出高电平 */
        gpio.write(1);

        Log.i("GPIO", "输出高电平完毕");
    }


    /* 实现引脚输出低电平方法 */
    private void gpioOutputLow(){

        if (gpio == null)
            return;

        /* 初始化为输出引脚 */
        gpio.dir(Dir.DIR_OUT);

        /* 调用接口，输出低电平 */
        gpio.write(0);

        Log.i("GPIO", "输出低电平完毕");
    }


    /* 实现添加下拉框监听器的方法 */
    private void setSpinnerforChoicePin() {

        spinnerPin.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* 显示选择的引脚 */
                textDisplayChoicePin.setText(PinName[position]);

                /* 将选择的引脚对象从集合中提取出来赋值到引脚对象变量中
                 * 1. 将之前的引脚对象先unexport了
                 * 2. 提取出集合中的引脚对象
                */
                if (gpio != null) gpio.unexport();

                if ( (gpio = gpioList.get(position)) == null) {
                    Toast.makeText(activity, "GPIO pin 16 cannot be used!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                /* NONE */
                textDisplayChoicePin.setText("NONE");
            }
        });
    }



    /* 实现将实例化的对象添加到集合中的方法 */
    private void addPinObject(){

        gpioList.add( new Gpio(RockPI4.ROCK_PI_4_PIN12.swigValue()) );
        gpioList.add( new Gpio(RockPI4.ROCK_PI_4_PIN15.swigValue()) );
        gpioList.add( null );
        gpioList.add( new Gpio(RockPI4.ROCK_PI_4_PIN18.swigValue()) );
        gpioList.add( new Gpio(RockPI4.ROCK_PI_4_PIN22.swigValue()) );
        gpioList.add( new Gpio(RockPI4.ROCK_PI_4_PIN23.swigValue()) );
        gpioList.add( new Gpio(RockPI4.ROCK_PI_4_PIN24.swigValue()) );
        gpioList.add( new Gpio(RockPI4.ROCK_PI_4_PIN32.swigValue()) );
        gpioList.add( new Gpio(RockPI4.ROCK_PI_4_PIN35.swigValue()) );
        gpioList.add( new Gpio(RockPI4.ROCK_PI_4_PIN36.swigValue()) );
        gpioList.add( new Gpio(RockPI4.ROCK_PI_4_PIN37.swigValue()) );
        gpioList.add( new Gpio(RockPI4.ROCK_PI_4_PIN38.swigValue()) );
        gpioList.add( new Gpio(RockPI4.ROCK_PI_4_PIN40.swigValue()) );
    }


    /* 设置输入功能的按键 */
    private void setInputButton() {

        gpioButtonI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 打印log */
                Log.i("Gpio", "按下了输入模式的按钮");

                /* 显示隐藏的TextView */
                gpioTextI.setVisibility(View.VISIBLE);
                gpioTextDisplay.setVisibility(View.VISIBLE);
                gpioButtonRefresh.setVisibility(View.VISIBLE);

                /* 隐藏Output功能按钮组件 */
                gpioButtonHigh.setVisibility(GONE);
                gpioButtonLow.setVisibility(GONE);
                gpioTextO.setVisibility(GONE);

            }
        });

    }


    /* 设置输出功能的按键 */
    private void setOutputButton() {

        gpioButtonO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 打印Log */
                Log.i("Gpio", "按下了输出模式的按钮");

                /* 显示隐藏的 Button 和 TextView */
                gpioButtonHigh.setVisibility(View.VISIBLE);
                gpioButtonLow.setVisibility(View.VISIBLE);
                gpioTextO.setVisibility(View.VISIBLE);

                /* 隐藏Input功能按钮组件 */
                gpioTextI.setVisibility(GONE);
                gpioTextDisplay.setVisibility(GONE);
                gpioButtonRefresh.setVisibility(GONE);

            }
        });

    }


    /* 设置输出高电平的按钮 */
    private void setButtonOfOutputHight() {

        gpioButtonHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 打印Log */
                Log.i("gpio", "按下了HIGH按钮");

                /* 调用输出高电平的实现方法 */
                gpioOutputHigh();

            }
        });

    }


    /* 设置输出低电平的按钮 */
    private void setButtonOfOutputLow() {

        gpioButtonLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 打印Log */
                Log.i("gpio", "按下了LOW按钮");

                /* 调用输出低电平的实现方法 */
                gpioOutputLow();

            }
        });
    }


    /* 设置刷新引脚值的按钮 */
    private void setButtonOfRefreshPinValue() {

        gpioButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 打印Log */
                Log.i("GPIO", " 按下了Refresh按钮");

                /* 调用刷新引脚值并显示的方法 */
                gpioRefreshvalue(gpioTextDisplay);
            }
        });

    }







}  /* GpioAction.class */
