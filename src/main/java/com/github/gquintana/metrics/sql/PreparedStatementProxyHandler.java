package com.github.gquintana.metrics.sql;

/*
 * #%L
 * Metrics SQL
 * %%
 * Copyright (C) 2014 Open-Source
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.codahale.metrics.Timer;
import com.github.gquintana.metrics.proxy.MethodInvocation;

import java.sql.PreparedStatement;

/**
 * JDBC proxy handler for {@link PreparedStatement}
 */
public class PreparedStatementProxyHandler extends AbstractStatementProxyHandler<PreparedStatement> {

    private final Query query;

    public PreparedStatementProxyHandler(PreparedStatement delegate, JdbcProxyFactory proxyFactory, Query query, Timer.Context lifeTimerContext) {
        super(delegate, PreparedStatement.class, proxyFactory, lifeTimerContext);
        this.query = query;
    }

    protected final Object execute(MethodInvocation<PreparedStatement> methodInvocation) throws Throwable {
        Query currentQuery;
        if (methodInvocation.getArgCount() > 0) {
            currentQuery = new Query(methodInvocation.getArgAt(0, String.class));
        } else {
            currentQuery = this.query;
        }
        Timer.Context timerContext = getTimerStarter().startPreparedStatementExecuteTimer(currentQuery);
        Object result = methodInvocation.proceed();
        stopTimer(timerContext);
        return wrapResultSet(currentQuery, result);
    }

}
