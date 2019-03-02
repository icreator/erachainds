package org.erachain.repositories;

import org.erachain.entities.blocks.Block;
import org.erachain.entities.transactions.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

//@Repository
public class DBTransaction {
    private static  final int MAX_BLOCK_SIZE = 5000;
    private static  final int QUEUE_SIZE = 1000;
    private static  final AtomicInteger sequenceNumber = new AtomicInteger(0);
    private static BlockingQueue<Transaction> queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
    private static SortedMap<Long,Transaction> trMap = new ConcurrentSkipListMap<>();
    private static SortedSet<Long> keySet = new ConcurrentSkipListSet<>();
    private static SortedSet<Long> blockKeys = new ConcurrentSkipListSet<>();
    private static final long MILLIS = 1_540_000_000_000L;
    private static AtomicLong lastBlockKey = new AtomicLong(0);
    private static AtomicLong firstBlockKey = new AtomicLong(0);
    private static AtomicLong nOfTrInBlock = new AtomicLong(0);

    private static BlockSave blockSave;

    private static  void  setBlockSave(BlockSave blockS) {
        blockSave = blockS;
    }

    private static Long getKey(Transaction transaction) {

        long micro = (transaction.getTimestamp() - MILLIS) * 100_000_000;
        micro += transaction.getCreator().getAccountId() * 1000;
        return micro;
    }

    @Autowired
    public DBTransaction(BlockSave blockSave) {
        setBlockSave(blockSave);
        TrThread.startThread();
        BlockTr.startThread();
    }

    public void setTransaction(Transaction tr) throws InterruptedException {
        while(tr != null) {
            if(!queue.offer(tr)) {
                TimeUnit.MILLISECONDS.sleep((long) 1);
            }
            else {
                tr = null;
            }
        }
    }

    public static class TrThread implements Runnable {
        private static AtomicInteger actTrhreads = new AtomicInteger(0);
        private final AtomicInteger micros = new AtomicInteger(0);
        private static boolean startIt = false;

        static void startThread() {
            actTrhreads.incrementAndGet();
            startIt = true;
            new Thread(new TrThread()).start();
        }

        public  void run()  {

            while (startIt) {
                while (queue.isEmpty()) {
                    try {
                        TimeUnit.MILLISECONDS.sleep((long) 1);
                    } catch (InterruptedException e) {

                    }
                }
				 // put into trans map
                queue.parallelStream().forEach(o -> {
                    Long key = getKey(o);
                    while(!keySet.add(key)) {
                        key +=  micros.updateAndGet(n -> (n >= 999) ? 1 : n + 1);
                    }
                    trMap.putIfAbsent(key, o);
                    sequenceNumber.incrementAndGet();
                    queue.remove();
                });
            }
        }
    }

    public static class BlockTr implements Runnable {
        private static  boolean started = false;
        private static  long iSleep = 1000;
        private static  long maxElapsed = 100_000;
        private static Date lastBlockDate;

        private  static   void setSleepTime()
        {
            iSleep = (iSleep * MAX_BLOCK_SIZE / 2) / sequenceNumber.get();
        }

        private static boolean isTooLong() {
            if (lastBlockDate == null)
                lastBlockDate = new Date();
            return  (new Date().getTime() - lastBlockDate.getTime()) > maxElapsed;
        }

        private static boolean isToStartBlock() {
            return isTooLong();
        }

        static void startThread () {
            started = true;
            new Thread(new BlockTr()).start();
        }

        public void run() {

            while(started) {
                if (isTooLong() && sequenceNumber.get() > 0 || sequenceNumber.get() >= MAX_BLOCK_SIZE) {
                    //setSleepTime();
                    Block block = new Block();
                    int itr = 0;
                    Iterator<Long> iterator = keySet.iterator();
                    blockKeys.clear();
                    int limit = sequenceNumber.get() >= MAX_BLOCK_SIZE ? sequenceNumber.get() / 2 : sequenceNumber.get();
                    while(iterator.hasNext()) {
                        if (++itr > limit) {
                            break;
                        }
                        Long key = iterator.next();
                        Transaction transaction = trMap.remove(key);
                        block.addTransaction(transaction);
                        blockKeys.add(key);
                        sequenceNumber.decrementAndGet();
                    }
                    lastBlockKey.set(blockKeys.last());
                    firstBlockKey.set(blockKeys.first());
                    keySet.removeAll(blockKeys);
                    nOfTrInBlock.set(block.getTransactionCount());

                    blockSave.save(block);
                    lastBlockDate = new Date();

                }
                else {
                    try {
                        TimeUnit.MILLISECONDS.sleep(iSleep);
                    } catch (InterruptedException e) {

                    }
                }
            }
        }
    }
}
