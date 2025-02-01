import java.util.ArrayList;
import java.util.List;

abstract public class Expression {
    abstract <T> T accept(ExpressionVisitor<T> visitor);

    static class Binary extends Expression {
        final Expression left;
        final Token operator;
        final Expression right;

        Binary(Expression left, Token operator, Expression right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <T> T accept(ExpressionVisitor<T> visitor) {
            return visitor.visitBinary(this);
        }
    }

    static class Unary extends Expression {
        final Token operator;
        final Expression expr;

        Unary(Token operator, Expression expr) {
            this.operator = operator;
            this.expr = expr;
        }

        @Override
        <T> T accept(ExpressionVisitor<T> visitor) {
            return visitor.visitUnary(this);
        }
    }

    static class Literal extends Expression {
        final Object value;

        Literal(Object value) {
            this.value = value;
        }

        @Override
        <T> T accept(ExpressionVisitor<T> visitor) {
            return visitor.visitLiteral(this);
        }
    }

    static class Variable extends Expression {
        final Token varName;

        Variable(Token varName) {
            this.varName = varName;
        }

        @Override
        <T> T accept(ExpressionVisitor<T> visitor) {
            return visitor.visitVariable(this);
        }
    }

    static class FunctionCall extends Expression {
        final Token funcName;
        final ArrayList<Expression> args;

        FunctionCall(Token funcName, ArrayList<Expression> args) {
            this.funcName = funcName;
            this.args = args;
        }

        @Override
        <T> T accept(ExpressionVisitor<T> visitor) {
            return visitor.visitFuncCall(this);
        }
    }

    static class Assignment extends Expression {
        final Token varName;
        final Expression value;

        Assignment(Token varName, Expression value) {
            this.varName = varName;
            this.value = value;
        }

        @Override
        <T> T accept(ExpressionVisitor<T> visitor) {
            return visitor.visitAssignment(this);
        }
    }

    static class Grouping extends Expression {
        final Expression expr;

        Grouping(Expression expr) {
            this.expr = expr;
        }

        @Override
        <T> T accept(ExpressionVisitor<T> visitor) {
            return visitor.visitGrouping(this);
        }
    }

    static class Comma extends Expression {
        final List<Expression> expressions;

        Comma(List<Expression> expressions) {
            this.expressions = expressions;
        }

        @Override
        <T> T accept(ExpressionVisitor<T> visitor) {
            return visitor.visitComma(this);
        }
    }
}
