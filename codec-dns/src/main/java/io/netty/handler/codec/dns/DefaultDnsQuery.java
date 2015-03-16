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

public class DefaultDnsQuery extends AbstractDnsMessage implements DnsQuery {

    public DefaultDnsQuery(int id) {
        super(id);
    }

    public DefaultDnsQuery(int id, DnsOpCode opCode) {
        super(id, opCode);
    }

    @Override
    public DnsQuery setId(int id) {
        return (DnsQuery) super.setId(id);
    }

    @Override
    public DnsQuery setOpCode(DnsOpCode opCode) {
        return (DnsQuery) super.setOpCode(opCode);
    }

    @Override
    public DnsQuery setRecursionDesired(boolean recursionDesired) {
        return (DnsQuery) super.setRecursionDesired(recursionDesired);
    }

    @Override
    public DnsQuery setZ(int z) {
        return (DnsQuery) super.setZ(z);
    }

    @Override
    public DnsQuery setQuestion(DnsQuestion question) {
        return (DnsQuery) super.setQuestion(question);
    }

    @Override
    public DnsQuery setAdditionalRecord(DnsRecord record) {
        return (DnsQuery) super.setAdditionalRecord(record);
    }

    @Override
    public DnsQuery touch() {
        return (DnsQuery) super.touch();
    }

    @Override
    public DnsQuery touch(Object hint) {
        return (DnsQuery) super.touch(hint);
    }

    @Override
    public DnsQuery retain() {
        return (DnsQuery) super.retain();
    }

    @Override
    public DnsQuery retain(int increment) {
        return (DnsQuery) super.retain(increment);
    }

    @Override
    public String toString() {
        return DnsMessageUtil.appendQuery(new StringBuilder(128), this).toString();
    }
}
