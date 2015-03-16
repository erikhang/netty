/*
 * Copyright 2014 The Netty Project
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

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

public class DnsRecordClassTest {

    private static List<DnsRecordClass> allTypes() throws Exception {
        List<DnsRecordClass> result = new ArrayList<DnsRecordClass>();
        for (Field field : DnsRecordClass.class.getDeclaredFields()) {
            if ((field.getModifiers() & Modifier.STATIC) != 0 && field.getType() == DnsRecordClass.class) {
                result.add((DnsRecordClass) field.get(null));
            }
        }
        assertFalse(result.isEmpty());
        return result;
    }

    @Test
    public void testSanity() throws Exception {
        assertEquals("More than one type has the same int value",
                allTypes().size(), new HashSet<DnsRecordClass>(allTypes()).size());
    }

    /**
     * Test of hashCode method, of class DnsRecordClass.
     */
    @Test
    public void testHashCode() throws Exception {
        for (DnsRecordClass t : allTypes()) {
            assertEquals(t.intValue(), t.hashCode());
        }
    }

    /**
     * Test of equals method, of class DnsRecordClass.
     */
    @Test
    public void testEquals() throws Exception {
        for (DnsRecordClass t1 : allTypes()) {
            for (DnsRecordClass t2 : allTypes()) {
                if (t1 != t2) {
                    assertNotEquals(t1, t2);
                }
            }
        }
    }

    /**
     * Test of find method, of class DnsRecordClass.
     */
    @Test
    public void testFind() throws Exception {
        for (DnsRecordClass t : allTypes()) {
            DnsRecordClass found = DnsRecordClass.valueOf(t.intValue());
            assertSame(t, found);
            found = DnsRecordClass.valueOf(t.name());
            assertSame(t.name(), t, found);
        }
    }
}
