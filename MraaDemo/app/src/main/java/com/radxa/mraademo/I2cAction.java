package com.radxa.mraademo;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static mraa.Result.*;

import mraa.*;



public class I2cAction {

    /* 定义总线索引 */
    private enum BusAll{
        I2C2, I2C6, I2C7
    }

    /* 选择 I2C_2 总线, 创建集合用于保存总线对象 */
    I2c i2c = null;
    List<I2c> i2cList = new ArrayList<I2c>();


    /* 创建一个适配器 */
    ArrayAdapter<String> adapter;

    /* 选择设备的下拉框 */
    List<String> devAddr = new ArrayList<String>();

    /* 创建一个用于下拉框选项索引的集合, 里面存放设备的名称 */
    List<String> scanResults = new ArrayList<String>();


    /* Activity位置 */
    Activity activity;

    /* 保存内存的地址 */
    static String Address;

    /* 临时保存需要显示的值 */
    static String valueOut;

    /* 实例化组件 */
    private Button buttonConfirmDevBus;
    private Button buttonRead;
    private Button buttonWrite;
    private TextView textSelectRWTip;
    private TextView textenteraddrTip;
    private EditText editaddr;
    private Button buttonConfirmAddr;
    private TextView textReadOutTips;
    private TextView textDisplayValue;
    private TextView textWriteTips;
    private TextView textWriteAddressTips;
    private TextView textWriteValueTips;
    private EditText editWriteAddress;
    private EditText editWriteValue;
    private Button buttonWriteConfirm;
    private Spinner spinnerBus;
    private Spinner spinnerDevaddr;



    public View setI2cLayoutWidget(View view, Activity act) {

        /* 将传进来的Activity保存到类成员中 */
        activity = act;

        /* 实例化组件 */
        widgetInit(view);
        /* 设置适配器 */
        setAdapter();



        /* 初始化总线集合 */
        addI2cBusObject();

        /* 1. 选择Bus */
        /* 添加下拉框监听器, 用于选择Bus */
        setSpinnerListenerOfBus();

        /* 2. 选择Device */
        setSpinnerListenerOfDevice();

        /* 实现确定总线与设备地址的Confirm按钮的监听器 */
        setBtnOfDevBusConfirm();



        /* 实现Read 与 Write 的按钮监听器： */

        /* 一、实现Read按钮的监听器 */
        setBtnOfRead();

        /* 读取Read分支上的address edit输入的值并保存 */
        readAddrOfReadBranch();

        /* 实现Read分支上的Confirm按钮的监听器 */
        setBtnOfReadBranchComfirm();



        /* 二、实现write按钮的监听器 */
        setBtnOfWrite();

        /* 实现Address edit ChangedListener */
        readAddrOfWriteBranch();

        /* 实现Value Edit ChangedListener */
        readValueOfWriteBranch();

        /* 实现Write Confirm按钮的监听器 */
        setBtnOfWriteBranchComfirm();


        /* 返回UI容器 */
        return view;
    }



    private void widgetInit(View view) {

        buttonConfirmDevBus = (Button) view.findViewById(R.id.i2c_button_select_deviceandbus_confirm);
        buttonRead = (Button) view.findViewById(R.id.i2c_button_read);
        buttonWrite = (Button) view.findViewById(R.id.i2c_button_write);
        textSelectRWTip = (TextView) view.findViewById(R.id.i2c_select_readwrite);
        textenteraddrTip = (TextView) view.findViewById(R.id.i2c_text_read);
        editaddr = (EditText) view.findViewById(R.id.i2c_edit_read_reg_address);
        buttonConfirmAddr = (Button) view.findViewById(R.id.i2c_button_read_confirm);
        textReadOutTips = (TextView) view.findViewById(R.id.i2c_text_readvalue_Tips);
        textDisplayValue = (TextView) view.findViewById(R.id.i2c_text_readvalue_display);
        textWriteTips = (TextView) view.findViewById(R.id.i2c_text_write);
        textWriteAddressTips = (TextView) view.findViewById(R.id.i2c_text_write_address_Tips);
        textWriteValueTips = (TextView) view.findViewById(R.id.i2c_text_write_value_Tips);
        editWriteAddress = (EditText) view.findViewById(R.id.i2c_edit_write_address);
        editWriteValue = (EditText) view.findViewById(R.id.i2c_edit_write_value);
        buttonWriteConfirm = (Button) view.findViewById(R.id.i2c_button_write_confirm);
        spinnerBus = (Spinner) view.findViewById(R.id.i2c_spinner_bus);
        spinnerDevaddr = (Spinner) view.findViewById(R.id.i2c_spinner_devaddr);
    }


