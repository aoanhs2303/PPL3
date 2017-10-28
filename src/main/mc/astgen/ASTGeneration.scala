package mc.astgen
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import java.io.{PrintWriter,File}
import org.antlr.v4.runtime.ANTLRFileStream
import mc.utils._
import scala.collection.JavaConverters._
import org.antlr.v4.runtime.tree._
import mc.parser._
import mc.parser.MCParser._

class ASTGeneration extends MCBaseVisitor[Any] {

  override def visitProgram(ctx: ProgramContext) = {
    var decl = List[Decl]()
    for(i <- 0 until ctx.declerations.size)
      decl = decl ::: ctx.declerations(i).accept(this).asInstanceOf[List[Decl]]
    Program(decl)
  }

  override def visitDeclerations(ctx: DeclerationsContext) = {
    ctx.getChild(0).accept(this)
  }

  override def visitDeclerationsVariable(ctx: DeclerationsVariableContext) = {
    ctx.manyvariable.accept(this)
  }

  override def visitPrimitivetype(ctx: PrimitivetypeContext) = {
    if(ctx.INTTYPE != null) {IntType} 
    else if (ctx.FLOATTYPE != null) {FloatType}
    else if (ctx.STRINGTYPE != null) {StringType}
    else {BoolType} 
  }

  override def visitManyvariable(ctx: ManyvariableContext) = {
    ctx.variable.asScala.toList.map(_.accept(this))
  }

  override def visitVariable(ctx: VariableContext) = {
    if(ctx.ID != null){
      val vartype = ctx.getParent.getParent.asInstanceOf[DeclerationsVariableContext].primitivetype.accept(this).asInstanceOf[Type]
      val varname = Id(ctx.ID.getText)
      VarDecl(varname,vartype)
    }
    else ctx.array().accept(this)
  }

  override def visitArray(ctx: ArrayContext) = {
    val vartype = ctx.getParent.getParent.getParent.asInstanceOf[DeclerationsVariableContext].primitivetype.accept(this).asInstanceOf[Type]
    val varname = Id(ctx.ID.getText)
    val size = IntLiteral(ctx.INTLIT.getText.toInt)
    val arraytype = ArrayType(size,vartype)
    VarDecl(varname,arraytype)
  }
	
  override def visitDeclerationsFunction(ctx: DeclerationsFunctionContext) = {
    val name = Id(ctx.ID.getText)
    val param = ctx.parameterlist.accept(this).asInstanceOf[List[VarDecl]]
    val returnType = ctx.typeFunction.accept(this).asInstanceOf[Type]
    val body = ctx.blockstatement.accept(this).asInstanceOf[Stmt]
    List(FuncDecl(name,param,returnType,body))
  }

  override def visitTypeFunction(ctx: TypeFunctionContext) = {  
    if (ctx.getChildCount() == 3) 
      ArrayPointerType(ctx.primitivetype.accept(this).asInstanceOf[Type])
    else if(ctx.primitivetype != null) {ctx.primitivetype.accept(this)}
    else VoidType
    
  }  

  override def visitParameterlist(ctx: ParameterlistContext) = {
    ctx.parameter.asScala.toList.map(_.accept(this))
  }

  override def visitParameter(ctx: ParameterContext) = {
    if(ctx.getChildCount == 2) {
      val variable = Id(ctx.ID.getText)
      val typeP = ctx.primitivetype.accept(this).asInstanceOf[Type]
      VarDecl(variable, typeP)
    } 
    else {
      val variable = Id(ctx.ID.getText)
      val typeP = ArrayPointerType(ctx.primitivetype.accept(this).asInstanceOf[Type])
      VarDecl(variable, typeP)
    }
  }

  override def visitBlockstatement(ctx: BlockstatementContext) = {
    val decl = ctx.declarationPart.accept(this).asInstanceOf[List[Decl]]
    val stmt = ctx.statementPart.accept(this).asInstanceOf[List[Stmt]]
    Block(decl, stmt)
  }

  override def visitDeclarationPart(ctx: DeclarationPartContext) = {
    var decl = List[Decl]()
    for(i <- 0 until ctx.declerationsVariable.size)
      decl = decl ::: ctx.declerationsVariable(i).accept(this).asInstanceOf[List[Decl]]  
    decl
  }

  override def visitStatementPart(ctx: StatementPartContext) = {
    ctx.statement.asScala.toList.map(_.accept(this))
  }

  override def visitStatement(ctx: StatementContext) = {  
    ctx.getChild(0).accept(this)
  }

  override def visitIfStatement(ctx: IfStatementContext) = {
    if(ctx.getChildCount() == 5) {
      val expr = ctx.expression.accept(this).asInstanceOf[Expr]
      val thenStmt = ctx.statement(0).accept(this).asInstanceOf[Stmt]
      val elseStmt = None
      If(expr, thenStmt, elseStmt)
    } 
    else {
      val expr = ctx.expression.accept(this).asInstanceOf[Expr]
      val thenStmt = ctx.statement(0).accept(this).asInstanceOf[Stmt]
      val elseStmt = Some(ctx.statement(1).accept(this).asInstanceOf[Stmt])
      If(expr, thenStmt, elseStmt)
    }
  }

  override def visitDowhileStatement(ctx: DowhileStatementContext) = {
    val sl = ctx.statement.asScala.toList.map(_.accept(this)).asInstanceOf[List[Stmt]] 
    val exp = ctx.expression.accept(this).asInstanceOf[Expr]
    Dowhile(sl, exp)
  }

  override def visitForStatement(ctx: ForStatementContext)  = {
    val expr1 = ctx.expression(0).accept(this).asInstanceOf[Expr]
    val expr2 = ctx.expression(1).accept(this).asInstanceOf[Expr]
    val expr3 = ctx.expression(2).accept(this).asInstanceOf[Expr]
    val loop = ctx.statement.accept(this).asInstanceOf[Stmt]
    For(expr1, expr2, expr3, loop)
  }

  override def visitBreakStatement(ctx: BreakStatementContext) = {
    Break
  }

  override def visitContinueStatement(ctx: ContinueStatementContext) = {
    Continue
  }

  override def visitReturnStatement(ctx: ReturnStatementContext) = {
    if(ctx.getChildCount() == 3) {
      val expr = Some(ctx.expression.accept(this).asInstanceOf[Expr])
      Return(expr)
    } 
    else {
      val expr = None
      Return(expr)
    }
    
  }

  override def visitExpressionStatement(ctx: ExpressionStatementContext) = {
    ctx.expression.accept(this)
  }

  override def visitExpression(ctx: ExpressionContext) = {
    if(ctx.getChildCount() == 1) {
      ctx.expression1.accept(this)
    }
    else {
      val op = ctx.ASSIGN.getText
      val left = ctx.expression1.accept(this).asInstanceOf[Expr]
      var right = ctx.expression.accept(this).asInstanceOf[Expr]
      BinaryOp(op, left, right)
    }
  }

  override def visitExpression1(ctx: Expression1Context) = {
    if(ctx.getChildCount() == 1) {
      ctx.expression2.accept(this)
    }
    else {
      val op = ctx.OR.getText
      val left = ctx.expression1.accept(this).asInstanceOf[Expr]
      var right = ctx.expression2.accept(this).asInstanceOf[Expr]
      BinaryOp(op, left, right)
    }
  }

  override def visitExpression2(ctx: Expression2Context) = {
    if(ctx.getChildCount() == 1) {
      ctx.expression3.accept(this)
    }
    else {
      val op = ctx.AND.getText
      val left = ctx.expression2.accept(this).asInstanceOf[Expr]
      var right = ctx.expression3.accept(this).asInstanceOf[Expr]
      BinaryOp(op, left, right)
    }
  }

  override def visitExpression3(ctx: Expression3Context) = {
    if(ctx.getChildCount() == 1) {
      ctx.expression4(0).accept(this)
    }
    else {

      val op = ctx.getChild(1).getText
      val left = ctx.expression4(0).accept(this).asInstanceOf[Expr]
      var right = ctx.expression4(1).accept(this).asInstanceOf[Expr]
      BinaryOp(op, left, right)
    }
  }

  override def visitExpression4(ctx: Expression4Context) = {
    if(ctx.getChildCount() == 1) {
      ctx.expression5(0).accept(this)
    }
    else {
      val op = ctx.getChild(1).getText
      val left = ctx.expression5(0).accept(this).asInstanceOf[Expr]
      var right = ctx.expression5(1).accept(this).asInstanceOf[Expr]
      BinaryOp(op, left, right)
    }
  }

  override def visitExpression5(ctx: Expression5Context) = {
    if(ctx.getChildCount() == 1) {
      ctx.expression6.accept(this)
    }
    else {
      val op = ctx.getChild(1).getText
      val left = ctx.expression5.accept(this).asInstanceOf[Expr]
      var right = ctx.expression6.accept(this).asInstanceOf[Expr]
      BinaryOp(op, left, right)
    }
  }

  override def visitExpression6(ctx: Expression6Context) = {
    if(ctx.getChildCount() == 1) {
      ctx.expression7.accept(this)
    }
    else {
      val op = ctx.getChild(1).getText
      val left = ctx.expression6.accept(this).asInstanceOf[Expr]
      var right = ctx.expression7.accept(this).asInstanceOf[Expr]
      BinaryOp(op, left, right)
    }
  }

  override def visitExpression7(ctx: Expression7Context) = {
    if(ctx.getChildCount() == 1) {
      ctx.expression8.accept(this)
    }
    else {
      val op = ctx.getChild(0).getText
      val body = ctx.expression7.accept(this).asInstanceOf[Expr]
      UnaryOp(op, body)
    }
  }

  override def visitExpression8(ctx: Expression8Context) = {
    if(ctx.getChildCount() == 1) {
      ctx.expression9.accept(this)
    }
    else {
      val arr = ctx.expression9.accept(this).asInstanceOf[Expr]
      val idx = ctx.expression.accept(this).asInstanceOf[Expr] 
      ArrayCell(arr, idx) 
    }
  }

