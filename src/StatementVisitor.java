public interface StatementVisitor<T> {
    T visitExpressionStatement(Statement.ExpressionStmt statement);

    T visitPrintStatement(Statement.Print statement);

    T visitVarDeclStatement(Statement.VarDecl varDecl);

    T visitBlockStatement(Statement.Block block);

    T visitIfStatement(Statement.IfSequence ifSequence);

    T visitFuncStatement(Statement.FuncStatement funcStatement);
}
