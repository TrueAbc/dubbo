package org.apache.dubbo.rpc.protocol.dubbo;

import org.apache.dubbo.remoting.api.ProtocolDetector;
import org.apache.dubbo.remoting.buffer.ByteBufferBackedChannelBuffer;
import org.apache.dubbo.remoting.buffer.ChannelBuffer;
import org.apache.dubbo.remoting.buffer.ChannelBuffers;

import java.nio.ByteBuffer;

import static java.lang.Math.min;

public class DubboDetector implements ProtocolDetector {
    private final ChannelBuffer Preface = new ByteBufferBackedChannelBuffer(
        ByteBuffer.wrap(new byte[]{(byte)0xda, (byte)0xbb})
    );

    @Override
    public Result detect(ChannelBuffer in) {
        int prefaceLen = Preface.readableBytes();
        int bytesRead = min(in.readableBytes(), prefaceLen);

        if (bytesRead ==0 || !ChannelBuffers.prefixEquals(in,  Preface,  bytesRead)) {
            return Result.UNRECOGNIZED;
        }
        if (bytesRead == prefaceLen) {
            return Result.RECOGNIZED;
        }

        return Result.NEED_MORE_DATA;
    }
}
