package com.radxa.mraademo;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mraa.*;

public class SpiAction {

    /* 创建Widget变量 */
    private TextView textdisplay;
    private Spinner  spinnertext;
    private TextView textdisplaymode;
    private Spinner  spinnerdisplaymode;
    private EditText editInput;
    private Button   buttonSend;
    private TextView textRecv;

    /* 创建引脚变量 */
    Spi spi = null;
    List<Spi> spiList = new ArrayList<Spi>();

    /* 保存用户输入的值 */
    String toSend;
    byte[] Send = new byte[256];

    /* 获取返回值 */
    byte[] recv;

    /* 数据以什么类型去显示, true: Value, false: Ascii. */
    boolean displayMode;

    /* 存放所属的Activity */
    Activity activity;

    /* 设置Widget的功能 */
    public View setSpiLayoutWidget(View view, Activity act){

        /* 将传入的Activity放入类成员中 */
        activity = act;

        /* 实例化Widget */
        textdisplay = (TextView) view.findViewById(R.id.spi_text_choice_display);
        spinnertext = (Spinner)  view.findViewById(R.id.spi_spinner);
        editInput   = (EditText) view.findViewById(R.id.spi_edit_data);
        buttonSend  = (Button)   view.findViewById(R.id.spi_button_write_confirm);
        textRecv    = (TextView) view.findViewById(R.id.spi_text_recvcontent_display);
        textdisplaymode    = (TextView) view.findViewById(R.id.spi_text_choice_displaymodule);
        spinnerdisplaymode = (Spinner) view.findViewById(R.id.spi_spinner2);



        /* 实例化引脚对象, 并存入泛型集合中 */
        spiList.add(null);  // SPI1不可用, 故使用null占位
        spiList.add(new Spi(RockPI4SPI.ROCK_PI_4_SPI2.swigValue()));



        /* 添加下拉框监听器 */
        setSpinnerListener();

        /* 添加选择显示模式的下拉框的监听器 */
        setSpinnerListenerOfDisplayMode();

        /* 添加编辑框监听器 */
        setEditInputListener();

        /* 添加按钮监听器 */
        setButtonListenerOfSend();



        return view;
    }




    /* 为编辑框添加事件监听器 */
    private void setEditInputListener(){

        editInput.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                /* 保存用户输入的值 */
                if (!editInput.getText().toString().isEmpty()) {
                    toSend = editInput.getText().toString();
                }
            }
        });

    }


    /* 设置使用的Spi总线 */
    private void setSpiBus(int select){

        /* 使用switch进行选择 */
        switch (select) {
            case 0:
                this.spi = spiList.get(select);
                Log.i("SPI","SPI1不可用");
                Toast.makeText(activity, "SPI1 cannot be used!", Toast.LENGTH_LONG).show();
                break;

            case 1:
                this.spi = spiList.get(select);
                break;

            default:
                Log.i("SPI", "选择SPI总线时出错!!");
                break;
        }
    }



    /* 实现spi写数据的方法 */
    private byte[] spiWrite(byte[] data) {

        if (spi == null) {
            Toast.makeText(activity, "SPI1 cannot be used!", Toast.LENGTH_LONG).show();
            return null;
        }

        /* 将数据写入, 并返回从机传来的数据 */
        return spi.write(data);
    }


    /* 实现spi下拉框监听器的方法 */
    private void setSpinnerListener(){

        spinnertext.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* 在textView中显示选中的引脚信息 */
                if (position == 0) {
                    textdisplay.setText("SPI1");
                }else{
                    textdisplay.setText("SPI2");
                }

                /* 调用选择对象功能 */
                setSpiBus(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                /* NONE */
                textdisplay.setText("NONE");
            }
        });
    }


    /* 实现发送按钮的监听器的方法 */
    private void setButtonListenerOfSend(){

        buttonSend.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                /* 调用发送方法, 调用字符串处理函数进行传参给发送方法 */
                SendData();

                /* 调用显示接收的数据的方法 */
                RecvData(displayMode);
            }
        });
    }



    /* 实现发送数据的方法 */
    private void SendData() {

        /* 处理用户输入的字符串 */
        StringHandler(toSend);

        /* 调用发送接口, 传入用户输入的值, 并将接口的返回值保存起来 */
        recv = spiWrite(Send);

        /* 清理发送buf为0 */
        Arrays.fill(Send, (byte)0);
    }


    /* 实现接收数据的显示 */
    private void RecvData(boolean isValueMode) {

        if (recv == null)
            return;


        if (isValueMode){
            /* 以Value的形式, 在TextView 上显示数据 */
            textRecv.setText(byteToHexString(recv));
        }else {
            /* 以Ascii码的形式, 在TextView 上显示数据 */
            textRecv.setText(new String(recv));
        }
    }



    /* 处理字符串：使用字符串的查找, 提取, 转换方法.
     * 1. 找到0x开头的字符串
     * 2. 提取子字符串
     * 3. 转换成数字
     * 4. 将数字Int转换成Byte
     */
    private void StringHandler(String toSend) {

        String tmpStr;
        int begin, end;
        int result;
        int count;

        for (begin=0, count=0; count < 256; count++) {

            /* 1. 找到0x开头的字符串 */
            begin = toSend.indexOf('0', begin);
            end = begin + 4;

            if (begin != -1) {
                /* 2. 提取子字符串 */
                tmpStr = toSend.substring(begin, end);

                /* 3. 转换成数字: 主要用于处理 十六进制的 A B C D E F 的。 */
                result = Integer.parseInt(tmpStr.replaceAll("^0[x|X]", ""), 16);

                /* 4. 将数字Int转换成Byte */
                Send[count] = (byte) (result & 0xff);

                /* 将寻找索引叠加4, 跳到下一个用户输入值的开头 */
                begin += 4;
            }else{
                /* 已经转换完毕 */
                Send[count] = '\0';
                break;
            }

            /* 达到buf最末端, 该位置不存放值, 用来存放'\0' */
            if (count == 255) {
                /* 赋值结束符 */
                Send[count] = '\0';
            }
        }

    }



    /* 实现选择显示模式的下拉框的监听器的方法 */
    private void setSpinnerListenerOfDisplayMode() {

        spinnerdisplaymode.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* 在textView上显示选择的模式 */
                if (position == 0) {
                    textdisplaymode.setText("Ascii");

                    /* 设置显示模式标签 */
                    displayMode = false;
                }else{
                    textdisplaymode.setText("Value");

                    /* 设置显示模式标签 */
                    displayMode = true;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                /* NONE */
                textdisplaymode.setText("NONE");
            }
        });
    }




    /* 实现一个byte转16进制字符串的方法 */
    private String byteToHexString(byte[] recv) {

        String result = "";

        for (int i=0; (i<recv.length) && (recv[i] != 0); i++) {
            result += "0x" + Integer.toHexString(recv[i]) + ", ";
        }

        Log.i("SPI", "recv数组的长度: " + recv.length);

        return result;
    }






}  /* SpiAction.class */
