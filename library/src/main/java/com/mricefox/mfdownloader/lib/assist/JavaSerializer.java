package com.mricefox.mfdownloader.lib.assist;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/8
 */
public class JavaSerializer {
    private JavaSerializer() {
    }

    public static byte[] serialize(Object object) throws IOException {
        if (object == null) {
            return null;
        }
        ByteArrayOutputStream bos = null;
        try {
            ObjectOutput out = null;
            bos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            return bos.toByteArray();
        } finally {
            if (bos != null) {
                bos.close();
            }
        }
    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return in.readObject();
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public static byte[] safeSerialize(Object object) {
        try {
            return serialize(object);
        } catch (Throwable t) {
            t.printStackTrace();
            MFLog.e(String.format("error while serializing object %s", object.getClass().getSimpleName()), t);
        }
        return null;
    }

    public static Object safeDeserialize(byte[] bytes) {
        try {
            return deserialize(bytes);
        } catch (Throwable t) {
            t.printStackTrace();
            MFLog.e("error while deserializing job", t);
        }
        return null;
    }

    public static String safeSerialize2String(Object object) {
        byte[] bytes = safeSerialize(object);
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static Object safeDeserialize2Object(String s) {
        byte[] bytes = Base64.decode(s, Base64.DEFAULT);
        return safeDeserialize(bytes);
    }
}
