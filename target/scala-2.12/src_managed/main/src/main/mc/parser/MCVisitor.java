// Generated from src/main/mc/parser/MC.g4 by ANTLR 4.6

    package mc.parser;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MCParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MCVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MCParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(MCParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#declerations}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclerations(MCParser.DeclerationsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#declerationsVariable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclerationsVariable(MCParser.DeclerationsVariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#primitivetype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimitivetype(MCParser.PrimitivetypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#manyvariable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitManyvariable(MCParser.ManyvariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#variable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(MCParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray(MCParser.ArrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#declerationsFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclerationsFunction(MCParser.DeclerationsFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#typeFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeFunction(MCParser.TypeFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#parameterlist}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterlist(MCParser.ParameterlistContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(MCParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#blockstatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockstatement(MCParser.BlockstatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#declarationPart}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclarationPart(MCParser.DeclarationPartContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#statementPart}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatementPart(MCParser.StatementPartContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(MCParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(MCParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#dowhileStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDowhileStatement(MCParser.DowhileStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#forStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStatement(MCParser.ForStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#breakStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStatement(MCParser.BreakStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#continueStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinueStatement(MCParser.ContinueStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#returnStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStatement(MCParser.ReturnStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#expressionStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionStatement(MCParser.ExpressionStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(MCParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#expression1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression1(MCParser.Expression1Context ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#expression2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression2(MCParser.Expression2Context ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#expression3}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression3(MCParser.Expression3Context ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#expression4}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression4(MCParser.Expression4Context ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#expression5}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression5(MCParser.Expression5Context ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#expression6}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression6(MCParser.Expression6Context ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#expression7}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression7(MCParser.Expression7Context ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#expression8}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression8(MCParser.Expression8Context ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#expression9}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression9(MCParser.Expression9Context ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#funcall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncall(MCParser.FuncallContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCParser#expressionList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionList(MCParser.ExpressionListContext ctx);
}