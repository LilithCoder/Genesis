package com.hatsukoi.genesis.serialize.hessian2;

import com.caucho.hessian.io.Hessian2Input;
import com.hatsukoi.genesis.serialize.DataInput;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author gaoweilin
 * @date 2022/06/07 Tue 12:00 AM
 */
public class Hessian2DataInput implements DataInput {
    private final Hessian2Input hessian2Input;

    public Hessian2DataInput(InputStream input) {
        hessian2Input = new Hessian2Input(input);
    }
    @Override
    public Object readObject() throws IOException {
        return hessian2Input.readObject();
    }

    @Override
    public byte[] readBytes() throws IOException {
        return hessian2Input.readBytes();
    }
}
