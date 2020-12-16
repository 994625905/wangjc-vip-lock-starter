package vip.wangjc.lock.builder.service.impl;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;
import vip.wangjc.lock.builder.service.ILockKeyBuilderService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 锁的key生成器
 * @author wangjc
 * @title: DefaultLockKeyBuilderServiceImpl
 * @projectName wangjc-vip
 * @date 2020/12/12 - 14:48
 */
public class DefaultLockKeyBuilderServiceImpl implements ILockKeyBuilderService {

    /**
     * 记录参数名解析器
     */
    private static final ParameterNameDiscoverer NAME_DISCOVERER = new DefaultParameterNameDiscoverer();
    /**
     * 表达式解析器
     */
    private static final ExpressionParser PARSER = new SpelExpressionParser();

    @Override
    public String buildKey(MethodInvocation invocation, String[] definitionKeys) {
        StringBuffer sb = new StringBuffer("vip-lock:");

        /** 全类名+方法名 */
        Method method = invocation.getMethod();
        sb.append(method.getDeclaringClass().getName());
        sb.append(".");
        sb.append(method.getName());
        sb.append("#");

        /** 参数定义 */
        if( definitionKeys.length > 1 || !"".equals(definitionKeys[0]) ){
            sb.append(getElDefinitionKey(definitionKeys,method,invocation.getArguments()));
        }
        return sb.toString();
    }

    /**
     * 获取EL表达式值
     * @param definitionKeys
     * @param method
     * @param parameterValues
     * @return
     */
    private String getElDefinitionKey(String[] definitionKeys, Method method,Object[] parameterValues){
        try {
            EvaluationContext context = new MethodBasedEvaluationContext(null,method,parameterValues,NAME_DISCOVERER);

            /** 格式化获取值 */
            List<String> list = new ArrayList<>(definitionKeys.length);
            for(String key:definitionKeys){
                if(key != null && !key.isEmpty()){
                    list.add(PARSER.parseExpression(key).getValue(context).toString());
                }
            }
            return StringUtils.collectionToDelimitedString(list,".","","");
        }catch (Exception e){
            return definitionKeys.toString();
        }
    }


}
