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
package org.pgpainless.decryption_verification;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;

public class DecryptionStream extends InputStream {

    private final InputStream inputStream;
    private final OpenPgpMetadata.Builder resultBuilder;
    private boolean isClosed = false;

    DecryptionStream(@Nonnull InputStream wrapped, @Nonnull OpenPgpMetadata.Builder resultBuilder) {
        this.inputStream = wrapped;
        this.resultBuilder = resultBuilder;
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
        this.isClosed = true;
    }

    public OpenPgpMetadata getResult() {
        if (!isClosed) {
            throw new IllegalStateException("DecryptionStream MUST be closed before the result can be accessed.");
        }
        return resultBuilder.build();
    }
}
