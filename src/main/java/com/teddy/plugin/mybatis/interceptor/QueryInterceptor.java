package com.teddy.plugin.mybatis.interceptor;

import com.teddy.plugin.mybatis.dialect.Dialect;
import com.teddy.plugin.mybatis.exception.QueryException;
import com.teddy.plugin.mybatis.helper.QueryHelper;
import com.teddy.plugin.mybatis.query.Query;
import com.teddy.plugin.mybatis.util.MSUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.*;

/**
 * @author teddy
 * @Package com.teddy.plugin.mybatis.interceptor
 * @Description: mybatis查询插件（包括分页、过滤、排序等功能）
 * @date 2018-5-4 17:48
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class,
                ResultHandler.class, CacheKey.class, BoundSql.class})})
@Component
@Slf4j
public class QueryInterceptor implements Interceptor {
    /**
     * 这是对应上面的args的序号
     */
    private static final int MAPPED_STATEMENT_INDEX = 0;
    private static final int PARAMETER_INDEX = 1;
    private static final int RESULT_HANDLER_INDEX = 3;
    private static final int BOUND_SQL_INDEX = 5;

    private static final String DEFAULT_DIALECT_CLASS = "com.teddy.plugin.mybatis.dialect.MysqlDialect";
    private static final int DEFAULT_SQL_SESSION_QUERY_PARAM_LEN = 4;
    private static final int CACHING_EXECUTOR_QUERY_PARAM_LEN = 6;

    @Value("${queryHelper.dialect}")
    private String dialectClass;
    private Dialect dialect;
    private Field additionalParametersField;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Query query = QueryHelper.getQuery();
        try {
            if (query == null) {
                return invocation.proceed();
            }
            final Object[] args = invocation.getArgs();
            ResultHandler resultHandler = (ResultHandler) args[RESULT_HANDLER_INDEX];
            Executor executor = (Executor) invocation.getTarget();
            MappedStatement statement = (MappedStatement) args[MAPPED_STATEMENT_INDEX];
            BoundSql boundSql;
            Object parameter = args[PARAMETER_INDEX];
            if (args.length == DEFAULT_SQL_SESSION_QUERY_PARAM_LEN) {
                boundSql = statement.getBoundSql(parameter);
            } else {
                boundSql = (BoundSql) args[BOUND_SQL_INDEX];
            }

            // 创建一个新的查询对象
            MappedStatement querySm = createNewMappedStatement(query, statement, invocation);
            Object queryParameter = args[PARAMETER_INDEX];
            BoundSql queryBoundSql = querySm.getBoundSql(queryParameter);
            if (args.length == CACHING_EXECUTOR_QUERY_PARAM_LEN) {
                args[BOUND_SQL_INDEX] = queryBoundSql;
            }
            args[MAPPED_STATEMENT_INDEX] = querySm;

            // 查询总数
            if (null != query.getPage()) {
                MappedStatement countMs = MSUtils.newCountMappedStatement(statement);
                Long count = executeAutoCount(executor, countMs, queryParameter, boundSql, resultHandler);
                query.getPage().setTotal(count);
                if (0 == count) {
                    return query;
                }
            }

            List resultList = (List) invocation.proceed();
            query.addAll(resultList);
            return query;
        } finally {
            // 清除当前线程的query对象
            QueryHelper.clearQuery();
        }
    }

    private Long executeAutoCount(Executor executor, MappedStatement countMs, Object parameter, BoundSql boundSql,
                                  ResultHandler resultHandler) throws IllegalAccessException, SQLException {
        Map<String, Object> additionalParameters = (Map) this.additionalParametersField.get(boundSql);
        CacheKey countKey = executor.createCacheKey(countMs, parameter, RowBounds.DEFAULT, boundSql);
        String countSql = this.dialect.getCountSql(QueryHelper.getQuery(), boundSql.getSql());

        // 设置parameterMapping
        List<ParameterMapping> parameterMappings = buildFilterParameterMappings(countMs, QueryHelper.getQuery());
        parameterMappings.addAll(0, boundSql.getParameterMappings());
        BoundSql countBoundSql = new BoundSql(countMs.getConfiguration(), countSql, parameterMappings, parameter);

        additionalParameters.forEach(countBoundSql::setAdditionalParameter);
        Object countResultList
                = executor.query(countMs, parameter, RowBounds.DEFAULT, resultHandler, countKey, countBoundSql);

        return (Long) ((List) countResultList).get(0);
    }

    private MappedStatement createNewMappedStatement(Query query, MappedStatement statement, Invocation invocation)
            throws IllegalAccessException {
        Map parameter = (Map) invocation.getArgs()[PARAMETER_INDEX];
        BoundSql boundSql = statement.getBoundSql(parameter);
        // 重新new一个查询语句对像
        BoundSql newBoundSql = buildNewBoundSql(query, statement, boundSql, invocation);

        // 把新的查询放到statement里
        MappedStatement newMs = copyFromMappedStatement(statement, new BoundSqlSqlSource(newBoundSql));

        Map<String, Object> additionalParameters = (Map) this.additionalParametersField.get(boundSql);
        additionalParameters.forEach(newBoundSql::setAdditionalParameter);
        return newMs;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @PostConstruct
    public void init() {
        if (StringUtils.isBlank(dialectClass)) {
            dialectClass = DEFAULT_DIALECT_CLASS;
        }
        try {
            Class<?> aClass = Class.forName(dialectClass);
            this.dialect = (Dialect) aClass.newInstance();
        } catch (Exception e) {
            throw new QueryException("不支持的方言类型：" + dialectClass, e);
        }

        try {
            this.additionalParametersField = BoundSql.class.getDeclaredField("additionalParameters");
            this.additionalParametersField.setAccessible(true);
        } catch (NoSuchFieldException var5) {
            throw new QueryException(var5);
        }
    }

    @Override
    public void setProperties(Properties properties) {

    }

    private BoundSql buildNewBoundSql(Query query, MappedStatement statement, BoundSql boundSql,
                                      Invocation invocation) {
        String sql = this.dialect.getQuerySql(query, boundSql.getSql());
        // 设置filter参数
        Map paramMap = buildFilterPara(query, invocation);
        invocation.getArgs()[PARAMETER_INDEX] = paramMap;
        // 设置parameterMapping
        List<ParameterMapping> parameterMappings = buildFilterParameterMappings(statement, query);
        parameterMappings.addAll(0, boundSql.getParameterMappings());
        return new BoundSql(statement.getConfiguration(), sql, parameterMappings, boundSql.getParameterObject());
    }

    private List<ParameterMapping> buildFilterParameterMappings(MappedStatement statement, Query query) {
        List<ParameterMapping> parameterMappings = new ArrayList<>();
        if(null == query.getFilter()){
            return parameterMappings;
        }
        Map paramMap = query.getFilter().getFilterMap();

        paramMap.forEach((k, v) -> {
            // 如果是集合类型，则循环加入
            if (v instanceof Collection) {
                Collection c = (Collection)v;
                for(int i = 0; i < c.size(); i++) {
                    parameterMappings.add(new ParameterMapping.Builder(statement.getConfiguration(), (String)k + i, Object.class).build());
                }
            } else {
                parameterMappings
                        .add(new ParameterMapping.Builder(statement.getConfiguration(), (String) k, Object.class).build());
            }
        });
        // MapperMethod.ParamMap继承的是HashMap，这要需要对ParameterMapping进行排序，保证和SQL中的参数顺序一致
        parameterMappings.sort(Comparator.comparing(ParameterMapping::getProperty));
        return parameterMappings;
    }

    private Map buildFilterPara(Query query, Invocation invocation) {
        Map parameter = (Map) invocation.getArgs()[PARAMETER_INDEX];
        if (null == parameter) {
            if (invocation.getArgs().length == DEFAULT_SQL_SESSION_QUERY_PARAM_LEN) {
                parameter = new DefaultSqlSession.StrictMap();
            } else {
                parameter = new MapperMethod.ParamMap();
            }
        }
        if(null == query.getFilter()){
            return parameter;
        }
        Map filterParamMap = query.getFilter().getFilterMap();
        parameter.putAll(filterParamMap);
        return parameter;
    }

    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder
                = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ArrayUtils.isNotEmpty(ms.getKeyProperties())) {
            builder.keyProperty(ms.getKeyProperties()[0]);
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

    public static class BoundSqlSqlSource implements SqlSource {
        private BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}