  override def visitExpression9(ctx: Expression9Context) = {
    if(ctx.getChildCount() == 3) {
      ctx.expression.accept(this)
    } 
    else if (ctx.ID != null){
      Id(ctx.ID.getText)
    }   
    else if (ctx.BOOLEANLIT != null){
      BooleanLiteral(ctx.BOOLEANLIT.getText.toBoolean)
    } 
    else if (ctx.STRING != null){
      StringLiteral(ctx.STRING.getText)
    } 
    else if (ctx.INTLIT != null){
      IntLiteral(ctx.INTLIT.getText.toInt)
    } 
    else if (ctx.FLOAT != null){
      FloatLiteral(ctx.FLOAT.getText.toFloat)
    } 
    else {
      ctx.funcall.accept(this)
    } 
  }

  override def visitFuncall(ctx: FuncallContext) = {
    val method = Id(ctx.ID.getText)
    val params = ctx.expressionList.accept(this).asInstanceOf[List[Expr]]
    CallExpr(method, params)      
  }

  override def visitExpressionList(ctx:ExpressionListContext) = {
    ctx.expression.asScala.toList.map(_.accept(this))
  }

}



















/*
package mc.utils

/**
 * @author nhphung
 */


trait AST {
        def accept(v: Visitor, param: Any) =  v.visit(this,param)
}

case class Program(val decl: List[Decl]) extends AST {
        override def toString = "Program(" + decl.mkString("List(",",",")") + ")"
        override def accept(v: Visitor, param: Any) = v.visitProgram(this,param)
}


trait Decl extends AST 
case class VarDecl(val variable: Id, val varType: Type) extends Decl {
        override def toString = "VarDecl(" + variable + "," + varType + ")"
        override def accept(v: Visitor, param: Any) = v.visitVarDecl(this,param)
}
case class FuncDecl(val name: Id, val param: List[VarDecl], val returnType: Type,val body: Stmt) extends Decl {
        override def toString = "FuncDecl(" + name + "," + param.mkString("List(",",",")")  + "," +  returnType +"," + body + ")"
        override def accept(v: Visitor, param: Any) = v.visitFuncDecl(this,param)
}

/*        TYPE        */
trait Type extends AST
object IntType extends Type {
        override def toString = "IntType"
        override def accept(v: Visitor, param: Any) = v.visitIntType(this,param)
}
object FloatType extends Type {
        override def toString = "FloatType"
        override def accept(v: Visitor, param: Any) = v.visitFloatType(this,param)
}
object BoolType extends Type {
        override def toString = "BoolType"
        override def accept(v: Visitor, param: Any) = v.visitBoolType(this,param)
}
object StringType extends Type {
        override def toString = "StringType"
        override def accept(v: Visitor, param: Any) = v.visitStringType(this,param)
}
object VoidType extends Type {
        override def toString = "VoidType"
        override def accept(v: Visitor, param: Any) = v.visitVoidType(this,param)
}
case class ArrayType(val dimen: IntLiteral, val eleType: Type) extends Type {
        override def toString = "ArrayType(" + dimen + "," + eleType + ")"
        override def accept(v: Visitor, param: Any) = v.visitArrayType(this,param)
}
case class ArrayPointerType(val eleType:Type) extends Type {
        override def toString = "ArrayPointerType(" + eleType + ")"
        override def accept(v: Visitor, param: Any) = v.visitArrayPointerType(this,param)
}
/*        expr        */
trait Expr extends Stmt 
case class BinaryOp(op: String, left: Expr, right: Expr) extends Expr {
        override def toString = "BinaryOp(" + op + "," + left + "," + right + ")"
        override def accept(v: Visitor, param: Any) = v.visitBinaryOp(this,param)
}
case class UnaryOp(op: String, body: Expr) extends Expr {
        override def toString = "UnaryOp(" + op + "," + body + ")"
        override def accept(v: Visitor, param: Any) = v.visitUnaryOp(this,param)
}

case class CallExpr(val method: Id, val params: List[Expr]) extends Expr {
        override def toString = "CallExpr("  + method + "," + params.mkString("List(",",",")")+ ")"
        override def accept(v: Visitor, param: Any) = v.visitCallExpr(this,param)
}


//LHS ------------------------
trait LHS extends Expr
case class Id(val name: String) extends LHS {
        override def toString = "Id(" + name + ")"
        override def accept(v: Visitor, param: Any) = v.visitId(this,param)
}
// element access
case class ArrayCell(arr: Expr, idx: Expr) extends LHS {
        override def toString = "ArrayCell(" + arr + "," + idx + ")"
        override def accept(v: Visitor, param: Any) = v.visitArrayCell(this,param)
}

/*        STMT        */
trait Stmt extends AST
case class Block(val decl: List[Decl], val stmt: List[Stmt]) extends Stmt {
        override def toString = "Block(" +  decl.mkString("List(",",",")") + "," + stmt.mkString("List(",",",")") + ")"
        override def accept(v: Visitor, param: Any) = v.visitBlock(this,param)
}

case class If(val expr: Expr, val thenStmt: Stmt, val elseStmt: Option[Stmt]) extends Stmt {
        override def toString = "If(" + expr + "," + thenStmt + (if (elseStmt == None) ",None" else  ",Some(" + elseStmt.get +")") + ")"
        override def accept(v: Visitor, param: Any) = v.visitIf(this,param)
}

case class For(val expr1:Expr,  val expr2: Expr, val expr3: Expr, val loop: Stmt) extends Stmt {
        override def toString = "For("  + expr1 + "," + expr2 + "," + expr3 + "," + loop + ")"
        override def accept(v: Visitor, param: Any) = v.visitFor(this,param)
}

object Break extends Stmt {
        override def toString = "Break"
        override def accept(v: Visitor, param: Any) = v.visitBreak(this,param)
}
object Continue extends Stmt {
        override def toString = "Continue"
        override def accept(v: Visitor, param: Any) = v.visitContinue(this,param)
}
case class Return(val expr: Option[Expr]) extends Stmt {
        override def toString = "Return(" + (if (expr == None) "None" else "Some("+expr.get+")") + ")"
        override def accept(v: Visitor, param: Any) = v.visitReturn(this,param)
}
case class Dowhile(val sl:List[Stmt],val exp:Expr) extends Stmt {
        override def toString = "Dowhile("+sl.mkString("List(",",",")")+","+ exp+")"
        override def accept(v: Visitor, param: Any) = v.visitDowhile(this,param)
}


/*        LITERAL        */
trait Literal extends Expr
case class IntLiteral(val value: Int) extends Literal {
        override def toString = "IntLiteral(" + value + ")"
        override def accept(v: Visitor, param: Any) = v.visitIntLiteral(this,param)
}
case class FloatLiteral(val value: Float) extends Literal {
        override def toString = "FloatLiteral(" + value + ")"
        override def accept(v: Visitor, param: Any) = v.visitFloatLiteral(this,param)
}
case class StringLiteral(val value: String) extends Literal {
        override def toString = "StringLiteral(" + value + ")"
        override def accept(v: Visitor, param: Any) = v.visitStringLiteral(this,param)

}
case class BooleanLiteral(val value: Boolean) extends Literal {
        override def toString = "BooleanLiteral(" + value + ")"
        override def accept(v: Visitor, param: Any) = v.visitBooleanLiteral(this,param)
}



*/






























