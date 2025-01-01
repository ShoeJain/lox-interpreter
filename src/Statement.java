import java.util.List;
import java.util.ArrayList;

abstract class Statement {
    abstract <T> T accept(StatementVisitor<T> visitor);

    static class ExpressionStmt extends Statement{
        final Expression expr;
        
        ExpressionStmt(Expression expr) {
            this.expr = expr;
        }

        @Override
        <T> T accept(StatementVisitor<T> visitor) {
            return visitor.visitExpressionStatement(this);
        }
    }

    static class Print extends Statement {
        final Expression expr;

        Print(Expression expr) {
            this.expr = expr;
        }

        @Override
        <T> T accept(StatementVisitor<T> visitor) {
            return visitor.visitPrintStatement(this);
        }
    }
    
    static class VarDecl extends Statement {
        final Token varName;
        final Expression expr;

        VarDecl(Token varName, Expression expr) {
            this.varName = varName;
            this.expr = expr;
        }

        @Override
        <T> T accept(StatementVisitor<T> visitor) {
            return visitor.visitVarDeclStatement(this);
        }
    }
    
    static class Block extends Statement {
        final List<Statement> statements;
        
        Block(List<Statement> stmts) {
            this.statements = stmts;
        }

        @Override
        <T> T accept(StatementVisitor<T> visitor) {
            return visitor.visitBlockStatement(this);
        }
    }
}
