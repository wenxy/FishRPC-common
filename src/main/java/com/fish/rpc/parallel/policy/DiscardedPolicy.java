/**
 * Copyright (C) 2017 FishRPC Group Holding Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fish.rpc.parallel.policy;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

 
public class DiscardedPolicy implements RejectedExecutionHandler {
 
    private String threadName;

    public DiscardedPolicy() {
        this(null);
    }

    public DiscardedPolicy(String threadName) {
        this.threadName = threadName;
    }

    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        if (threadName != null) {
            System.err.println("RPC Thread pool [{}] is exhausted, executor="+executor.toString()+", threadName");
        }

        if (!executor.isShutdown()) {
            BlockingQueue<Runnable> queue = executor.getQueue();
            int discardSize = queue.size() >> 1;
            for (int i = 0; i < discardSize; i++) {
                queue.poll();
            }

            queue.offer(runnable);
        }
    }
}
