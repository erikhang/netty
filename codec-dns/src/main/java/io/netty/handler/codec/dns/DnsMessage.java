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

import io.netty.util.ReferenceCounted;

import java.util.List;

/**
 * The message super-class which contains core information concerning DNS
 * packets, both outgoing and incoming.
 */
public interface DnsMessage extends ReferenceCounted {

    /**
     * Returns the 2 byte unsigned identifier number.
     */
    int id();

    /**
     * Sets the ID of this message.
     */
    DnsMessage setId(int id);

    /**
     * Returns the 4 bit opcode.
     */
    DnsOpCode opCode();

    /**
     * Sets the opCode.
     *
     * @return the message to allow method chaining
     */
    DnsMessage setOpCode(DnsOpCode opCode);

    /**
     * Returns {@code true} if a query is to be pursued recursively.
     */
    boolean isRecursionDesired();

    /**
     * Sets whether a name server is directed to pursue a query recursively or not.
     *
     * @param recursionDesired if set to {@code true}, pursues query recursively
     * @return the message to allow method chaining
     */
    DnsMessage setRecursionDesired(boolean recursionDesired);

    /**
     * Returns the 3 bit reserved field 'Z'.
     */
    int z();

    /**
     * Sets the field Z. This field is reserved and should remain as 0 if the
     * DNS server does not make usage of this field.
     *
     * @param z
     *            the value for the reserved field Z
     */
    DnsMessage setZ(int z);

    /**
     * Returns the number of questions this message has.
     */
    int questionCount();

    /**
     * Returns the number of answers this message has.
     */
    int answerCount();

    /**
     * Returns the number of authority records this message has.
     */
    int authorityRecordCount();

    /**
     * Returns the number of additional records this message has.
     */
    int additionalRecordCount();

    /**
     * Returns the first question of this message.
     *
     * @return {@code null} if this message doesn't have a question.
     */
    DnsQuestion question();

    /**
     * Sets the question of this message.
     */
    DnsMessage setQuestion(DnsQuestion question);

    /**
     * Returns the list of all the questions in this message.
     */
    List<DnsQuestion> questions();

    /**
     * Returns the list of all the answer resource records in this message.
     */
    List<DnsRecord> answers();

    /**
     * Returns the list of all the authority resource records in this message.
     */
    List<DnsRecord> authorityRecords();

    /**
     * Returns the first additional record of this message.
     *
     * @return {@code null} if this message doesn't have an additional record.
     */
    DnsRecord additionalRecord();

    /**
     * Sets the additional record of this message.
     */
    DnsMessage setAdditionalRecord(DnsRecord record);

    /**
     * Returns the list of all the additional resource records in this message.
     */
    List<DnsRecord> additionalRecords();

    @Override
    DnsMessage touch();

    @Override
    DnsMessage touch(Object hint);

    @Override
    DnsMessage retain();

    @Override
    DnsMessage retain(int increment);
}
