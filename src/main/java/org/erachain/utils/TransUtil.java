package org.erachain.utils;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import org.erachain.entities.account.Account;
import org.erachain.entities.account.PublicKeyAccount;
import org.erachain.entities.transactions.Transaction;
import org.erachain.utils.crypto.Base58;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.erachain.utils.ByteConstants.*;

//@Service
public class TransUtil {



    private interface DataOperator<T> {
        T apply(byte[] data,
                int position,
                int dataSize);
    }

    private static DataOperator<byte[]> toBytes = (x, y, z) ->
            Arrays.copyOfRange(x, y, y + z);

    private static DataOperator<Integer> toInt = (x, y, z) ->
            Ints.fromByteArray(toBytes.apply(x, y, z));

    private static DataOperator<Long> toLong = (x, y, z) ->
            Longs.fromByteArray(toBytes.apply(x, y, z));

    private static DataOperator<String> toStr = (x, y, z) ->
            new String(toBytes.apply(x, y, z), StandardCharsets.UTF_8);

    private static DataOperator<Transaction.Header> toHead = (x, position, z) -> {

        byte[] amount, arbitraryData , isEncrypted, isText;
        amount = arbitraryData = isEncrypted = isText = null;
        long key = 0;

        if (x[2] >= 0) {

            amount = toBytes.apply(x, position, AMOUNT_LENGTH);
            key = toLong.apply(x, position, AMOUNT_DEFAULT_SCALE);
            position += AMOUNT_LENGTH + AMOUNT_DEFAULT_SCALE;
        }

        int length = Byte.toUnsignedInt(x[position]);

        String head = new String(toBytes.apply(x, position + 1, length), StandardCharsets.UTF_8);

        if (x[3] >= 0) {

            position += length + 1;
            length = toInt.apply(x, position ,DATA_SIZE_LENGTH);
            arbitraryData = toBytes.apply(x, position + DATA_SIZE_LENGTH, length);
            isEncrypted = toBytes.apply(x, position + length + DATA_SIZE_LENGTH, ENCRYPTED_LENGTH);
            isText = toBytes.apply(x, position + length + DATA_SIZE_LENGTH + ENCRYPTED_LENGTH, IS_TEXT_LENGTH);
        }

        return new Transaction.Header(amount, key, head, arbitraryData, isEncrypted, isText);
    };

    private static DataOperator<BigDecimal> toBigDecimal = (x, y, z) ->
            new BigDecimal(new BigInteger(toBytes.apply(x, y, AMOUNT_DEFAULT_SCALE)), AMOUNT_DEFAULT_SCALE);

    private static DataOperator<Account> toAccount = (x, y, z) ->
            new Account(Base58.encode(toBytes.apply(x, y, z)));

    private static DataOperator<PublicKeyAccount> toPublicKeyAccount = (x, y, z) -> {

        byte[] key = toBytes.apply(x, y, z);
        return new PublicKeyAccount(key);
    };

    private static final Map<String, DataOperator> convMap = new HashMap<>();

    static {
        convMap.put("INT", toInt);
        convMap.put("LONG", toLong);
        convMap.put("STRING", toStr);
        convMap.put("BYTE[]", toBytes);
        convMap.put("HEADER", toHead);
        convMap.put("BIGDECIMAL", toBigDecimal);
        convMap.put("PUBLICKEYACCOUNT", toPublicKeyAccount);
        convMap.put("ACCOUNT", toAccount);
    }

    public  <T> T getField(byte[] data, String type, int position, int dataSize) {

        return (T) convMap.get(type.toUpperCase()).apply(data, position, dataSize);
    }

}
