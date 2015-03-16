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

import static io.netty.util.internal.ObjectUtil.checkNotNull;

/**
 * Represents a class field in DNS protocol
 */
public class DnsRecordClass implements Comparable<DnsRecordClass> {

    /**
     * Default class for DNS entries.
     */
    public static final DnsRecordClass IN = new DnsRecordClass(0x0001, "IN");
    public static final DnsRecordClass CSNET = new DnsRecordClass(0x0002, "CSNET");
    public static final DnsRecordClass CHAOS = new DnsRecordClass(0x0003, "CHAOS");
    public static final DnsRecordClass HESIOD = new DnsRecordClass(0x0004, "HESIOD");
    public static final DnsRecordClass NONE = new DnsRecordClass(0x00fe, "NONE");
    public static final DnsRecordClass ANY = new DnsRecordClass(0x00ff, "ANY");

    private static final String EXPECTED =
            " (expected: " + IN + ", " + CSNET + ", " + CHAOS + ", " + HESIOD + ", " + NONE + ", " + ANY + ')';

    public static DnsRecordClass valueOf(String name) {
        if (IN.name().equals(name)) {
            return IN;
        }
        if (NONE.name().equals(name)) {
            return NONE;
        }
        if (ANY.name().equals(name)) {
            return ANY;
        }
        if (CSNET.name().equals(name)) {
            return CSNET;
        }
        if (CHAOS.name().equals(name)) {
            return CHAOS;
        }
        if (HESIOD.name().equals(name)) {
            return HESIOD;
        }

        throw new IllegalArgumentException("name: " + name + EXPECTED);
    }

    public static DnsRecordClass valueOf(int intValue) {
        switch (intValue) {
        case 0x0001:
            return IN;
        case 0x0002:
            return CSNET;
        case 0x0003:
            return CHAOS;
        case 0x0004:
            return HESIOD;
        case 0x00fe:
            return NONE;
        case 0x00ff:
            return ANY;
        default:
            return new DnsRecordClass(intValue, "UNKNOWN");
        }
    }

    /**
     * Returns an instance of DnsClass for a custom type.
     *
     * @param clazz The class
     * @param name The name
     */
    public static DnsRecordClass valueOf(int clazz, String name) {
        return new DnsRecordClass(clazz, name);
    }

    /**
     * The protocol value of this DNS class
     */
    private final int intValue;

    /**
     * The name of this DNS class
     */
    private final String name;

    private String text;

    public DnsRecordClass(int intValue) {
        this(intValue, "UNKNOWN");
    }

    public DnsRecordClass(int intValue, String name) {
        if ((intValue & 0xffff) != intValue) {
            throw new IllegalArgumentException("intValue: " + intValue + " (expected: 0 ~ 65535)");
        }

        this.intValue = intValue;
        this.name = checkNotNull(name, "name");
    }

    /**
     * Returns the name of this class as used in bind config files
     */
    public String name() {
        return name;
    }

    /**
     * Returns the protocol value represented by this class
     */
    public int intValue() {
        return intValue;
    }

    @Override
    public int hashCode() {
        return intValue;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DnsRecordClass && ((DnsRecordClass) o).intValue == intValue;
    }

    @Override
    public int compareTo(DnsRecordClass o) {
        return intValue() - o.intValue();
    }

    @Override
    public String toString() {
        String text = this.text;
        if (text == null) {
            this.text = text = name + '(' + intValue() + ')';
        }
        return text;
    }
}
