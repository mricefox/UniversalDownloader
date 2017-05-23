package com.mricefox.universaldownloader;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/5/22
 */

public class TimeoutWatcher {
    private static TimeoutWatcher head;

    private final long timeoutWhen;
    private TimeoutWatcher next;

    public TimeoutWatcher(long timeoutMillis) {
        this.timeoutWhen = System.nanoTime() + timeoutMillis * 1000_000;
    }

    private long remainingNanos(long now) {
        return timeoutWhen - now;
    }

    public void start() {
        addWatcher(this);
    }

    private static synchronized void addWatcher(TimeoutWatcher watcher) {
        long now = System.nanoTime();
        if (head == null) {
            head = watcher;
            new Watchdog().start();
        } else if (head.remainingNanos(now) > watcher.remainingNanos(now)) {
            //this watcher will reach timeout earlier then head, so reset head to this
            watcher.next = head;
            head = watcher;
            TimeoutWatcher.class.notify();
        } else {
            //sort the linked list by remaining time in ascending order
            for (TimeoutWatcher timeoutWatcher = head; ; timeoutWatcher = timeoutWatcher.next) {
                if (timeoutWatcher.next == null
                        || timeoutWatcher.next.remainingNanos(now) > watcher.remainingNanos(now)) {
                    watcher.next = timeoutWatcher.next;
                    timeoutWatcher.next = watcher;
                    break;
                }
            }
        }
    }

    /**
     * @return true- not timed out yet, false-timed out or has cancelled
     */
    public boolean cancel() {
        return removeWatcher(this);
    }

    private static synchronized boolean removeWatcher(TimeoutWatcher watcher) {
        if (head == watcher) {
            head = head.next;
            TimeoutWatcher.class.notify();
            return true;
        } else {
            for (TimeoutWatcher timeoutWatcher = head; timeoutWatcher != null;
                 timeoutWatcher = timeoutWatcher.next) {
                if (timeoutWatcher.next == watcher) {
                    timeoutWatcher.next = watcher.next;
                    watcher.next = null;
                    return true;
                }
            }
        }
        return false;
    }

    protected void onTimedOut() {
        //empty
    }

    private static synchronized boolean awaitTimeout() {
        if (head == null) {
            //no more watcher, tear down the watchdog thread
            return false;
        } else {
            //has remaining time till timeout
            long remainingNanos;
            if ((remainingNanos = head.remainingNanos(System.nanoTime())) > 0) {
                try {
                    long remainingMillis = remainingNanos / 1000_000;
                    remainingNanos -= remainingMillis * 1000_000;
                    TimeoutWatcher.class.wait(remainingMillis, (int) remainingNanos);
                } catch (InterruptedException ignored) {
                }
            } else {
                //time's up, remove head
                head.onTimedOut();
                head = head.next;
            }
            return true;
        }
    }

    private static final class Watchdog extends Thread {
        Watchdog() {
            super("timeout-watchdog");
            setDaemon(true);
        }

        @Override
        public void run() {
            super.run();
            while (awaitTimeout()) {
            }
        }
    }
}