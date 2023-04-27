package eu.cessda.cvs.web.rest.utils;

import java.io.ByteArrayOutputStream;

public class AccesibleByteArrayOutputStream extends ByteArrayOutputStream {
    /**
     * Returns the byte array backing this output stream.
     */
    public synchronized byte[] getBuffer() {
        return buf;
    }
}
