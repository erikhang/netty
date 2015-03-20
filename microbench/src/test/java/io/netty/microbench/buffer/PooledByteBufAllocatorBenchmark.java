/*
 * Copyright 2012 The Netty Project
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
package io.netty.microbench.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.microbench.util.AbstractMicrobenchmark;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Threads;

/**
 * This class benchmarks different allocators with different allocation sizes.
 */
@Threads(16)
public class PooledByteBufAllocatorBenchmark extends AbstractMicrobenchmark {

    private static final ByteBufAllocator pooledAllocator = PooledByteBufAllocator.DEFAULT;
    private static final int SIZE = 128;
    @Param({ "00008", "00016", "00032", "00064" })
    public int allocations;

    @Setup(Level.Iteration)
    public void populateCache() {
        final ByteBuf[] buffers = new ByteBuf[128];
        // Allocate multiple times
        for (int i = 0; i < buffers.length; i++) {
            buffers[i] = pooledAllocator.buffer(SIZE);
        }

        // Release all previous allocated buffers which means
        // these should be put back in the ThreadLocal cache
        for (int i = 0; i < buffers.length; i++) {
            buffers[i].release();
        }
    }

    @Benchmark
    public ByteBuf[] allocAndFree() {
        final ByteBuf[] buffers = new ByteBuf[allocations];
        // Allocate again which should now be served out of the
        // ThreadLocal cache
        for (int i = 0; i < allocations; i++) {
            buffers[i] = pooledAllocator.heapBuffer(SIZE);
        }

        return buffers;
    }
}
