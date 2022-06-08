package com.hatsukoi.genesis.serialize.hessian2;

import com.caucho.hessian.io.Hessian2Output;
import com.hatsukoi.genesis.serialize.DataOutput;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author gaoweilin
 * @date 2022/06/07 Tue 12:00 AM
 */
public class Hessian2DataOutput implements DataOutput {
    private final Hessian2Output hessian2Output;

    public Hessian2DataOutput(OutputStream output) {
        hessian2Output = new Hessian2Output(output);
    }

    @Override
    public void writeObject(Object data) throws IOException {
        hessian2Output.writeObject(data);
    }

    @Override
    public void writeBytes(byte[] data) throws IOException {
        hessian2Output.writeBytes(data);
    }
}
