/* Generated By:JJTree: Do not edit this line. OParenthesisExpression.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orientechnologies.orient.core.sql.parser;

import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.exception.OCommandExecutionException;
import com.orientechnologies.orient.core.sql.executor.OInsertExecutionPlan;
import com.orientechnologies.orient.core.sql.executor.OInternalExecutionPlan;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultInternal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OParenthesisExpression extends OMathExpression {

  protected OExpression expression;
  protected OStatement  statement;

  public OParenthesisExpression(int id) {
    super(id);
  }

  public OParenthesisExpression(OrientSql p, int id) {
    super(p, id);
  }

  public OParenthesisExpression(OExpression exp) {
    super(-1);
    this.expression = exp;
  }

  /**
   * Accept the visitor.
   **/
  public Object jjtAccept(OrientSqlVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

  @Override
  public Object execute(OIdentifiable iCurrentRecord, OCommandContext ctx) {
    if (expression != null) {
      return expression.execute(iCurrentRecord, ctx);
    }
    if (statement != null) {
      throw new UnsupportedOperationException("Execution of select in parentheses is not supported");
    }
    return super.execute(iCurrentRecord, ctx);
  }

  @Override
  public Object execute(OResult iCurrentRecord, OCommandContext ctx) {
    if (expression != null) {
      return expression.execute(iCurrentRecord, ctx);
    }
    if (statement != null) {
      OInternalExecutionPlan execPlan;
      if (statement.originalStatement == null || statement.originalStatement.contains("?")) {
        //cannot cache statements with positional params, especially when it's in a subquery/expression.
        execPlan = statement.createExecutionPlanNoCache(ctx, false);
      } else {
        execPlan = statement.createExecutionPlan(ctx, false);
      }
      if (execPlan instanceof OInsertExecutionPlan) {
        ((OInsertExecutionPlan) execPlan).executeInternal();
      }
      OLocalResultSet rs = new OLocalResultSet(execPlan);
      List<OResult> result = new ArrayList<>();
      while (rs.hasNext()) {
        result.add(rs.next());
      }
//      List<OResult> result = rs.stream().collect(Collectors.toList());//TODO streamed...
      rs.close();
      return result;
    }
    return super.execute(iCurrentRecord, ctx);
  }

  public void toString(Map<Object, Object> params, StringBuilder builder) {
    builder.append("(");
    if (expression != null) {
      expression.toString(params, builder);
    } else if (statement != null) {
      statement.toString(params, builder);
    }
    builder.append(")");
  }

  @Override
  protected boolean supportsBasicCalculation() {
    if (expression != null) {
      return expression.supportsBasicCalculation();
    }
    return true;
  }

  @Override
  public boolean isEarlyCalculated(OCommandContext ctx) {
    // TODO implement query execution and early calculation;
    return expression != null && expression.isEarlyCalculated(ctx);
  }

  public boolean needsAliases(Set<String> aliases) {
    if (expression.needsAliases(aliases)) {
      return true;
    }
    return false;
  }

  public boolean isExpand() {
    if (expression != null) {
      return expression.isExpand();
    }
    return false;
  }

  public boolean isAggregate() {
    if (expression != null) {
      return expression.isAggregate();
    }
    return false;
  }

  public boolean isCount() {
    if (expression != null) {
      return expression.isCount();
    }
    return false;
  }

  public SimpleNode splitForAggregation(AggregateProjectionSplit aggregateProj, OCommandContext ctx) {
    if (isAggregate()) {
      OParenthesisExpression result = new OParenthesisExpression(-1);
      result.expression = expression.splitForAggregation(aggregateProj, ctx);
      return result;
    } else {
      return this;
    }
  }

  @Override
  public OParenthesisExpression copy() {
    OParenthesisExpression result = new OParenthesisExpression(-1);
    result.expression = expression == null ? null : expression.copy();
    result.statement = statement == null ? null : statement.copy();
    return result;
  }

  public void setStatement(OStatement statement) {
    this.statement = statement;
  }

  public void extractSubQueries(SubQueryCollector collector) {
    if (expression != null) {
      expression.extractSubQueries(collector);
    } else if (statement != null) {
      OIdentifier alias = collector.addStatement(statement);
      statement = null;
      expression = new OExpression(alias);
    }
  }

  public void extractSubQueries(OIdentifier letAlias, SubQueryCollector collector) {
    if (expression != null) {
      expression.extractSubQueries(collector);
    } else if (statement != null) {
      OIdentifier alias = collector.addStatement(letAlias, statement);
      statement = null;
      expression = new OExpression(alias);
    }
  }

  public boolean refersToParent() {
    if (expression != null && expression.refersToParent()) {
      return true;
    }
    if (statement != null && statement.refersToParent()) {
      return true;
    }
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;

    OParenthesisExpression that = (OParenthesisExpression) o;

    if (expression != null ? !expression.equals(that.expression) : that.expression != null)
      return false;
    if (statement != null ? !statement.equals(that.statement) : that.statement != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (expression != null ? expression.hashCode() : 0);
    result = 31 * result + (statement != null ? statement.hashCode() : 0);
    return result;
  }

  public List<String> getMatchPatternInvolvedAliases() {
    return expression.getMatchPatternInvolvedAliases();//TODO also check the statement...?
  }

  @Override
  public void applyRemove(OResultInternal result, OCommandContext ctx) {
    if (expression != null) {
      expression.applyRemove(result, ctx);
    } else {
      throw new OCommandExecutionException("Cannot apply REMOVE " + toString());
    }
  }

  public OResult serialize() {
    OResultInternal result = (OResultInternal) super.serialize();
    if (expression != null) {
      result.setProperty("expression", expression.serialize());
    }
    if (statement != null) {
      result.setProperty("statement", statement.serialize());
    }
    return result;
  }

  public void deserialize(OResult fromResult) {
    super.deserialize(fromResult);
    if (fromResult.getProperty("expression") != null) {
      expression = new OExpression(-1);
      expression.deserialize(fromResult.getProperty("expression"));
    }
    if (fromResult.getProperty("statement") != null) {
      statement = OStatement.deserializeFromOResult(fromResult.getProperty("statement"));
    }
  }

  @Override
  public boolean isCacheable() {
    if (expression != null) {
      return expression.isCacheable();
    }
    if (statement != null) {
      return statement.executinPlanCanBeCached();
    }
    return true;
  }
}
/* JavaCC - OriginalChecksum=4656e5faf4f54dc3fc45a06d8e375c35 (do not edit this line) */
