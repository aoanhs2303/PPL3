package mc.checker

/**
 * @author nhphung
 */

import mc.parser._
import mc.utils._
import java.io. {
	File,
	PrintWriter
}

//import mc.codegen.Val
import org.antlr.v4.runtime.ANTLRFileStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree._
import scala.collection.JavaConverters._

//Symbol
case class Symbol(name: String, typ: Type, value: Val)
trait Val
case class FunctionType(input: List[Type], output: Type) extends Type

class StaticChecker(ast: AST) extends BaseVisitor with Utils {

	def check() = visit(ast, null)

	// *****SYMBOL*****

	def listSymbol(declst: List[Decl], symlst: Any, kind: Kind) = {
		declst.foldLeft(symlst.asInstanceOf[List[Symbol]])((lst, decl) => {
			val sym = toSymbol(decl)
			lookup(sym.name, lst, (s: Symbol) => s.name) match {
				case None => sym::lst
				case Some(_) => sym.typ match {
					case FunctionType(_, _) => throw Redeclared(Function, sym.name)
					case _ => kind match {
						case Parameter => throw Redeclared(Parameter, sym.name)
						case _ => throw Redeclared(Variable, sym.name)
					}
				}
			}
		})
	}

	def toSymbol(decl: Decl) = decl match {
		case VarDecl(n, vt) => Symbol(n.name, vt, null)
		case FuncDecl(n, p, rt, _) => Symbol(n.name, FunctionType(p.map(_.varType), rt), null)
	}


	override def visitProgram(ast: Program, c: Any): Any = {
		//checkRepeatDecl(ast.decl)
		//ast.decl.filter(_.isInstanceOf[FuncDecl]).map(_.accept(this, ))
		val symlst = listSymbol(ast.decl,List(),null)
		
		ast.decl.map(x =>
			if (x.isInstanceOf[FuncDecl]) x.accept(this, symlst))
		//ast.decl.accept(this, ast.decl)
		//ast.decl.map(x => if(x.isInstanceOf[FuncDecl]) x.accept(this, ast.decl))
	}

	override def visitVarDecl(ast: VarDecl, c: Any): Any = {
		//ast.variable.accept(this,c)
		//ast.varType
	}

	override def visitFuncDecl(ast: FuncDecl, c: Any): Any = {
		val paramlst = listSymbol(ast.param,List(),Parameter) // parameter
		val isReturn = visit(ast.body, List(ast.returnType, c, paramlst)) // di vao visitBlock

		
		if(isReturn == false && ast.returnType != VoidType) throw FunctionNotReturn(ast.name.name)

	}

	override def visitBlock(ast: Block, c: Any): Any = {
		val argList = c.asInstanceOf[List[Any]]

		val localst = listSymbol(ast.decl,if(argList.size == 3) argList(2) else List() ,null)

		val returnType = argList(0)

		val glolst = argList(1).asInstanceOf[List[Symbol]]
		val lst = List(returnType, localst:::glolst)
		//ast.stmt.foreach((x: Stmt) => x.accept(this, lst))
		ast.stmt.exists(_.accept(this, lst) == true)


	}

	override def visitBinaryOp(ast: BinaryOp, c: Any): Any = {

		val ltype = ast.left.accept(this, c) 
		val rtype = ast.right.accept(this, c)

		(ast.op,ltype,rtype) match {
			case ("+"|"-"|"*"|"/",IntType,IntType) => IntType
			case ("+"|"-"|"*"|"/",FloatType,IntType) => FloatType
			case ("+"|"-"|"*"|"/",IntType,FloatType) => FloatType
			case ("+"|"-"|"*"|"/",FloatType,FloatType) => FloatType
			case ("%",IntType,IntType) => IntType
			case (">"|">="|"<"|"<=",IntType|FloatType,IntType|FloatType) => BoolType
			case ("=="|"!=",IntType,IntType) => BoolType
			case ("=="|"!=",BoolType,BoolType) => BoolType
			case ("&&"|"||",BoolType,BoolType) => BoolType
			case ("=",IntType,IntType) => IntType
			case ("=",FloatType,IntType|FloatType) => FloatType
			case ("=",BoolType,BoolType) => BoolType
			case ("=",StringType,StringType) => StringType

			case _ => throw TypeMismatchInExpression(ast)
		}

	}
	override def visitUnaryOp(ast: UnaryOp, c: Any): Any = {
		//println("Unary:"+c)
		val body = ast.body.accept(this, c)
		(ast.op, body) match {
			case ("!", BoolType) => BoolType
			case ("-", IntType) => IntType
			case ("-", FloatType) => FloatType

			case _ => throw TypeMismatchInExpression(ast)
		}
	}
	override def visitCallExpr(ast: CallExpr, c: Any): Any = {
		val name = ast.method.name
		//println("LIST DECL: " + c)
		val argList = c.asInstanceOf[List[Any]]
		val symlst = argList(0).asInstanceOf[List[Symbol]]
		//println("AAA: " + name)

		checkUndeclaredFunc(name, symlst)

	}
	override def visitArrayCell(ast: ArrayCell, c: Any): Any = {
		val arrtype = ast.arr.accept(this, c)
		val index = ast.idx.accept(this, c)
		(arrtype,index) match {
			case (ArrayType(_,eleType),IntType) =>	eleType
			case (ArrayPointerType(eleType),IntType) =>	eleType

			case _ => throw TypeMismatchInExpression(ast)
		}
	}

	override def visitId(ast: Id, c: Any): Any = {
		println("id")
		val argList = c.asInstanceOf[List[Any]]
		val symlst = argList(1).asInstanceOf[List[Symbol]]

		checkUndeclared(ast.name, symlst)

	}


