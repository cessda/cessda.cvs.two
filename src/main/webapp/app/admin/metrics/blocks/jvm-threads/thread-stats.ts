/*
 * Copyright © 2017-2026 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { Thread } from '../../metrics.service';

export interface ThreadStats {
  threadDumpAll: number;
  threadDumpRunnable: number;
  threadDumpTimedWaiting: number;
  threadDumpWaiting: number;
  threadDumpBlocked: number;
}

export function countThreadStates(threads: Thread[]): ThreadStats {
  const count = (state: string): number => threads.filter(thread => thread.threadState === state).length;
  const stats = {
    threadDumpRunnable: count('RUNNABLE'),
    threadDumpWaiting: count('WAITING'),
    threadDumpTimedWaiting: count('TIMED_WAITING'),
    threadDumpBlocked: count('BLOCKED'),
  };
  return {
    ...stats,
    threadDumpAll: stats.threadDumpRunnable + stats.threadDumpWaiting + stats.threadDumpTimedWaiting + stats.threadDumpBlocked,
  };
}
