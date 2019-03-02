package org.erachain.entities;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import org.erachain.entities.transactions.Transaction;
import org.erachain.repositories.ExistingAccountGetter;
import org.erachain.repositories.TransactionTypeGetter;
import org.erachain.repositories.ValidSignaturesGetter;
import org.erachain.utils.TransUtil;
import org.erachain.utils.crypto.Base58;
import org.erachain.utils.crypto.Crypto;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.erachain.utils.ByteConstants.TYPE_LENGTH;

//@Service
@PropertySource("classpath:custom.properties")
public class TransactionFactory {

    private int port;

    public TransactionFactory(@Value("${VALIDATION_PORT}") int port) {
        this.port = port;
    }

    @Autowired
    private TransactionTypeGetter typeGetter;

    @Autowired
    private TransUtil transUtil;

    @Autowired
    private ExistingAccountGetter existingAccountGetter;

    @Autowired
    private TransactionTypeGetter transactionTypeGetter;

    @Autowired
    private Logger logger;

    private static final Field[] fields = Transaction.class.getDeclaredFields();

    private void setTransactionFields(byte[] data, int type, Transaction transaction) {

        List<TransactionInfo> transactionInfoList = typeGetter.getTransactionsListInfo(type);
        int position = 0;
        int nextSize = 0;
        for(TransactionInfo info : transactionInfoList) {

            String dType = info.getFieldType();
            int dataSize = nextSize == 0 ? info.getFieldLength() : nextSize;
            String name = info.getFieldName();
            if ("length".equalsIgnoreCase(dType)) {
                nextSize = transUtil.getField(data, dType, position, dataSize);
                if (nextSize > info.getMaxValue()) {
                    transaction.setErrMessage(name + " exсeeds Max Value");
                    transaction.setOk(false);
                    return ;
                }
            }
            else if ("Class".equalsIgnoreCase(dType)){
                BaseEntity poll = transactionTypeGetter.getCustomObject(name);
                if (!poll.apply(data, position)) {
                    transaction.setErrMessage(poll.getErrors());
                    transaction.setOk(false);
                    return;
                }
                dataSize = poll.getLength();
            }
            else {
                transaction.setFldByName(name, transUtil.getField(data, dType, position, dataSize));
                nextSize = 0;
            }
            if ("signature".equalsIgnoreCase(name)) {
                transaction.setSignatureLength(dataSize);
                transaction.setSignaturePosition(position);
            }
            position += dataSize;
        }
    }

    public Transaction parse(String rawString) {
        byte[] data = Base58.decode(rawString);
        int type = Arrays.copyOfRange(data, 0, TYPE_LENGTH)[0];
        if (typeGetter.getTransactionsListInfo(type) == null)
            return null;
        Transaction transaction = new Transaction(data);
        setTransactionFields(data, type, transaction);
        if (!transaction.isOk())
            return transaction;
        Arrays.stream(fields).forEach(f -> {
            String key = type + "_" + f.getName();
            TransactionInfo info = typeGetter.getInstance(key);
            if (info != null) {

                try {
                    f.setAccessible(true);
                    f.set(transaction, transaction.getFldByName(f.getName()));

                }
                catch (IllegalAccessException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });

        if (isValidTransaction(transaction)){

            transaction.getCreator().setAccountId(checkRegisteredAccount(transaction));
            transaction.setRowData(rawString.getBytes());
            return transaction;
        }
        return null;
    }

    private int checkRegisteredAccount(Transaction transaction) {
        String key = Crypto.getInstance().getAddress(transaction.getCreator().getPublicKey());
        Integer existingAccountId = existingAccountGetter.getExistingAccounts().entrySet().stream()
                .filter(e -> e.getKey().equals(key))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);

        if (existingAccountId == null) {
            return existingAccountGetter.insertNewAccount(
                    transaction.getCreator().getPublicKey());
        }

        return existingAccountId;
    }

    private boolean isValidTransaction(Transaction transaction) {
        int signaturePosition = transaction.getSignaturePosition();
        int signatureLength = transaction.getSignatureLength();
        //get transaction's byte[] without signature
        byte[] dataNoSignatures;
        dataNoSignatures = Bytes.concat(
                Arrays.copyOfRange(transaction.getData(), 0, signaturePosition),
                Arrays.copyOfRange(transaction.getData(), signaturePosition + signatureLength, transaction.getData().length));
        dataNoSignatures = Bytes.concat(dataNoSignatures, Ints.toByteArray(port));

        if (Crypto.getInstance().verify(
                transaction.getCreator().getPublicKey(),
                transaction.getSignature(),
                dataNoSignatures)) {
            return true;
        }

        for (String valid_sign: ValidSignaturesGetter.getValidSignatures()){
            if (Arrays.equals(dataNoSignatures, Base58.decode(valid_sign))) {
                return true;
            }
        }

        return false;
    }
}
