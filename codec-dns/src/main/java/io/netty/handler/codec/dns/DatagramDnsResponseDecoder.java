/*
 * Copyright 2015 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.net.InetSocketAddress;
import java.util.List;

import static io.netty.util.internal.ObjectUtil.checkNotNull;

/**
 * Decodes a {@link DatagramPacket} into an {@link AddressedEnvelope} of {@link DnsResponse}.
 */
@ChannelHandler.Sharable
public class DatagramDnsResponseDecoder extends MessageToMessageDecoder<DatagramPacket> {

    private final DnsRecordDecoder recordDecoder;

    public DatagramDnsResponseDecoder() {
        this(DnsRecordDecoder.DEFAULT);
    }

    public DatagramDnsResponseDecoder(DnsRecordDecoder recordDecoder) {
        this.recordDecoder = checkNotNull(recordDecoder, "recordDecoder");
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket packet, List<Object> out) throws Exception {
        final InetSocketAddress sender = packet.sender();
        final ByteBuf buf = packet.content();

        final DnsResponse response = newResponse(sender, buf);
        boolean success = false;
        try {
            final int questionCount = buf.readUnsignedShort();
            final int answerCount = buf.readUnsignedShort();
            final int authorityRecordCount = buf.readUnsignedShort();
            final int additionalRecordCount = buf.readUnsignedShort();

            decodeQuestions(response, buf, questionCount);
            for (int i = answerCount; i > 0; i --) {
                response.answers().add(recordDecoder.decodeRecord(buf));
            }
            for (int i = authorityRecordCount; i > 0; i --) {
                response.authorityRecords().add(recordDecoder.decodeRecord(buf));
            }
            decodeAdditionalRecords(response, buf, additionalRecordCount);

            out.add(response);
            success = true;
        } finally {
            if (!success) {
                response.release();
            }
        }
    }

    private static DnsResponse newResponse(InetSocketAddress sender, ByteBuf buf) {
        final int id = buf.readUnsignedShort();

        final int flags = buf.readUnsignedShort();
        if (flags >> 15 == 0) {
            throw new CorruptedFrameException("not a response");
        }

        final DnsResponse response = new DatagramDnsResponse(
                sender, null,
                id, DnsOpCode.valueOf((byte) (flags >> 11 & 0xf)), DnsResponseCode.valueOf((byte) (flags & 0xf)));

        response.setRecursionDesired((flags >> 8 & 1) == 1);
        response.setAuthoritativeAnswer((flags >> 10 & 1) == 1);
        response.setTruncated((flags >> 9 & 1) == 1);
        response.setRecursionAvailable((flags >> 7 & 1) == 1);
        response.setZ(flags >> 4 & 0x7);
        return response;
    }

    private void decodeQuestions(DnsResponse response, ByteBuf buf, int questionCount) throws Exception {
        if (questionCount == 1) {
            response.setQuestion(recordDecoder.decodeQuestion(buf));
        } else if (questionCount > 0) {
            final List<DnsQuestion> questions = response.questions();
            for (int i = questionCount; i > 0; i --) {
                questions.add(recordDecoder.decodeQuestion(buf));
            }
        }
    }

    private void decodeAdditionalRecords(
            DnsResponse response, ByteBuf buf, int additionalRecordCount) throws Exception {
        if (additionalRecordCount == 1) {
            response.setAdditionalRecord(recordDecoder.decodeRecord(buf));
        } else if (additionalRecordCount > 0) {
            final List<DnsRecord> additionalRecords = response.additionalRecords();
            for (int i = additionalRecordCount; i > 0; i --) {
                additionalRecords.add(recordDecoder.decodeRecord(buf));
            }
        }
    }
}
