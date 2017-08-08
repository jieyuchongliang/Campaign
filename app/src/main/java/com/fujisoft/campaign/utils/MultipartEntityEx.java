package com.fujisoft.campaign.utils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by 860115015 on 2017/1/10.
 */

public class MultipartEntityEx implements HttpEntity {

    private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private String boundary = null;

    boolean isSetLast = false;
    boolean isSetFirst = false;

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    List<InputStream> isl = new ArrayList<InputStream>();
    SequenceInputStream sis = null;
    long sisAvailable = 0;

    boolean isGenSis = false;
    public void genSisIfNeeds(){
        if(!isGenSis){
            if(out.size() > 0){
                isl.add(new ByteArrayInputStream(out.toByteArray()));
                out.reset();
            }
            for(InputStream is:isl)
                try {
                    sisAvailable += is.available();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            sis = new SequenceInputStream(Collections.enumeration(isl));
        }
        isGenSis = true;
    }

    public MultipartEntityEx() {
        final StringBuffer buf = new StringBuffer();
        final Random rand = new Random();
        for (int i = 0; i < 30; i++) {
            buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        this.boundary = buf.toString();
    }

    public void writeFirstBoundaryIfNeeds() {
        if (!isSetFirst) {
            try {
                out.write(("--" + boundary + "\r\n").getBytes());
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        isSetFirst = true;
    }

    public void writeLastBoundaryIfNeeds() {
        if (isSetLast) {
            return;
        }

        try {
            out.write(("\r\n--" + boundary + "--\r\n").getBytes());
        } catch (final IOException e) {
            e.printStackTrace();
        }

        isSetLast = true;
    }

    public void addPart(final String key, final String value) {
        addPart(key, value, false);
    }

    public void addPart(final String key, final String value, final boolean isLast) {
        writeFirstBoundaryIfNeeds();
        try {
            out.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n").getBytes());
            out.write(value.getBytes());

            if (!isLast)
                out.write(("\r\n--" + boundary + "\r\n").getBytes());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void addPart(final String key, final File value) {
        addPart(key, value, false);
    }

    public void addPart(final String key, final File value, final boolean isLast) {
        try {
            addPart(key, value.getName(), new FileInputStream(value), isLast);
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addPart(final String key, final String fileName, final InputStream fin, final boolean isLast) {
        addPart(key, fileName, fin, "application/octet-stream", isLast);
    }

    public void addPart(final String key, final String fileName, final InputStream fin, String type, final boolean isLast) {
        writeFirstBoundaryIfNeeds();
        try {
            type = "Content-Type: " + type + "\r\n";
            out.write(("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + fileName + "\"\r\n").getBytes());
            out.write(type.getBytes());
            out.write("Content-Transfer-Encoding: binary\r\n\r\n".getBytes());
            out.flush();

            isl.add(new ByteArrayInputStream(out.toByteArray()));
            isl.add(fin);
            out.reset();

            if (!isLast)
                out.write(("\r\n--" + boundary + "\r\n").getBytes());
            out.flush();

        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getContentLength() {
        writeLastBoundaryIfNeeds();
        genSisIfNeeds();
        return sisAvailable;
    }

    @Override
    public Header getContentType() {
        return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        byte[] buff = new byte[65536];
        int len = -1;
        while((len = sis.read(buff)) != -1){
            outstream.write(buff, 0, len);
        }
    }

    @Override
    public Header getContentEncoding() {
        return null;
    }

    @Override
    public void consumeContent() throws IOException, UnsupportedOperationException {
        if (isStreaming()) {
            throw new UnsupportedOperationException( "Streaming entity does not implement #consumeContent()");
        }
    }

    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("getContent does not supported");
    }
}
