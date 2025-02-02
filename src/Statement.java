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
    
    static class IfSequence extends Statement {
        final Expression ifCondition;
        final Statement thenStatements;
        final Statement elseStatements;

        IfSequence(Expression ifCondition, Statement thenStatements, Statement elseStatements) {
            this.ifCondition = ifCondition;
            this.thenStatements = thenStatements;
            this.elseStatements = elseStatements;
        }

        @Override
        <T> T accept(StatementVisitor<T> visitor) {
            return visitor.visitIfStatement(this);
        }
    }
    
    static class FuncStatement extends Statement {
        final Token funcName;
        final List<Token> paramList;
        final Statement funcStmts;
        
        FuncStatement(Token funcName, List<Token> paramList, Statement funcStmts) {
            this.funcName = funcName;
            this.paramList = paramList;
            this.funcStmts = funcStmts;
        }

        @Override
        <T> T accept(StatementVisitor<T> visitor) {
            return visitor.visitFuncStatement(this);
        }
    }
}
