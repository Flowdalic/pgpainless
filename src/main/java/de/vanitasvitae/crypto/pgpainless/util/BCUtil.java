package de.vanitasvitae.crypto.pgpainless.util;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;

public class BCUtil {

    public static PGPPublicKeyRingCollection keyRingsToKeyRingCollection(PGPPublicKeyRing... rings)
            throws IOException, PGPException {
        return new PGPPublicKeyRingCollection(Arrays.asList(rings));
    }

    public static PGPSecretKeyRingCollection keyRingsToKeyRingCollection(PGPSecretKeyRing... rings)
            throws IOException, PGPException {
        return new PGPSecretKeyRingCollection(Arrays.asList(rings));
    }

    public static PGPPublicKeyRing publicKeyRingFromSecretKeyRing(PGPSecretKeyRing secring) {
        List<PGPPublicKey> list = new ArrayList<>();
        for (Iterator<PGPPublicKey> i = secring.getPublicKeys(); i.hasNext(); ) {
            PGPPublicKey k = i.next();
            list.add(k);
        }

        // TODO: Change to simply using the List constructor once BC 1.60 gets released.
        try {
            Constructor<PGPPublicKeyRing> constructor;
            constructor = PGPPublicKeyRing.class.getDeclaredConstructor(List.class);
            constructor.setAccessible(true);
            PGPPublicKeyRing pubring = constructor.newInstance(list);
            return pubring;
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}