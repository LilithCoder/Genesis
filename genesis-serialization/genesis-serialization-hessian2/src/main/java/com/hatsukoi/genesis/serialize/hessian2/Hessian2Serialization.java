package com.hatsukoi.genesis.serialize.hessian2;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.common.constant.Hessian2Constant;
import com.hatsukoi.genesis.serialize.DataInput;
import com.hatsukoi.genesis.serialize.DataOutput;
import com.hatsukoi.genesis.serialize.Serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



/**
 * @author gaoweilin
 * @date 2022/06/06 Mon 2:24 AM
 */
public class Hessian2Serialization implements Serialization {

    @Override
    public String getContentType() {
        return Hessian2Constant.HESSIAN2_CONTENT_TYPE;
    }

    @Override
    public byte getContentTypeId() {
        return Hessian2Constant.HESSIAN2_SERIALIZATION_ID;
    }

    @Override
    public DataOutput serialize(URL url, OutputStream output) throws IOException {
        return new Hessian2DataOutput(output);
    }

    @Override
    public DataInput deserialize(URL url, InputStream input) throws IOException {
        return new Hessian2DataInput(input);
    }


}
 d