/*// Generated from src/main/mc/parser/MC.g4 by ANTLR 4.6

  package mc.parser;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MCParser extends Parser {
  static { RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION); }

  protected static final DFA[] _decisionToDFA;
  protected static final PredictionContextCache _sharedContextCache =
    new PredictionContextCache();
  public static final int
    BOOLEANLIT=1, INTLIT=2, FLOAT=3, STRING=4, INTTYPE=5, VOIDTYPE=6, BOOLEANTYPE=7, 
    FLOATTYPE=8, STRINGTYPE=9, BREAK=10, CONTINUE=11, ELSE=12, IF=13, RETURN=14, 
    WHILE=15, DO=16, FOR=17, TRUE=18, FALSE=19, SUB=20, ADD=21, MUL=22, NOT=23, 
    OR=24, NOTEQUAL=25, LESS=26, LESSEQUAL=27, ASSIGN=28, DIV=29, MOD=30, 
    AND=31, EQUAL=32, GREATER=33, GREATEREQUAL=34, LSB=35, RSB=36, LP=37, 
    RP=38, LB=39, RB=40, SM=41, CM=42, ID=43, WS=44, COMMENT_LINE=45, COMMENT_BLOCK=46, 
    ILLEGAL_ESCAPE=47, UNCLOSE_STRING=48, ERROR_CHAR=49;
  public static final int
    RULE_program = 0, RULE_declerations = 1, RULE_declerationsVariable = 2, 
    RULE_primitivetype = 3, RULE_manyvariable = 4, RULE_variable = 5, RULE_array = 6, 
    RULE_declerationsFunction = 7, RULE_typeFunction = 8, RULE_parameterlist = 9, 
    RULE_parameter = 10, RULE_blockstatement = 11, RULE_declarationPart = 12, 
    RULE_statementPart = 13, RULE_statement = 14, RULE_ifStatement = 15, RULE_dowhileStatement = 16, 
    RULE_forStatement = 17, RULE_breakStatement = 18, RULE_continueStatement = 19, 
    RULE_returnStatement = 20, RULE_expressionStatement = 21, RULE_expression = 22, 
    RULE_expression1 = 23, RULE_expression2 = 24, RULE_expression3 = 25, RULE_expression4 = 26, 
    RULE_expression5 = 27, RULE_expression6 = 28, RULE_expression7 = 29, RULE_expression8 = 30, 
    RULE_expression9 = 31, RULE_funcall = 32, RULE_expressionList = 33;
  public static final String[] ruleNames = {
    "program", "declerations", "declerationsVariable", "primitivetype", "manyvariable", 
    "variable", "array", "declerationsFunction", "typeFunction", "parameterlist", 
    "parameter", "blockstatement", "declarationPart", "statementPart", "statement", 
    "ifStatement", "dowhileStatement", "forStatement", "breakStatement", "continueStatement", 
    "returnStatement", "expressionStatement", "expression", "expression1", 
    "expression2", "expression3", "expression4", "expression5", "expression6", 
    "expression7", "expression8", "expression9", "funcall", "expressionList"
  };

  private static final String[] _LITERAL_NAMES = {
    null, null, null, null, null, "'int'", "'void'", "'boolean'", "'float'", 
    "'string'", "'break'", "'continue'", "'else'", "'if'", "'return'", "'while'", 
    "'do'", "'for'", "'true'", "'false'", "'-'", "'+'", "'*'", "'!'", "'||'", 
    "'!='", "'<'", "'<='", "'='", "'/'", "'%'", "'&&'", "'=='", "'>'", "'>='", 
    "'['", "']'", "'{'", "'}'", "'('", "')'", "';'", "','"
  };
  private static final String[] _SYMBOLIC_NAMES = {
    null, "BOOLEANLIT", "INTLIT", "FLOAT", "STRING", "INTTYPE", "VOIDTYPE", 
    "BOOLEANTYPE", "FLOATTYPE", "STRINGTYPE", "BREAK", "CONTINUE", "ELSE", 
    "IF", "RETURN", "WHILE", "DO", "FOR", "TRUE", "FALSE", "SUB", "ADD", "MUL", 
    "NOT", "OR", "NOTEQUAL", "LESS", "LESSEQUAL", "ASSIGN", "DIV", "MOD", 
    "AND", "EQUAL", "GREATER", "GREATEREQUAL", "LSB", "RSB", "LP", "RP", "LB", 
    "RB", "SM", "CM", "ID", "WS", "COMMENT_LINE", "COMMENT_BLOCK", "ILLEGAL_ESCAPE", 
    "UNCLOSE_STRING", "ERROR_CHAR"
  };
  public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

  /**
   * @deprecated Use {@link #VOCABULARY} instead.
   */
  @Deprecated
  public static final String[] tokenNames;
  static {
    tokenNames = new String[_SYMBOLIC_NAMES.length];
    for (int i = 0; i < tokenNames.length; i++) {
      tokenNames[i] = VOCABULARY.getLiteralName(i);
      if (tokenNames[i] == null) {
        tokenNames[i] = VOCABULARY.getSymbolicName(i);
      }

      if (tokenNames[i] == null) {
        tokenNames[i] = "<INVALID>";
      }
    }
  }

  @Override
  @Deprecated
  public String[] getTokenNames() {
    return tokenNames;
  }

  @Override

  public Vocabulary getVocabulary() {
    return VOCABULARY;
  }

  @Override
  public String getGrammarFileName() { return "MC.g4"; }

  @Override
  public String[] getRuleNames() { return ruleNames; }

  @Override
  public String getSerializedATN() { return _serializedATN; }

  @Override
  public ATN getATN() { return _ATN; }

  public MCParser(TokenStream input) {
    super(input);
    _interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
  }
  public static class ProgramContext extends ParserRuleContext {
    public DeclerationsContext declerations() {
      return getRuleContext(DeclerationsContext.class,0);
    }
    public ProgramContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_program; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitProgram(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ProgramContext program() throws RecognitionException {
    ProgramContext _localctx = new ProgramContext(_ctx, getState());
    enterRule(_localctx, 0, RULE_program);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(68);
      declerations();
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class DeclerationsContext extends ParserRuleContext {
    public List<DeclerationsVariableContext> declerationsVariable() {
      return getRuleContexts(DeclerationsVariableContext.class);
    }
    public DeclerationsVariableContext declerationsVariable(int i) {
      return getRuleContext(DeclerationsVariableContext.class,i);
    }
    public List<DeclerationsFunctionContext> declerationsFunction() {
      return getRuleContexts(DeclerationsFunctionContext.class);
    }
    public DeclerationsFunctionContext declerationsFunction(int i) {
      return getRuleContext(DeclerationsFunctionContext.class,i);
    }
    public DeclerationsContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_declerations; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitDeclerations(this);
      else return visitor.visitChildren(this);
    }
  }

  public final DeclerationsContext declerations() throws RecognitionException {
    DeclerationsContext _localctx = new DeclerationsContext(_ctx, getState());
    enterRule(_localctx, 2, RULE_declerations);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(74);
      _errHandler.sync(this);
      _la = _input.LA(1);
      while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INTTYPE) | (1L << VOIDTYPE) | (1L << BOOLEANTYPE) | (1L << FLOATTYPE) | (1L << STRINGTYPE))) != 0)) {
        {
        setState(72);
        _errHandler.sync(this);
        switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
        case 1:
          {
          setState(70);
          declerationsVariable();
          }
          break;
        case 2:
          {
          setState(71);
          declerationsFunction();
          }
          break;
        }
        }
        setState(76);
        _errHandler.sync(this);
        _la = _input.LA(1);
      }
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class DeclerationsVariableContext extends ParserRuleContext {
    public PrimitivetypeContext primitivetype() {
      return getRuleContext(PrimitivetypeContext.class,0);
    }
    public ManyvariableContext manyvariable() {
      return getRuleContext(ManyvariableContext.class,0);
    }
    public TerminalNode SM() { return getToken(MCParser.SM, 0); }
    public DeclerationsVariableContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_declerationsVariable; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitDeclerationsVariable(this);
      else return visitor.visitChildren(this);
    }
  }

  public final DeclerationsVariableContext declerationsVariable() throws RecognitionException {
    DeclerationsVariableContext _localctx = new DeclerationsVariableContext(_ctx, getState());
    enterRule(_localctx, 4, RULE_declerationsVariable);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(77);
      primitivetype();
      setState(78);
      manyvariable(0);
      setState(79);
      match(SM);
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class PrimitivetypeContext extends ParserRuleContext {
    public TerminalNode INTTYPE() { return getToken(MCParser.INTTYPE, 0); }
    public TerminalNode FLOATTYPE() { return getToken(MCParser.FLOATTYPE, 0); }
    public TerminalNode STRINGTYPE() { return getToken(MCParser.STRINGTYPE, 0); }
    public TerminalNode BOOLEANTYPE() { return getToken(MCParser.BOOLEANTYPE, 0); }
    public PrimitivetypeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_primitivetype; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitPrimitivetype(this);
      else return visitor.visitChildren(this);
    }
  }

  public final PrimitivetypeContext primitivetype() throws RecognitionException {
    PrimitivetypeContext _localctx = new PrimitivetypeContext(_ctx, getState());
    enterRule(_localctx, 6, RULE_primitivetype);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(81);
      _la = _input.LA(1);
      if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INTTYPE) | (1L << BOOLEANTYPE) | (1L << FLOATTYPE) | (1L << STRINGTYPE))) != 0)) ) {
      _errHandler.recoverInline(this);
      }
      else {
        if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
        _errHandler.reportMatch(this);
        consume();
      }
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class ManyvariableContext extends ParserRuleContext {
    public VariableContext variable() {
      return getRuleContext(VariableContext.class,0);
    }
    public ManyvariableContext manyvariable() {
      return getRuleContext(ManyvariableContext.class,0);
    }
    public TerminalNode CM() { return getToken(MCParser.CM, 0); }
    public ManyvariableContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_manyvariable; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitManyvariable(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ManyvariableContext manyvariable() throws RecognitionException {
    return manyvariable(0);
  }

  private ManyvariableContext manyvariable(int _p) throws RecognitionException {
    ParserRuleContext _parentctx = _ctx;
    int _parentState = getState();
    ManyvariableContext _localctx = new ManyvariableContext(_ctx, _parentState);
    ManyvariableContext _prevctx = _localctx;
    int _startState = 8;
    enterRecursionRule(_localctx, 8, RULE_manyvariable, _p);
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
      {
      setState(84);
      variable();
      }
      _ctx.stop = _input.LT(-1);
      setState(91);
      _errHandler.sync(this);
      _alt = getInterpreter().adaptivePredict(_input,2,_ctx);
      while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
        if ( _alt==1 ) {
          if ( _parseListeners!=null ) triggerExitRuleEvent();
          _prevctx = _localctx;
          {
          {
          _localctx = new ManyvariableContext(_parentctx, _parentState);
          pushNewRecursionContext(_localctx, _startState, RULE_manyvariable);
          setState(86);
          if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
          setState(87);
          match(CM);
          setState(88);
          variable();
          }
          } 
        }
        setState(93);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,2,_ctx);
      }
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      unrollRecursionContexts(_parentctx);
    }
    return _localctx;
  }

  public static class VariableContext extends ParserRuleContext {
    public TerminalNode ID() { return getToken(MCParser.ID, 0); }
    public ArrayContext array() {
      return getRuleContext(ArrayContext.class,0);
    }
    public VariableContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_variable; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitVariable(this);
      else return visitor.visitChildren(this);
    }
  }

  public final VariableContext variable() throws RecognitionException {
    VariableContext _localctx = new VariableContext(_ctx, getState());
    enterRule(_localctx, 10, RULE_variable);
    try {
      setState(96);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(94);
        match(ID);
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(95);
        array();
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class ArrayContext extends ParserRuleContext {
    public TerminalNode ID() { return getToken(MCParser.ID, 0); }
    public TerminalNode LSB() { return getToken(MCParser.LSB, 0); }
    public TerminalNode INTLIT() { return getToken(MCParser.INTLIT, 0); }
    public TerminalNode RSB() { return getToken(MCParser.RSB, 0); }
    public ArrayContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_array; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitArray(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ArrayContext array() throws RecognitionException {
    ArrayContext _localctx = new ArrayContext(_ctx, getState());
    enterRule(_localctx, 12, RULE_array);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(98);
      match(ID);
      setState(99);
      match(LSB);
      setState(100);
      match(INTLIT);
      setState(101);
      match(RSB);
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class DeclerationsFunctionContext extends ParserRuleContext {
    public TypeFunctionContext typeFunction() {
      return getRuleContext(TypeFunctionContext.class,0);
    }
    public TerminalNode ID() { return getToken(MCParser.ID, 0); }
    public TerminalNode LB() { return getToken(MCParser.LB, 0); }
    public ParameterlistContext parameterlist() {
      return getRuleContext(ParameterlistContext.class,0);
    }
    public TerminalNode RB() { return getToken(MCParser.RB, 0); }
    public BlockstatementContext blockstatement() {
      return getRuleContext(BlockstatementContext.class,0);
    }
    public DeclerationsFunctionContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_declerationsFunction; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitDeclerationsFunction(this);
      else return visitor.visitChildren(this);
    }
  }

  public final DeclerationsFunctionContext declerationsFunction() throws RecognitionException {
    DeclerationsFunctionContext _localctx = new DeclerationsFunctionContext(_ctx, getState());
    enterRule(_localctx, 14, RULE_declerationsFunction);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(103);
      typeFunction();
      setState(104);
      match(ID);
      setState(105);
      match(LB);
      setState(106);
      parameterlist();
      setState(107);
      match(RB);
      setState(108);
      blockstatement();
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class TypeFunctionContext extends ParserRuleContext {
    public PrimitivetypeContext primitivetype() {
      return getRuleContext(PrimitivetypeContext.class,0);
    }
    public TerminalNode VOIDTYPE() { return getToken(MCParser.VOIDTYPE, 0); }
    public TerminalNode LSB() { return getToken(MCParser.LSB, 0); }
    public TerminalNode RSB() { return getToken(MCParser.RSB, 0); }
    public TypeFunctionContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_typeFunction; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitTypeFunction(this);
      else return visitor.visitChildren(this);
    }
  }

  public final TypeFunctionContext typeFunction() throws RecognitionException {
    TypeFunctionContext _localctx = new TypeFunctionContext(_ctx, getState());
    enterRule(_localctx, 16, RULE_typeFunction);
    try {
      setState(116);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(110);
        primitivetype();
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(111);
        match(VOIDTYPE);
        }
        break;
      case 3:
        enterOuterAlt(_localctx, 3);
        {
        setState(112);
        primitivetype();
        setState(113);
        match(LSB);
        setState(114);
        match(RSB);
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class ParameterlistContext extends ParserRuleContext {
    public List<ParameterContext> parameter() {
      return getRuleContexts(ParameterContext.class);
    }
    public ParameterContext parameter(int i) {
      return getRuleContext(ParameterContext.class,i);
    }
    public List<TerminalNode> CM() { return getTokens(MCParser.CM); }
    public TerminalNode CM(int i) {
      return getToken(MCParser.CM, i);
    }
    public ParameterlistContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_parameterlist; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitParameterlist(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ParameterlistContext parameterlist() throws RecognitionException {
    ParameterlistContext _localctx = new ParameterlistContext(_ctx, getState());
    enterRule(_localctx, 18, RULE_parameterlist);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(126);
      _errHandler.sync(this);
      _la = _input.LA(1);
      if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INTTYPE) | (1L << BOOLEANTYPE) | (1L << FLOATTYPE) | (1L << STRINGTYPE))) != 0)) {
        {
        setState(118);
        parameter();
        setState(123);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la==CM) {
          {
          {
          setState(119);
          match(CM);
          setState(120);
          parameter();
          }
          }
          setState(125);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        }
      }

      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class ParameterContext extends ParserRuleContext {
    public PrimitivetypeContext primitivetype() {
      return getRuleContext(PrimitivetypeContext.class,0);
    }
    public TerminalNode ID() { return getToken(MCParser.ID, 0); }
    public TerminalNode LSB() { return getToken(MCParser.LSB, 0); }
    public TerminalNode RSB() { return getToken(MCParser.RSB, 0); }
    public ParameterContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_parameter; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitParameter(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ParameterContext parameter() throws RecognitionException {
    ParameterContext _localctx = new ParameterContext(_ctx, getState());
    enterRule(_localctx, 20, RULE_parameter);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(128);
      primitivetype();
      setState(129);
      match(ID);
      setState(132);
      _errHandler.sync(this);
      _la = _input.LA(1);
      if (_la==LSB) {
        {
        setState(130);
        match(LSB);
        setState(131);
        match(RSB);
        }
      }

      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class BlockstatementContext extends ParserRuleContext {
    public TerminalNode LP() { return getToken(MCParser.LP, 0); }
    public DeclarationPartContext declarationPart() {
      return getRuleContext(DeclarationPartContext.class,0);
    }
    public StatementPartContext statementPart() {
      return getRuleContext(StatementPartContext.class,0);
    }
    public TerminalNode RP() { return getToken(MCParser.RP, 0); }
    public BlockstatementContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_blockstatement; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitBlockstatement(this);
      else return visitor.visitChildren(this);
    }
  }

  public final BlockstatementContext blockstatement() throws RecognitionException {
    BlockstatementContext _localctx = new BlockstatementContext(_ctx, getState());
    enterRule(_localctx, 22, RULE_blockstatement);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(134);
      match(LP);
      setState(135);
      declarationPart();
      setState(136);
      statementPart();
      setState(137);
      match(RP);
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class DeclarationPartContext extends ParserRuleContext {
    public List<DeclerationsVariableContext> declerationsVariable() {
      return getRuleContexts(DeclerationsVariableContext.class);
    }
    public DeclerationsVariableContext declerationsVariable(int i) {
      return getRuleContext(DeclerationsVariableContext.class,i);
    }
    public DeclarationPartContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_declarationPart; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitDeclarationPart(this);
      else return visitor.visitChildren(this);
    }
  }

  public final DeclarationPartContext declarationPart() throws RecognitionException {
    DeclarationPartContext _localctx = new DeclarationPartContext(_ctx, getState());
    enterRule(_localctx, 24, RULE_declarationPart);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(142);
      _errHandler.sync(this);
      _la = _input.LA(1);
      while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INTTYPE) | (1L << BOOLEANTYPE) | (1L << FLOATTYPE) | (1L << STRINGTYPE))) != 0)) {
        {
        {
        setState(139);
        declerationsVariable();
        }
        }
        setState(144);
        _errHandler.sync(this);
        _la = _input.LA(1);
      }
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class StatementPartContext extends ParserRuleContext {
    public List<StatementContext> statement() {
      return getRuleContexts(StatementContext.class);
    }
    public StatementContext statement(int i) {
      return getRuleContext(StatementContext.class,i);
    }
    public StatementPartContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_statementPart; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitStatementPart(this);
      else return visitor.visitChildren(this);
    }
  }

  public final StatementPartContext statementPart() throws RecognitionException {
    StatementPartContext _localctx = new StatementPartContext(_ctx, getState());
    enterRule(_localctx, 26, RULE_statementPart);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(148);
      _errHandler.sync(this);
      _la = _input.LA(1);
      while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEANLIT) | (1L << INTLIT) | (1L << FLOAT) | (1L << STRING) | (1L << BREAK) | (1L << CONTINUE) | (1L << IF) | (1L << RETURN) | (1L << DO) | (1L << FOR) | (1L << SUB) | (1L << NOT) | (1L << LP) | (1L << LB) | (1L << ID))) != 0)) {
        {
        {
        setState(145);
        statement();
        }
        }
        setState(150);
        _errHandler.sync(this);
        _la = _input.LA(1);
      }
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class StatementContext extends ParserRuleContext {
    public IfStatementContext ifStatement() {
      return getRuleContext(IfStatementContext.class,0);
    }
    public DowhileStatementContext dowhileStatement() {
      return getRuleContext(DowhileStatementContext.class,0);
    }
    public ForStatementContext forStatement() {
      return getRuleContext(ForStatementContext.class,0);
    }
    public BreakStatementContext breakStatement() {
      return getRuleContext(BreakStatementContext.class,0);
    }
    public ContinueStatementContext continueStatement() {
      return getRuleContext(ContinueStatementContext.class,0);
    }
    public ReturnStatementContext returnStatement() {
      return getRuleContext(ReturnStatementContext.class,0);
    }
    public ExpressionStatementContext expressionStatement() {
      return getRuleContext(ExpressionStatementContext.class,0);
    }
    public BlockstatementContext blockstatement() {
      return getRuleContext(BlockstatementContext.class,0);
    }
    public StatementContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_statement; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitStatement(this);
      else return visitor.visitChildren(this);
    }
  }

  public final StatementContext statement() throws RecognitionException {
    StatementContext _localctx = new StatementContext(_ctx, getState());
    enterRule(_localctx, 28, RULE_statement);
    try {
      setState(159);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
      case IF:
        enterOuterAlt(_localctx, 1);
        {
        setState(151);
        ifStatement();
        }
        break;
      case DO:
        enterOuterAlt(_localctx, 2);
        {
        setState(152);
        dowhileStatement();
        }
        break;
      case FOR:
        enterOuterAlt(_localctx, 3);
        {
        setState(153);
        forStatement();
        }
        break;
      case BREAK:
        enterOuterAlt(_localctx, 4);
        {
        setState(154);
        breakStatement();
        }
        break;
      case CONTINUE:
        enterOuterAlt(_localctx, 5);
        {
        setState(155);
        continueStatement();
        }
        break;
      case RETURN:
        enterOuterAlt(_localctx, 6);
        {
        setState(156);
        returnStatement();
        }
        break;
      case BOOLEANLIT:
      case INTLIT:
      case FLOAT:
      case STRING:
      case SUB:
      case NOT:
      case LB:
      case ID:
        enterOuterAlt(_localctx, 7);
        {
        setState(157);
        expressionStatement();
        }
        break;
      case LP:
        enterOuterAlt(_localctx, 8);
        {
        setState(158);
        blockstatement();
        }
        break;
      default:
        throw new NoViableAltException(this);
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class IfStatementContext extends ParserRuleContext {
    public TerminalNode IF() { return getToken(MCParser.IF, 0); }
    public TerminalNode LB() { return getToken(MCParser.LB, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode RB() { return getToken(MCParser.RB, 0); }
    public List<StatementContext> statement() {
      return getRuleContexts(StatementContext.class);
    }
    public StatementContext statement(int i) {
      return getRuleContext(StatementContext.class,i);
    }
    public TerminalNode ELSE() { return getToken(MCParser.ELSE, 0); }
    public IfStatementContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_ifStatement; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitIfStatement(this);
      else return visitor.visitChildren(this);
    }
  }

  public final IfStatementContext ifStatement() throws RecognitionException {
    IfStatementContext _localctx = new IfStatementContext(_ctx, getState());
    enterRule(_localctx, 30, RULE_ifStatement);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(161);
      match(IF);
      setState(162);
      match(LB);
      setState(163);
      expression();
      setState(164);
      match(RB);
      setState(165);
      statement();
      setState(168);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
      case 1:
        {
        setState(166);
        match(ELSE);
        setState(167);
        statement();
        }
        break;
      }
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class DowhileStatementContext extends ParserRuleContext {
    public TerminalNode DO() { return getToken(MCParser.DO, 0); }
    public TerminalNode WHILE() { return getToken(MCParser.WHILE, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode SM() { return getToken(MCParser.SM, 0); }
    public List<StatementContext> statement() {
      return getRuleContexts(StatementContext.class);
    }
    public StatementContext statement(int i) {
      return getRuleContext(StatementContext.class,i);
    }
    public DowhileStatementContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_dowhileStatement; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitDowhileStatement(this);
      else return visitor.visitChildren(this);
    }
  }

  public final DowhileStatementContext dowhileStatement() throws RecognitionException {
    DowhileStatementContext _localctx = new DowhileStatementContext(_ctx, getState());
    enterRule(_localctx, 32, RULE_dowhileStatement);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(170);
      match(DO);
      setState(172); 
      _errHandler.sync(this);
      _la = _input.LA(1);
      do {
        {
        {
        setState(171);
        statement();
        }
        }
        setState(174); 
        _errHandler.sync(this);
        _la = _input.LA(1);
      } while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEANLIT) | (1L << INTLIT) | (1L << FLOAT) | (1L << STRING) | (1L << BREAK) | (1L << CONTINUE) | (1L << IF) | (1L << RETURN) | (1L << DO) | (1L << FOR) | (1L << SUB) | (1L << NOT) | (1L << LP) | (1L << LB) | (1L << ID))) != 0) );
      setState(176);
      match(WHILE);
      setState(177);
      expression();
      setState(178);
      match(SM);
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class ForStatementContext extends ParserRuleContext {
    public TerminalNode FOR() { return getToken(MCParser.FOR, 0); }
    public TerminalNode LB() { return getToken(MCParser.LB, 0); }
    public List<ExpressionContext> expression() {
      return getRuleContexts(ExpressionContext.class);
    }
    public ExpressionContext expression(int i) {
      return getRuleContext(ExpressionContext.class,i);
    }
    public List<TerminalNode> SM() { return getTokens(MCParser.SM); }
    public TerminalNode SM(int i) {
      return getToken(MCParser.SM, i);
    }
    public TerminalNode RB() { return getToken(MCParser.RB, 0); }
    public StatementContext statement() {
      return getRuleContext(StatementContext.class,0);
    }
    public ForStatementContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_forStatement; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitForStatement(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ForStatementContext forStatement() throws RecognitionException {
    ForStatementContext _localctx = new ForStatementContext(_ctx, getState());
    enterRule(_localctx, 34, RULE_forStatement);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(180);
      match(FOR);
      setState(181);
      match(LB);
      setState(182);
      expression();
      setState(183);
      match(SM);
      setState(184);
      expression();
      setState(185);
      match(SM);
      setState(186);
      expression();
      setState(187);
      match(RB);
      setState(188);
      statement();
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class BreakStatementContext extends ParserRuleContext {
    public TerminalNode BREAK() { return getToken(MCParser.BREAK, 0); }
    public TerminalNode SM() { return getToken(MCParser.SM, 0); }
    public BreakStatementContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_breakStatement; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitBreakStatement(this);
      else return visitor.visitChildren(this);
    }
  }

  public final BreakStatementContext breakStatement() throws RecognitionException {
    BreakStatementContext _localctx = new BreakStatementContext(_ctx, getState());
    enterRule(_localctx, 36, RULE_breakStatement);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(190);
      match(BREAK);
      setState(191);
      match(SM);
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class ContinueStatementContext extends ParserRuleContext {
    public TerminalNode CONTINUE() { return getToken(MCParser.CONTINUE, 0); }
    public TerminalNode SM() { return getToken(MCParser.SM, 0); }
    public ContinueStatementContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_continueStatement; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitContinueStatement(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ContinueStatementContext continueStatement() throws RecognitionException {
    ContinueStatementContext _localctx = new ContinueStatementContext(_ctx, getState());
    enterRule(_localctx, 38, RULE_continueStatement);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(193);
      match(CONTINUE);
      setState(194);
      match(SM);
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class ReturnStatementContext extends ParserRuleContext {
    public TerminalNode RETURN() { return getToken(MCParser.RETURN, 0); }
    public TerminalNode SM() { return getToken(MCParser.SM, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public ReturnStatementContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_returnStatement; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitReturnStatement(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ReturnStatementContext returnStatement() throws RecognitionException {
    ReturnStatementContext _localctx = new ReturnStatementContext(_ctx, getState());
    enterRule(_localctx, 40, RULE_returnStatement);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(196);
      match(RETURN);
      setState(198);
      _errHandler.sync(this);
      _la = _input.LA(1);
      if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEANLIT) | (1L << INTLIT) | (1L << FLOAT) | (1L << STRING) | (1L << SUB) | (1L << NOT) | (1L << LB) | (1L << ID))) != 0)) {
        {
        setState(197);
        expression();
        }
      }

      setState(200);
      match(SM);
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class ExpressionStatementContext extends ParserRuleContext {
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode SM() { return getToken(MCParser.SM, 0); }
    public ExpressionStatementContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_expressionStatement; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitExpressionStatement(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ExpressionStatementContext expressionStatement() throws RecognitionException {
    ExpressionStatementContext _localctx = new ExpressionStatementContext(_ctx, getState());
    enterRule(_localctx, 42, RULE_expressionStatement);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(202);
      expression();
      setState(203);
      match(SM);
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class ExpressionContext extends ParserRuleContext {
    public Expression1Context expression1() {
      return getRuleContext(Expression1Context.class,0);
    }
    public TerminalNode ASSIGN() { return getToken(MCParser.ASSIGN, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public ExpressionContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_expression; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitExpression(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ExpressionContext expression() throws RecognitionException {
    ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
    enterRule(_localctx, 44, RULE_expression);
    try {
      setState(210);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(205);
        expression1(0);
        setState(206);
        match(ASSIGN);
        setState(207);
        expression();
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(209);
        expression1(0);
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class Expression1Context extends ParserRuleContext {
    public Expression2Context expression2() {
      return getRuleContext(Expression2Context.class,0);
    }
    public Expression1Context expression1() {
      return getRuleContext(Expression1Context.class,0);
    }
    public TerminalNode OR() { return getToken(MCParser.OR, 0); }
    public Expression1Context(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_expression1; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitExpression1(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Expression1Context expression1() throws RecognitionException {
    return expression1(0);
  }

  private Expression1Context expression1(int _p) throws RecognitionException {
    ParserRuleContext _parentctx = _ctx;
    int _parentState = getState();
    Expression1Context _localctx = new Expression1Context(_ctx, _parentState);
    Expression1Context _prevctx = _localctx;
    int _startState = 46;
    enterRecursionRule(_localctx, 46, RULE_expression1, _p);
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
      {
      setState(213);
      expression2(0);
      }
      _ctx.stop = _input.LT(-1);
      setState(220);
      _errHandler.sync(this);
      _alt = getInterpreter().adaptivePredict(_input,15,_ctx);
      while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
        if ( _alt==1 ) {
          if ( _parseListeners!=null ) triggerExitRuleEvent();
          _prevctx = _localctx;
          {
          {
          _localctx = new Expression1Context(_parentctx, _parentState);
          pushNewRecursionContext(_localctx, _startState, RULE_expression1);
          setState(215);
          if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
          setState(216);
          match(OR);
          setState(217);
          expression2(0);
          }
          } 
        }
        setState(222);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,15,_ctx);
      }
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      unrollRecursionContexts(_parentctx);
    }
    return _localctx;
  }

  public static class Expression2Context extends ParserRuleContext {
    public Expression3Context expression3() {
      return getRuleContext(Expression3Context.class,0);
    }
    public Expression2Context expression2() {
      return getRuleContext(Expression2Context.class,0);
    }
    public TerminalNode AND() { return getToken(MCParser.AND, 0); }
    public Expression2Context(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_expression2; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitExpression2(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Expression2Context expression2() throws RecognitionException {
    return expression2(0);
  }

  private Expression2Context expression2(int _p) throws RecognitionException {
    ParserRuleContext _parentctx = _ctx;
    int _parentState = getState();
    Expression2Context _localctx = new Expression2Context(_ctx, _parentState);
    Expression2Context _prevctx = _localctx;
    int _startState = 48;
    enterRecursionRule(_localctx, 48, RULE_expression2, _p);
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
      {
      setState(224);
      expression3();
      }
      _ctx.stop = _input.LT(-1);
      setState(231);
      _errHandler.sync(this);
      _alt = getInterpreter().adaptivePredict(_input,16,_ctx);
      while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
        if ( _alt==1 ) {
          if ( _parseListeners!=null ) triggerExitRuleEvent();
          _prevctx = _localctx;
          {
          {
          _localctx = new Expression2Context(_parentctx, _parentState);
          pushNewRecursionContext(_localctx, _startState, RULE_expression2);
          setState(226);
          if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
          setState(227);
          match(AND);
          setState(228);
          expression3();
          }
          } 
        }
        setState(233);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,16,_ctx);
      }
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      unrollRecursionContexts(_parentctx);
    }
    return _localctx;
  }

  public static class Expression3Context extends ParserRuleContext {
    public List<Expression4Context> expression4() {
      return getRuleContexts(Expression4Context.class);
    }
    public Expression4Context expression4(int i) {
      return getRuleContext(Expression4Context.class,i);
    }
    public TerminalNode EQUAL() { return getToken(MCParser.EQUAL, 0); }
    public TerminalNode NOTEQUAL() { return getToken(MCParser.NOTEQUAL, 0); }
    public Expression3Context(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_expression3; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitExpression3(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Expression3Context expression3() throws RecognitionException {
    Expression3Context _localctx = new Expression3Context(_ctx, getState());
    enterRule(_localctx, 50, RULE_expression3);
    int _la;
    try {
      setState(239);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(234);
        expression4();
        setState(235);
        _la = _input.LA(1);
        if ( !(_la==NOTEQUAL || _la==EQUAL) ) {
        _errHandler.recoverInline(this);
        }
        else {
          if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
          _errHandler.reportMatch(this);
          consume();
        }
        setState(236);
        expression4();
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(238);
        expression4();
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class Expression4Context extends ParserRuleContext {
    public List<Expression5Context> expression5() {
      return getRuleContexts(Expression5Context.class);
    }
    public Expression5Context expression5(int i) {
      return getRuleContext(Expression5Context.class,i);
    }
    public TerminalNode LESS() { return getToken(MCParser.LESS, 0); }
    public TerminalNode LESSEQUAL() { return getToken(MCParser.LESSEQUAL, 0); }
    public TerminalNode GREATER() { return getToken(MCParser.GREATER, 0); }
    public TerminalNode GREATEREQUAL() { return getToken(MCParser.GREATEREQUAL, 0); }
    public Expression4Context(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_expression4; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitExpression4(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Expression4Context expression4() throws RecognitionException {
    Expression4Context _localctx = new Expression4Context(_ctx, getState());
    enterRule(_localctx, 52, RULE_expression4);
    int _la;
    try {
      setState(246);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(241);
        expression5(0);
        setState(242);
        _la = _input.LA(1);
        if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LESS) | (1L << LESSEQUAL) | (1L << GREATER) | (1L << GREATEREQUAL))) != 0)) ) {
        _errHandler.recoverInline(this);
        }
        else {
          if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
          _errHandler.reportMatch(this);
          consume();
        }
        setState(243);
        expression5(0);
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(245);
        expression5(0);
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class Expression5Context extends ParserRuleContext {
    public Expression6Context expression6() {
      return getRuleContext(Expression6Context.class,0);
    }
    public Expression5Context expression5() {
      return getRuleContext(Expression5Context.class,0);
    }
    public TerminalNode ADD() { return getToken(MCParser.ADD, 0); }
    public TerminalNode SUB() { return getToken(MCParser.SUB, 0); }
    public Expression5Context(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_expression5; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitExpression5(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Expression5Context expression5() throws RecognitionException {
    return expression5(0);
  }

  private Expression5Context expression5(int _p) throws RecognitionException {
    ParserRuleContext _parentctx = _ctx;
    int _parentState = getState();
    Expression5Context _localctx = new Expression5Context(_ctx, _parentState);
    Expression5Context _prevctx = _localctx;
    int _startState = 54;
    enterRecursionRule(_localctx, 54, RULE_expression5, _p);
    int _la;
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
      {
      setState(249);
      expression6(0);
      }
      _ctx.stop = _input.LT(-1);
      setState(256);
      _errHandler.sync(this);
      _alt = getInterpreter().adaptivePredict(_input,19,_ctx);
      while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
        if ( _alt==1 ) {
          if ( _parseListeners!=null ) triggerExitRuleEvent();
          _prevctx = _localctx;
          {
          {
          _localctx = new Expression5Context(_parentctx, _parentState);
          pushNewRecursionContext(_localctx, _startState, RULE_expression5);
          setState(251);
          if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
          setState(252);
          _la = _input.LA(1);
          if ( !(_la==SUB || _la==ADD) ) {
          _errHandler.recoverInline(this);
          }
          else {
            if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
            _errHandler.reportMatch(this);
            consume();
          }
          setState(253);
          expression6(0);
          }
          } 
        }
        setState(258);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,19,_ctx);
      }
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      unrollRecursionContexts(_parentctx);
    }
    return _localctx;
  }

  public static class Expression6Context extends ParserRuleContext {
    public Expression7Context expression7() {
      return getRuleContext(Expression7Context.class,0);
    }
    public Expression6Context expression6() {
      return getRuleContext(Expression6Context.class,0);
    }
    public TerminalNode DIV() { return getToken(MCParser.DIV, 0); }
    public TerminalNode MUL() { return getToken(MCParser.MUL, 0); }
    public TerminalNode MOD() { return getToken(MCParser.MOD, 0); }
    public Expression6Context(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_expression6; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitExpression6(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Expression6Context expression6() throws RecognitionException {
    return expression6(0);
  }

  private Expression6Context expression6(int _p) throws RecognitionException {
    ParserRuleContext _parentctx = _ctx;
    int _parentState = getState();
    Expression6Context _localctx = new Expression6Context(_ctx, _parentState);
    Expression6Context _prevctx = _localctx;
    int _startState = 56;
    enterRecursionRule(_localctx, 56, RULE_expression6, _p);
    int _la;
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
      {
      setState(260);
      expression7();
      }
      _ctx.stop = _input.LT(-1);
      setState(267);
      _errHandler.sync(this);
      _alt = getInterpreter().adaptivePredict(_input,20,_ctx);
      while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
        if ( _alt==1 ) {
          if ( _parseListeners!=null ) triggerExitRuleEvent();
          _prevctx = _localctx;
          {
          {
          _localctx = new Expression6Context(_parentctx, _parentState);
          pushNewRecursionContext(_localctx, _startState, RULE_expression6);
          setState(262);
          if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
          setState(263);
          _la = _input.LA(1);
          if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MUL) | (1L << DIV) | (1L << MOD))) != 0)) ) {
          _errHandler.recoverInline(this);
          }
          else {
            if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
            _errHandler.reportMatch(this);
            consume();
          }
          setState(264);
          expression7();
          }
          } 
        }
        setState(269);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,20,_ctx);
      }
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      unrollRecursionContexts(_parentctx);
    }
    return _localctx;
  }

  public static class Expression7Context extends ParserRuleContext {
    public Expression7Context expression7() {
      return getRuleContext(Expression7Context.class,0);
    }
    public TerminalNode SUB() { return getToken(MCParser.SUB, 0); }
    public TerminalNode NOT() { return getToken(MCParser.NOT, 0); }
    public Expression8Context expression8() {
      return getRuleContext(Expression8Context.class,0);
    }
    public Expression7Context(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_expression7; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitExpression7(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Expression7Context expression7() throws RecognitionException {
    Expression7Context _localctx = new Expression7Context(_ctx, getState());
    enterRule(_localctx, 58, RULE_expression7);
    int _la;
    try {
      setState(273);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
      case SUB:
      case NOT:
        enterOuterAlt(_localctx, 1);
        {
        setState(270);
        _la = _input.LA(1);
        if ( !(_la==SUB || _la==NOT) ) {
        _errHandler.recoverInline(this);
        }
        else {
          if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
          _errHandler.reportMatch(this);
          consume();
        }
        setState(271);
        expression7();
        }
        break;
      case BOOLEANLIT:
      case INTLIT:
      case FLOAT:
      case STRING:
      case LB:
      case ID:
        enterOuterAlt(_localctx, 2);
        {
        setState(272);
        expression8();
        }
        break;
      default:
        throw new NoViableAltException(this);
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class Expression8Context extends ParserRuleContext {
    public Expression9Context expression9() {
      return getRuleContext(Expression9Context.class,0);
    }
    public TerminalNode LSB() { return getToken(MCParser.LSB, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode RSB() { return getToken(MCParser.RSB, 0); }
    public Expression8Context(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_expression8; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitExpression8(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Expression8Context expression8() throws RecognitionException {
    Expression8Context _localctx = new Expression8Context(_ctx, getState());
    enterRule(_localctx, 60, RULE_expression8);
    try {
      setState(281);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(275);
        expression9();
        setState(276);
        match(LSB);
        setState(277);
        expression();
        setState(278);
        match(RSB);
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(280);
        expression9();
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class Expression9Context extends ParserRuleContext {
    public TerminalNode LB() { return getToken(MCParser.LB, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode RB() { return getToken(MCParser.RB, 0); }
    public TerminalNode ID() { return getToken(MCParser.ID, 0); }
    public TerminalNode BOOLEANLIT() { return getToken(MCParser.BOOLEANLIT, 0); }
    public TerminalNode STRING() { return getToken(MCParser.STRING, 0); }
    public TerminalNode INTLIT() { return getToken(MCParser.INTLIT, 0); }
    public TerminalNode FLOAT() { return getToken(MCParser.FLOAT, 0); }
    public FuncallContext funcall() {
      return getRuleContext(FuncallContext.class,0);
    }
    public Expression9Context(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_expression9; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitExpression9(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Expression9Context expression9() throws RecognitionException {
    Expression9Context _localctx = new Expression9Context(_ctx, getState());
    enterRule(_localctx, 62, RULE_expression9);
    try {
      setState(293);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(283);
        match(LB);
        setState(284);
        expression();
        setState(285);
        match(RB);
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(287);
        match(ID);
        }
        break;
      case 3:
        enterOuterAlt(_localctx, 3);
        {
        setState(288);
        match(BOOLEANLIT);
        }
        break;
      case 4:
        enterOuterAlt(_localctx, 4);
        {
        setState(289);
        match(STRING);
        }
        break;
      case 5:
        enterOuterAlt(_localctx, 5);
        {
        setState(290);
        match(INTLIT);
        }
        break;
      case 6:
        enterOuterAlt(_localctx, 6);
        {
        setState(291);
        match(FLOAT);
        }
        break;
      case 7:
        enterOuterAlt(_localctx, 7);
        {
        setState(292);
        funcall();
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class FuncallContext extends ParserRuleContext {
    public TerminalNode ID() { return getToken(MCParser.ID, 0); }
    public TerminalNode LB() { return getToken(MCParser.LB, 0); }
    public ExpressionListContext expressionList() {
      return getRuleContext(ExpressionListContext.class,0);
    }
    public TerminalNode RB() { return getToken(MCParser.RB, 0); }
    public FuncallContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_funcall; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitFuncall(this);
      else return visitor.visitChildren(this);
    }
  }

  public final FuncallContext funcall() throws RecognitionException {
    FuncallContext _localctx = new FuncallContext(_ctx, getState());
    enterRule(_localctx, 64, RULE_funcall);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(295);
      match(ID);
      setState(296);
      match(LB);
      setState(297);
      expressionList();
      setState(298);
      match(RB);
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class ExpressionListContext extends ParserRuleContext {
    public List<ExpressionContext> expression() {
      return getRuleContexts(ExpressionContext.class);
    }
    public ExpressionContext expression(int i) {
      return getRuleContext(ExpressionContext.class,i);
    }
    public List<TerminalNode> CM() { return getTokens(MCParser.CM); }
    public TerminalNode CM(int i) {
      return getToken(MCParser.CM, i);
    }
    public ExpressionListContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_expressionList; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof MCVisitor ) return ((MCVisitor<? extends T>)visitor).visitExpressionList(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ExpressionListContext expressionList() throws RecognitionException {
    ExpressionListContext _localctx = new ExpressionListContext(_ctx, getState());
    enterRule(_localctx, 66, RULE_expressionList);
    int _la;
    try {
      setState(311);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(301);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEANLIT) | (1L << INTLIT) | (1L << FLOAT) | (1L << STRING) | (1L << SUB) | (1L << NOT) | (1L << LB) | (1L << ID))) != 0)) {
          {
          setState(300);
          expression();
          }
        }

        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(303);
        expression();
        setState(308);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la==CM) {
          {
          {
          setState(304);
          match(CM);
          setState(305);
          expression();
          }
          }
          setState(310);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
    switch (ruleIndex) {
    case 4:
      return manyvariable_sempred((ManyvariableContext)_localctx, predIndex);
    case 23:
      return expression1_sempred((Expression1Context)_localctx, predIndex);
    case 24:
      return expression2_sempred((Expression2Context)_localctx, predIndex);
    case 27:
      return expression5_sempred((Expression5Context)_localctx, predIndex);
    case 28:
      return expression6_sempred((Expression6Context)_localctx, predIndex);
    }
    return true;
  }
  private boolean manyvariable_sempred(ManyvariableContext _localctx, int predIndex) {
    switch (predIndex) {
    case 0:
      return precpred(_ctx, 2);
    }
    return true;
  }
  private boolean expression1_sempred(Expression1Context _localctx, int predIndex) {
    switch (predIndex) {
    case 1:
      return precpred(_ctx, 2);
    }
    return true;
  }
  private boolean expression2_sempred(Expression2Context _localctx, int predIndex) {
    switch (predIndex) {
    case 2:
      return precpred(_ctx, 2);
    }
    return true;
  }
  private boolean expression5_sempred(Expression5Context _localctx, int predIndex) {
    switch (predIndex) {
    case 3:
      return precpred(_ctx, 2);
    }
    return true;
  }
  private boolean expression6_sempred(Expression6Context _localctx, int predIndex) {
    switch (predIndex) {
    case 4:
      return precpred(_ctx, 2);
    }
    return true;
  }

  public static final String _serializedATN =
    "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\63\u013c\4\2\t\2"+
    "\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
    "\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
    "\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
    "\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
    "\t!\4\"\t\"\4#\t#\3\2\3\2\3\3\3\3\7\3K\n\3\f\3\16\3N\13\3\3\4\3\4\3\4"+
    "\3\4\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\7\6\\\n\6\f\6\16\6_\13\6\3\7\3\7"+
    "\5\7c\n\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n"+
    "\3\n\3\n\3\n\5\nw\n\n\3\13\3\13\3\13\7\13|\n\13\f\13\16\13\177\13\13\5"+
    "\13\u0081\n\13\3\f\3\f\3\f\3\f\5\f\u0087\n\f\3\r\3\r\3\r\3\r\3\r\3\16"+
    "\7\16\u008f\n\16\f\16\16\16\u0092\13\16\3\17\7\17\u0095\n\17\f\17\16\17"+
    "\u0098\13\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\5\20\u00a2\n\20\3"+
    "\21\3\21\3\21\3\21\3\21\3\21\3\21\5\21\u00ab\n\21\3\22\3\22\6\22\u00af"+
    "\n\22\r\22\16\22\u00b0\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3"+
    "\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\25\3\25\3\25\3\26\3\26\5\26\u00c9"+
    "\n\26\3\26\3\26\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30\5\30\u00d5\n\30"+
    "\3\31\3\31\3\31\3\31\3\31\3\31\7\31\u00dd\n\31\f\31\16\31\u00e0\13\31"+
    "\3\32\3\32\3\32\3\32\3\32\3\32\7\32\u00e8\n\32\f\32\16\32\u00eb\13\32"+
    "\3\33\3\33\3\33\3\33\3\33\5\33\u00f2\n\33\3\34\3\34\3\34\3\34\3\34\5\34"+
    "\u00f9\n\34\3\35\3\35\3\35\3\35\3\35\3\35\7\35\u0101\n\35\f\35\16\35\u0104"+
    "\13\35\3\36\3\36\3\36\3\36\3\36\3\36\7\36\u010c\n\36\f\36\16\36\u010f"+
    "\13\36\3\37\3\37\3\37\5\37\u0114\n\37\3 \3 \3 \3 \3 \3 \5 \u011c\n \3"+
    "!\3!\3!\3!\3!\3!\3!\3!\3!\3!\5!\u0128\n!\3\"\3\"\3\"\3\"\3\"\3#\5#\u0130"+
    "\n#\3#\3#\3#\7#\u0135\n#\f#\16#\u0138\13#\5#\u013a\n#\3#\2\7\n\60\628"+
    ":$\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BD"+
    "\2\b\4\2\7\7\t\13\4\2\33\33\"\"\4\2\34\35#$\3\2\26\27\4\2\30\30\37 \4"+
    "\2\26\26\31\31\u0140\2F\3\2\2\2\4L\3\2\2\2\6O\3\2\2\2\bS\3\2\2\2\nU\3"+
    "\2\2\2\fb\3\2\2\2\16d\3\2\2\2\20i\3\2\2\2\22v\3\2\2\2\24\u0080\3\2\2\2"+
    "\26\u0082\3\2\2\2\30\u0088\3\2\2\2\32\u0090\3\2\2\2\34\u0096\3\2\2\2\36"+
    "\u00a1\3\2\2\2 \u00a3\3\2\2\2\"\u00ac\3\2\2\2$\u00b6\3\2\2\2&\u00c0\3"+
    "\2\2\2(\u00c3\3\2\2\2*\u00c6\3\2\2\2,\u00cc\3\2\2\2.\u00d4\3\2\2\2\60"+
    "\u00d6\3\2\2\2\62\u00e1\3\2\2\2\64\u00f1\3\2\2\2\66\u00f8\3\2\2\28\u00fa"+
    "\3\2\2\2:\u0105\3\2\2\2<\u0113\3\2\2\2>\u011b\3\2\2\2@\u0127\3\2\2\2B"+
    "\u0129\3\2\2\2D\u0139\3\2\2\2FG\5\4\3\2G\3\3\2\2\2HK\5\6\4\2IK\5\20\t"+
    "\2JH\3\2\2\2JI\3\2\2\2KN\3\2\2\2LJ\3\2\2\2LM\3\2\2\2M\5\3\2\2\2NL\3\2"+
    "\2\2OP\5\b\5\2PQ\5\n\6\2QR\7+\2\2R\7\3\2\2\2ST\t\2\2\2T\t\3\2\2\2UV\b"+
    "\6\1\2VW\5\f\7\2W]\3\2\2\2XY\f\4\2\2YZ\7,\2\2Z\\\5\f\7\2[X\3\2\2\2\\_"+
    "\3\2\2\2][\3\2\2\2]^\3\2\2\2^\13\3\2\2\2_]\3\2\2\2`c\7-\2\2ac\5\16\b\2"+
    "b`\3\2\2\2ba\3\2\2\2c\r\3\2\2\2de\7-\2\2ef\7%\2\2fg\7\4\2\2gh\7&\2\2h"+
    "\17\3\2\2\2ij\5\22\n\2jk\7-\2\2kl\7)\2\2lm\5\24\13\2mn\7*\2\2no\5\30\r"+
    "\2o\21\3\2\2\2pw\5\b\5\2qw\7\b\2\2rs\5\b\5\2st\7%\2\2tu\7&\2\2uw\3\2\2"+
    "\2vp\3\2\2\2vq\3\2\2\2vr\3\2\2\2w\23\3\2\2\2x}\5\26\f\2yz\7,\2\2z|\5\26"+
    "\f\2{y\3\2\2\2|\177\3\2\2\2}{\3\2\2\2}~\3\2\2\2~\u0081\3\2\2\2\177}\3"+
    "\2\2\2\u0080x\3\2\2\2\u0080\u0081\3\2\2\2\u0081\25\3\2\2\2\u0082\u0083"+
    "\5\b\5\2\u0083\u0086\7-\2\2\u0084\u0085\7%\2\2\u0085\u0087\7&\2\2\u0086"+
    "\u0084\3\2\2\2\u0086\u0087\3\2\2\2\u0087\27\3\2\2\2\u0088\u0089\7\'\2"+
    "\2\u0089\u008a\5\32\16\2\u008a\u008b\5\34\17\2\u008b\u008c\7(\2\2\u008c"+
    "\31\3\2\2\2\u008d\u008f\5\6\4\2\u008e\u008d\3\2\2\2\u008f\u0092\3\2\2"+
    "\2\u0090\u008e\3\2\2\2\u0090\u0091\3\2\2\2\u0091\33\3\2\2\2\u0092\u0090"+
    "\3\2\2\2\u0093\u0095\5\36\20\2\u0094\u0093\3\2\2\2\u0095\u0098\3\2\2\2"+
    "\u0096\u0094\3\2\2\2\u0096\u0097\3\2\2\2\u0097\35\3\2\2\2\u0098\u0096"+
    "\3\2\2\2\u0099\u00a2\5 \21\2\u009a\u00a2\5\"\22\2\u009b\u00a2\5$\23\2"+
    "\u009c\u00a2\5&\24\2\u009d\u00a2\5(\25\2\u009e\u00a2\5*\26\2\u009f\u00a2"+
    "\5,\27\2\u00a0\u00a2\5\30\r\2\u00a1\u0099\3\2\2\2\u00a1\u009a\3\2\2\2"+
    "\u00a1\u009b\3\2\2\2\u00a1\u009c\3\2\2\2\u00a1\u009d\3\2\2\2\u00a1\u009e"+
    "\3\2\2\2\u00a1\u009f\3\2\2\2\u00a1\u00a0\3\2\2\2\u00a2\37\3\2\2\2\u00a3"+
    "\u00a4\7\17\2\2\u00a4\u00a5\7)\2\2\u00a5\u00a6\5.\30\2\u00a6\u00a7\7*"+
    "\2\2\u00a7\u00aa\5\36\20\2\u00a8\u00a9\7\16\2\2\u00a9\u00ab\5\36\20\2"+
    "\u00aa\u00a8\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab!\3\2\2\2\u00ac\u00ae\7"+
    "\22\2\2\u00ad\u00af\5\36\20\2\u00ae\u00ad\3\2\2\2\u00af\u00b0\3\2\2\2"+
    "\u00b0\u00ae\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00b2\3\2\2\2\u00b2\u00b3"+
    "\7\21\2\2\u00b3\u00b4\5.\30\2\u00b4\u00b5\7+\2\2\u00b5#\3\2\2\2\u00b6"+
    "\u00b7\7\23\2\2\u00b7\u00b8\7)\2\2\u00b8\u00b9\5.\30\2\u00b9\u00ba\7+"+
    "\2\2\u00ba\u00bb\5.\30\2\u00bb\u00bc\7+\2\2\u00bc\u00bd\5.\30\2\u00bd"+
    "\u00be\7*\2\2\u00be\u00bf\5\36\20\2\u00bf%\3\2\2\2\u00c0\u00c1\7\f\2\2"+
    "\u00c1\u00c2\7+\2\2\u00c2\'\3\2\2\2\u00c3\u00c4\7\r\2\2\u00c4\u00c5\7"+
    "+\2\2\u00c5)\3\2\2\2\u00c6\u00c8\7\20\2\2\u00c7\u00c9\5.\30\2\u00c8\u00c7"+
    "\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9\u00ca\3\2\2\2\u00ca\u00cb\7+\2\2\u00cb"+
    "+\3\2\2\2\u00cc\u00cd\5.\30\2\u00cd\u00ce\7+\2\2\u00ce-\3\2\2\2\u00cf"+
    "\u00d0\5\60\31\2\u00d0\u00d1\7\36\2\2\u00d1\u00d2\5.\30\2\u00d2\u00d5"+
    "\3\2\2\2\u00d3\u00d5\5\60\31\2\u00d4\u00cf\3\2\2\2\u00d4\u00d3\3\2\2\2"+
    "\u00d5/\3\2\2\2\u00d6\u00d7\b\31\1\2\u00d7\u00d8\5\62\32\2\u00d8\u00de"+
    "\3\2\2\2\u00d9\u00da\f\4\2\2\u00da\u00db\7\32\2\2\u00db\u00dd\5\62\32"+
    "\2\u00dc\u00d9\3\2\2\2\u00dd\u00e0\3\2\2\2\u00de\u00dc\3\2\2\2\u00de\u00df"+
    "\3\2\2\2\u00df\61\3\2\2\2\u00e0\u00de\3\2\2\2\u00e1\u00e2\b\32\1\2\u00e2"+
    "\u00e3\5\64\33\2\u00e3\u00e9\3\2\2\2\u00e4\u00e5\f\4\2\2\u00e5\u00e6\7"+
    "!\2\2\u00e6\u00e8\5\64\33\2\u00e7\u00e4\3\2\2\2\u00e8\u00eb\3\2\2\2\u00e9"+
    "\u00e7\3\2\2\2\u00e9\u00ea\3\2\2\2\u00ea\63\3\2\2\2\u00eb\u00e9\3\2\2"+
    "\2\u00ec\u00ed\5\66\34\2\u00ed\u00ee\t\3\2\2\u00ee\u00ef\5\66\34\2\u00ef"+
    "\u00f2\3\2\2\2\u00f0\u00f2\5\66\34\2\u00f1\u00ec\3\2\2\2\u00f1\u00f0\3"+
    "\2\2\2\u00f2\65\3\2\2\2\u00f3\u00f4\58\35\2\u00f4\u00f5\t\4\2\2\u00f5"+
    "\u00f6\58\35\2\u00f6\u00f9\3\2\2\2\u00f7\u00f9\58\35\2\u00f8\u00f3\3\2"+
    "\2\2\u00f8\u00f7\3\2\2\2\u00f9\67\3\2\2\2\u00fa\u00fb\b\35\1\2\u00fb\u00fc"+
    "\5:\36\2\u00fc\u0102\3\2\2\2\u00fd\u00fe\f\4\2\2\u00fe\u00ff\t\5\2\2\u00ff"+
    "\u0101\5:\36\2\u0100\u00fd\3\2\2\2\u0101\u0104\3\2\2\2\u0102\u0100\3\2"+
    "\2\2\u0102\u0103\3\2\2\2\u01039\3\2\2\2\u0104\u0102\3\2\2\2\u0105\u0106"+
    "\b\36\1\2\u0106\u0107\5<\37\2\u0107\u010d\3\2\2\2\u0108\u0109\f\4\2\2"+
    "\u0109\u010a\t\6\2\2\u010a\u010c\5<\37\2\u010b\u0108\3\2\2\2\u010c\u010f"+
    "\3\2\2\2\u010d\u010b\3\2\2\2\u010d\u010e\3\2\2\2\u010e;\3\2\2\2\u010f"+
    "\u010d\3\2\2\2\u0110\u0111\t\7\2\2\u0111\u0114\5<\37\2\u0112\u0114\5>"+
    " \2\u0113\u0110\3\2\2\2\u0113\u0112\3\2\2\2\u0114=\3\2\2\2\u0115\u0116"+
    "\5@!\2\u0116\u0117\7%\2\2\u0117\u0118\5.\30\2\u0118\u0119\7&\2\2\u0119"+
    "\u011c\3\2\2\2\u011a\u011c\5@!\2\u011b\u0115\3\2\2\2\u011b\u011a\3\2\2"+
    "\2\u011c?\3\2\2\2\u011d\u011e\7)\2\2\u011e\u011f\5.\30\2\u011f\u0120\7"+
    "*\2\2\u0120\u0128\3\2\2\2\u0121\u0128\7-\2\2\u0122\u0128\7\3\2\2\u0123"+
    "\u0128\7\6\2\2\u0124\u0128\7\4\2\2\u0125\u0128\7\5\2\2\u0126\u0128\5B"+
    "\"\2\u0127\u011d\3\2\2\2\u0127\u0121\3\2\2\2\u0127\u0122\3\2\2\2\u0127"+
    "\u0123\3\2\2\2\u0127\u0124\3\2\2\2\u0127\u0125\3\2\2\2\u0127\u0126\3\2"+
    "\2\2\u0128A\3\2\2\2\u0129\u012a\7-\2\2\u012a\u012b\7)\2\2\u012b\u012c"+
    "\5D#\2\u012c\u012d\7*\2\2\u012dC\3\2\2\2\u012e\u0130\5.\30\2\u012f\u012e"+
    "\3\2\2\2\u012f\u0130\3\2\2\2\u0130\u013a\3\2\2\2\u0131\u0136\5.\30\2\u0132"+
    "\u0133\7,\2\2\u0133\u0135\5.\30\2\u0134\u0132\3\2\2\2\u0135\u0138\3\2"+
    "\2\2\u0136\u0134\3\2\2\2\u0136\u0137\3\2\2\2\u0137\u013a\3\2\2\2\u0138"+
    "\u0136\3\2\2\2\u0139\u012f\3\2\2\2\u0139\u0131\3\2\2\2\u013aE\3\2\2\2"+
    "\35JL]bv}\u0080\u0086\u0090\u0096\u00a1\u00aa\u00b0\u00c8\u00d4\u00de"+
    "\u00e9\u00f1\u00f8\u0102\u010d\u0113\u011b\u0127\u012f\u0136\u0139";
  public static final ATN _ATN =
    new ATNDeserializer().deserialize(_serializedATN.toCharArray());
  static {
    _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
    for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
      _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
    }
  }
}*/








