package fr.enssat.guillaumelaise.videoplayer;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private final String PROGRESS_WEB_VIEW="Progress web view";

    private Thread thread;
    private Handler handler;
    private WebView webview;
    private VideoView videoView;
    private ChapterService chapterService;
    private ProgressDialog progressDialog;
    private MediaController mediaController;

    private int position          = 0;
    private String url            = "https://en.wikipedia.org/wiki/Big_Buck_Bunny";
    private String currentChapter = "Intro";

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Chapter
        this.initChapter();

        //Video
        this.initVideo();

        //WebView
        this.initWebView();

        //Button scrollbar
        this.initButtonScrollBar();

        //Handler
        this.handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                String url = msg.getData().getString(PROGRESS_WEB_VIEW);
                webview.loadUrl(url);
            }
        };
    }

    public void onStart(){
        super.onStart();
        this.thread = new Thread(new Runnable()
        {
            Bundle messageBundle = new Bundle();
            Message myMessage;
            @Override
            public void run()
            {
                try
                {
                    while(true)
                    {
                        if(videoView.isPlaying())
                        {
                            if(!currentChapter.equals(chapterService.getChapterTitleByPosition(videoView.getCurrentPosition()/1000)))
                            {
                                currentChapter = chapterService.getChapterTitleByPosition(videoView.getCurrentPosition()/1000);
                                myMessage = handler.obtainMessage();
                                messageBundle.putString(PROGRESS_WEB_VIEW, chapterService.getUrlByPosition(videoView.getCurrentPosition()/1000));
                                myMessage.setData(messageBundle);
                                handler.sendMessage(myMessage);
                            }
                        }
                        Thread.sleep(1000);
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        this.thread.start();
    }

    //Link the chapters to the video and the web-view chapters (wikipedia categories)
    private void initChapter()
    {
        //ChapterService
        this.chapterService = new ChapterService();
        this.chapterService.add(0,"Intro","");
        this.chapterService.add(28,"Title","Production_history");
        this.chapterService.add(75,"Butterflies","Characters");
        this.chapterService.add(160,"Assault","Release");
        this.chapterService.add(290,"Payback","Plot");
        this.chapterService.add(495,"Cast","See_also");
    }

    //Init the video view, media controller, progress bar video player with the video en play it
    private void initVideo()
    {
        if (this.mediaController == null)
        {
            this.mediaController = new MediaController(MainActivity.this);
        }

        this.videoView = findViewById(R.id.video_view);

        this.progressDialog = new ProgressDialog(MainActivity.this);
        this.progressDialog.setTitle("Video Player");
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();

        this.videoView.setMediaController(this.mediaController);
        this.videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video));
        this.videoView.requestFocus();
        this.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            public void onPrepared(MediaPlayer mediaPlayer)
            {
                progressDialog.dismiss();
                videoView.seekTo(position);
                if (position == 0)
                {
                    videoView.start();
                }
                else
                {
                    videoView.pause();
                }
            }
        });
    }

    //Init the web view with wikipedia content
    private void initWebView()
    {
        this.webview = findViewById(R.id.web_view);
        this.webview.setWebViewClient(new WebViewClient());
        this.webview.loadUrl(this.url);
    }

    //Init the button scrollBar with the button link to the video chapters and web view
    private void initButtonScrollBar()
    {
        Button buttonIntro = findViewById(R.id.buttonIntro);
        Button buttonTitle = findViewById(R.id.buttonTitle);
        Button buttonAssault = findViewById(R.id.buttonAssault);
        Button buttonButterflies = findViewById(R.id.buttonButterflies);
        Button buttonPayback = findViewById(R.id.buttonPayback);
        Button buttonCast = findViewById(R.id.buttonCast);

        buttonIntro.setTag("Intro");
        buttonTitle.setTag("Title");
        buttonAssault.setTag("Assault");
        buttonButterflies.setTag("Butterflies");
        buttonPayback.setTag("Payback");
        buttonCast.setTag("Cast");

        buttonIntro.setOnClickListener(clickHandler);
        buttonTitle.setOnClickListener(clickHandler);
        buttonAssault.setOnClickListener(clickHandler);
        buttonButterflies.setOnClickListener(clickHandler);
        buttonPayback.setOnClickListener(clickHandler);
        buttonCast.setOnClickListener(clickHandler);
    }

    private View.OnClickListener clickHandler = new View.OnClickListener()
    {
        public void onClick(View view)
        {
            videoView.seekTo(chapterService.getPositionByChapterTitle(view.getTag().toString())*1000);
        }
    };

    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        this.thread.interrupt();
        savedInstanceState.putInt("Position", this.videoView.getCurrentPosition());
        this.videoView.suspend();
    }

    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        this.position = savedInstanceState.getInt("Position");
        this.videoView.seekTo(this.position);
        this.videoView.isPlaying();
    }
}