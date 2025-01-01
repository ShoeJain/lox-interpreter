import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

public class LoxInterpreter implements ExpressionVisitor<Object>, StatementVisitor<Void> {

    final static LoxEnvironment globalScope = new LoxEnvironment(null);
    static LoxEnvironment currentScope = globalScope;       //Typically the innermost scope block - LoxEnvironment is capable of searching up enclosing scopes (see get())
    final String divideByZero = "Imagine that you have zero cookies and you split them evenly among zero friends. How many cookies does each person get? See? It doesn't make sense. And Cookie Monster is sad that there are no cookies, and you are sad that you have no friends.";

    public void interpret(List<Statement> program) {
        try {
            //Object result = evaluateExpression(expr);
            //System.out.println(objectToString(result));
            for (Statement stmt : program) {
                stmt.accept(this);
            }
        }
        catch (LoxError.RuntimeError e) {
            LoxError.printError(LoxError.Module.INTERPRETER, e.token.lineNumber, e.getMessage());
        }
    }

    private Object evaluateExpression(Expression expr) {
        return expr.accept(this);
    }

    private Object evaluateStatement(Statement stmt) {
        return stmt.accept(this);
    }

    private String objectToString(Object obj) {
        if (obj == null)
            return "nil";
        return obj.toString();
    }

    @Override
    public Object visitBinary(Expression.Binary binaryExp) {
        Object op1 = evaluateExpression(binaryExp.left);
        Object op2 = evaluateExpression(binaryExp.right);

        switch (binaryExp.operator.type) {
            case PLUS:
                if (op1 instanceof Double && op2 instanceof Double)
                    return (double) op1 + (double) op2;
                if (op1 instanceof String && op2 instanceof String)
                    return (String) op1 + (String) op2;
                throw new LoxError.RuntimeError(binaryExp.operator, "Operands must both be numbers or strings");
            case MINUS:
                checkBinaryNumberOperand(binaryExp.operator, op1, op2);
                return (double) op1 - (double) op2;
            case SLASH:
                checkBinaryNumberOperand(binaryExp.operator, op1, op2);
                if((double) op2 == 0) throw new LoxError.RuntimeError(binaryExp.operator, divideByZero);
                return (double) op1 / (double) op2;
            case STAR:
                checkBinaryNumberOperand(binaryExp.operator, op1, op2);
                return (double) op1 * (double) op2;
            case EQUAL_EQUAL:
                if (op1 == null && op2 == null)
                    return true; //If both are null - we need to convert Lox's nil to Java's null to successfully interpret
                if (op1 == null || op2 == null)
                    return false; //If only 1 of them is null
                if (op1 instanceof Double && op2 instanceof Double)
                    return (double) op1 == (double) op2;
                if (op1 instanceof String && op2 instanceof String)
                    return ((String) op1).compareTo((String) op2) == 0 ? true : false;
                throw new LoxError.RuntimeError(binaryExp.operator, "Operands must both be numbers or strings");
            case NOT_EQUAL:
                if (op1 instanceof Double && op2 instanceof Double)
                    return (double) op1 != (double) op2;
                if (op1 instanceof String && op2 instanceof String)
                    return ((String) op1).compareTo((String) op2) != 0 ? true : false;
                throw new LoxError.RuntimeError(binaryExp.operator, "Operands must both be numbers or strings");
            case GREATER_EQUAL:
                if (op1 instanceof Double && op2 instanceof Double)
                    return (double) op1 >= (double) op2;
                if (op1 instanceof String && op2 instanceof String)
                    return ((String) op1).compareTo((String) op2) >= 0 ? true : false;
                throw new LoxError.RuntimeError(binaryExp.operator, "Operands must both be numbers or strings");
            case LESSER_EQUAL:
                if (op1 instanceof Double && op2 instanceof Double)
                    return (double) op1 <= (double) op2;
                if (op1 instanceof String && op2 instanceof String)
                    return ((String) op1).compareTo((String) op2) <= 0 ? true : false;
                throw new LoxError.RuntimeError(binaryExp.operator, "Operands must both be numbers or strings");
            case GREATER:
                if (op1 instanceof Double && op2 instanceof Double)
                    return (double) op1 > (double) op2;
                if (op1 instanceof String && op2 instanceof String)
                    return ((String) op1).compareTo((String) op2) > 0 ? true : false;
                throw new LoxError.RuntimeError(binaryExp.operator, "Operands must both be numbers or strings");
            case LESSER:
                if (op1 instanceof Double && op2 instanceof Double)
                    return (double) op1 < (double) op2;
                if (op1 instanceof String && op2 instanceof String)
                    return ((String) op1).compareTo((String) op2) < 0 ? true : false;
                throw new LoxError.RuntimeError(binaryExp.operator, "Operands must both be numbers or strings");
            default:
                return null;
        }
    }

    @Override
    public Object visitUnary(Expression.Unary unaryExp) {
        Object expressionResult = evaluateExpression(unaryExp.expr);

        switch (unaryExp.operator.type) {
            case MINUS:
                checkUnaryNumberOperand(unaryExp.operator, expressionResult);
                return -1 * (double) expressionResult;
            case NOT:
                return !isTruthy(expressionResult);
            default:
                return null;
        }
    }

    @Override
    public Object visitAssignment(Expression.Assignment assignment) {
        Object value = evaluateExpression(assignment.value);
        //System.out.println("Assigning value of \'" + assignment.varName.lexeme + "\' to = " + objectToString(value));
        currentScope.assign(assignment.varName, value);

        return value;   //This may have issues in the future... Maybe this should return the Variable type instead?
    }

    @Override
    public Object visitVariable(Expression.Variable variable) {
        //System.out.println("Getting value of \'" + variable.varName.lexeme + "\' = " + objectToString(env.get(variable.varName)));
        return currentScope.get(variable.varName);
    }

    @Override
    public Object visitGrouping(Expression.Grouping groupingExp) {
        return evaluateExpression(groupingExp.expr);
    }

    @Override
    public Object visitLiteral(Expression.Literal literalExp) {
        return literalExp.value;
    }

    private boolean isTruthy(Object expression) {
        if (expression == null)
            return false;
        if (expression instanceof Boolean)
            return (boolean) expression;
        return true;
    }

    private void checkUnaryNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double)
            return;
        throw new LoxError.RuntimeError(operator, "Invalid operand type for operation");
    }

    private void checkBinaryNumberOperand(Token operator, Object operand1, Object operand2) {
        if (operand1 instanceof Double && operand2 instanceof Double)
            return;
        throw new LoxError.RuntimeError(operator, "Invalid operand types for operation");
    }

    @Override
    public Void visitExpressionStatement(Statement.ExpressionStmt statement) {
        evaluateExpression(statement.expr);
        return null;
    }

    @Override
    public Void visitPrintStatement(Statement.Print statement) {
        Object result = evaluateExpression(statement.expr);
        System.out.println(objectToString(result));
        return null;
    }

    @Override
    public Void visitVarDeclStatement(Statement.VarDecl varDecl) {
        if (varDecl.expr == null) { //if is just declaration and not initialization as well;
            currentScope.define(varDecl.varName, null);
        }
        else currentScope.define(varDecl.varName, evaluateExpression(varDecl.expr));

        return null;
    }

    @Override
    public Void visitBlockStatement(Statement.Block block) {
        LoxEnvironment enclosingScope = this.currentScope;
        this.currentScope = new LoxEnvironment(enclosingScope);

        for (Statement stmt : block.statements)
            evaluateStatement(stmt);

        this.currentScope = enclosingScope; //Should auto-free block scop
        return null;
    }
}
