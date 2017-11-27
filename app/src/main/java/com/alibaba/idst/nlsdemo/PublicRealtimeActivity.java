package com.alibaba.idst.nlsdemo;

import android.app.Activity;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.idst.R;
import com.alibaba.idst.nls.realtime.NlsClient;
import com.alibaba.idst.nls.realtime.NlsListener;
import com.alibaba.idst.nls.realtime.StageListener;
import com.alibaba.idst.nls.realtime.internal.protocol.NlsRequest;
import com.alibaba.idst.nls.realtime.internal.protocol.NlsResponse;

import java.util.HashMap;

public class PublicRealtimeActivity extends Activity {

    private boolean isRecognizing = false;
    private EditText mFullEdit;
    private EditText mResultEdit;
    private Button mStartButton;
    private Button mStopButton;
    private NlsClient mNlsClient;
    private NlsRequest mNlsRequest;
    private HashMap<Integer,String> resultMap = new HashMap<Integer, String>();
    private int sentenceId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_asr);

        mFullEdit = (EditText) findViewById(R.id.editText2);
        mResultEdit = (EditText) findViewById(R.id.editText);
        mStartButton = (Button) findViewById(R.id.button);
        mStopButton = (Button) findViewById(R.id.button2);

        String appkey = "nls-service-shurufa16khz"; //请设置文档中Appkey
//        String appkey = "nls-service-streaming"; //请设置文档中Appkey

        mNlsRequest = new NlsRequest();
        mNlsRequest.setAppkey(appkey);    //appkey请从 "快速开始" 帮助页面的appkey列表中获取
        mNlsRequest.setResponseMode("streaming");//流式为streaming,非流式为normal

        
        /*设置热词相关属性*/
//        mNlsRequest.setVocabularyId("vocabid");
        /*设置热词相关属性*/

        NlsClient.openLog(true);
        NlsClient.configure(getApplicationContext()); //全局配置
        mNlsClient = NlsClient.newInstance(this, mRecognizeListener, mStageListener,mNlsRequest);                          //实例化NlsClient

        mNlsClient.setMaxRecordTime(60000);  //设置最长语音
        mNlsClient.setMaxStallTime(1000);    //设置最短语音
        mNlsClient.setMinRecordTime(500);    //设置最大录音中断时间
        mNlsClient.setRecordAutoStop(false);  //设置VAD
        mNlsClient.setMinVoiceValueInterval(200); //设置音量回调时长

        initStartRecognizing();
        initStopRecognizing();
    }

    private void initStartRecognizing(){
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRecognizing = true;
                mResultEdit.setText("正在录音，请稍候！");
                mNlsRequest.authorize("LTAIP1NV1vrzkoMA", "JxGX130e5pvrkXnk3WJyBm770V8XvQ"); //请替换为用户申请到的数加认证key和密钥
                mNlsClient.start();
                mStartButton.setText("录音中。。。");
            }
        });
    }

    private void initStopRecognizing(){
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRecognizing = false;
                mResultEdit.setText("");
                mNlsClient.stop();
                mStartButton.setText("开始 录音");

            }
        });
    }

    private NlsListener mRecognizeListener = new NlsListener() {

        @Override
        public void onRecognizingResult(int status, NlsResponse result) {
            switch (status) {
                case NlsClient.ErrorCode.SUCCESS:
                    if (result!=null){
                        if(result.getResult()!=null) {
                            //获取句子id对应结果。
                            if (sentenceId != result.getSentenceId()) {
                                sentenceId = result.getSentenceId();
                            }
                            resultMap.put(sentenceId,result.getText());

                            Log.i("asr", "[demo] callback onRecognizResult :" + result.getResult().getText());
                            mResultEdit.setText(resultMap.get(sentenceId));
                            mFullEdit.setText(JSON.toJSONString(result.getResult()));
                        }
                    }else {
                        Log.i("asr", "[demo] callback onRecognizResult finish!" );
                        mResultEdit.setText("Recognize finish!");
                        mFullEdit.setText("Recognize finish!");
                    }
                    break;
                case NlsClient.ErrorCode.RECOGNIZE_ERROR:
                    Toast.makeText(PublicRealtimeActivity.this, "recognizer error", Toast.LENGTH_LONG).show();
                    break;
                case NlsClient.ErrorCode.RECORDING_ERROR:
                    Toast.makeText(PublicRealtimeActivity.this,"recording error", Toast.LENGTH_LONG).show();
                    break;
                case NlsClient.ErrorCode.NOTHING:
                    Toast.makeText(PublicRealtimeActivity.this,"nothing", Toast.LENGTH_LONG).show();
                    break;
            }
            isRecognizing = false;
        }


    } ;

    private StageListener mStageListener = new StageListener() {
        @Override
        public void onStartRecognizing(NlsClient recognizer) {
            super.onStartRecognizing(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        public void onStopRecognizing(NlsClient recognizer) {
            super.onStopRecognizing(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
            mResultEdit.setText("");
            mNlsClient.stop();
            mStartButton.setText("开始 录音");
        }

        @Override
        public void onStartRecording(NlsClient recognizer) {
            super.onStartRecording(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        public void onStopRecording(NlsClient recognizer) {
            super.onStopRecording(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        public void onVoiceVolume(int volume) {
            super.onVoiceVolume(volume);
        }

    };

}
