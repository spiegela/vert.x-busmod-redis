/*
 * Copyright 2011-2012 the original author or authors.
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
package net.ml.vertx.mods.redis.commands.sortedsets;


import java.util.concurrent.Future;

import net.ml.vertx.mods.redis.CommandContext;
import net.ml.vertx.mods.redis.commands.Command;
import net.ml.vertx.mods.redis.commands.CommandException;

import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

/**
 * ZRemRangeByScoreCommand
 * <p>
 * 
 * @author <a href="http://marx-labs.de">Thorsten Marx</a>
 */
public class ZRemRangeByScoreCommand extends Command {

	public static final String COMMAND = "zremrangebyscore";

	public ZRemRangeByScoreCommand() {
		super(COMMAND);
	}

	@Override
	public void handle(Message<JsonObject> message, CommandContext context) throws CommandException {
		String key = getMandatoryString("key", message);
		checkNull(key, "key can not be null");
		
		
		Object min = message.body.getObject("min");
		checkNull(min, "min can not be null");
		checkType(min, "min must be of type double or string", new Class[] {Double.class, String.class});
		
		Object max = message.body.getObject("max");
		checkNull(max, "max can not be null");
		checkType(max, "max must be of type double or string", new Class[] {Double.class, String.class});
		
		
		try {
			Future<Long> response = null; 
			
			if (min instanceof String && max instanceof String) {
				response = context.getConnection().zremrangebyscore(key, (String)min, (String)max);
			} else if (min instanceof Double && max instanceof Double) {
				response = context.getConnection().zremrangebyscore(key, (Double)min, (Double)max);
			} else {
				throw new CommandException("min and max must be of the same type");
			}

			
			response(message, response.get());
			
		} catch (Exception e) {
			sendError(message, e.getLocalizedMessage());
		}

	}
}
