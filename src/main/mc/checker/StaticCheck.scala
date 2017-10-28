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



class StaticChecker(ast: AST) extends BaseVisitor with Utils {

	def check() = visit(ast, null)
	override def visitProgram(ast: Program, c: Any): Any = {
		checkRepeatDecl(ast.decl)
		//ast.decl.filter(_.isInstanceOf[FuncDecl]).map(_.accept(this, ))
		ast.decl.map(x => if(x.isInstanceOf[FuncDecl]) x.accept(this, ast.decl))
		//ast.decl.accept(this, ast.decl)
		//ast.decl.map(x => if(x.isInstanceOf[FuncDecl]) x.accept(this, ast.decl))
	}

	override def visitVarDecl(ast: VarDecl, c: Any): Any = {
		//ast.variable.accept(this,c)
		ast.varType
	}

	override def visitFuncDecl(ast: FuncDecl, c: Any): Any = {
		//ast.name.accept(this, c)
		//ast.param
		//ast.returnType
		//ast.body
		//val listDecl = c.asInstanceOf[List[Decl]]
		//println("DMM: " + c)
		//val nameFunc = c.asInstanceOf[List[FuncDecl]]
		//println("BBBB: " + nameFunc)
		checkRepeatParam(ast.param)
		visit(ast.body, ast.param) // di vao visitBlock
		
		//checkUndeclaredFunc(ast.name.name, c);
		//visit(ast, ast.name) // di vao ID
		//ast.name.foreach(_.accept(this, null))
	}
	
	override def visitBlock(ast: Block, c: Any): Any = {
		val param = c.asInstanceOf[List[VarDecl]]
		val local = param:::ast.decl.asInstanceOf[List[VarDecl]]
		checkRepeatDecl(local)
		//ast.stmt.foreach(_.accept(this,List(ReturnType,List[decl],.....)))
		//println(ast.stmt)
		ast.stmt.foreach(_.accept(this,local))
		// checkUndeclared(ast.stmt, ast.decl.asInstanceOf[List[VarDecl]])
	}

	override def visitBinaryOp(ast: BinaryOp, c: Any): Any ={
		println("BinOp:"+c)
		ast.left.accept(this,c)
		ast.right.accept(this,c)

	}
	override def visitUnaryOp(ast: UnaryOp, c: Any): Any = {
		//println("Unary:"+c)
		ast.body.accept(this,c)
	}
	override def visitCallExpr(ast: CallExpr, c: Any): Any = {
		val name = ast.method.name
		println("LIST DECL: " + c)
		val listDecl = c.asInstanceOf[List[Decl]]
		//checkUndeclaredFunc(name, listDecl)
	}
	override def visitArrayCell(ast: ArrayCell, c: Any): Any = {
		ast.arr.accept(this,c)

	}

	override def visitId(ast: Id, c: Any): Any = {
		//println("Id:"+c)
		val argList = c.asInstanceOf[List[VarDecl]]

		//println("AAA: " + argList)
		//val funcList = c.asInstanceOf[List[FuncDecl]]
		

		//val enviList = argList(1)
		checkUndeclared(ast.name, argList)
		//checkUndeclaredFunc(ast.name, c)
	}


	def checkRepeatDecl(lst: List[Decl]):Unit = {
		lst match {
			case List() => Unit
			case head::tail => {
				val func = (x: Decl) => x match {
					case VarDecl(n,_) => n.name
					case FuncDecl(n,_,_,_) => n.name
				}
				val tmp = lookup(func(head), tail, func)
				if(tmp != None) {
					if(tmp.get.isInstanceOf[VarDecl])
						throw Redeclared(Variable, func(tmp.get))
					else
						throw Redeclared(Function, func(tmp.get))
				}
				else
					checkRepeatDecl(tail)
			}
		}
	}

	def checkRepeatParam(param: List[VarDecl]):Unit = {
		param match {
			case List() => Unit
			case head::tail => {
				val func = (x: VarDecl) => x.variable.name
				val tmp = lookup(func(head), tail, func)
				if(tmp != None){
					throw Redeclared(Parameter, func(tmp.get))
				} else checkRepeatParam(tail)
			}
		}
	}

	def checkUndeclared(name: String, vari: List[VarDecl]):Unit = {
		val func = (x: VarDecl) => x.variable.name
		val tmp = lookup(name, vari, func)
		if(tmp == None) {
			throw Undeclared(Identifier, name)
		}
	}	

	def checkUndeclaredFunc(nameFunc: String, listFunc: List[Decl]):Unit = {
		val func = (x: Decl) => x match {
			case FuncDecl(n,_,_,_) => n.name
		}
		val tmp = lookup(nameFunc, listFunc, func)
		if (tmp == None) {
			throw Undeclared(Function, nameFunc)
		}
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
	override def visitIf(ast: If, c: Any): Any = null
	override def visitFor(ast: For, c: Any): Any = null
	override def visitBreak(ast: Break.type, c: Any): Any = null
	override def visitContinue(ast: Continue.type, c: Any): Any = null
	override def visitReturn(ast: Return, c: Any): Any = null
	override def visitDowhile(ast: Dowhile, c: Any): Any = null
	override def visitIntLiteral(ast: IntLiteral, c: Any): Any = null
	override def visitFloatLiteral(ast: FloatLiteral, c: Any): Any = null
	override def visitStringLiteral(ast: StringLiteral, c: Any): Any = null
	override def visitBooleanLiteral(ast: BooleanLiteral, c: Any): Any = null


}