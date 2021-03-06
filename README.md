PGPainless - Use OpenPGP Painlessly!
====================================

[![Travis (.org)](https://travis-ci.org/pgpainless/pgpainless.svg)](https://travis-ci.org/pgpainless/pgpainless)
[![Git Tag](https://badgen.now.sh/github/tag/pgpainless/pgpainless)](https://github.com/pgpainless/pgpainless/tags)
[![Coverage Status](https://coveralls.io/repos/github/pgpainless/pgpainless/badge.svg?branch=master)](https://coveralls.io/github/pgpainless/pgpainless?branch=master)

About
-----

PGPainless aims to make using OpenPGP in Java projects as simple as possible.
It does so by introducing an intuitive Builder structure, which allows easy 
setup of encryption / decrytion operations, as well as straight forward key generation.

PGPainless is based around the Bouncycastle java library and can be used on Android down to API level 9.

### NOTE: PGPainless is in a *very* early state of development and should under no circumstances be used for serious production usage yet.

## Include PGPainless in your Project

PGPainless is available on maven central. In order to include it in your project, just add the 
maven central repository and add PGPainless as a dependency.

```gradle
repositories {
	mavenCentral()
}

dependencies {
	compile 'org.pgpainless:pgpainless-core:0.0.1-alpha1'
}
```

## How to use PGPainless

The entry point to the API is the `PGPainless` class. Here you can find methods for a quick start :)

### Generate Keys

The first thing you probably want to do is generate you some nice tasty Key Pairs. The most straight forward way to do so is by calling

```java
        PGPSecretKeyRing keyRing = PGPainless.generateKeyRing()
                .simpleRsaKeyRing("Juliet <juliet@montague.lit>", RsaLength._4096);
```

but feel free to explore the API further. PGPainless allows you to create Key Pairs consisting of a master key plus several sub keys, even with different algorithms at the same time!
Take for example a look at this delicious key:

```java
        PGPSecretKeyRing keyRing = PGPainless.generateKeyRing()
                .withSubKey(
                        KeySpec.getBuilder(ECDSA.fromCurve(EllipticCurve._P256))
                        .withKeyFlags(KeyFlag.SIGN_DATA)
                        .withDetailedConfiguration()
                        .withDefaultSymmetricAlgorithms()
                        .withDefaultHashAlgorithms()
                        .withPreferredCompressionAlgorithms(CompressionAlgorithm.ZLIB)
                        .withFeature(Feature.MODIFICATION_DETECTION)
                        .done())
                .withSubKey(
                        KeySpec.getBuilder(ECDH.fromCurve(EllipticCurve._P256))
                        .withKeyFlags(KeyFlag.ENCRYPT_COMMS, KeyFlag.ENCRYPT_STORAGE)
                        .withDefaultAlgorithms())
                .withMasterKey(
                        KeySpec.getBuilder(RSA_GENERAL.withLength(RsaLength._8192))
                                .withKeyFlags(KeyFlag.SIGN_DATA, KeyFlag.CERTIFY_OTHER)
                                .withDefaultAlgorithms())
                .withPrimaryUserId("Juliet <juliet@montague.lit>")
                .withPassphrase("romeo_oh_Romeo<3")
                .build();
```

### Encrypt / Sign Data

Encrypting and signing data is pretty straight forward as well.
```java
        EncryptionStream encryptor = PGPainless.createEncryptor()
                .onOutputStream(targetOuputStream)
                .toRecipients(publicKeyRings)
                .usingSecureAlgorithms()
                .signWith(secretKeyDecryptor, signingKeyRing)
                .noArmor();
```

The resulting `EncryptionStream` can then be used to encrypt data like follows:

```java
        Streams.pipeAll(sourceInputStream, encryptor);
        sourceInputStream.close();
        encryptor.close();
```

The encrypted data will be written to the provided `targetOutputStream`.

Additionally you can get information about the encrypted data by calling

```java
        PainlessResult result = encryptor.getResult();
```

That object will contain information like to which keys the message is encrypted, which keys were used for signing and so on.

### Decrypt / Verify Encrypted Data

To process incoming encrypted / signed data, just do the following:

```java
        DecryptionStream decryptor = PGPainless.createDecryptor()
                .onInputStream(sourceInputStream) // insert encrypted data here
                .decryptWith(secretKeyDecryptor, secretKey)
                .verifyWith(trustedKeyIds, senderKeys)
                .ignoreMissingPublicKeys()
                .build();
```

Again, the resulting `DecryptionStream` can be used like a normal stream.

```java
        Streams.pipeAll(decryptor, targetOutputStream);
        decryptor.close();
```

*After* the `DecryptionStream` was closed, you can get metadata about the processed data by retrieving the `PainlessResult`.
Again, this object will contain information about how the message was encrypted, who signed it and so on.

```java
        PainlessResult result = decryptor.getResult();
```

## About
PGPainless is a by-product of my [Summer of Code 2018 project](https://vanitasvitae.github.io/GSOC2018/).
For that project I was in need of a simple to use OpenPGP library.

Originally I was going to use [Bouncy-GPG](https://github.com/neuhalje/bouncy-gpg) for my project,
but ultimately I decided to create my own OpenPGP library which better fits my needs.

However, PGPainless is heavily influenced by Bouncy-GPG and  I would definitely recommend you to
use it instead of PGPainless if you want a more mature, better tested code base.

To reach out to the development team, feel free to send a mail: info@pgpainless.org

## Development
PGPainless is developed in - and accepts contributions from - the following places:

* [Github](https://github.com/pgpainless/pgpainless)
* [Teahub](https://teahub.io/PGPainless/pgpainless)

Please follow the [code of conduct](CODE_OF_CONDUCT.md) if you want to be part of the project.
