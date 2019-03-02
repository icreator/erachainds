package org.erachain.entities.blocks;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import org.erachain.entities.transactions.Transaction;
import org.erachain.utils.crypto.Crypto;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Block {

    @Autowired
    Crypto cryptoService;

    public static byte[] parentHash;

    protected int transactionCount;

    public int getTransactionCount() {
        return transactionCount;
    }

    public byte[] getTransactionsHash() {
        return transactionsHash;
    }

    protected byte[] transactionsHash;

    protected byte[] data = new byte[0];

    protected byte[] reference;

    protected List<Short> offSet = new ArrayList<>();
    protected byte[] atBytes;
    protected Date start ;
    protected Date end;


    public Date getStart() {
        return start;
    }
    public Date getEnd() {
        return end;
    }

    public void addTransaction(Transaction transaction) {

        Date date = new Date(transaction.getTimestamp());
        addTransaction(date, transaction);
    }

    public byte[] getBlob(Crypto cryptoService) {
        byte[] blob = new byte[0];
        transactionsHash = cryptoService.digest(data);
        blob = Bytes.concat(blob, transactionsHash);
        if (parentHash == null)
            parentHash =  cryptoService.digest("This is the start block".getBytes());
        blob = Bytes.concat(blob, parentHash);
        parentHash = transactionsHash;
        blob = Bytes.concat(blob, Ints.toByteArray(transactionCount));

        ByteBuffer buffer = ByteBuffer.allocate(offSet.size() * 2);
        offSet.stream().forEach(o -> buffer.asShortBuffer().put(o));
        blob = Bytes.concat(blob, buffer.array());
        blob = Bytes.concat(blob, data);
        return blob;
    }
    public void addTransaction(Date date, Transaction transaction) {

        data = Bytes.concat(data, transaction.getRowData());

        offSet.add((short) data.length);
        transactionCount ++;
        if (start == null || date.before(start)) {
            start = date;
        }
        if (end == null || date.after(end)) {
            end = date;
        }
    }
}
