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
}
