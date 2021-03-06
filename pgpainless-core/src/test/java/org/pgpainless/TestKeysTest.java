/*
 * Copyright 2018 Paul Schaub.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pgpainless;

import static junit.framework.TestCase.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;

import junit.framework.TestCase;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.util.io.Streams;
import org.junit.Test;
import org.pgpainless.decryption_verification.DecryptionStream;
import org.pgpainless.key.protection.UnprotectedKeysProtector;

public class TestKeysTest extends AbstractPGPainlessTest {

    private final PGPSecretKeyRing juliet;
    private final PGPSecretKeyRing romeo;

    public TestKeysTest() throws IOException, PGPException {
        this.juliet = TestKeys.getJulietSecretKeyRing();
        this.romeo = TestKeys.getRomeoSecretKeyRing();
    }

    @Test
    public void keyIdTest() {
        TestCase.assertEquals(TestKeys.JULIET_KEY_ID, juliet.getSecretKey().getKeyID());
        TestCase.assertEquals(TestKeys.ROMEO_KEY_ID, romeo.getSecretKey().getKeyID());
    }

    @Test
    public void decryptVerifyTest() throws Exception {
        String encryptedMessage = TestKeys.TEST_MESSAGE_01;

        DecryptionStream decryptor = PGPainless.createDecryptor()
                .onInputStream(new ByteArrayInputStream(encryptedMessage.getBytes()))
                .decryptWith(new UnprotectedKeysProtector(), new PGPSecretKeyRingCollection(Collections.singleton(juliet)))
                .verifyWith(Collections.singleton(new PGPPublicKeyRing(Collections.singletonList(juliet.getPublicKey()))))
                .ignoreMissingPublicKeys()
                .build();

        ByteArrayOutputStream toPlain = new ByteArrayOutputStream();
        Streams.pipeAll(decryptor, toPlain);
        decryptor.close();
        toPlain.close();

        byte[] expected = TestKeys.TEST_MESSAGE_01_PLAIN.getBytes(Charset.forName("UTF-8"));
        byte[] actual = toPlain.toByteArray();

        assertTrue(Arrays.equals(expected, actual));
    }
}
