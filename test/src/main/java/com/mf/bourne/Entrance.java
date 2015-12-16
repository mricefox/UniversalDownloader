package com.mf.bourne;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class Entrance {
    public static void main(String[] args) throws Exception {
//        System.out.println("111");
//
//        try {
//            fun();
//        } catch (CustomException e) {
//            e.printStackTrace();
//            System.out.println("333");
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("444");
//        }
//        System.out.println("222");

//        a:
//        for (int i = 0; i < 5; ++i) {
//            System.out.println("i=" + i);
//            for (int j = 0; j < 4; ++j) {
//                if (j == 2)
//                    continue a;
//                System.out.println("j=" + j);
//            }
//        }

//        int a = 1 << 10;
//        System.out.println("a:" + a);

//        System.out.println("b:" + 1452 / (1 << 8));
//        System.out.println("b:" + 1452 % (1 << 8));
//
//        int a = 3;
//
//        int b_size = 3 + a-- <= 0 ? 0 : 1;
//
//        System.out.println("b_size:" + b_size);
//
        //序列化对象
//        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("D:\\objectFile.obj"));
//        Ser customer = new Ser();
//        customer.name = "mklop";
//        customer.age = 121;
//        out.writeObject("mnb");    //写入字面值常量
//        out.writeObject(new Date());    //写入匿名Date对象
//        out.writeObject(new A());    //写入customer对象
//        out.close();
//
//        //反序列化对象
//        ObjectInputStream in = new ObjectInputStream(new FileInputStream("D:\\objectFile.obj"));
//        System.out.println("obj1 " + (String) in.readObject());    //读取字面值常量
//        System.out.println("obj2 " + (Date) in.readObject());    //读取匿名Date对象
////        Ser obj3 = (Ser) in.readObject();    //读取customer对象
////        System.out.println("obj3 " + obj3);
//        in.close();


//        long a = 67887, b = 4567;
//
//        float c = 456, d = 45;
//        System.out.println(String.format("==%.2f", (b + 0.0f) / a));


//        try {
//            System.out.println("222");
////            throw new Exception("nn");
//        }catch (Exception e){
//            System.out.println("eee");
//        }finally {
//            System.out.println("ff");
//        }
//        System.out.println("11111");

//        System.out.println("ex=" + ex());
//
//        long current = 46;
//        long total = 67;
//        int r = (int) ((current + 0.0f) / total * 100);
//
//
//        System.out.println("r="+r);
//        En e1 = new En();
//        e1.a = 3;
//
//        En e2 = new En();
//        System.out.println("e2.a="+e2.a);

//        KK k1 = new KK();
//        k1.a = 4;
//        KK k2 = new KK();
//        k2.a = 7;
//        KK k3 = new KK();
//        k3.a = 3;
//        KK k4 = new KK();
//        k4.a = 8;
//
//        List<KK> list = new ArrayList<>();
//        list.add(k1);
//        list.add(k2);
//        list.add(k3);
//        list.add(k4);
//
//        for (KK k : list) {
//            System.out.println("==" + k.a);
//        }
//
//        Collections.sort(list, new Comparator<KK>() {
//            @Override
//            public int compare(KK o1, KK o2) {
//                if (o1.a > o2.a) return 1;
//                else if (o1.a < o2.a) return -1;
//                else return 0;
//            }
//        });
//        System.out.println("===");
//        for (KK k : list) {
//            System.out.println("==" + k.a);
//        }

//        GG g1 = new GG();
//        g1.a = 3;
//        g1.b = "bb";
//        List<GG> l = new ArrayList<>();
//        l.add(g1);
//        GG[] arr = new GG[l.size()];
//        l.toArray(arr);
//
//        g1.a = 4;
//        g1.b = "aa";
//        System.out.println("---" + arr[0].a);
//        System.out.println("---" + arr[0].b);


//        g1.out();
//
//        Command command0 = new Command();
//        command0.id = 0;
//        command0.doTaskTime = 500;
//
//        Command command1 = new Command();
//        command1.id = 1;
//        command1.doTaskTime = 3000;
//
//        Command command2 = new Command();
//        command2.id = 2;
//        command2.doTaskTime = 4000;
//


//        Stpe stpe = new Stpe(1);
//        stpe.scheduleAtFixedRate(command0, 1000, 1000, TimeUnit.MILLISECONDS);
////        stpe.scheduleWithFixedDelay(command0, 1000, 1000, TimeUnit.MILLISECONDS);
//        command0.startTime = System.currentTimeMillis();
//
//        stpe.scheduleAtFixedRate(command1, 1000, 1000, TimeUnit.MILLISECONDS);
//        command1.startTime = System.currentTimeMillis();
//
//        stpe.scheduleAtFixedRate(command2, 1000, 1000, TimeUnit.MILLISECONDS);
//        command2.startTime = System.currentTimeMillis();

//        System.out.println("size=" + displayFilesize(10));

//        ExecutorService manualUpdateExecutor = Executors.newCachedThreadPool();
//
//        manualUpdateExecutor.execute(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("1 s" + System.currentTimeMillis());
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("1 e" + System.currentTimeMillis());
//            }
//        });
//        manualUpdateExecutor.execute(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("2 s" + System.currentTimeMillis());
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("2 e" + System.currentTimeMillis());
//            }
//        });
//        manualUpdateExecutor.execute(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("3 s" + System.currentTimeMillis());
//                System.out.println("3 e" + System.currentTimeMillis());
//            }
//        });

//        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
////        Calendar calendar =Calendar.getInstance();
////        calendar.setTimeInMillis(300*1000);
////        calendar.get(Calendar.HOUR)
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//        String str = sdf.format(new Date(300 * 1000));
//
//        System.out.println("str=" + str);
//
//        int a = 10 < 12 ? 3 : 4 + 5;
//        System.out.println("str=" + convertMills2hhmmss(99999999999L));


//        printBinary(COUNT_BITS);
//        printBinary(RUNNING);
//        printBinary(STOP);

        printBinary(MODE_MASK);
        int size = Integer.MAX_VALUE;
        printBinary(size);
        int mode = EXACTLY;
        printBinary(mode);
        int measureSpec = (size & ~MODE_MASK) | (mode & MODE_MASK);
        printBinary(measureSpec);

        int remode = (measureSpec & MODE_MASK);
        printBinary(remode);
        int resize = (measureSpec & ~MODE_MASK);
        printBinary(resize);

        try {
//            int a = 9 / 0;
            System.out.println("eeeee");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("bbb");
        }
        System.out.println("iiiiiiiiiiii");


        System.out.println("-------------");
//        Sub s = new Sub(4, "", "");
        JJ j = new JJ(1);
    }

    public static class JJ {
        public JJ(int a) {
            this(a, "");
            System.out.println("1");
        }

        public JJ(int a, String b) {
            this(a, b, "");
            System.out.println("2");
        }

        public JJ(int a, String b, String c) {
            System.out.println("3");
        }
    }

    public static class Sub extends JJ {

        public Sub(int a) {
            super(a);
//            init(a);
        }

        public Sub(int a, String b) {
            super(a, b);
//            init(a);
        }

        public Sub(int a, String b, String c) {
            super(a, b, c);
            init(a);
        }

        private void init(int a) {
            System.out.println("init" + a);
        }
    }


    private static final int MODE_SHIFT = 31;
    private static final int MODE_MASK = 0x3 << MODE_SHIFT;

    public static final int EXACTLY = 1 << MODE_SHIFT;


    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
    private static final int COUNT_BITS = Integer.SIZE - 3;
    private static final int CAPACITY = (1 << COUNT_BITS) - 1;

    // runState is stored in the high-order bits
    private static final int RUNNING = -1 << COUNT_BITS;
    private static final int SHUTDOWN = 0 << COUNT_BITS;
    private static final int STOP = 1 << COUNT_BITS;
    private static final int TIDYING = 2 << COUNT_BITS;
    private static final int TERMINATED = 3 << COUNT_BITS;

    private static int runStateOf(int c) {
        return c & ~CAPACITY;
    }

    private static int workerCountOf(int c) {
        return c & CAPACITY;
    }

    private static int ctlOf(int rs, int wc) {
        return rs | wc;
    }

    public static void printBinary(int i) {
        System.out.println(i + " to binary:" + Integer.toBinaryString(i));
    }


    public static String convertMills2hhmmss(long ms) {
        long time;
        int second, minute, hour;
        time = ms / 1000;
        second = (int) (time % 60);
        minute = (int) (time / 60 % 60);
        hour = (int) (time / 3600);
        return (hour < 10 ? "0" : "") + hour
                + ":" + (minute < 10 ? "0" : "") + minute
                + ":" + (second < 10 ? "0" : "") + second;
    }

    public static String displayFilesize(long fileSize) {
        if (fileSize <= 0) {
            return "0";
        } else {
            final String[] fileUnit = new String[]{"B", "KB", "MB", "GB", "TB"};
            int group = (int) (Math.log10(fileSize) / Math.log10(1024));
            return new DecimalFormat("#,##0.#").format(fileSize
                    / Math.pow(1024, group)) + fileUnit[group];
        }
    }

    private static class Stpe extends ScheduledThreadPoolExecutor {

        public Stpe(int corePoolSize) {
            super(corePoolSize);
        }

        public Stpe(int corePoolSize, ThreadFactory threadFactory) {
            super(corePoolSize, threadFactory);
        }

        public Stpe(int corePoolSize, RejectedExecutionHandler handler) {
            super(corePoolSize, handler);
        }

        public Stpe(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
            super(corePoolSize, threadFactory, handler);
        }

        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
//            System.out.println("r="+r);
//
////            if (r instanceof Command){
//                System.out.println("instanceof");
//                Command c = (Command) r;
//                c.startTime = System.currentTimeMillis();
////            }
//            System.out.println("not instanceof");
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
        }
    }

    public static class Command implements Runnable {
        long id;
        long startTime;
        long doTaskTime;

        public void run() {
            try {
                Thread.sleep(doTaskTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long interval = System.currentTimeMillis() - startTime;
            System.out.println("id:" + id + "interval:" + interval);
//            int i = 10/0;
        }
    }

    public static class GG {
        int a;
        String b;

        synchronized void out() {
            inner();
            System.out.println("---out");
        }

        synchronized void inner() {
            System.out.println("---inner");
        }
    }

    public static interface Serialize extends Serializable {
        void print();
    }


    private static class A implements Serializable {
        String a;
    }

    public static class KK {
        int a;
    }


    public static int ex() {
        try {
            System.out.println("222");
//            throw new Exception("nn");

//            return -1;
        } catch (Exception e) {
            System.out.println("eee");
        } finally {
            System.out.println("ff");
        }
        System.out.println("11111");
        return 0;
    }

    static class Ser implements Serializable {
        String name;
        int age;

        @Override
        public String toString() {
//            return super.toString();
            return name + age;
        }
    }

    public static void fun() throws CustomException {
        System.out.println("fun");
        throw new RuntimeException();
    }

    public static class CustomException extends Exception {
        public CustomException() {
            super("CustomException");
        }
    }
}
