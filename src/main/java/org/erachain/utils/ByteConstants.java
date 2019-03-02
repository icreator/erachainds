package org.erachain.utils;

public abstract class ByteConstants {

    public static final int AMOUNT_LENGTH = 8;
    public static final int AMOUNT_DEFAULT_SCALE = 8;
    public static final int IS_TEXT_LENGTH = 1;
    public static final int DATA_SIZE_LENGTH = 4;
    public static final int ENCRYPTED_LENGTH = 1;
    public static final int TYPE_LENGTH = 4;

    private ByteConstants() {
        //to prohibit accidental instantiation
    }
}
