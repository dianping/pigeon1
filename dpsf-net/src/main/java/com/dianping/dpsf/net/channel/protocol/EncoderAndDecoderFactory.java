/**
 *
 */
package com.dianping.dpsf.net.channel.protocol;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.protocol.hessian.Hessian1Encoder;
import com.dianping.dpsf.protocol.hessian.HessianDecoder;
import com.dianping.dpsf.protocol.hessian.HessianEncoder;
import com.dianping.dpsf.protocol.java.JavaDecoder;
import com.dianping.dpsf.protocol.java.JavaEncoder;

/**
 * <p>
 * Title: EncoderAndDecoderFactory.java
 * </p>
 * <p>
 * Description: 描述
 * </p>
 *
 * @author saber miao
 * @version 1.0
 * @created 2010-8-19 下午05:12:28
 */
public class EncoderAndDecoderFactory {

    private final static Encoder javaEncoder = new JavaEncoder();
    private final static Decoder javaDecoder = new JavaDecoder();

    private final static Encoder hessianEncoder = new HessianEncoder();
    private final static Encoder hessian1Encoder = new Hessian1Encoder();
    private final static Decoder hessianDecoder = new HessianDecoder();

    public static Encoder getEncoder(byte serializable) throws NetException {
        switch (serializable) {
            case Constants.SERILIZABLE_JAVA:
                return javaEncoder;
            case Constants.SERILIZABLE_HESSIAN:
                return hessianEncoder;
            case Constants.SERILIZABLE_HESSIAN1:
                return hessian1Encoder;
        }

        throw newNetException();
    }

    public static Decoder getClientDecoder(int serializable) throws NetException {

        switch (serializable) {
            case Constants.SERILIZABLE_JAVA:
                return javaDecoder;
            case Constants.SERILIZABLE_HESSIAN:
                return hessianDecoder;
        }
        throw newNetException();
    }

    public static Decoder getServerDecoder(int serializable) throws NetException {
        switch (serializable) {
            case Constants.SERILIZABLE_JAVA:
                return javaDecoder;
            case Constants.SERILIZABLE_HESSIAN:
            case Constants.SERILIZABLE_HESSIAN1:
                return hessianDecoder;
        }
        throw newNetException();
    }

    private static NetException newNetException() {
        return new NetException("only support protobuf hessian and java serializable protocol");
    }

}
