package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;
import com.unistack.tamboo.compute.process.until.dataclean.filter.FilterType;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.reflect.internal.Trees;

import java.util.Stack;

/**
 * @author chinaLi
 *
 */
@FilterType("bool")
public class BoolFilter implements Filter {
	private static Logger LOGGER = LoggerFactory.getLogger(BoolFilter.class);
	private String conditions;


	//{"type":"Bool","fields":[{"conditions":"判断条件#string"}]}
	@Override
	public void init(JSONObject config) {
		conditions = config.getJSONArray("fields").getJSONObject(0).getString("conditions");
		LOGGER.info("conditions = " + conditions);
	}

	@Override
	public JSONObject filter(JSONObject source){
		JSONObject result = new JSONObject();
		ExpressionVisitorImpl visitor = new ExpressionVisitorImpl(JSON.parseObject(source.toJSONString()));
		Statement stmt = null;
		try{
			stmt = CCJSqlParserUtil.parse("select * from t where " + conditions);
		}catch(JSQLParserException e){
			e.printStackTrace();
			result.put("code","199");
			result.put("msg","json条件书写错误!");
			return result;
		}

		Select select = (Select) stmt;
		PlainSelect ps = (PlainSelect) select.getSelectBody();
		ps.getWhere().accept(visitor);
		if (visitor.getResult()) {
			result.put("code","200");
			result.put("msg","");
			result.put("data",source);
			return result;
		}

		result.put("code","198");
		result.put("msg","为获得结果");
		return result;
	}

}

class ExpressionVisitorImpl extends ExpressionVisitorAdapter {
	private JSONObject source;
	private Stack<Boolean> stack = new Stack<>();

	public boolean getResult() {
		return stack.pop();
	}

	ExpressionVisitorImpl(JSONObject source) {
		this.source = source;
	}

	private void processComparisonOperator(ComparisonOperator expression, String operator) {
		Expression right = expression.getRightExpression();
		Expression left = expression.getLeftExpression();
		String key = left.toString();
		expression.getLeftExpression().accept(this);
		if (right instanceof StringValue) {
			String value = ((StringValue) right).getValue();
			stack.push(compare(source.getString(key).compareTo(value), 0, operator));
		} else if (right instanceof LongValue) {
			long value = ((LongValue) right).getValue();
			stack.push(compare(source.getLong(key), value, operator));
		} else if (right instanceof DoubleValue) {
			double value = ((DoubleValue) right).getValue();
			stack.push(compare(source.getDouble(key), value, operator));
		}
	}

	boolean compare(double i1, double i2, String operator) {
		if (operator.equals(">")) {
			return i1 > i2;
		} else if (operator.equals(">=")) {
			return i1 >= i2;
		} else if (operator.equals("<")) {
			return i1 < i2;
		} else if (operator.equals("<=")) {
			return i1 <= i2;
		} else if (operator.equals("==")) {
			return i1 == i2;
		} else if (operator.equals("!=")) {
			return i1 != i2;
		} else {
			return false;
		}
	}

	@Override
	public void visit(NotEqualsTo notEqualsTo) {
		processComparisonOperator(notEqualsTo, "!=");
	}

	@Override
	public void visit(MinorThanEquals minorThanEquals) {
		processComparisonOperator(minorThanEquals, "<=");
	}

	@Override
	public void visit(MinorThan minorThan) {
		processComparisonOperator(minorThan, "<");
	}

	@Override
	public void visit(GreaterThanEquals greaterThanEquals) {
		processComparisonOperator(greaterThanEquals, ">=");
	}

	@Override
	public void visit(GreaterThan greaterThan) {
		processComparisonOperator(greaterThan, ">");
	}

	@Override
	public void visit(EqualsTo equalsTo) {
		processComparisonOperator(equalsTo, "==");
	}

	@Override
	public void visit(OrExpression orExpression) {
		orExpression.getLeftExpression().accept(this);
		orExpression.getRightExpression().accept(this);
		stack.push(stack.pop() || stack.pop());
	}

	@Override
	public void visit(AndExpression andExpression) {
		andExpression.getLeftExpression().accept(this);
		andExpression.getRightExpression().accept(this);
		stack.push(stack.pop() && stack.pop());
	}

	@Override
	public void visit(IsNullExpression expr) {
		Expression left = expr.getLeftExpression();
		String key = left.toString();
		// "包含该key" 和 "is null" 异或
		stack.push(source.containsKey(key) ^ !expr.isNot());
	}
}
