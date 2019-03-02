package org.erachain.utils.crypto;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Crypto.class})
public class CryptoTest {

    @Autowired
    private Crypto crypto;

    @Test
    public void ripemd160() {

        var publicKey = "AQyCxEXLewJvqzLegTW41xF3qjnTCr7tVvT6639WJsKb";
        var result = crypto.ripemd160(Base58.decode(publicKey));

        Assert.assertEquals("+6sDkzWQBSz8Kr4iSfnJpSagx7A=", Base64.encode(result));
    }
}