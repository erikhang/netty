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

import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.ResourceLeak;
import io.netty.util.ResourceLeakDetector;

import java.util.LinkedList;
import java.util.List;

import static io.netty.util.internal.ObjectUtil.checkNotNull;

/**
 * A skeletal implementation of {@link DnsMessage}.
 */
public abstract class AbstractDnsMessage extends AbstractReferenceCounted implements DnsMessage {

    private static final ResourceLeakDetector<DnsMessage> leakDetector =
            new ResourceLeakDetector<DnsMessage>(DnsMessage.class);

    private final ResourceLeak leak = leakDetector.open(this);
    private short id;
    private DnsOpCode opCode;
    private boolean recursionDesired;
    private byte z;

    private Object questions; // a single question or the list of questions
    private List<DnsRecord> answers;
    private List<DnsRecord> authorityRecords;
    private Object additionalRecords; // a single record or the list of records.

    /**
     * Creates a new instance with the specified {@code id} and {@link DnsOpCode#QUERY} opCode.
     */
    protected AbstractDnsMessage(int id) {
        this(id, DnsOpCode.QUERY);
    }

    /**
     * Creates a new instance with the specified {@code id} and {@code opCode}.
     */
    protected AbstractDnsMessage(int id, DnsOpCode opCode) {
        setId(id);
        setOpCode(opCode);
    }

    @Override
    public int id() {
        return id & 0xFFFF;
    }

    @Override
    public DnsMessage setId(int id) {
        this.id = (short) id;
        return this;
    }

    @Override
    public DnsOpCode opCode() {
        return opCode;
    }

    @Override
    public DnsMessage setOpCode(DnsOpCode opCode) {
        this.opCode = checkNotNull(opCode, "opCode");
        return this;
    }

    @Override
    public boolean isRecursionDesired() {
        return recursionDesired;
    }

    @Override
    public DnsMessage setRecursionDesired(boolean recursionDesired) {
        this.recursionDesired = recursionDesired;
        return this;
    }

    @Override
    public int z() {
        return z;
    }

    @Override
    public DnsMessage setZ(int z) {
        this.z = (byte) (z & 7);
        return this;
    }

    @Override
    public int questionCount() {
        final Object questions = this.questions;
        if (questions == null) {
            return 0;
        }
        if (questions instanceof DnsQuestion) {
            return 1;
        }

        @SuppressWarnings("unchecked")
        List<DnsQuestion> questionList = (List<DnsQuestion>) questions;
        return questionList.size();
    }

    @Override
    public int answerCount() {
        final List<DnsRecord> answers = this.answers;
        if (answers == null) {
            return 0;
        } else {
            return answers.size();
        }
    }

    @Override
    public int authorityRecordCount() {
        final List<DnsRecord> authorityRecords = this.authorityRecords;
        if (authorityRecords == null) {
            return 0;
        } else {
            return authorityRecords.size();
        }
    }

    @Override
    public int additionalRecordCount() {
        final Object additionalRecords = this.additionalRecords;
        if (additionalRecords == null) {
            return 0;
        }
        if (additionalRecords instanceof DnsRecord) {
            return 1;
        }

        @SuppressWarnings("unchecked")
        List<DnsRecord> additionalRecordList = (List<DnsRecord>) additionalRecords;
        return additionalRecordList.size();
    }

    @Override
    public DnsQuestion question() {
        final Object questions = this.questions;
        if (questions == null) {
            return null;
        }
        if (questions instanceof DnsQuestion) {
            return (DnsQuestion) questions;
        }

        @SuppressWarnings("unchecked")
        final LinkedList<DnsQuestion> questionList = (LinkedList<DnsQuestion>) questions;
        if (questionList.isEmpty()) {
            return null;
        }

        return questionList.getFirst();
    }

