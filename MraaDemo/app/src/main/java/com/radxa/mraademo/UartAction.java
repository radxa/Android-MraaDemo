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
import java.util.List;

import mraa.RockPI4Uart;
import mraa.Uart;

public class UartAction {


    /* 创建Widget变量 */
    private TextView textDisplayUartChoice;
    private TextView textDisplayBaudRateChoice;
    private Spinner  spinnerText;
    private Spinner  spinnerBaudRate;
    private EditText editWriteContent;
    private Button   buttonSend;
    private Button   buttonRecv;
    private TextView textDisplayRecvContent;

    /* 发送的字符串 */
    String toSend;
    //byte[]  toSend = {'1', '2'};

    /* 接收的字符串 */
    String recvStr;

    /* 创建保存Uart对象的变量 */
    Uart uart = null;

    /* 创建集合变量 */
    List<Uart> uartList = new ArrayList<Uart>();

    /* 波特率 */
    final int[] BaudRateList = {1500000, 115200, 57600, 38400, 19200, 9600, 4800, 2400, 1200, 300};
    int BaudRate;

    /* 存放layout所在的Activity */
    Activity activity;


    /* 设置Widget的功能 */
    public View setUartLayoutWidget(View view, Activity act){

        /* 将传入的Activity 存入类成员中 */
        activity = act;

        /* 实例化Widget对象 */
        textDisplayUartChoice     = (TextView) view.findViewById(R.id.uart_text_display_Tips);
        textDisplayBaudRateChoice = (TextView) view.findViewById(R.id.uart_text_display_baudrate_Tips);
        spinnerText       = (Spinner)  view.findViewById(R.id.uart_spinner);
        spinnerBaudRate   = (Spinner)  view.findViewById(R.id.uart_spinner_baudrate);
        editWriteContent  = (EditText) view.findViewById(R.id.uart_edit_inputdata_to_send);
        buttonSend        = (Button)   view.findViewById(R.id.uart_button_for_send);
        buttonRecv        = (Button)   view.findViewById(R.id.uart_button_for_recv);
        textDisplayRecvContent = (TextView) view.findViewById(R.id.uart_text_recv_display);

        /* 添加uart对象进集合中 */
        uartList.add(null);  // 串口2不可用, 使用null占位
        uartList.add(new Uart(RockPI4Uart.ROCK_PI_4_UART4.swigValue()));

        /* 添加下拉框监听器, 选择串口 */
        setSpinnerListenerOfUart();

        /* 添加下拉框监听器, 选择波特率 */
        setSpinnerListenerOfBaudRate();

        /* 添加编辑框监听器 */
        setEditListenerOfSendContent();

        /* 添加发送按钮监听器 */
        setButtonListenerOfSend();

        /* 添加接收方法 */
        setButtonListenerOfRecv();


        return view;
    }




    /* 实现添加下拉框的监听器方法, 选择串口 */
    private void setSpinnerListenerOfUart(){

        spinnerText.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {

                    /* 将选择的结果在TextView 上显示出来 */
                    textDisplayUartChoice.setText("Uart2");
                }else{

                    /* 将选择的结果在TextView 上显示出来 */
                    textDisplayUartChoice.setText("Uart4");
                }

                /* 调用选择串口 */
                selectUartPort(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                textDisplayUartChoice.setText("NONE");
            }
        });
    }


    /* 实现添加下拉框的监听器方法, 选择波特率 */
    private void setSpinnerListenerOfBaudRate(){

        spinnerBaudRate.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* 选择波特率 */
                BaudRate = BaudRateList[position];
                textDisplayBaudRateChoice.setText(", " + String.valueOf(BaudRateList[position]) + ".");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                /* NONE */
                textDisplayBaudRateChoice.setText(", NONE.");
            }
        });
    }


    /* 实现选择串口的方法 */
    private void selectUartPort(int select){

        /* 根据传入的下拉框的position参数进行选择 */
        switch (select) {
            case 0:
                this.uart = uartList.get(select);
                Log.i("Uart","串口2不可用");
                Toast.makeText(activity, "Serial port 2 does not work!", Toast.LENGTH_LONG).show();
                break;

            case 1:
                this.uart = uartList.get(select);
                break;



            default:
                Log.i("Uart", "在选择串口时出错!!");
                break;
        }
    }



    /* 实现添加编辑框的监听器方法 */
    private void setEditListenerOfSendContent(){

        editWriteContent.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                /* 保存输入的字符串 */
                if (!editWriteContent.getText().toString().isEmpty()){
                    toSend = editWriteContent.getText().toString();
                }
            }
        });
    }



    /* 实现添加发送按钮的监听器方法 */
    private void setButtonListenerOfSend(){

        buttonSend.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                /* 调用发送方法 */
                snedData();
            }
        });
    }


    /* 实现添加接收数据的按钮的监听器的方法 */
    private void setButtonListenerOfRecv(){

        buttonRecv.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                /* 接收串口发来的数据 */
                recvStr = recvData();

                /* 显示到 textView 上 */
                textDisplayRecvContent.setText(recvStr);
            }
        });

    }



    /* 实现串口发送的方法 */
    private void snedData(){

        if (uart == null) {
            Toast.makeText(activity, "Serial port 2 does not work!", Toast.LENGTH_LONG).show();
            return;
        }

        /* 配置串口参数 */
        this.uart.defaultConfig();

        /* 设置波特率 */
        this.uart.setBaudRate(this.BaudRate);

        /* 发送 */
        this.uart.writeStr(toSend);

    }


    /* 实现串口接受的方法 */
    private String recvData() {

        if (uart == null) {
            Toast.makeText(activity, "Serial port 2 does not work!", Toast.LENGTH_LONG).show();
            return null;
        }
        /* 检查设备上是否有数据可提供读取 */
        if (this.uart.dataAvailable()) {
            /* 有则, 接受数据并返回 */
            return this.uart.readStr(256);
        }else{
            /* 打印Log */
            Log.i("Uart", "串口缓冲寄存器中还没有数据可读取");
            return "Not thing";
        }

    }




}  /* UartAction.class */
