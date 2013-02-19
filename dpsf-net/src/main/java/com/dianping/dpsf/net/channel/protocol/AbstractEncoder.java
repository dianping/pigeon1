/**
 *
 */
package com.dianping.dpsf.net.channel.protocol;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.component.DPSFSerializable;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;

import java.io.IOException;

/**
 * <p>
 * Title: AbstractEncoder.java
 * </p>
 * <p>
 * Description: 描述
 * </p>
 *
 * @author saber miao
 * @version 1.0
 * @created 2010-10-13 上午10:43:08
 */
public abstract class AbstractEncoder implements Encoder {

    protected static final byte[] LENGTH_PLACEHOLDER = new byte[7];

    //+3是在结尾写入扩展表示EXPEND_FLAG
    //+8是由于后面要写入long类型的扩展字段seq
    public static final int EXPAND_LANGTH = 3 + 8;

    protected void beforeDo(Object buffer) throws IOException {
        if (buffer instanceof ChannelBufferOutputStream) {
            ChannelBufferOutputStream buffer_ = (ChannelBufferOutputStream) buffer;
            buffer_.write(LENGTH_PLACEHOLDER);
        } else if (buffer instanceof ChannelBuffer) {
            ChannelBuffer buffer_ = (ChannelBuffer) buffer;
            buffer_.writeBytes(LENGTH_PLACEHOLDER);
        }

    }

    protected void afterDo(ChannelBuffer cb, Object msg) {

        //-7是由于减去占位符的长度，
        cb.setInt(3, cb.writerIndex() - 7 + EXPAND_LANGTH);
        expand(cb, msg);
    }

    private void expand(ChannelBuffer cb, Object msg) {
        //将msg的seq写入序列化外的数据流中，便于在序列化出问题是使用
        cb.writeLong(getSeq(msg));
        cb.writeBytes(Constants.EXPAND_FLAG);
    }

    private long getSeq(Object msg) {
        long seq = 0;
        if (msg instanceof DPSFSerializable) {
            DPSFSerializable msg_ = (DPSFSerializable) msg;
            seq = msg_.getSequence();
        }
        return seq;
    }

}
