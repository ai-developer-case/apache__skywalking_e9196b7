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
 *
 */

package org.apache.skywalking.apm.agent.core.jvm;

import io.grpc.Channel;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.skywalking.apm.agent.core.boot.BootService;
import org.apache.skywalking.apm.agent.core.boot.DefaultImplementor;
import org.apache.skywalking.apm.agent.core.boot.ServiceManager;
import org.apache.skywalking.apm.agent.core.commands.CommandService;
import org.apache.skywalking.apm.agent.core.conf.Config;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.agent.core.remote.GRPCChannelListener;
import org.apache.skywalking.apm.agent.core.remote.GRPCChannelManager;
import org.apache.skywalking.apm.agent.core.remote.GRPCChannelStatus;
import org.apache.skywalking.apm.network.common.v3.Commands;
import org.apache.skywalking.apm.network.language.agent.v3.JVMMetric;
import org.apache.skywalking.apm.network.language.agent.v3.JVMMetricCollection;
import org.apache.skywalking.apm.network.language.agent.v3.JVMMetricReportServiceGrpc;

import static org.apache.skywalking.apm.agent.core.conf.Config.Collector.GRPC_UPSTREAM_TIMEOUT;

@DefaultImplementor
public class JVMMetricsSender implements BootService, GRPCChannelListener {
    private static final ILog LOGGER = LogManager.getLogger(JVMMetricsSender.class);

    private volatile JVMMetricReportServiceGrpc.JVMMetricReportServiceBlockingStub stub = null;

    private LinkedBlockingQueue<JVMMetric> queue;
    public void statusChanged(GRPCChannelStatus status) {/**...**/}
    public void onComplete() {/**...**/}
    public void shutdown() {/**...**/}
    @Override
    public void prepare() {
        queue = new LinkedBlockingQueue<>(Config.Jvm.BUFFER_SIZE);
        ServiceManager.INSTANCE.findService(GRPCChannelManager.class).addChannelListener(this);
        Channel channel = ServiceManager.INSTANCE.findService(GRPCChannelManager.class).getChannel();
        stub = JVMMetricReportServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public void boot() {

    }

    public void offer(JVMMetric metric) {
        // drop last message and re-deliver
        if (!queue.offer(metric)) {
            queue.poll();
            queue.offer(metric);
        }
    }

    @Override
    public void run() {
        if (status == GRPCChannelStatus.CONNECTED && stub != null) {
            // ...此处为省略代码...
            }
        }
    }

    @Override
    public void statusChanged(GRPCChannelStatus status) {
        if (status == GRPCChannelStatus.CONNECTED && this.status == GRPCChannelStatus.DISCONNECT) {
            reconnect();
        }
        this.status = status;
    }

    private void reconnect() {
        Channel channel = ServiceManager.INSTANCE.findService(GRPCChannelManager.class).getChannel();
        stub = JVMMetricReportServiceGrpc.newBlockingStub(channel);
    }
    @Override
    public void onComplete() {

    }

    @Override
    public void shutdown() {

    }
}
