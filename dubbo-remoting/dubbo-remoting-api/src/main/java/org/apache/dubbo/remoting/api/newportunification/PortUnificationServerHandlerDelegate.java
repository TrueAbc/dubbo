package org.apache.dubbo.remoting.api.newportunification;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.io.Bytes;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.buffer.ChannelBuffer;
import org.apache.dubbo.remoting.transport.ChannelHandlerDelegate;

import java.util.List;
import java.util.concurrent.ConcurrentMap;


// 最终触发事件的是handler, sent,
public class PortUnificationServerHandlerDelegate implements ChannelHandlerDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        PortUnificationServerHandlerDelegate.class);

    // 内部装饰的handler, 识别完成之后将最外层的handler剥离,
    private ChannelHandler handler;

    public final List<NewWireProtocol> protocols;
    public final ConcurrentMap<NewWireProtocol, URL> wireProtocolURLConcurrentMap;

    // 传递的应该是server的数据
    public PortUnificationServerHandlerDelegate(List<NewWireProtocol> protocols,
                                                ConcurrentMap<NewWireProtocol, URL> wireProtocolURLConcurrentMap,
                                                ChannelHandler handler) {
        this.protocols = protocols;
        this.handler = handler;
        this.wireProtocolURLConcurrentMap = wireProtocolURLConcurrentMap;
    }

    @Override
    public void connected(Channel channel) throws RemotingException {
        if(channel.getUrl() != null) {
            // 否则触发上层的连接建立逻辑存在问题
            handler.connected(channel);
        }
    }

    @Override
    public void disconnected(Channel channel) throws RemotingException {
        if(channel.getUrl() != null) {
            // 否则触发上层的连接建立逻辑存在问题
            handler.disconnected(channel);
        }
    }

    @Override
    public void sent(Channel channel, Object message) throws RemotingException {
        if(channel.getUrl() != null) {
            handler.sent(channel, message);
        }
    }

    // received 方法覆盖了自己装饰的handler, 在完成了识别后可以重新进行配置
    @Override
    public void received(Channel channel, Object message) throws RemotingException {
        // message 应该传输一个channel buffer
        LOGGER.debug("trigger handler delegate for wire protocol ");
        if (message instanceof ChannelBuffer) {
            // 调用的先是
            ChannelBuffer in = (ChannelBuffer) message;
            if (in.readableBytes() < 5) {
                return;
            }

            for (final NewWireProtocol protocol : protocols) {
                in.markReaderIndex();
                final NewProtocolDetector.Result result = protocol.detector().detect((ChannelWithHandler) channel, in);
                in.resetReaderIndex();
                switch (result) {
                    case UNRECOGNIZED:
                        continue;
                    case RECOGNIZED:
                        URL local_url = wireProtocolURLConcurrentMap.get(protocol);
                        // wire protocol 还要负责将现有的handler删除
                        protocol.configServerPipeline(local_url, (ChannelWithHandler) channel);
                        // 识别ok的话就正常触发检查逻辑, 但是connected需要再重新加载
                        ((ChannelWithHandler) channel).connected(channel);
// 让下次的消息读写触发
//                        ((ChannelWithHandler) channel).received(channel, message);
                        return;
                    case NEED_MORE_DATA:
                        return;
                    default:
                        return;
                }
            }
            byte[] preface = new byte[in.readableBytes()];
            in.readBytes(preface);

            LOGGER.error(String.format("Can not recognize protocol from downstream=%s . "
                    + "preface=%s protocols=%s", channel.getRemoteAddress(),
                Bytes.bytes2hex(preface)));

            // Unknown protocol; discard everything and close the connection.
            in.clear();
            channel.close();
        }
    }

    @Override
    public void caught(Channel channel, Throwable exception) throws RemotingException {
        LOGGER.error("unexpected exception from downstream before detected.", exception);
    }

    @Override
    public ChannelHandler getHandler() {
        return handler;
    }
}
