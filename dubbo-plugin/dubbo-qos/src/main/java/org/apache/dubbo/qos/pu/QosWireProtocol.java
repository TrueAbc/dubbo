/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.qos.pu;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.qos.server.DubboLogo;
import org.apache.dubbo.qos.server.handler.QosProcessHandler;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.remoting.api.AbstractWireProtocol;
import org.apache.dubbo.remoting.api.pu.ChannelHandlerPretender;
import org.apache.dubbo.remoting.api.pu.ChannelOperator;
import org.apache.dubbo.rpc.model.FrameworkModel;
import org.apache.dubbo.rpc.model.ScopeModelAware;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslContext;
import io.netty.util.concurrent.ScheduledFuture;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Activate
public class QosWireProtocol extends AbstractWireProtocol implements ScopeModelAware {
    public static final String PROMPT = "dubbo>";
    public static final String welcome = DubboLogo.DUBBO;
    private ScheduledFuture<?> welcomeFuture;

    public QosWireProtocol() {
        super(new QosDetector());
    }

    @Override
    public void configServerProtocolHandler(URL url, ChannelOperator operator) {
        // add qosProcess handler
        if (welcomeFuture != null && welcomeFuture.isCancellable()) {
            welcomeFuture.cancel(false);
        }
        QosProcessHandler handler = new QosProcessHandler(url.getOrDefaultFrameworkModel(),DubboLogo.DUBBO, false);
        List<ChannelHandler> handlers = new ArrayList<>();
        handlers.add(new ChannelHandlerPretender(handler));
        operator.configChannelHandler(handlers);
    }


    @Override
    public void configClientPipeline(URL url, ChannelPipeline pipeline, SslContext sslContext) {

    }

    @Override
    public void setFrameworkModel(FrameworkModel frameworkModel){
        QosDetector detector = (QosDetector) this.detector();
        detector.setFrameWorkModel(frameworkModel);
    }

    @Override
    public byte[] runActivateTask() throws IOException {
        if (welcome != null) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(welcome.getBytes());
            os.write(PROMPT.getBytes());
            return os.toByteArray();
        }else {
            return null;
        }
    }
}