    @Override
    public List<DnsQuestion> questions() {
        final Object questions = this.questions;
        if (questions == null) {
            List<DnsQuestion> newList = new LinkedList<DnsQuestion>();
            this.questions = newList;
            return newList;
        }
        if (questions instanceof DnsQuestion) {
            List<DnsQuestion> newList = new LinkedList<DnsQuestion>();
            newList.add((DnsQuestion) questions);
            this.questions = newList;
            return newList;
        }

        @SuppressWarnings("unchecked")
        final List<DnsQuestion> questionList = (List<DnsQuestion>) questions;
        return questionList;
    }

    @Override
    public DnsMessage setQuestion(DnsQuestion question) {
        questions = checkNotNull(question, "question");
        return this;
    }

    @Override
    public List<DnsRecord> answers() {
        List<DnsRecord> answers = this.answers;
        if (answers == null) {
            this.answers = answers = new LinkedList<DnsRecord>();
        }
        return answers;
    }

    @Override
    public List<DnsRecord> authorityRecords() {
        List<DnsRecord> authorityRecords = this.authorityRecords;
        if (authorityRecords == null) {
            this.authorityRecords = authorityRecords = new LinkedList<DnsRecord>();
        }
        return authorityRecords;
    }

    @Override
    public DnsRecord additionalRecord() {
        final Object additionalRecords = this.additionalRecords;
        if (additionalRecords == null) {
            return null;
        }
        if (additionalRecords instanceof DnsRecord) {
            return (DnsRecord) additionalRecords;
        }

        @SuppressWarnings("unchecked")
        final LinkedList<DnsRecord> additionalRecordList = (LinkedList<DnsRecord>) additionalRecords;
        if (additionalRecordList.isEmpty()) {
            return null;
        }

        return additionalRecordList.getFirst();
    }

    @Override
    public DnsMessage setAdditionalRecord(DnsRecord record) {
        additionalRecords = checkNotNull(record, "record");
        return this;
    }

    @Override
    public List<DnsRecord> additionalRecords() {
        final Object additionalRecords = this.additionalRecords;
        if (additionalRecords == null) {
            List<DnsRecord> newList = new LinkedList<DnsRecord>();
            this.additionalRecords = newList;
            return newList;
        }
        if (additionalRecords instanceof DnsRecord) {
            List<DnsRecord> newList = new LinkedList<DnsRecord>();
            newList.add((DnsRecord) additionalRecords);
            this.additionalRecords = newList;
            return newList;
        }

        @SuppressWarnings("unchecked")
        final List<DnsRecord> additionalRecordList = (List<DnsRecord>) additionalRecords;
        return additionalRecordList;
    }

    @Override
    public DnsMessage touch() {
        return (DnsMessage) super.touch();
    }

    @Override
    public DnsMessage touch(Object hint) {
        if (leak != null) {
            leak.record(hint);
        }
        return this;
    }

    @Override
    public DnsMessage retain() {
        return (DnsMessage) super.retain();
    }

    @Override
    public DnsMessage retain(int increment) {
        return (DnsMessage) super.retain(increment);
    }

    @Override
    protected void deallocate() {
        deallocate(questions);
        deallocateList(answers);
        deallocateList(authorityRecords);
        deallocate(additionalRecords);
    }

    private static void deallocate(Object recordOrList) {
        if (recordOrList != null) {
            if (recordOrList instanceof ReferenceCounted) {
                ((ReferenceCounted) recordOrList).release();
            } else if (recordOrList instanceof List) {
                @SuppressWarnings("unchecked")
                List<DnsRecord> list = (List<DnsRecord>) recordOrList;
                deallocateList(list);
            }
        }
    }

    private static void deallocateList(List<?> records) {
        if (records != null) {
            for (Object r : records) {
                ReferenceCountUtil.release(r);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DnsMessage)) {
            return false;
        }

        final DnsMessage that = (DnsMessage) obj;
        if (id() != that.id()) {
            return false;
        }

        if (this instanceof DnsQuery) {
            if (!(that instanceof DnsQuery)) {
                return false;
            }
        } else if (that instanceof DnsQuery) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return id() * 31 + (this instanceof DnsQuery? 0 : 1);
    }
}
