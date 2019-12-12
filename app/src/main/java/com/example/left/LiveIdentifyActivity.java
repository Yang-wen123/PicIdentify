package com.example.left;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.*;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.baidu.aip.imageclassify.AipImageClassify;
import com.example.left.Utils.BitmapUtil;
import com.example.left.Utils.GetBitmapByte;
import com.example.left.Utils.PermisstionUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("NewApi")
public class LiveIdentifyActivity extends Activity {
    private static final String TAG = "Camera2Activity";
    private static final String FILEPATH = Environment.getExternalStorageDirectory() + "/MyCamera/";
    private Context context;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private int currentCameraId = CameraCharacteristics.LENS_FACING_FRONT;

    private CameraManager cameraManager;
    private HandlerThread handlerThread;
    private Handler handler;
    private Size previewSize;
    private CameraDevice cameraDevice;
    private ImageReader imageReader;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder previewBuilder;
    private CaptureRequest.Builder captureBuilder;
    ObjectAnimator objectAnimator,objectAnimator2;
    ImageView image,image2;
    View v;
    int imageId=0;
    Button liveIdentify;
    TextView tv;
    boolean b=true;
    Bitmap bitmap;
    private AipImageClassify aipImageClassify;
    private String name=null,score=null,name1=null,score1=null,baike_info=null,baike_url,description;
    private GetBitmapByte getBB=new GetBitmapByte();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initView();
    }
    //初始化界面属性
    private void initView() {
        context = this;
        v= LayoutInflater.from(this).inflate(R.layout.activity_camera,null);
        image=findViewById(R.id.rorate);
        image2=findViewById(R.id.rorate2);
        tv=findViewById(R.id.textview);
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        liveIdentify=findViewById(R.id.liveidentify);
        liveIdentify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identify();
            }
        });
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.i(TAG, "surfaceCreated");
                openCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.i(TAG, "surfaceChanged");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i(TAG, "surfaceDestroyed");
                closeCamera();
            }
        });
        findViewById(R.id.btnTakePhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        findViewById(R.id.btnSwitch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });
        initData();
    }
    //开启识别
    private void identify() {
        animator();
        generalidentify();
    }
    //识别方式
    private void generalidentify() {
        if(imageId==1){
            takePhoto();
            final Timer t = new Timer();
            t.schedule(new TimerTask() {
                public void run() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            byte[] content = getBB.getBitmapByte(bitmap);
                            //获取Acess_token
                            aipImageClassify = new AipImageClassify(APPInfo.APP_ID, APPInfo.API_KEY, APPInfo.SECRET_KEY);
                            aipImageClassify.setConnectionTimeoutInMillis(2000);
                            aipImageClassify.setSocketTimeoutInMillis(6000);
                            HashMap<String, String> options = new HashMap<String, String>();
                            options.put("baike_num", "5");//请求http
                            JSONObject res = aipImageClassify.advancedGeneral(content, options);
                            try {
                                JSONArray jsonArray = new JSONArray(res.optString("result"));
                                name=jsonArray.optJSONObject(0).optString("root");
                                score=jsonArray.optJSONObject(0).optString("score");
                                description=jsonArray.optJSONObject(0).getJSONObject("baike_info").optString("description");
                                // baike_url = jsonArray.optJSONObject(0).getJSONObject("baike_info").optString("baike_url");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //子线程发送数据
                            int UPDATE_TEXT=1;
                            Message message = new Message();
                            message.what = UPDATE_TEXT;
                            handler1.sendMessage(message);
                        }
                    }).start();
                    t.cancel();
                }
            }, 5000);
        }
    }
    @SuppressLint("HandlerLeak")
    private Handler handler1 = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            //响应布局内容
            if(name==null&&score==null&&description==null){
                tv.setText("网络不给力，请稍后重试");
            }else{
                tv.setText("名称：" + name+"\n可能性：" + score+"\n介绍：" + description);
            }

            generalidentify();
        }
    };
    //识别过程的动画
    private void animator() {
        image.setVisibility(View.VISIBLE);
        image2.setVisibility(View.VISIBLE);
        if(imageId==0){
            if(b){
                rotate();
            }
            imageId=1;
            liveIdentify.setText("停止");
        }else {
            image.setVisibility(View.GONE);
            image2.setVisibility(View.GONE);
            liveIdentify.setText("开始");
           // tv.setText("");
            imageId=0;
        }
    }
    @SuppressLint("WrongConstant")
    private void rotate() {
        b=false;
        objectAnimator = ObjectAnimator.ofFloat(image,"rotation",360f);
        objectAnimator.setRepeatMode(Animation.RESTART);
        objectAnimator.setRepeatCount(-1);
        objectAnimator.setDuration(400);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();
        objectAnimator2 = ObjectAnimator.ofFloat(image2,"rotation",360f);
        objectAnimator2.setRepeatMode(Animation.RESTART);
        objectAnimator2.setRepeatCount(-1);
        objectAnimator2.setDuration(400);
        objectAnimator2.setInterpolator(new LinearInterpolator());
        objectAnimator2.start();
    }
    private void initData() {
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        //Camera2全程异步
        handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    //打开相机回调
    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.i(TAG, "onOpened");
            //相机开启，打开预览
            cameraDevice = camera;
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.i(TAG, "onDisconnected");
            //相机关闭
            camera.close();
            tv.setText("");
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.i(TAG, "onError");
            //相机报错
            camera.close();
            cameraDevice = null;
            finish();
        }
    };

    //创建session回调
    private CameraCaptureSession.StateCallback sessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            try {
                cameraCaptureSession = session;
                //自动对焦
                previewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                cameraCaptureSession.setRepeatingRequest(previewBuilder.build(), null, handler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            session.close();
            cameraCaptureSession = null;
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    //拍完照回调
    private CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            try {
                //自动对焦
                captureBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_CANCEL);
                //重新打开预览
                session.setRepeatingRequest(previewBuilder.build(), null, handler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            cameraCaptureSession.close();
            cameraCaptureSession = null;
            cameraDevice.close();
            cameraDevice = null;
        }
    };
    /**
     * 打开相机
     */
    private void openCamera() {
        PermisstionUtil.requestPermissions(context, PermisstionUtil.CAMERA, 101, "正在请求拍照权限", new PermisstionUtil.OnPermissionResult() {
            @Override
            public void granted(int requestCode) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    //获取属性CameraDevice属性描述
                    CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(String.valueOf(currentCameraId));
                    //获取摄像头支持的配置属性
                    StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    previewSize = getMaxSize(map.getOutputSizes(SurfaceHolder.class));
                    initImageReader();
                    //第一个参数指定哪个摄像头，第二个参数打开摄像头的状态回调，第三个参数是运行在哪个线程(null是当前线程)
                    cameraManager.openCamera(String.valueOf(currentCameraId), stateCallback, handler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void denied(int requestCode) {

            }
        });
    }

    /**
     * 初始化拍照处理器
     */
    private void initImageReader() {
        imageReader = ImageReader.newInstance(previewSize.getWidth(), previewSize.getHeight(), ImageFormat.JPEG, 1);
        //监听ImageReader时间，有图像数据可用时回调，参数就是帧数据
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = reader.acquireLatestImage();
                //将帧数据转换成字节数组
                ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
                byte[] data = new byte[byteBuffer.remaining()];
                byteBuffer.get(data);
                //保存照片
                savePicture(data);
                image.close();
            }
        }, handler);
    }

    /**
     * 开始预览
     */
    private void startPreview() {
        try {
            previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            //设置预览数据输出界面
            previewBuilder.addTarget(surfaceHolder.getSurface());
            //创建相机捕获会话，
            //第一个参数是捕获数据的输出Surface列表，第二个参数是CameraCaptureSession状态回调接口，第三个参数是在哪个线程(null是当前线程)
            cameraDevice.createCaptureSession(Arrays.asList(surfaceHolder.getSurface(), imageReader.getSurface()), sessionStateCallback, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * 拍照
     */
    private void takePhoto() {
        try {
            captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(imageReader.getSurface());
            //自动对焦
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            cameraCaptureSession.stopRepeating();
            //拍照
            cameraCaptureSession.capture(captureBuilder.build(), captureCallback, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 切换摄像头
     */
    private void switchCamera() {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                Size maxSize = getMaxSize(map.getOutputSizes(SurfaceHolder.class));
                if (currentCameraId == CameraCharacteristics.LENS_FACING_BACK && characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    //前置转后置
                    previewSize = maxSize;
                    currentCameraId = CameraCharacteristics.LENS_FACING_FRONT;
                    cameraDevice.close();
                    openCamera();
                    break;
                } else if (currentCameraId == CameraCharacteristics.LENS_FACING_FRONT && characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                    //后置转前置
                    previewSize = maxSize;
                    currentCameraId = CameraCharacteristics.LENS_FACING_BACK;
                    cameraDevice.close();
                    openCamera();
                    break;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭相机
     */
    private void closeCamera() {
        //关闭捕捉会话
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }
        //关闭相机
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        //关闭拍照处理器
        if (imageReader != null) {
            imageReader.close();
            imageReader = null;
        }
    }

    /**
     * 获取最大预览尺寸
     *
     * @param outputSizes
     * @return
     */
    private Size getMaxSize(Size[] outputSizes) {
        Size sizeMax = null;
        if (outputSizes != null) {
            sizeMax = outputSizes[0];
            for (Size size : outputSizes) {
                if (size.getWidth() * size.getHeight() > sizeMax.getWidth() * sizeMax.getHeight()) {
                    sizeMax = size;
                }
            }
        }
        return sizeMax;
    }

    /**
     * 保存图像
     *
     * @param data
     */
    private void savePicture(final byte[] data) {
        PermisstionUtil.requestPermissions(context, PermisstionUtil.STORAGE, 101, "正在获取读写权限", new PermisstionUtil.OnPermissionResult() {
            @Override
            public void granted(int requestCode) {
                Toast.makeText(context, "正在进行……", Toast.LENGTH_LONG).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Matrix matrix = new Matrix();
                            matrix.setRotate(currentCameraId == CameraCharacteristics.LENS_FACING_FRONT ? 90 : 270);
                            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            BitmapUtil.save(bitmap, FILEPATH + System.currentTimeMillis() + ".jpg");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "run: "+FILEPATH);
                                    Toast.makeText(context, "已保存到"+FILEPATH, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void denied(int requestCode) {
                Toast.makeText(context, "读写权限被禁止", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermisstionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}