package com.radxa.mraademo;



import android.app.Activity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.SeekBar;
import mraa.Pwm;
import mraa.RockPI4;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class PwmAction {

    private TextView textSelectDisplay;
    private Spinner spinnertext;
    private SeekBar seekBar;
    private EditText editPeriod;
    private TextView textprogress;

    /* pwm对象 */
    Pwm pwm = null;
    List<Pwm> pwmList = new ArrayList<Pwm>();

    /* 引脚和周期与占空比 */
    private int period;
    private float percentage;


    /* 设置PWM的Widget功能 */
    public View setPwmLayoutWidget(View view){

        /* 实例化Widget对象 */
        textSelectDisplay = (TextView) view.findViewById(R.id.pwm_text_display_pin_choice);
        spinnertext       = (Spinner)  view.findViewById(R.id.spinner1);
        seekBar           = (SeekBar)  view.findViewById(R.id.pwm_seekbar);
        editPeriod        = (EditText) view.findViewById(R.id.pwm_edit_period);
        textprogress      = (TextView) view.findViewById(R.id.pwm_text_progress_display);

        /* 实例化两个pwm引脚 */
        pwmList.add(new Pwm(RockPI4.ROCK_PI_4_PIN11.swigValue()));
        pwmList.add(new Pwm(RockPI4.ROCK_PI_4_PIN13.swigValue()));

        /* to avoid non-numeric characters */
        editPeriod.setInputType(InputType.TYPE_CLASS_NUMBER);

        /* 添加监听器, 为下拉列表添加事件响应 */
        setSpinnerListener();

        /* 添加监听器, 为输入框添加事件响应 */
        setEditPeriodListener();

        /* 添加监听器, 为拖动条添加事件响应 */
        setSeekBarListener();


        return view;
    }



    /* 实现编辑框的监听器方法 */
    private void setEditPeriodListener(){

        editPeriod.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                /* 保存输入值 */
                if (!editPeriod.getText().toString().isEmpty()) {
                    period = Integer.valueOf(editPeriod.getText().toString());
                }
            }
        });
    }


    /* 实现添加拖动条监听器的方法 */
    private void setSeekBarListener(){

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                /* 拖动条进度变化时调用: progress代表进度
                 * 1. 显示到 textview 上
                 * 2. 获取进度并换算成百分比(除以100), 保存结果到成员变量percentage中
                 * 3. 调用控制pwm方法
                */
                textprogress.setText(String.valueOf(progress) + "%");
                percentage = ((float)progress)/100;
                controlPwm(period, percentage);
            }
      

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    /* 实现下拉列表的监听器事件响应方法 */
    private void setSpinnerListener() {

        /* 添加监听器, 为下拉列表添加事件响应 */
        spinnertext.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* 将选中的项，在textview display中显示出来 */
                if (position == 0) {

                    /* 在textview display中显示出来 */
                    textSelectDisplay.setText("PWM0");

                    /* 选择引脚 */
                    selectPinOfPwm(position);
                }else if (position == 1){

                    /* 在textview display中显示出来 */
                    textSelectDisplay.setText("PWM1");

                    /* 选择引脚 */
                    selectPinOfPwm(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                textSelectDisplay.setText("NONE");
            }
        });

    }


    /* 实现选择pwm引脚的方法 */
    private void selectPinOfPwm(int select){

        /* 选择并实例化PWM对象 */
        switch (select){

            case 0:
                this.pwm = pwmList.get(select);
                break;

            case 1:
                this.pwm = pwmList.get(select);
                break;

            default:
                Log.i("PWM", "选择需要实例化的引脚时出错!");
                break;
        }
    }


    /* 实现设置pwm周期的方法 */
    private void setPwmPeriod(int period){

        /* 设置pwm的周期: 单位us */
        this.pwm.period_us(period);
    }


    /* 实现控制引脚pwm的方法 */
    private void controlPwm(int period, float percentage){

        /* 设置pwm周期: 单位us */
        setPwmPeriod(period);

        /* 设置pwm的占空比 */
        this.pwm.write(percentage);

        /* 使能pwm引脚 */
        this.pwm.enable(true);
    }





}  /* PwmAction.class */