    /* 实现设置适配器 */
    private void setAdapter(){

        devAddr.add("");

        /* 实例化并配置适配器 */
        adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, devAddr);

        /* 设置下拉框样式 */
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        /* 将适配器添加给下拉框 */
        spinnerDevaddr.setAdapter(adapter);
    }


    /* 往下拉框中添加项 */
    private void adapterAdd() {

        short device_address;

        /* 清空设备下拉框中的所有元素 */
        adapter.clear();
        /* 清空存放设备名称的集合 */
        scanResults.clear();

        /* 遍历总线上的设备 */
        for (device_address=0; device_address<0x80; device_address++) {

            /* 获取总线上的设备 */
            if (i2cGet(device_address) == SUCCESS) {

                /* 设置适配器添加条目 */
                adapter.add("0x" + Integer.toHexString(device_address));

                /* 添加该设备到集合 */
                scanResults.add(String.valueOf(device_address));
            }
        }

    }

    /* 扫描总线, 查看是否有设备存在, 有则在下拉框中显示 */
    private Result i2cGet(short device_address) {

        Result status = SUCCESS;
        short register_address = 0;
        byte data = 1;

        if (i2c == null) {
            return ERROR_NO_RESOURCES;
        }
        status = i2c.address(device_address);
        if (status != SUCCESS) {
            return status;
        }
        status = writeDataToAddress(register_address, data);
        if (status != SUCCESS) {
            return status;
        }
        status = readDatafromAddress(register_address) == 1 ? SUCCESS : ERROR_UNSPECIFIED;
        if (status != SUCCESS) {
            return status;
        }

        return status;
    }




    /* 实现选择设备总线的方法：
     * 产品总共有3条I2C总线, 分别为:
     * 2: I2C2
     * 6: I2C6  //该总线不可用
     * 7: I2C7
     */
    private void setI2cBus(int select){

        /* 选择实例化的对象 */
        switch (select) {

            case 0:
                /* 使用枚举作为索引来获取集合中的对象,  添加总线时应该随便按顺序添加枚举中的成员*/
                this.i2c = i2cList.get(BusAll.I2C2.ordinal());
                break;
            case 1:
                this.i2c = i2cList.get(BusAll.I2C6.ordinal());
                Log.i("I2C","I2C6不可用!");
                Toast.makeText(activity, "I2c6 cannot be used!", Toast.LENGTH_LONG).show();
                break;
            case 2:
                /* 使用枚举作为索引来获取集合中的对象,  添加总线时应该随便按顺序添加枚举中的成员*/
                this.i2c = i2cList.get(BusAll.I2C7.ordinal());  /* 总线集合中缺少I2C6, 因此索引减1 */
                break;

            default:
                Log.i("I2C", "选择总线时传入的参数有误, 只有2,6,7可选择");
                break;
        }

    }


    /* 实现写数据到指定地址的方法 */
    private Result writeDataToAddress(int address, int data){

        Result result;

        if (i2c == null) {
            Toast.makeText(activity, "I2c6 cannot be used!", Toast.LENGTH_LONG).show();
            return ERROR_INVALID_RESOURCE;
        }

        /* 调用写数据的mraa接口 */
        result = i2c.writeReg((short)address, (short)data);

        /* 线程睡眠 */
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }


    /* 实现从指定地址读出数据的方法 */
    private int readDatafromAddress(int address){

        if (i2c == null) {
            Toast.makeText(activity, "I2c6 cannot be used!", Toast.LENGTH_LONG).show();
            return -1;
        }


        /* 调用读数据的mraa接口 */
        return i2c.readReg((short)address);
    }


    /* 添加下拉框监听器的方法, 用于选择Bus */
    private void setSpinnerListenerOfBus() {

        spinnerBus.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                /* 选择总线 */
                setI2cBus(position);

                /* 扫描总线上的设备, 并将扫描得到的设备添加到设备选择下拉框中 */
                adapterAdd();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                /* NONE */
            }
        });

    }


    /* 添加下拉框监听器, 用于选择Device */
    private void setSpinnerListenerOfDevice() {

        spinnerDevaddr.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* 使用索引, 用于提取集合中的设备地址, 并设置要读写的地址 */
                i2c.address(Short.valueOf(scanResults.get(position)));
                //Log.i("I2C","" + Short.valueOf(scanResults.get(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                /* NONE */
            }
        });
    }


    /* 实现确定总线与设备地址的Confirm按钮的监听器的方法 */
    private void setBtnOfDevBusConfirm() {

        buttonConfirmDevBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* 1. 显示read & write按钮和提示textView */
                buttonRead.setVisibility(View.VISIBLE);
                buttonWrite.setVisibility(View.VISIBLE);
                textSelectRWTip.setVisibility(View.VISIBLE);
            }
        });
    }

    /* 实现Read按钮的监听器的方法 */
    private void setBtnOfRead() {

        buttonRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* a. 隐藏Write分支中的控件 */
                textWriteTips.setVisibility(GONE);
                textWriteAddressTips.setVisibility(GONE);
                textWriteValueTips.setVisibility(GONE);
                editWriteAddress.setVisibility(GONE);
                editWriteValue.setVisibility(GONE);
                buttonWriteConfirm.setVisibility(GONE);


                /* b. 显示Read分支中被隐藏的控件 */
                textenteraddrTip.setVisibility(View.VISIBLE);
                editaddr.setVisibility(View.VISIBLE);
                buttonConfirmAddr.setVisibility(View.VISIBLE);
                textReadOutTips.setVisibility(View.VISIBLE);
                textDisplayValue.setVisibility(View.VISIBLE);
            }
        });
    }


    /* 实现读取edit中输入的值并保存的方法 */
    private void readAddrOfReadBranch() {

        editaddr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /* 保存到类中的变量 */
                if (!editaddr.getText().toString().isEmpty()) {
                    Address = editaddr.getText().toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /* 实现Read分支上的Confirm按钮的监听器的方法 */
    private void setBtnOfReadBranchComfirm() {

        buttonConfirmAddr.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /* a. 将返回的值显示在TextView上 */
                textDisplayValue.setText(String.valueOf( readDatafromAddress(HexStringToDecValue(Address)) ));
            }
        });
    }


    /* 实现write按钮的监听器的方法 */
    private void setBtnOfWrite() {

        buttonWrite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /* a. 隐藏Read分支中的控件 */
                textenteraddrTip.setVisibility(GONE);
                editaddr.setVisibility(GONE);
                buttonConfirmAddr.setVisibility(GONE);
                textReadOutTips.setVisibility(GONE);
                textDisplayValue.setVisibility(GONE);

                /* b. 显示Write分支中被隐藏的控件 */
                textWriteTips.setVisibility(View.VISIBLE);
                textWriteAddressTips.setVisibility(View.VISIBLE);
                textWriteValueTips.setVisibility(View.VISIBLE);
                editWriteAddress.setVisibility(View.VISIBLE);
                editWriteValue.setVisibility(View.VISIBLE);
                buttonWriteConfirm.setVisibility(View.VISIBLE);
            }
        });
    }


    /* 实现edit ChangedListener的方法 */
    private void readAddrOfWriteBranch() {

        editWriteAddress.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                /* 保存address中的值 */
                if(!editWriteAddress.getText().toString().isEmpty()) {
                    Address = editWriteAddress.getText().toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /* 实现Edit ChangedListener */
    private void readValueOfWriteBranch() {

        editWriteValue.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                /* 保存value中的值 */
                if (!editWriteValue.getText().toString().isEmpty()) {
                    valueOut = editWriteValue.getText().toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /* 实现Write Confirm按钮的监听器的方法 */
    private void setBtnOfWriteBranchComfirm() {

        buttonWriteConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /* a. 调用往地址中写入值的方法, 将用户传入的地址与值传入方法中 */
                writeDataToAddress(HexStringToDecValue(Address), HexStringToDecValue(valueOut));
            }
        });
    }


    /* 实现用来往I2cList集合中添加I2c总线对象的方法 */
    private void addI2cBusObject() {

        /* 实例化总线对象. 并添加到集合中 */
        i2cList.add(new I2c(RockPI4I2C.ROCK_PI_4_I2C2.swigValue()));
        i2cList.add(null);
        i2cList.add(new I2c(RockPI4I2C.ROCK_PI_4_I2C7.swigValue()));
    }


    /* 实现十六进制字符串转十进制数值的方法 */
    private final int HexStringToDecValue(String Str) {

        if (Str.indexOf("0x") == -1){
            /* 如果输入是十进制的 */
            return Integer.valueOf(Str);
        }else {
            /* 如果输入的是十六进制的 */
            return Integer.parseInt(Str.replaceAll("0[x|X]", ""), 16);
        }
    }















}  /* I2CAction.class */
