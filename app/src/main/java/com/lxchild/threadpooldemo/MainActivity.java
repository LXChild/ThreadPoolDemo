package com.lxchild.threadpooldemo;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    //http://blog.csdn.net/u012702547/article/details/52259529
    private ThreadPoolExecutor poolExecutor;
    private ExecutorService fixedThreadPool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        poolExecutor = new ThreadPoolExecutor(3, 30,
                1,TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>(6));


        fixedThreadPool = Executors.newFixedThreadPool(3);

        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                Log.d("google_lenve_fb", "run: ----");
            }
        };
        //延迟1s执行
        scheduledExecutorService.schedule(runnable, 1, TimeUnit.SECONDS);
        //在初始延迟后定时执行
        scheduledExecutorService.scheduleAtFixedRate(runnable, 1, 1, TimeUnit.SECONDS);
        //第一次延迟initialDelay秒，以后每次延迟delay秒执行一个任务
        scheduledExecutorService.scheduleWithFixedDelay(runnable, 1, 1, TimeUnit.SECONDS);

        btnClick2();
    }

//    1.shutDown()  关闭线程池，不影响已经提交的任务
//    2.shutDownNow() 关闭线程池，并尝试去终止正在执行的线程
//    3.allowCoreThreadTimeOut(boolean value) 允许核心线程闲置超时时被回收

    //submit 一般情况下我们使用execute来提交任务，但是有时候可能也会用到submit，使用submit的好处是submit有返回值
    //使用submit时我们可以通过实现Callable接口来实现异步任务。在call方法中执行异步任务，返回值即为该任务的返回值。Future是返回结果，返回它的isDone属性表示异步任务执行成功
    public void submit(View view) {
        List<Future<String>> futures = new ArrayList<>();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 5, 1,
                TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
        for (int i = 0; i < 10; i++) {
            Future<String> taskFuture = threadPoolExecutor.submit(new MyTask(i));
            //将每一个任务的执行结果保存起来
            futures.add(taskFuture);
        }
        try {
            //遍历所有任务的执行结果
            for (Future<String> future : futures) {
                Log.d("google_lenve_fb", "submit: " + future.get());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    class MyTask implements Callable<String> {

        private int taskId;

        public MyTask(int taskId) {
            this.taskId = taskId;
        }

        @Override
        public String call() throws Exception {
            SystemClock.sleep(1000);
            //返回每一个任务的执行结果
            return "call()方法被调用----" + Thread.currentThread().getName() + "-------" + taskId;
        }
    }

    public void runPoolExecutor() {
        for (int i = 0; i < 30; i++) {
            final int finalI = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(2000);
                    synchronized ("a") {
                        Log.d("google_lenve_fb", "run: " + finalI);
                        Log.d("google_lenve_fb", "run >>>>>>>>>>>>>: ");
                    }
                }
            };
            poolExecutor.execute(runnable);
        }
    }

    public void btnClick2() {
        for (int i = 0; i < 30; i++) {
            final int finalI = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(2000);
                    synchronized ("a") {
                        Log.d("google_lenve_fb", "run: " + finalI);
                        Log.d("google_lenve_fb", "run >>>>>>>>>>>>>: ");
                    }
                }
            };
            fixedThreadPool.execute(runnable);
        }
    }
}
