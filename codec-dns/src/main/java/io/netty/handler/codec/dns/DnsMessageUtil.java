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

import io.netty.channel.AddressedEnvelope;
import io.netty.util.internal.StringUtil;

import java.net.SocketAddress;
import java.util.List;

/**
 * Provides some utility methods for DNS message implementations.
 */
final class DnsMessageUtil {

    static StringBuilder appendQuery(StringBuilder buf, DnsQuery query) {
        appendQueryHeader(buf, query);
        appendAllRecords(buf, query);
        return buf;
    }

    static StringBuilder appendResponse(StringBuilder buf, DnsResponse response) {
        appendResponseHeader(buf, response);
        appendAllRecords(buf, response);
        return buf;
    }

    private static void appendQueryHeader(StringBuilder buf, DnsQuery msg) {
        buf.append(StringUtil.simpleClassName(msg));
        buf.append('(');
        appendAddresses(buf, msg);
        buf.append(msg.id());
        buf.append(", ");
        buf.append(msg.opCode());
        if (msg.isRecursionDesired()) {
            buf.append(", RD");
        }
        if (msg.z() != 0) {
            buf.append(", Z: ");
            buf.append(msg.z());
        }
        buf.append(')');
    }

    private static void appendResponseHeader(StringBuilder buf, DnsResponse msg) {
        buf.append(StringUtil.simpleClassName(msg));
        buf.append('(');
        appendAddresses(buf, msg);
        buf.append(msg.id());
        buf.append(", ");
        buf.append(msg.opCode());
        buf.append(", ");
        buf.append(msg.code());
        buf.append(',');

        boolean hasComma = true;
        if (msg.isRecursionDesired()) {
            hasComma = false;
            buf.append(" RD");
        }
        if (msg.isAuthoritativeAnswer()) {
            hasComma = false;
            buf.append(" AA");
        }
        if (msg.isTruncated()) {
            hasComma = false;
            buf.append(" TC");
        }
        if (msg.isRecursionAvailable()) {
            hasComma = false;
            buf.append(" RA");
        }
        if (msg.z() != 0) {
            if (!hasComma) {
                buf.append(',');
            }
            buf.append(" Z: ");
            buf.append(msg.z());
        }

        if (hasComma) {
            buf.setCharAt(buf.length() - 1, ')');
        } else {
            buf.append(')');
        }
    }

    private static void appendAddresses(StringBuilder buf, DnsMessage msg) {

        if (!(msg instanceof AddressedEnvelope)) {
            return;
        }

        @SuppressWarnings("unchecked")
        AddressedEnvelope<?, SocketAddress> envelope = (AddressedEnvelope<?, SocketAddress>) msg;

        SocketAddress addr = envelope.sender();
        if (addr != null) {
            buf.append("from: ");
            buf.append(addr);
            buf.append(", ");
        }

        addr = envelope.recipient();
        if (addr != null) {
            buf.append("to: ");
            buf.append(addr);
            buf.append(", ");
        }
    }

    private static void appendAllRecords(StringBuilder buf, DnsMessage msg) {
        appendQuestions(buf, msg);
        if (msg.answerCount() != 0) {
            appendRecords(buf, msg.answers());
        }
        if (msg.authorityRecordCount() != 0) {
            appendRecords(buf, msg.authorityRecords());
        }
        appendAdditionalRecords(buf, msg);
    }

    private static void appendQuestions(StringBuilder buf, DnsMessage msg) {
        final int questionCount = msg.questionCount();
        if (questionCount == 0) {
            return;
        }

        if (questionCount == 1) {
            buf.append(StringUtil.NEWLINE);
            buf.append(StringUtil.TAB);
            buf.append(msg.question());
        } else {
            for (DnsQuestion q: msg.questions()) {
                buf.append(StringUtil.NEWLINE);
                buf.append(StringUtil.TAB);
                buf.append(q);
            }
        }
    }

    private static void appendAdditionalRecords(StringBuilder buf, DnsMessage msg) {
        final int additionalRecordCount = msg.additionalRecordCount();
        if (additionalRecordCount == 0) {
            return;
        }

        if (additionalRecordCount == 1) {
            buf.append(StringUtil.NEWLINE);
            buf.append(StringUtil.TAB);
            buf.append(msg.additionalRecord());
        } else {
            appendRecords(buf, msg.additionalRecords());
        }
    }

    private static void appendRecords(StringBuilder buf, List<DnsRecord> records) {
        for (DnsRecord r: records) {
            buf.append(StringUtil.NEWLINE);
            buf.append(StringUtil.TAB);
            buf.append(r);
        }
    }

    private DnsMessageUtil() { }
}