	def checkRepeatDecl(lst: List[Decl]): Unit = {
		lst match {
			case List() => Unit
			case head::tail => {
				val func = (x: Decl) => x match {
					case VarDecl(n, _) => n.name
					case FuncDecl(n, _, _, _) => n.name
				}
				val tmp = lookup(func(head), tail, func)
				if (tmp != None) {
					if (tmp.get.isInstanceOf[VarDecl])
						throw Redeclared(Variable, func(tmp.get))
					else
						throw Redeclared(Function, func(tmp.get))
				} else
					checkRepeatDecl(tail)
			}
		}
	}

	def checkRepeatParam(param: List[VarDecl]): Unit = {
		param match {
			case List() => Unit
			case head::tail => {
				val func = (x: VarDecl) => x.variable.name
				val tmp = lookup(func(head), tail, func)
				if (tmp != None) {
					throw Redeclared(Parameter, func(tmp.get))
				} else checkRepeatParam(tail)
			}
		}
	}

	def checkUndeclared(name: String, lst: List[Symbol]) = {
		val func = (x: Symbol) => x.name
		val tmp = lookup(name, lst, func)
		if (tmp == None) {
			throw Undeclared(Identifier, name)
		} else if(tmp.get.typ.isInstanceOf[FunctionType]) {
			throw Undeclared(Identifier, name)
		} else {
			tmp.get.typ
		}

	}

	def checkUndeclaredFunc(nameFunc: String, lst: List[Symbol]): Unit = {
		val func = (x: Symbol) => x.name
		val tmp = lookup(nameFunc, lst, func)
		if (tmp == None) {
			throw Undeclared(Function, nameFunc)
		}
		else if(!tmp.get.typ.isInstanceOf[FunctionType]){
			throw Undeclared(Function, nameFunc)			
		}
		else tmp.get.typ.asInstanceOf[FunctionType].output
	}


	//override def visitFuncDecl(ast: FuncDecl, c: Any): Any = null
	override def visitIntType(ast: IntType.type, c: Any): Any = null
	override def visitFloatType(ast: FloatType.type, c: Any): Any = null
	override def visitBoolType(ast: BoolType.type, c: Any): Any = null
	override def visitStringType(ast: StringType.type, c: Any): Any = null
	override def visitVoidType(ast: VoidType.type, c: Any): Any = null
	override def visitArrayType(ast: ArrayType, c: Any): Any = null
	override def visitArrayPointerType(ast: ArrayPointerType, c: Any): Any = null

	//override def visitBlock(ast: Block, c: Any): Any = null
	override def visitIf(ast: If, c: Any): Any = {
		//println("IF: " + c);
		val exptype = ast.expr.accept(this, c);
		if(exptype != BoolType) throw TypeMismatchInStatement(ast)

		val thenRt = ast.thenStmt.accept(this, c)
		val elseRt = ast.elseStmt
		if(thenRt != true || elseRt == None) false
		else if(elseRt.get.accept(this, c) == true ) true
		else false
	}
	override def visitFor(ast: For, c: Any): Any = {
		val expr1 = ast.expr1.accept(this, c);
		val expr2 = ast.expr2.accept(this, c);
		val expr3 = ast.expr3.accept(this, c);

		(expr1, expr2, expr3) match {
			case (IntType, BoolType, IntType) => null //can thi sua lai
			case _ => throw TypeMismatchInStatement(ast)
		}
		false
	}
	override def visitBreak(ast: Break.type, c: Any): Any = null
	override def visitContinue(ast: Continue.type, c: Any): Any = null

	override def visitReturn(ast: Return, c: Any): Any = {
		val argList = c.asInstanceOf[List[Any]]
		val returnType = argList(0).asInstanceOf[Type]
		//Da lay duoc kieu
	
		if(ast.expr == None && returnType == VoidType) {
			null
		} else if (ast.expr == None && returnType != VoidType) {
			throw TypeMismatchInStatement(ast)
		}
		else {
			val returnX =  ast.expr.get.accept(this, c)
			(returnX, returnType) match {
				case (IntType, IntType) => null
				case (FloatType, FloatType|IntType) => null
				case (BoolType, BoolType) => null
				case (StringType, StringType) => null

				case (ArrayPointerType(t1), ArrayPointerType(t2)) => (t1, t2) match {
					case (IntType, IntType) => null
					case (FloatType, FloatType) => null
					case (BoolType, BoolType) => null
					case (StringType, StringType) => null

					case _ => throw TypeMismatchInStatement(ast)

				}
				case (ArrayPointerType(t1), ArrayType(_,t2)) => (t1, t2) match {
					case (IntType,IntType) => null
                    case (FloatType,FloatType) => null
                    case (BoolType, BoolType) =>null
                    case (StringType, StringType) => null

                    case _ => throw TypeMismatchInStatement(ast)
				}

				case _ => throw TypeMismatchInStatement(ast)
			}	

		}

	}

	override def visitDowhile(ast: Dowhile, c: Any): Any = {
		val exprtype = ast.exp.accept(this, c);
		if(exprtype != BoolType) throw TypeMismatchInStatement(ast)
		ast.sl.exists(_.accept(this,c) == true)
	}

	override def visitIntLiteral(ast: IntLiteral, c: Any): Any = IntType
	override def visitFloatLiteral(ast: FloatLiteral, c: Any): Any = FloatType
	override def visitStringLiteral(ast: StringLiteral, c: Any): Any = StringType
	override def visitBooleanLiteral(ast: BooleanLiteral, c: Any): Any = BoolType


}