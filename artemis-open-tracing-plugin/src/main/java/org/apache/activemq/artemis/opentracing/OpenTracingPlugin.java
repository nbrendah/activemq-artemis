/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.artemis.opentracing;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.Message;
import org.apache.activemq.artemis.core.postoffice.RoutingStatus;
import org.apache.activemq.artemis.core.server.ServerSession;
import org.apache.activemq.artemis.core.server.plugin.ActiveMQServerPlugin;
import org.apache.activemq.artemis.core.transaction.Transaction;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name="open-tracing", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class OpenTracingPlugin extends AbstractMojo implements ActiveMQServerPlugin {

    CurrentSpan currentSpan;
    private Tracer tracer;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("*** Open Tracing plugin BEGIN ***");
        OpenTelemetry openTelemetry = AutoConfiguredOpenTelemetrySdk.initialize().getOpenTelemetrySdk();
        tracer = openTelemetry.getTracer(OpenTracingPlugin.class.getName());
    }

    @Override
    public void beforeSend(ServerSession session,
                           Transaction tx,
                           Message message,
                           boolean direct,
                           boolean noAutoCreateQueue) throws ActiveMQException {
        Span span = tracer.spanBuilder(String.format("message: %s", message.getMessageID())).setSpanKind(SpanKind.PRODUCER).startSpan();
        span.addEvent(String.format("message: %s", message.getMessageID()));
        currentSpan = new CurrentSpan(span);
    }

    @Override
    public void afterSend(Transaction tx, Message message, boolean direct, boolean noAutoCreateQueue,
                          RoutingStatus result) throws ActiveMQException {
        currentSpan.getSpan().end();
    }

    private class CurrentSpan {
        Span span;

        CurrentSpan(Span span){
            setSpan(span);
        }
        public Span getSpan() {
            return this.span;
        }
        private void setSpan(Span span){
            this.span = span;
        }
    }
}
