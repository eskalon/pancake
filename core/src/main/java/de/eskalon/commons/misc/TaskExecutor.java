/*
 * Copyright 2023 eskalon
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.eskalon.commons.misc;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;

import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.async.ThreadUtils;

/**
 * This Executor executes {@link #execute(Runnable) submitted} {@link Runnable}
 * tasks on the current thread when it is {@link #update() updated}.
 * 
 * @author damios
 */
public class TaskExecutor implements Executor {

	private Queue<Runnable> taskQueue = new LinkedList<>();
	private int taskCount = 0;

	public boolean update(int millis) {
		long endTime = TimeUtils.millis() + millis;
		while (true) {
			boolean done = update();
			if (done || TimeUtils.millis() > endTime)
				return done;
			ThreadUtils.yield();
		}
	}

	public synchronized boolean update() {
		Runnable task;

		if ((task = taskQueue.poll()) == null)
			return true;

		task.run();

		return taskQueue.isEmpty();
	}

	@Override
	public synchronized void execute(Runnable task) {
		taskQueue.add(task);
		taskCount++;
	}

	public synchronized float getProgress() {
		if (taskQueue.isEmpty())
			return 1;

		return (taskCount - taskQueue.size()) / (float) taskCount;
	}

}
