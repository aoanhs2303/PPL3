import org.scalatest.FunSuite
import mc.utils._

/**
  * Created by nhphung on 4/29/17.
  * Modified by Tran Nhu Luc - 1511918
  */

class AstSui
 extends FunSuite with TestAst {

  test("Test__case 01: Declare integer variable") {
    val input = """int myVar;"""
    val expected = Program(List(VarDecl(Id("myVar"),IntType)))
    assert(checkAst(input,expected,201))
  }

  test("Test__case 02: Declare float variable") {
    val input = """float myVar;"""
    val expected = Program(List(VarDecl(Id("myVar"),FloatType)))
    assert(checkAst(input,expected,202))
  }

  test("Test__case 03: Declare array type variable") {
    val input = """boolean arr[3];"""
    val expected = Program(List(VarDecl(Id("arr"),ArrayType(IntLiteral(3),BoolType))))
    assert(checkAst(input,expected,203))
  }

  test("Test__case 04: Declare many variables in same type") {
    val input = """boolean a,b,arr[3];"""
    val expected = Program(List(VarDecl(Id("a"),BoolType),VarDecl(Id("b"),BoolType),VarDecl(Id("arr"),ArrayType(IntLiteral(3),BoolType))))
    assert(checkAst(input,expected,204))
  }

  test("Test__case 05: Declare all type of variable") {
    val input = """int a; float b; boolean c; string d;"""
    val expected = Program(List(VarDecl(Id("a"),IntType),VarDecl(Id("b"),FloatType),VarDecl(Id("c"),BoolType),VarDecl(Id("d"),StringType)))
    assert(checkAst(input,expected,205))
  }

  test("Test__case 06: declare 2 variable in multiple line") {
    val input = """int i;
                   float f;
                   string str;
                   boolean b;
                   string arr[3];"""
    val expected = Program(List(VarDecl(Id("i"),IntType),VarDecl(Id("f"),FloatType),VarDecl(Id("str"),StringType),VarDecl(Id("b"),BoolType),VarDecl(Id("arr"),ArrayType(IntLiteral(3),StringType))))
    assert(checkAst(input,expected,206))
  }

  test ("Test__case 07: Complex variable declaration") { 
    val input  = """  int __x1, y[2];
              string x, y[99], z;
              int x[2]; """
    val expect = Program(List(VarDecl(Id("__x1"),IntType),VarDecl(Id("y"),ArrayType(IntLiteral(2),IntType)),VarDecl(Id("x"),StringType),VarDecl(Id("y"),ArrayType(IntLiteral(99),StringType)),VarDecl(Id("z"),StringType),VarDecl(Id("x"),ArrayType(IntLiteral(2),IntType))))
    assert(checkAst(input,expect,207))
  }

  test("Test__case 08: Declare and assign an integer variable") {
    val input = """int main(){}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List()))))
    assert(checkAst(input,expected,208))
  }

  test("Test__case 09: Declare function 1 paramater no body") {
    val input = """float main(string a){}"""
    val expected = Program(List(FuncDecl(Id("main"),List(VarDecl(Id("a"),StringType)),FloatType,Block(List(),List()))))
    assert(checkAst(input,expected,209))
  }

  test("Test__case 10: Declare function with 1 paramater no body") {
    val input = """string main(int c[]){}"""
    val expected = Program(List(FuncDecl(Id("main"),List(VarDecl(Id("c"),ArrayPointerType(IntType))),StringType,Block(List(),List()))))
    assert(checkAst(input,expected,210))
  }

  test("Test__case 11: Declare function with many paramater no body") {
    val input = """boolean main(string str, boolean b){}"""
    val expected = Program(List(FuncDecl(Id("main"),List(VarDecl(Id("str"),StringType),VarDecl(Id("b"),BoolType)),BoolType,Block(List(),List()))))
    assert(checkAst(input,expected,211))
  }

  test("Test__case 12: declare function with many paramater no body") {
    val input = """string main(int a, boolean b[], float f, string str){}"""
    val expected = Program(List(FuncDecl(Id("main"),List(VarDecl(Id("a"),IntType),VarDecl(Id("b"),ArrayPointerType(BoolType)),VarDecl(Id("f"),FloatType),VarDecl(Id("str"),StringType)),StringType,Block(List(),List()))))
    assert(checkAst(input,expected,212))
  }

  test("Test__case 13: Function with integer declare") {
    val input = """int[] main(){int myVar;}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),ArrayPointerType(IntType),Block(List(VarDecl(Id("myVar"),IntType)),List()))))
  }

  test("Test__case 14: Function with arr declare") {
    val input = """float[] main(string a[]){boolean b[5];}"""
    val expected = Program(List(FuncDecl(Id("main"),List(VarDecl(Id("a"),ArrayPointerType(StringType))),ArrayPointerType(FloatType),Block(List(VarDecl(Id("b"),ArrayType(IntLiteral(5),BoolType))),List()))))
    assert(checkAst(input,expected,214))
  }

  test("Test__case 15: Function with complex declare") {
    val input = """float main(){int a; float b; string s; boolean b[3];}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),FloatType,Block(List(VarDecl(Id("a"),IntType),VarDecl(Id("b"),FloatType),VarDecl(Id("s"),StringType),VarDecl(Id("b"),ArrayType(IntLiteral(3),BoolType))),List()))))
    assert(checkAst(input,expected,215))
  }

  test ("Test__case 16: Complex simple function declaration") { 
    val input  = """  void main(){}
              int main(){}
              string main(){}
              boolean main(){}
              float main(){} """
    val expected = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List())),FuncDecl(Id("main"),List(),IntType,Block(List(),List())),FuncDecl(Id("main"),List(),StringType,Block(List(),List())),FuncDecl(Id("main"),List(),BoolType,Block(List(),List())),FuncDecl(Id("main"),List(),FloatType,Block(List(),List()))))
    assert(checkAst(input,expected,216))
  }

  test ("Test__case 17: Declare array return function") { 
    val input  = """  int [] main(){}
              int [] foo(){}
              float [] foo(){}
              string [] foo(){}
              boolean [] foo(){} """
    val expected = Program(List(FuncDecl(Id("main"),List(),ArrayPointerType(IntType),Block(List(),List())),FuncDecl(Id("foo"),List(),ArrayPointerType(IntType),Block(List(),List())),FuncDecl(Id("foo"),List(),ArrayPointerType(FloatType),Block(List(),List())),FuncDecl(Id("foo"),List(),ArrayPointerType(StringType),Block(List(),List())),FuncDecl(Id("foo"),List(),ArrayPointerType(BoolType),Block(List(),List()))))
    assert(checkAst(input,expected,217))
  }

  test ("Test__case 18: Function declarations complicate") { 
    val input  = """  void main(){}
              int [] main(){}
              int foo(){}
              int []foo(){}
              string foo(){}
              string [] foo(){}
              boolean foo(){}
              boolean []foo(){}
              float foo(){}
              float []foo(){} """
    val expected = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List())),FuncDecl(Id("main"),List(),ArrayPointerType(IntType),Block(List(),List())),FuncDecl(Id("foo"),List(),IntType,Block(List(),List())),FuncDecl(Id("foo"),List(),ArrayPointerType(IntType),Block(List(),List())),FuncDecl(Id("foo"),List(),StringType,Block(List(),List())),FuncDecl(Id("foo"),List(),ArrayPointerType(StringType),Block(List(),List())),FuncDecl(Id("foo"),List(),BoolType,Block(List(),List())),FuncDecl(Id("foo"),List(),ArrayPointerType(BoolType),Block(List(),List())),FuncDecl(Id("foo"),List(),FloatType,Block(List(),List())),FuncDecl(Id("foo"),List(),ArrayPointerType(FloatType),Block(List(),List()))))
    assert(checkAst(input,expected,218))
  }

  test("Test__case 19: A simple function with comment inside") {
    val input = """void main() {float foo[3]; /*this is a comment*/}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(VarDecl(Id("foo"),ArrayType(IntLiteral(3),FloatType))),List()))))
    assert(checkAst(input,expected,219))
  }

  test("Test__case 20: Function have return operator") {
    val input = """void main(){a = c + 1; return a;}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List(BinaryOp("=",Id("a"),BinaryOp("+",Id("c"),IntLiteral(1))),Return(Some(Id("a"))))))))
    assert(checkAst(input,expected,220))
  }

  test("Test__case 21: Function hava all unary operator") {
    val input = """void main() {int i; boolean b; b = -i; b = 2; !i;}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(VarDecl(Id("i"),IntType),VarDecl(Id("b"),BoolType)),List(BinaryOp("=",Id("b"),UnaryOp("-",Id("i"))),BinaryOp("=",Id("b"),IntLiteral(2)),UnaryOp("!",Id("i")))))))
    assert(checkAst(input,expected,221))
  }

  test("Test__case 22: Add operator") {
    val input = """int main () {add = (a + 2) + 100;}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(BinaryOp("=",Id("add"),BinaryOp("+",BinaryOp("+",Id("a"),IntLiteral(2)),IntLiteral(100))))))))
    assert(checkAst(input,expected,222))
  }

  test("Test__case 23: Sub operator") {
    val input = """int main () {sub = 100 - (a - b) + (c); }"""
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(BinaryOp("=",Id("sub"),BinaryOp("+",BinaryOp("-",IntLiteral(100),BinaryOp("-",Id("a"),Id("b"))),Id("c"))))))))
    assert(checkAst(input,expected,223))
  }

  test("Test__case 24: Mul operator") {
    val input = """int main () {mul = mul * 2 * 3 * (x + 4) * 5;}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(BinaryOp("=",Id("mul"),BinaryOp("*",BinaryOp("*",BinaryOp("*",BinaryOp("*",Id("mul"),IntLiteral(2)),IntLiteral(3)),BinaryOp("+",Id("x"),IntLiteral(4))),IntLiteral(5))))))))
    assert(checkAst(input,expected,224))
  }

  test("Test__case 25: Modulo operator") {
    val input = """int main () {mod = 1000 % 100 % 10 % (x % 2);}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(BinaryOp("=",Id("mod"),BinaryOp("%",BinaryOp("%",BinaryOp("%",IntLiteral(1000),IntLiteral(100)),IntLiteral(10)),BinaryOp("%",Id("x"),IntLiteral(2)))))))))
    assert(checkAst(input,expected,225))
  }

  test("Test__case 26: Assign operator") {
    val input = """int main () {a = b = c = z;}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(BinaryOp("=",Id("a"),BinaryOp("=",Id("b"),BinaryOp("=",Id("c"),Id("z")))))))))
    assert(checkAst(input,expected,226))
  }

  test("Test__case 27: Equal operator") {
    val input = """int main () {i = true == false;}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(BinaryOp("=",Id("i"),BinaryOp("==",BooleanLiteral(true),BooleanLiteral(false))))))))
    assert(checkAst(input,expected,227))
  }

  test("Test__case 28: Not equal operator") {
    val input = """int main () {i = true != false;}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(BinaryOp("=",Id("i"),BinaryOp("!=",BooleanLiteral(true),BooleanLiteral(false))))))))
    assert(checkAst(input,expected,228))
  }

  test("Test__case 29: > operator") {
    val input = """int main () {i = 100 > 10;}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(BinaryOp("=",Id("i"),BinaryOp(">",IntLiteral(100),IntLiteral(10))))))))
    assert(checkAst(input,expected,229))
  }

  test("Test__case 30: >= operator") {
    val input = """int main () {i = 100 >= 10;}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(BinaryOp("=",Id("i"),BinaryOp(">=",IntLiteral(100),IntLiteral(10))))))))
    assert(checkAst(input,expected,230))
  }

  test("Test__case 31: < operator") {
    val input = """int main () {i = 10 < 100;}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(BinaryOp("=",Id("i"),BinaryOp("<",IntLiteral(10),IntLiteral(100))))))))
    assert(checkAst(input,expected,231))
  }

  test("Test__case 32: <= operator") {
    val input = """int main () {i = 10 <= 100;}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(BinaryOp("=",Id("i"),BinaryOp("<=",IntLiteral(10),IntLiteral(100))))))))
    assert(checkAst(input,expected,232))
  }

  test("Test__case 33: AND operator") {
    val input = """int main () {i = true && false;}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(BinaryOp("=",Id("i"),BinaryOp("&&",BooleanLiteral(true),BooleanLiteral(false))))))))
    assert(checkAst(input,expected,233))
  }

  test("Test__case 34: OR operator") {
    val input = """int main () {i = true || false;}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(BinaryOp("=",Id("i"),BinaryOp("||",BooleanLiteral(true),BooleanLiteral(false))))))))
    assert(checkAst(input,expected,234))
  }

  test ("Test__case 35: Precedence of operator (>,<,>=,<=) ") { 
    val input  = """  void main(){
                x1 > x2 = x3; x1 < x2 = x3; x1 >= x2 = x3; x1 <= x2 = x3;
                x1 > x2 || x3; x1 < x2 || x3; x1 >= x2 || x3; x1 <= x2 || x3;
                x1 > x2 && x3; x1 < x2 && x3; x1 >= x2 && x3; x1 <= x2 && x3;
                x1 > x2 == x3; x1 < x2 == x3;x1 >= x2 == x3;x1 <= x2 == x3;
                x1 > x2 != x3;x1 < x2 != x3;x1 >= x2 != x3;x1 <= x2 != x3; 
              } 

              int [] main(){
                x1> x2 = x3 || x4 && x5 == x6 >= x7;
                x1<= x2 = x3 || x4 && x5 != x6 < x7;
              }"""
    val expect = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List(BinaryOp("=",BinaryOp(">",Id("x1"),Id("x2")),Id("x3")),BinaryOp("=",BinaryOp("<",Id("x1"),Id("x2")),Id("x3")),BinaryOp("=",BinaryOp(">=",Id("x1"),Id("x2")),Id("x3")),BinaryOp("=",BinaryOp("<=",Id("x1"),Id("x2")),Id("x3")),BinaryOp("||",BinaryOp(">",Id("x1"),Id("x2")),Id("x3")),BinaryOp("||",BinaryOp("<",Id("x1"),Id("x2")),Id("x3")),BinaryOp("||",BinaryOp(">=",Id("x1"),Id("x2")),Id("x3")),BinaryOp("||",BinaryOp("<=",Id("x1"),Id("x2")),Id("x3")),BinaryOp("&&",BinaryOp(">",Id("x1"),Id("x2")),Id("x3")),BinaryOp("&&",BinaryOp("<",Id("x1"),Id("x2")),Id("x3")),BinaryOp("&&",BinaryOp(">=",Id("x1"),Id("x2")),Id("x3")),BinaryOp("&&",BinaryOp("<=",Id("x1"),Id("x2")),Id("x3")),BinaryOp("==",BinaryOp(">",Id("x1"),Id("x2")),Id("x3")),BinaryOp("==",BinaryOp("<",Id("x1"),Id("x2")),Id("x3")),BinaryOp("==",BinaryOp(">=",Id("x1"),Id("x2")),Id("x3")),BinaryOp("==",BinaryOp("<=",Id("x1"),Id("x2")),Id("x3")),BinaryOp("!=",BinaryOp(">",Id("x1"),Id("x2")),Id("x3")),BinaryOp("!=",BinaryOp("<",Id("x1"),Id("x2")),Id("x3")),BinaryOp("!=",BinaryOp(">=",Id("x1"),Id("x2")),Id("x3")),BinaryOp("!=",BinaryOp("<=",Id("x1"),Id("x2")),Id("x3"))))),FuncDecl(Id("main"),List(),ArrayPointerType(IntType),Block(List(),List(BinaryOp("=",BinaryOp(">",Id("x1"),Id("x2")),BinaryOp("||",Id("x3"),BinaryOp("&&",Id("x4"),BinaryOp("==",Id("x5"),BinaryOp(">=",Id("x6"),Id("x7")))))),BinaryOp("=",BinaryOp("<=",Id("x1"),Id("x2")),BinaryOp("||",Id("x3"),BinaryOp("&&",Id("x4"),BinaryOp("!=",Id("x5"),BinaryOp("<",Id("x6"),Id("x7")))))))))))
    assert(checkAst(input,expect,235))
  }

  test ("Test__case 36: Precedence of operator (/, *, %, *, + , -)") { 
    val input  = """  void main(){
                x1 / x2 = x3; x1 * x2 = x3; x1 % x2 = x3;
                x1 / x2 || x3; x1 + x2 || x3; x1 % x2 || x3;
                x1 / x2 && x3; x1 - x2 && x3; x1 % x2 && x3;
                x1 / x2 == x3; x1 * x2 == x3; x1 % x2 == x3;
                x1 / x2 != x3; x1 % x2 != x3; x1 % x2 != x3;
                x1 / x2 < x3; x1 * x2 < x3; x1 % x2 < x3;
                x1 / x2 > x3; x1 * x2 > x3; x1 % x2 > x3;
                x1 / x2 <= x3; x1 * x2 <= x3; x1 % x2 <= x3;
                x1 / x2 >= x3; x1 * x2 >= x3; x1 % x2 >= x3;
                x1 / x2 + x3; x1 * x2 + x3; x1 % x2 + x3;
                x1 / x2 - x3; x1 * x2 - x3; x1 % x2 - x3;
              }
              int [] main(){
                x1 / x2 * x3; x1 - x2 / x3;
                x1 / x2 % x3; x1 * x2 / x3;
                x1 * x2 % x3; x1 % x2 * x3; 

              } """
    val expect = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List(BinaryOp("=",BinaryOp("/",Id("x1"),Id("x2")),Id("x3")),BinaryOp("=",BinaryOp("*",Id("x1"),Id("x2")),Id("x3")),BinaryOp("=",BinaryOp("%",Id("x1"),Id("x2")),Id("x3")),BinaryOp("||",BinaryOp("/",Id("x1"),Id("x2")),Id("x3")),BinaryOp("||",BinaryOp("+",Id("x1"),Id("x2")),Id("x3")),BinaryOp("||",BinaryOp("%",Id("x1"),Id("x2")),Id("x3")),BinaryOp("&&",BinaryOp("/",Id("x1"),Id("x2")),Id("x3")),BinaryOp("&&",BinaryOp("-",Id("x1"),Id("x2")),Id("x3")),BinaryOp("&&",BinaryOp("%",Id("x1"),Id("x2")),Id("x3")),BinaryOp("==",BinaryOp("/",Id("x1"),Id("x2")),Id("x3")),BinaryOp("==",BinaryOp("*",Id("x1"),Id("x2")),Id("x3")),BinaryOp("==",BinaryOp("%",Id("x1"),Id("x2")),Id("x3")),BinaryOp("!=",BinaryOp("/",Id("x1"),Id("x2")),Id("x3")),BinaryOp("!=",BinaryOp("%",Id("x1"),Id("x2")),Id("x3")),BinaryOp("!=",BinaryOp("%",Id("x1"),Id("x2")),Id("x3")),BinaryOp("<",BinaryOp("/",Id("x1"),Id("x2")),Id("x3")),BinaryOp("<",BinaryOp("*",Id("x1"),Id("x2")),Id("x3")),BinaryOp("<",BinaryOp("%",Id("x1"),Id("x2")),Id("x3")),BinaryOp(">",BinaryOp("/",Id("x1"),Id("x2")),Id("x3")),BinaryOp(">",BinaryOp("*",Id("x1"),Id("x2")),Id("x3")),BinaryOp(">",BinaryOp("%",Id("x1"),Id("x2")),Id("x3")),BinaryOp("<=",BinaryOp("/",Id("x1"),Id("x2")),Id("x3")),BinaryOp("<=",BinaryOp("*",Id("x1"),Id("x2")),Id("x3")),BinaryOp("<=",BinaryOp("%",Id("x1"),Id("x2")),Id("x3")),BinaryOp(">=",BinaryOp("/",Id("x1"),Id("x2")),Id("x3")),BinaryOp(">=",BinaryOp("*",Id("x1"),Id("x2")),Id("x3")),BinaryOp(">=",BinaryOp("%",Id("x1"),Id("x2")),Id("x3")),BinaryOp("+",BinaryOp("/",Id("x1"),Id("x2")),Id("x3")),BinaryOp("+",BinaryOp("*",Id("x1"),Id("x2")),Id("x3")),BinaryOp("+",BinaryOp("%",Id("x1"),Id("x2")),Id("x3")),BinaryOp("-",BinaryOp("/",Id("x1"),Id("x2")),Id("x3")),BinaryOp("-",BinaryOp("*",Id("x1"),Id("x2")),Id("x3")),BinaryOp("-",BinaryOp("%",Id("x1"),Id("x2")),Id("x3"))))),FuncDecl(Id("main"),List(),ArrayPointerType(IntType),Block(List(),List(BinaryOp("*",BinaryOp("/",Id("x1"),Id("x2")),Id("x3")),BinaryOp("-",Id("x1"),BinaryOp("/",Id("x2"),Id("x3"))),BinaryOp("%",BinaryOp("/",Id("x1"),Id("x2")),Id("x3")),BinaryOp("/",BinaryOp("*",Id("x1"),Id("x2")),Id("x3")),BinaryOp("%",BinaryOp("*",Id("x1"),Id("x2")),Id("x3")),BinaryOp("*",BinaryOp("%",Id("x1"),Id("x2")),Id("x3")))))))
    assert(checkAst(input,expect,236))
  }

  test("Test__case 37: Multiple unary operator") {
    val input = """
    int main(){
      !!a;
      -!1;
      --1.2;
      ---false;
    }
    """
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(UnaryOp("!",UnaryOp("!",Id("a"))),UnaryOp("-",UnaryOp("!",IntLiteral(1))),UnaryOp("-",UnaryOp("-",FloatLiteral(1.2f))),UnaryOp("-",UnaryOp("-",UnaryOp("-",BooleanLiteral(false)))))))))
    assert(checkAst(input,expected,237))
  }

  test("Test__case 38: Complex index array") {
    val input = """int main()
    {
      int arr[3];
      float f;
      f = 2.2;
      return a[(i - j) * (i + j) / 2];
    }"""
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(VarDecl(Id("arr"),ArrayType(IntLiteral(3),IntType)),VarDecl(Id("f"),FloatType)),List(BinaryOp("=",Id("f"),FloatLiteral(2.2f)),Return(Some(ArrayCell(Id("a"),BinaryOp("/",BinaryOp("*",BinaryOp("-",Id("i"),Id("j")),BinaryOp("+",Id("i"),Id("j"))),IntLiteral(2))))))))))
    assert(checkAst(input,expected,238))
  }

  test("Test__case 39: Complex all of operator") {
    val input = """
    int main(){
      a + b * c / d % 2 > (e && f) || !g;
    }
    """
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(BinaryOp("||",BinaryOp(">",BinaryOp("+",Id("a"),BinaryOp("%",BinaryOp("/",BinaryOp("*",Id("b"),Id("c")),Id("d")),IntLiteral(2))),BinaryOp("&&",Id("e"),Id("f"))),UnaryOp("!",Id("g"))))))))
    assert(checkAst(input,expected,239))
  }

  test("Test__case 40: Break statement") {
    val input = """int main () {break;}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(Break)))))
    assert(checkAst(input,expected,240))
  }

  test("Test__case 41: Continue statement") {
    val input = """int main(){continue;}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(Continue)))))
    assert(checkAst(input,expected,241))
  }

  test("Test__case 42: Return statement") {
    val input = """void main () {return;}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List(Return(None))))))
    assert(checkAst(input,expected,242))
  }

  test ("Test__case 43: Combined all expression statement") { 
    val input  = """  void main(int x, int y[]){
                25/3.2e-5+"string"*boolean1== 2 > 5 && to || 2 == --7[8[8[9+2+3]]] ;
                !(2%true)---24E5*"String"==boolean2 >2[5>3] = 8 > 3; 
              } """
    val expected = Program(List(FuncDecl(Id("main"),List(VarDecl(Id("x"),IntType),VarDecl(Id("y"),ArrayPointerType(IntType))),VoidType,Block(List(),List(BinaryOp("||",BinaryOp("&&",BinaryOp("==",BinaryOp("+",BinaryOp("/",IntLiteral(25),FloatLiteral(3.2E-5F)),BinaryOp("*",StringLiteral("string"),Id("boolean1"))),BinaryOp(">",IntLiteral(2),IntLiteral(5))),Id("to")),BinaryOp("==",IntLiteral(2),UnaryOp("-",UnaryOp("-",ArrayCell(IntLiteral(7),ArrayCell(IntLiteral(8),ArrayCell(IntLiteral(8),BinaryOp("+",BinaryOp("+",IntLiteral(9),IntLiteral(2)),IntLiteral(3))))))))),BinaryOp("=",BinaryOp("==",BinaryOp("-",UnaryOp("!",BinaryOp("%",IntLiteral(2),BooleanLiteral(true))),BinaryOp("*",UnaryOp("-",UnaryOp("-",FloatLiteral(2400000.0F))),StringLiteral("String"))),BinaryOp(">",Id("boolean2"),ArrayCell(IntLiteral(2),BinaryOp(">",IntLiteral(5),IntLiteral(3))))),BinaryOp(">",IntLiteral(8),IntLiteral(3))))))))
    assert(checkAst(input,expected,243))
  }

  test("Test__case 44: A simple IF Statement"){
    val input ="""int foo() {
                    if (100 > 1) {return true;} 
                  }"""
    val expect = Program(List(FuncDecl(Id("foo"),List(),IntType,Block(List(),List(If(BinaryOp(">",IntLiteral(100),IntLiteral(1)),Block(List(),List(Return(Some(BooleanLiteral(true))))),None))))))
    assert(checkAst(input,expect,244))
  }

  test("Test__case 45: Another IF Statement"){
    val input ="""int foo() {
                    int testscore;
                    testscore = 76;
                    if (testscore >= 90) {
                      grade = "A";
                    }
                  }"""
    val expect = Program(List(FuncDecl(Id("foo"),List(),IntType,Block(List(VarDecl(Id("testscore"),IntType)),List(BinaryOp("=",Id("testscore"),IntLiteral(76)),If(BinaryOp(">=",Id("testscore"),IntLiteral(90)),Block(List(),List(BinaryOp("=",Id("grade"),StringLiteral("A")))),None))))))
    assert(checkAst(input,expect,245))
  }

  test ("Test__case 46: Simple If statement") { 
    val input  = """  void main(){
                if (x[2] > y) rename;
                if(5 - 2 == 1) x==y;
              } """
    val expect = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List(If(BinaryOp(">",ArrayCell(Id("x"),IntLiteral(2)),Id("y")),Id("rename"),None),If(BinaryOp("==",BinaryOp("-",IntLiteral(5),IntLiteral(2)),IntLiteral(1)),BinaryOp("==",Id("x"),Id("y")),None))))))
    assert(checkAst(input,expect,246))
  }

  test ("Test__case 47: Complex If statement") { 
    val input  = """  void main(){
                foo = "hello world :)";
                if(2[2]-!-2[(2)]) then; if (2>0) foo;
              } """
    val expect = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List(BinaryOp("=",Id("foo"),StringLiteral("hello world :)")),If(BinaryOp("-",ArrayCell(IntLiteral(2),IntLiteral(2)),UnaryOp("!",UnaryOp("-",ArrayCell(IntLiteral(2),IntLiteral(2))))),Id("then"),None),If(BinaryOp(">",IntLiteral(2),IntLiteral(0)),Id("foo"),None))))))
    assert(checkAst(input,expect,247))
  }

  test ("Test__case 48: Nested If statement") { 
    val input  = """  void main(){
                if(x>0)if(x % 2 == 0) Do-some-thing;
               
              } """
    val expect = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List(If(BinaryOp(">",Id("x"),IntLiteral(0)),If(BinaryOp("==",BinaryOp("%",Id("x"),IntLiteral(2)),IntLiteral(0)),BinaryOp("-",BinaryOp("-",Id("Do"),Id("some")),Id("thing")),None),None))))))
    assert(checkAst(input,expect,248))
  }

  test ("Test__case 49: IF ELSE Statement 1 ") { 
    val input  = """int foo() {
                    int testscore;
                    testscore = 76;
                    if (testscore >= 90) {
                      grade = "A";
                    } else {
                      grade = "B";
                    }
                  }"""
    val expect = Program(List(FuncDecl(Id("foo"),List(),IntType,Block(List(VarDecl(Id("testscore"),IntType)),List(BinaryOp("=",Id("testscore"),IntLiteral(76)),If(BinaryOp(">=",Id("testscore"),IntLiteral(90)),Block(List(),List(BinaryOp("=",Id("grade"),StringLiteral("A")))),Some(Block(List(),List(BinaryOp("=",Id("grade"),StringLiteral("B")))))))))))
    assert(checkAst(input,expect,249))
  }

  test ("Test__case 50: IF ELSE Statement 2 ") { 
    val input  = """  void main(){
                if (x>y) x; else y;
                if (x[a + 2] % 2 == 0) return (a+2); else return false;
              } """
    val expect = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List(If(BinaryOp(">",Id("x"),Id("y")),Id("x"),Some(Id("y"))),If(BinaryOp("==",BinaryOp("%",ArrayCell(Id("x"),BinaryOp("+",Id("a"),IntLiteral(2))),IntLiteral(2)),IntLiteral(0)),Return(Some(BinaryOp("+",Id("a"),IntLiteral(2)))),Some(Return(Some(BooleanLiteral(false))))))))))
    assert(checkAst(input,expect,250))
  }

  test("Test__case 51: Nested if else statement 1") {
    val input = """
        int foo() {
          if (x > 0)
            if (x % 2 == 0)
              res = "So chan nguyen duong";
            else
              res = "So le nguyen duong";
          else
            res = "So am";
        }
      """
    val expected = Program(List(FuncDecl(Id("foo"),List(),IntType,Block(List(),List(If(BinaryOp(">",Id("x"),IntLiteral(0)),If(BinaryOp("==",BinaryOp("%",Id("x"),IntLiteral(2)),IntLiteral(0)),BinaryOp("=",Id("res"),StringLiteral("So chan nguyen duong")),Some(BinaryOp("=",Id("res"),StringLiteral("So le nguyen duong")))),Some(BinaryOp("=",Id("res"),StringLiteral("So am")))))))))
    assert(checkAst(input,expected,251))
  }


  test ("Test__case 52: Nested if else statement 2") { 
    val input  = """  void main(){
                foo=true;
                goo=false;
                if (foo) if (goo) true; else false;
                if (foo) true; else if (!foo) if (goo) false;
                if (!foo) true; else if (foo) true; else false;
              } """
    val expect = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List(BinaryOp("=",Id("foo"),BooleanLiteral(true)),BinaryOp("=",Id("goo"),BooleanLiteral(false)),If(Id("foo"),If(Id("goo"),BooleanLiteral(true),Some(BooleanLiteral(false))),None),If(Id("foo"),BooleanLiteral(true),Some(If(UnaryOp("!",Id("foo")),If(Id("goo"),BooleanLiteral(false),None),None))),If(UnaryOp("!",Id("foo")),BooleanLiteral(true),Some(If(Id("foo"),BooleanLiteral(true),Some(BooleanLiteral(false))))))))))
    assert(checkAst(input,expect,252))
  }

  test ("Test__case 53: FOR statement") { 
    val input  = """int foo() {
                    for (i=0; i<100; i=i+1) {
                      S = S + i;
                    }
                }"""
    val expect = Program(List(FuncDecl(Id("foo"),List(),IntType,Block(List(),List(For(BinaryOp("=",Id("i"),IntLiteral(0)),BinaryOp("<",Id("i"),IntLiteral(100)),BinaryOp("=",Id("i"),BinaryOp("+",Id("i"),IntLiteral(1))),Block(List(),List(BinaryOp("=",Id("S"),BinaryOp("+",Id("S"),Id("i")))))))))))
    assert(checkAst(input,expect,253))
  }

  test("Test__case 54: Nested for statement") {
    val input = """
    int main(){
      for(i=1;i<10;i=i+1)
        for(j=1;j<10;j=j+1)
          doSomething;
    }
    """
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(For(BinaryOp("=",Id("i"),IntLiteral(1)),BinaryOp("<",Id("i"),IntLiteral(10)),BinaryOp("=",Id("i"),BinaryOp("+",Id("i"),IntLiteral(1))),For(BinaryOp("=",Id("j"),IntLiteral(1)),BinaryOp("<",Id("j"),IntLiteral(10)),BinaryOp("=",Id("j"),BinaryOp("+",Id("j"),IntLiteral(1))),Id("doSomething"))))))))
    assert(checkAst(input,expected,254))
  }

  test("Test__case 55: For and If Statement") {
    val input = """
    int main(){
      for(i=1;i<10;i=i+2){
        if(i % 2 == 0) print(i);  
      }
    }
    """
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(For(BinaryOp("=",Id("i"),IntLiteral(1)),BinaryOp("<",Id("i"),IntLiteral(10)),BinaryOp("=",Id("i"),BinaryOp("+",Id("i"),IntLiteral(2))),Block(List(),List(If(BinaryOp("==",BinaryOp("%",Id("i"),IntLiteral(2)),IntLiteral(0)),CallExpr(Id("print"),List(Id("i"))),None)))))))))
    assert(checkAst(input,expected,255))
  }

  test("Test__case 56: Do while statement") {
    val input = """int foo() {
                    do
                      a=a+1;
                    while a=10;
                  }
                  """
    val expected = Program(List(FuncDecl(Id("foo"),List(),IntType,Block(List(),List(Dowhile(List(BinaryOp("=",Id("a"),BinaryOp("+",Id("a"),IntLiteral(1)))),BinaryOp("=",Id("a"),IntLiteral(10))))))))
    assert(checkAst(input,expected,256))
  }

  test ("Test__case 57: Complex Do-while statement") { 
    val input  = """  int [] main(int x[]){
                do do do Do;while(m); do do Do; while(x); while(y); while(z);while(t);
              } """
    val expect = Program(List(FuncDecl(Id("main"),List(VarDecl(Id("x"),ArrayPointerType(IntType))),ArrayPointerType(IntType),Block(List(),List(Dowhile(List(Dowhile(List(Dowhile(List(Id("Do")),Id("m")),Dowhile(List(Dowhile(List(Id("Do")),Id("x"))),Id("y"))),Id("z"))),Id("t")))))))
    assert(checkAst(input,expect,257))
  }

  test("Test__case 58: A simple program with for loop and return statement"){
    val input =
      """
        float foo() {
          return 12.5e-25;
        }
        void main() {
          int i;
          for (i=0; i<100; i=i+1) {
            putFloatLn(print());
          }
        }
      """
    val expect = Program(List(FuncDecl(Id("foo"),List(),FloatType,Block(List(),List(Return(Some(FloatLiteral(1.25E-24f)))))),FuncDecl(Id("main"),List(),VoidType,Block(List(VarDecl(Id("i"),IntType)),List(For(BinaryOp("=",Id("i"),IntLiteral(0)),BinaryOp("<",Id("i"),IntLiteral(100)),BinaryOp("=",Id("i"),BinaryOp("+",Id("i"),IntLiteral(1))),Block(List(),List(CallExpr(Id("putFloatLn"),List(CallExpr(Id("print"),List())))))))))))
    assert(checkAst(input,expect,258))
  }  

  test ("Test__case 59: Function call with no argument") { 
    val input  = """  void main(){
                foo();
                stringfoo();
              } """
    val expect = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List(CallExpr(Id("foo"),List()),CallExpr(Id("stringfoo"),List()))))))
    assert(checkAst(input,expect,259))
  }

  test("Test__case 60: Funcall as funcall argument") {
    val input = """
        void main() {
          a = foo(foo(foo()));
          return a;
        }
      """
    val expected = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List(BinaryOp("=",Id("a"),CallExpr(Id("foo"),List(CallExpr(Id("foo"),List(CallExpr(Id("foo"),List())))))),Return(Some(Id("a"))))))))
    assert(checkAst(input,expected,260))
  }

  test ("Test__case 61: Function call with argument") { 
    val input  = """  void main(){
                addTwoNumber(a, b);
                printScreen("Hello World");
              } """
    val expect = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List(CallExpr(Id("addTwoNumber"),List(Id("a"),Id("b"))),CallExpr(Id("printScreen"),List(StringLiteral("Hello World"))))))))
    assert(checkAst(input,expect,261))
  }

  test ("Test__case 62: Break statement - search number 9 in for loop") { 
    val input  = """  void main(){
                  int i;
                  for (i = 0; i <= 100; i = i + 1) {
                    if(i == 9) {
                      return i;
                      break;
                    }
                  }
                }"""
    val expect = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(VarDecl(Id("i"),IntType)),List(For(BinaryOp("=",Id("i"),IntLiteral(0)),BinaryOp("<=",Id("i"),IntLiteral(100)),BinaryOp("=",Id("i"),BinaryOp("+",Id("i"),IntLiteral(1))),Block(List(),List(If(BinaryOp("==",Id("i"),IntLiteral(9)),Block(List(),List(Return(Some(Id("i"))),Break)),None)))))))))
    assert(checkAst(input,expect,262))
  }

  test ("Test__case 63: Break statement in for nested loop") { 
    val input  = """  void main(){
                  int i, j;
                  for (i = 0; i <= 100; i = i + 1) {
                    for (j = 100; j <= 0; j = j -1){
                      if(i == j) {
                        return i;
                        break;
                      }
                    }  
                  }
                }"""
    val expect = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(VarDecl(Id("i"),IntType),VarDecl(Id("j"),IntType)),List(For(BinaryOp("=",Id("i"),IntLiteral(0)),BinaryOp("<=",Id("i"),IntLiteral(100)),BinaryOp("=",Id("i"),BinaryOp("+",Id("i"),IntLiteral(1))),Block(List(),List(For(BinaryOp("=",Id("j"),IntLiteral(100)),BinaryOp("<=",Id("j"),IntLiteral(0)),BinaryOp("=",Id("j"),BinaryOp("-",Id("j"),IntLiteral(1))),Block(List(),List(If(BinaryOp("==",Id("i"),Id("j")),Block(List(),List(Return(Some(Id("i"))),Break)),None))))))))))))
    assert(checkAst(input,expect,263))
  }

  test ("Test__case 64: Continue statement - ingore number 9 in for loop") { 
    val input  = """  void main(){
                  int i;
                  for (i = 0; i <= 100; i = i + 1) {
                    if(i == 9) {
                      continue;
                      return i;       
                    }
                  }
                }"""
    val expect = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(VarDecl(Id("i"),IntType)),List(For(BinaryOp("=",Id("i"),IntLiteral(0)),BinaryOp("<=",Id("i"),IntLiteral(100)),BinaryOp("=",Id("i"),BinaryOp("+",Id("i"),IntLiteral(1))),Block(List(),List(If(BinaryOp("==",Id("i"),IntLiteral(9)),Block(List(),List(Continue,Return(Some(Id("i"))))),None)))))))))
    assert(checkAst(input,expect,264))
  }

  test ("Test__case 65: Complex block statement and for statement") { 
    val input  = """  void main(){
                for(i=1;i<100;i=i+1){for(i=1;i<100;i=i+1){for(i=1;i<100;x=i+1){i;}}}
                for(x=1;x<100;x=x+1){int y[200]; y[x] = x*x; {Do-some-thing;} }
              } """
    val expect = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List(For(BinaryOp("=",Id("i"),IntLiteral(1)),BinaryOp("<",Id("i"),IntLiteral(100)),BinaryOp("=",Id("i"),BinaryOp("+",Id("i"),IntLiteral(1))),Block(List(),List(For(BinaryOp("=",Id("i"),IntLiteral(1)),BinaryOp("<",Id("i"),IntLiteral(100)),BinaryOp("=",Id("i"),BinaryOp("+",Id("i"),IntLiteral(1))),Block(List(),List(For(BinaryOp("=",Id("i"),IntLiteral(1)),BinaryOp("<",Id("i"),IntLiteral(100)),BinaryOp("=",Id("x"),BinaryOp("+",Id("i"),IntLiteral(1))),Block(List(),List(Id("i")))))))))),For(BinaryOp("=",Id("x"),IntLiteral(1)),BinaryOp("<",Id("x"),IntLiteral(100)),BinaryOp("=",Id("x"),BinaryOp("+",Id("x"),IntLiteral(1))),Block(List(VarDecl(Id("y"),ArrayType(IntLiteral(200),IntType))),List(BinaryOp("=",ArrayCell(Id("y"),Id("x")),BinaryOp("*",Id("x"),Id("x"))),Block(List(),List(BinaryOp("-",BinaryOp("-",Id("Do"),Id("some")),Id("thing"))))))))))))
    assert(checkAst(input,expect,265))
  }

  test ("Test__case 66: Block statement and Do-while statement") { 
    val input  = """  void main(){
                do {do xx; while(true);{do Do; While; while(.25);}} while(y);  
                do {x+1;}{x+2;}{x+3.;} while(x>2);
              } """
    val expect = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List(Dowhile(List(Block(List(),List(Dowhile(List(Id("xx")),BooleanLiteral(true)),Block(List(),List(Dowhile(List(Id("Do"),Id("While")),FloatLiteral(0.25f))))))),Id("y")),Dowhile(List(Block(List(),List(BinaryOp("+",Id("x"),IntLiteral(1)))),Block(List(),List(BinaryOp("+",Id("x"),IntLiteral(2)))),Block(List(),List(BinaryOp("+",Id("x"),FloatLiteral(3.0f))))),BinaryOp(">",Id("x"),IntLiteral(2))))))))
    assert(checkAst(input,expect,266))
  }

  test("Test__case 67: Do while with many block statement") {
    val input = """
    int main(){
      do{
        a=a+1;
      }{
        a=a+2;
      }
      while(a<10);
    }
    """
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(Dowhile(List(Block(List(),List(BinaryOp("=",Id("a"),BinaryOp("+",Id("a"),IntLiteral(1))))),Block(List(),List(BinaryOp("=",Id("a"),BinaryOp("+",Id("a"),IntLiteral(2)))))),BinaryOp("<",Id("a"),IntLiteral(10))))))))
    assert(checkAst(input,expected,267))
  }

  test("Test__case 68: Array function") {
    val input = """
    float[] array(int a,int b){
      boolean b[2];
      b[0]=true;
      b[1]=false;
      return b;
    }
    """
    val expected = Program(List(FuncDecl(Id("array"),List(VarDecl(Id("a"),IntType),VarDecl(Id("b"),IntType)),ArrayPointerType(FloatType),Block(List(VarDecl(Id("b"),ArrayType(IntLiteral(2),BoolType))),List(BinaryOp("=",ArrayCell(Id("b"),IntLiteral(0)),BooleanLiteral(true)),BinaryOp("=",ArrayCell(Id("b"),IntLiteral(1)),BooleanLiteral(false)),Return(Some(Id("b"))))))))
    assert(checkAst(input,expected,268))
  }

  test("Test__case 69: Multi statement in for loop body") {
    val input = """
        float foo() {
          return 0;
        }
        void main() {
          int i;
          for (i=0; i<10; i=i+1) {
            foo();
            break;
            continue;
          }
        }
      """
    val expected = Program(List(FuncDecl(Id("foo"),List(),FloatType,Block(List(),List(Return(Some(IntLiteral(0)))))),FuncDecl(Id("main"),List(),VoidType,Block(List(VarDecl(Id("i"),IntType)),List(For(BinaryOp("=",Id("i"),IntLiteral(0)),BinaryOp("<",Id("i"),IntLiteral(10)),BinaryOp("=",Id("i"),BinaryOp("+",Id("i"),IntLiteral(1))),Block(List(),List(CallExpr(Id("foo"),List()),Break,Continue))))))))
    assert(checkAst(input,expected,269))
  }

  test ("Test__case 70: Function call with many complex arguments") { 
    val input  = """  void main(){
                foo(a[2],true,false);
                goo(a[2[b[1]]], true);
              } """
    val expect = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List(CallExpr(Id("foo"),List(ArrayCell(Id("a"),IntLiteral(2)),BooleanLiteral(true),BooleanLiteral(false))),CallExpr(Id("goo"),List(ArrayCell(Id("a"),ArrayCell(IntLiteral(2),ArrayCell(Id("b"),IntLiteral(1)))),BooleanLiteral(true))))))))
    assert(checkAst(input,expect,270))
  }

  test("Test__case 71: Return a float number") {
    val input = """
    float main(){
      if(true) {
        return 0;
      }
      return 1.23E-4;
    }
    """
    val expected = Program(List(FuncDecl(Id("main"),List(),FloatType,Block(List(),List(If(BooleanLiteral(true),Block(List(),List(Return(Some(IntLiteral(0))))),None),Return(Some(FloatLiteral(1.23E-4f))))))))
    assert(checkAst(input,expected,271))
  }

  test("Test__case 72: Mix-up operator") {
    val input = """ 
    int foo(){
      foo(2)[3]=a||b&&(c-35.4e10)*9+10.0/4+true;
    }"""
    val expected = Program(List(FuncDecl(Id("foo"),List(),IntType,Block(List(),List(BinaryOp("=",ArrayCell(CallExpr(Id("foo"),List(IntLiteral(2))),IntLiteral(3)),BinaryOp("||",Id("a"),BinaryOp("&&",Id("b"),BinaryOp("+",BinaryOp("+",BinaryOp("*",BinaryOp("-",Id("c"),FloatLiteral(3.54000011E11f)),IntLiteral(9)),BinaryOp("/",FloatLiteral(10.0f),IntLiteral(4))),BooleanLiteral(true))))))))))
    assert(checkAst(input,expected,272))
  }

  test("Test__case 73: Rrogram with line comment") {
    val input = """
    //This is a function
    int main(){
      //This is comment line
    }
    """
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List()))))
    assert(checkAst(input,expected,273))
  }

  test("Test__case 74: Rrogram with block comment") {
    val input = """
    void main() {
      int i;
      /*
        Line 1
        Line 2
        Line 3
      */
    }
    """
    val expected = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(VarDecl(Id("i"),IntType)),List()))))
    assert(checkAst(input,expected,274))
  }

  test("Test__case 75: Negative with index expression") {
    val input = """
    int main(){
      float array[1];
      -array[1];
    }
    """
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(VarDecl(Id("array"),ArrayType(IntLiteral(1),FloatType))),List(UnaryOp("-",ArrayCell(Id("array"),IntLiteral(1))))))))
    assert(checkAst(input,expected,275))
  }

  test("Test__case 76: Negative with subtract operator in expression") {
    val input = """
    int main(){
      -100--10--1;
    }
    """
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(BinaryOp("-",BinaryOp("-",UnaryOp("-",IntLiteral(100)),UnaryOp("-",IntLiteral(10))),UnaryOp("-",IntLiteral(1))))))))
    assert(checkAst(input,expected,276))
  }

  test("Test__case 77: Operator +- precedence") {
    val input = """void main(){
      id[8]-6 = 1000;
      2--------------------8; 
      id[3]/ab[7] = 100;
      (2.9*!ef)+8+9+7+9 = x + 2;
     
     
   }  """
    val expected = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List(BinaryOp("=",BinaryOp("-",ArrayCell(Id("id"),IntLiteral(8)),IntLiteral(6)),IntLiteral(1000)),BinaryOp("-",IntLiteral(2),UnaryOp("-",UnaryOp("-",UnaryOp("-",UnaryOp("-",UnaryOp("-",UnaryOp("-",UnaryOp("-",UnaryOp("-",UnaryOp("-",UnaryOp("-",UnaryOp("-",UnaryOp("-",UnaryOp("-",UnaryOp("-",UnaryOp("-",UnaryOp("-",UnaryOp("-",UnaryOp("-",UnaryOp("-",IntLiteral(8))))))))))))))))))))),BinaryOp("=",BinaryOp("/",ArrayCell(Id("id"),IntLiteral(3)),ArrayCell(Id("ab"),IntLiteral(7))),IntLiteral(100)),BinaryOp("=",BinaryOp("+",BinaryOp("+",BinaryOp("+",BinaryOp("+",BinaryOp("*",FloatLiteral(2.9f),UnaryOp("!",Id("ef"))),IntLiteral(8)),IntLiteral(9)),IntLiteral(7)),IntLiteral(9)),BinaryOp("+",Id("x"),IntLiteral(2))))))))
    assert(checkAst(input,expected,277))
  }

  test("Test__case 78: Nested for-while loop statement") {
    val input = """void func(){
      int i,j;
      for(i=0;i<5;i=i+1){
        j=0;
        do
          j = j+1;
        while (j<5);
      }
    }"""
    val expected = Program(List(FuncDecl(Id("func"),List(),VoidType,Block(List(VarDecl(Id("i"),IntType),VarDecl(Id("j"),IntType)),List(For(BinaryOp("=",Id("i"),IntLiteral(0)),BinaryOp("<",Id("i"),IntLiteral(5)),BinaryOp("=",Id("i"),BinaryOp("+",Id("i"),IntLiteral(1))),Block(List(),List(BinaryOp("=",Id("j"),IntLiteral(0)),Dowhile(List(BinaryOp("=",Id("j"),BinaryOp("+",Id("j"),IntLiteral(1)))),BinaryOp("<",Id("j"),IntLiteral(5)))))))))))
    assert(checkAst(input,expected,278))
  }

  test("Test__case 79: Add, sub, mul, div with a number with a negative number") {
    val input = """
    void main(){
      x + -y;
      x - -y;
      x * -y;
      x / -y;
    }
    """
    val expected = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List(BinaryOp("+",Id("x"),UnaryOp("-",Id("y"))),BinaryOp("-",Id("x"),UnaryOp("-",Id("y"))),BinaryOp("*",Id("x"),UnaryOp("-",Id("y"))),BinaryOp("/",Id("x"),UnaryOp("-",Id("y"))))))))
    assert(checkAst(input,expected,279))
  }

  test("Test__case 80: Bracket and none associativity") {
    val input = """
    void main(){
      (a == b) == c;
      a = b == c = d == e;
      a = b == c = d;
      a=b=c=d;
    }
    """
    val expected = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List(BinaryOp("==",BinaryOp("==",Id("a"),Id("b")),Id("c")),BinaryOp("=",Id("a"),BinaryOp("=",BinaryOp("==",Id("b"),Id("c")),BinaryOp("==",Id("d"),Id("e")))),BinaryOp("=",Id("a"),BinaryOp("=",BinaryOp("==",Id("b"),Id("c")),Id("d"))),BinaryOp("=",Id("a"),BinaryOp("=",Id("b"),BinaryOp("=",Id("c"),Id("d")))))))))
    assert(checkAst(input,expected,280))
  }

  test("Test__case 81: Function declare more inside") {
    val input = """void foo (float a [] ) {}
                   void goo (float x [ ]) {
                    float y [10] ;
                    int z [10] ;
                    foo (x); //CORRECT
                    foo (y); //CORRECT
                    foo (z); //WRONG
                  }
    """
    val expected = Program(List(FuncDecl(Id("foo"),List(VarDecl(Id("a"),ArrayPointerType(FloatType))),VoidType,Block(List(),List())),FuncDecl(Id("goo"),List(VarDecl(Id("x"),ArrayPointerType(FloatType))),VoidType,Block(List(VarDecl(Id("y"),ArrayType(IntLiteral(10),FloatType)),VarDecl(Id("z"),ArrayType(IntLiteral(10),IntType))),List(CallExpr(Id("foo"),List(Id("x"))),CallExpr(Id("foo"),List(Id("y"))),CallExpr(Id("foo"),List(Id("z"))))))))
    assert(checkAst(input,expected,281))
  }

  test("Test__case 82: Test and operator, or operator combine together") {
    val input = """
        void main() {
          a&&b||c;
          a||(b&&c);
          a||b||c;
          a&&b&&c;
        }
      """
    val expected = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List(BinaryOp("||",BinaryOp("&&",Id("a"),Id("b")),Id("c")),BinaryOp("||",Id("a"),BinaryOp("&&",Id("b"),Id("c"))),BinaryOp("||",BinaryOp("||",Id("a"),Id("b")),Id("c")),BinaryOp("&&",BinaryOp("&&",Id("a"),Id("b")),Id("c")))))))
    assert(checkAst(input,expected,282))
  }

  test("Test__case 83: Expression with literal") {
    val input = """
        void main() {
          a+2;
          b == false;
          c-"Hi";
          d = 56.7e8;
          f >= 10;
          g != 100;
        }
      """
    val expected = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List(BinaryOp("+",Id("a"),IntLiteral(2)),BinaryOp("==",Id("b"),BooleanLiteral(false)),BinaryOp("-",Id("c"),StringLiteral("Hi")),BinaryOp("=",Id("d"),FloatLiteral(5.6700001E9f)),BinaryOp(">=",Id("f"),IntLiteral(10)),BinaryOp("!=",Id("g"),IntLiteral(100)))))))
    assert(checkAst(input,expected,283))
  }

  test("Test__case 84: Valid array declare") {
    val input = """void main() {int i[5];}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(VarDecl(Id("i"),ArrayType(IntLiteral(5),IntType))),List()))))
    assert(checkAst(input,expected,284))
  }

  test("Test__case 85: Many variable declare") {
    val input = """void main() {int i,j,k[5];}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(VarDecl(Id("i"),IntType),VarDecl(Id("j"),IntType),VarDecl(Id("k"),ArrayType(IntLiteral(5),IntType))),List()))))
    assert(checkAst(input,expected,285))
  }

  test ("Test__case 86: Complicate index of array ") { 
    val input  = """void main(){
                x1 [x2 = x3 || x4 && x5 == x6 <= x7 + x8 -- x9 - !x10[x11+x12]];
              } """
    val expect = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List(ArrayCell(Id("x1"),BinaryOp("=",Id("x2"),BinaryOp("||",Id("x3"),BinaryOp("&&",Id("x4"),BinaryOp("==",Id("x5"),BinaryOp("<=",Id("x6"),BinaryOp("-",BinaryOp("-",BinaryOp("+",Id("x7"),Id("x8")),UnaryOp("-",Id("x9"))),UnaryOp("!",ArrayCell(Id("x10"),BinaryOp("+",Id("x11"),Id("x12"))))))))))))))))
    assert(checkAst(input,expect,286))
  }

  test("Test__case 87: Continue break statement combine") {
    val input = """int main () {do continue; break; while true;}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(Dowhile(List(Continue,Break),BooleanLiteral(true)))))))
    assert(checkAst(input,expected,287))
  }

  test ("Test__case 88: An empty Program with commend") { 
    val input  = """ // This is a commend ;_;
            /* This
             is
             another 
             commend*/ """
    val expect = Program(List())
    assert(checkAst(input,expect,288))
  }

  test("Test__case 89: Multi multiple operator") {
    val input = """
        int fun() {
          a * 1.23 * 3 * 3.2E-5 * abc;
        }
      """
    val expected = Program(List(FuncDecl(Id("fun"),List(),IntType,Block(List(),List(BinaryOp("*",BinaryOp("*",BinaryOp("*",BinaryOp("*",Id("a"),FloatLiteral(1.23f)),IntLiteral(3)),FloatLiteral(3.2E-5f)),Id("abc")))))))
    assert(checkAst(input,expected,289))
  }

  test("Test__case 90: nested block statement") {
    val input = """
    void main(){
      {
        int a, b;
        a = b;
        {
          int c;
          c = 0;
        }
      }
    }
    """
    val expected = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List(Block(List(VarDecl(Id("a"),IntType),VarDecl(Id("b"),IntType)),List(BinaryOp("=",Id("a"),Id("b")),Block(List(VarDecl(Id("c"),IntType)),List(BinaryOp("=",Id("c"),IntLiteral(0)))))))))))
    assert(checkAst(input,expected,290))
  }

  test("Test__case 91: Bouble search function") {
    val input = """
    int[] bouble(int array[], int n){
      int i, j, swap;
      for(i=0; i<n-1; i=i+1){
        for (j=0; j<n-i-1; j=j+1){
          if (array[j]>array[j+1]){
            swap=array[j];
            array[j]=array[j+1];
            array[j+1]=swap;
          }
        }
      }
    }
    """
    val expected = Program(List(FuncDecl(Id("bouble"),List(VarDecl(Id("array"),ArrayPointerType(IntType)),VarDecl(Id("n"),IntType)),ArrayPointerType(IntType),Block(List(VarDecl(Id("i"),IntType),VarDecl(Id("j"),IntType),VarDecl(Id("swap"),IntType)),List(For(BinaryOp("=",Id("i"),IntLiteral(0)),BinaryOp("<",Id("i"),BinaryOp("-",Id("n"),IntLiteral(1))),BinaryOp("=",Id("i"),BinaryOp("+",Id("i"),IntLiteral(1))),Block(List(),List(For(BinaryOp("=",Id("j"),IntLiteral(0)),BinaryOp("<",Id("j"),BinaryOp("-",BinaryOp("-",Id("n"),Id("i")),IntLiteral(1))),BinaryOp("=",Id("j"),BinaryOp("+",Id("j"),IntLiteral(1))),Block(List(),List(If(BinaryOp(">",ArrayCell(Id("array"),Id("j")),ArrayCell(Id("array"),BinaryOp("+",Id("j"),IntLiteral(1)))),Block(List(),List(BinaryOp("=",Id("swap"),ArrayCell(Id("array"),Id("j"))),BinaryOp("=",ArrayCell(Id("array"),Id("j")),ArrayCell(Id("array"),BinaryOp("+",Id("j"),IntLiteral(1)))),BinaryOp("=",ArrayCell(Id("array"),BinaryOp("+",Id("j"),IntLiteral(1))),Id("swap")))),None))))))))))))
    assert(checkAst(input,expected,291))
  }

  test("Test__case 92: Calculate fibonacci numbers program") {
    val input = """
        int fibonacci(int N){
          if (N == 0) return  0;
          if (N == 1) return 1;
          else return fibonacci(N - 1) + fibonacci(N - 2);
        }
        int main(){
          int N,i;
          N=0;
          for (i = 0; i < N; i)
            cout(fibonacci(i), " ");
            system("pause");
          }
        """
    val expected = Program(List(FuncDecl(Id("fibonacci"),List(VarDecl(Id("N"),IntType)),IntType,Block(List(),List(If(BinaryOp("==",Id("N"),IntLiteral(0)),Return(Some(IntLiteral(0))),None),If(BinaryOp("==",Id("N"),IntLiteral(1)),Return(Some(IntLiteral(1))),Some(Return(Some(BinaryOp("+",CallExpr(Id("fibonacci"),List(BinaryOp("-",Id("N"),IntLiteral(1)))),CallExpr(Id("fibonacci"),List(BinaryOp("-",Id("N"),IntLiteral(2)))))))))))),FuncDecl(Id("main"),List(),IntType,Block(List(VarDecl(Id("N"),IntType),VarDecl(Id("i"),IntType)),List(BinaryOp("=",Id("N"),IntLiteral(0)),For(BinaryOp("=",Id("i"),IntLiteral(0)),BinaryOp("<",Id("i"),Id("N")),Id("i"),CallExpr(Id("cout"),List(CallExpr(Id("fibonacci"),List(Id("i"))),StringLiteral(" ")))),CallExpr(Id("system"),List(StringLiteral("pause"))))))))
    assert(checkAst(input,expected,292))
  }

  test("Test__case 93: Check leap year function") {
    val input = """
    boolean isLeapYear(int year){
      if (year < 0 )
        cout ("This is a invalid Year." ,endl);
      else
        if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0))
          return true;
        else
          return false;
    }
    """
    val expected = Program(List(FuncDecl(Id("isLeapYear"),List(VarDecl(Id("year"),IntType)),BoolType,Block(List(),List(If(BinaryOp("<",Id("year"),IntLiteral(0)),CallExpr(Id("cout"),List(StringLiteral("This is a invalid Year."),Id("endl"))),Some(If(BinaryOp("||",BinaryOp("==",BinaryOp("%",Id("year"),IntLiteral(400)),IntLiteral(0)),BinaryOp("&&",BinaryOp("==",BinaryOp("%",Id("year"),IntLiteral(4)),IntLiteral(0)),BinaryOp("!=",BinaryOp("%",Id("year"),IntLiteral(100)),IntLiteral(0)))),Return(Some(BooleanLiteral(true))),Some(Return(Some(BooleanLiteral(false))))))))))))
    assert(checkAst(input,expected,293))
  }

  test("Test__case 94: Hello Word") {
    val input = """
    int main(){
      print("Hello Word\n");
    }
    """
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(CallExpr(Id("print"),List(StringLiteral("Hello Word\\n"))))))))
    assert(checkAst(input,expected,294))
  }

  test("Test__case 94: Priority operator") {
    val input = """int main(){1 && 2 == 3 < 4 + 5 * 6 [7] = ! 8;}"""
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(BinaryOp("=",BinaryOp("&&",IntLiteral(1),BinaryOp("==",IntLiteral(2),BinaryOp("<",IntLiteral(3),BinaryOp("+",IntLiteral(4),BinaryOp("*",IntLiteral(5),ArrayCell(IntLiteral(6),IntLiteral(7))))))),UnaryOp("!",IntLiteral(8))))))))
    assert(checkAst(input,expected,294))
  }

  test("Test__case 95: Calculate prime numbers program") {
    val input = """
        int a[10];
        void ngto(int a, int b){
          for (i = a; counter=0; i <= b){
            for (j = 2; j <= sqrt(i); j)
              if (i%j == 0) counter=couter+1;
            if (counter == 0 && i!=1) cout(i);
            counter = 0;
          }
        }
        """
    val expected = Program(List(VarDecl(Id("a"),ArrayType(IntLiteral(10),IntType)),FuncDecl(Id("ngto"),List(VarDecl(Id("a"),IntType),VarDecl(Id("b"),IntType)),VoidType,Block(List(),List(For(BinaryOp("=",Id("i"),Id("a")),BinaryOp("=",Id("counter"),IntLiteral(0)),BinaryOp("<=",Id("i"),Id("b")),Block(List(),List(For(BinaryOp("=",Id("j"),IntLiteral(2)),BinaryOp("<=",Id("j"),CallExpr(Id("sqrt"),List(Id("i")))),Id("j"),If(BinaryOp("==",BinaryOp("%",Id("i"),Id("j")),IntLiteral(0)),BinaryOp("=",Id("counter"),BinaryOp("+",Id("couter"),IntLiteral(1))),None)),If(BinaryOp("&&",BinaryOp("==",Id("counter"),IntLiteral(0)),BinaryOp("!=",Id("i"),IntLiteral(1))),CallExpr(Id("cout"),List(Id("i"))),None),BinaryOp("=",Id("counter"),IntLiteral(0))))))))))
    assert(checkAst(input,expected,295))
  }

  test("Test__case 96: Program read a file in C++") {
    val input = """
    int main(){
      fp = fopen("file.txt", "r");
      fclose(fp);
      return 0;
    }"""
    val expected = Program(List(FuncDecl(Id("main"),List(),IntType,Block(List(),List(BinaryOp("=",Id("fp"),CallExpr(Id("fopen"),List(StringLiteral("file.txt"),StringLiteral("r")))),CallExpr(Id("fclose"),List(Id("fp"))),Return(Some(IntLiteral(0))))))))
    assert(checkAst(input,expected,296))
    }

  test("Test__case 97: Program") {
    val input = """
    void foo(){
      foo(x+b2)[a*7==6];
    }"""
    val expected = Program(List(FuncDecl(Id("foo"),List(),VoidType,Block(List(),List(ArrayCell(CallExpr(Id("foo"),List(BinaryOp("+",Id("x"),Id("b2")))),BinaryOp("==",BinaryOp("*",Id("a"),IntLiteral(7)),IntLiteral(6))))))))
    assert(checkAst(input,expected,297))
  }

  test("Test__case 98: Another mega complex program") {
    val input = """
        int zeroOne_myAttemp(){
          for ( i = place; i < numItem; i) {
            if (last_one || no_more_space(i == numItem - 1) || (size - itemSize == 0)) {
              if (itemSize > size) {
                if (value > max){
                  max = value;
                  return;
                }
                if (value + itemValue[at(i)] > max)
                  max = value + itemValue;
                return;
              }
              if (size < itemSize)
                continue;
              for (j = 1; j < numItem - i + 1;j+1) {
                findMax(size - itemSizeat(i),i + j,value + itemValue[at(i)]);
              }
            }
          }
          return max;
        }
        """
    val expected = Program(List(FuncDecl(Id("zeroOne_myAttemp"),List(),IntType,Block(List(),List(For(BinaryOp("=",Id("i"),Id("place")),BinaryOp("<",Id("i"),Id("numItem")),Id("i"),Block(List(),List(If(BinaryOp("||",BinaryOp("||",Id("last_one"),CallExpr(Id("no_more_space"),List(BinaryOp("==",Id("i"),BinaryOp("-",Id("numItem"),IntLiteral(1)))))),BinaryOp("==",BinaryOp("-",Id("size"),Id("itemSize")),IntLiteral(0))),Block(List(),List(If(BinaryOp(">",Id("itemSize"),Id("size")),Block(List(),List(If(BinaryOp(">",Id("value"),Id("max")),Block(List(),List(BinaryOp("=",Id("max"),Id("value")),Return(None))),None),If(BinaryOp(">",BinaryOp("+",Id("value"),ArrayCell(Id("itemValue"),CallExpr(Id("at"),List(Id("i"))))),Id("max")),BinaryOp("=",Id("max"),BinaryOp("+",Id("value"),Id("itemValue"))),None),Return(None))),None),If(BinaryOp("<",Id("size"),Id("itemSize")),Continue,None),For(BinaryOp("=",Id("j"),IntLiteral(1)),BinaryOp("<",Id("j"),BinaryOp("+",BinaryOp("-",Id("numItem"),Id("i")),IntLiteral(1))),BinaryOp("+",Id("j"),IntLiteral(1)),Block(List(),List(CallExpr(Id("findMax"),List(BinaryOp("-",Id("size"),CallExpr(Id("itemSizeat"),List(Id("i")))),BinaryOp("+",Id("i"),Id("j")),BinaryOp("+",Id("value"),ArrayCell(Id("itemValue"),CallExpr(Id("at"),List(Id("i")))))))))))),None)))),Return(Some(Id("max"))))))))
    assert(checkAst(input,expected,298))
  }

  test("Test__case 99: Return an expression") {
    val input = """
    void main(){
      return a+1==b*2 = c%3!=d/4;
    }
    """
    val expected = Program(List(FuncDecl(Id("main"),List(),VoidType,Block(List(),List(Return(Some(BinaryOp("=",BinaryOp("==",BinaryOp("+",Id("a"),IntLiteral(1)),BinaryOp("*",Id("b"),IntLiteral(2))),BinaryOp("!=",BinaryOp("%",Id("c"),IntLiteral(3)),BinaryOp("/",Id("d"),IntLiteral(4)))))))))))
    assert(checkAst(input,expected,299))
  }

  test("Test__case 100: Return an expression") {
    val input = """int giatrimin(int a,int b,int c){ 
                    if(a<b){ 
                    if(a<c) return(a); 
                      else return(c);} 
                      else if(b<c) return(b); 
                      else return(c);  
                  } """
    val expected = Program(List(FuncDecl(Id("giatrimin"),List(VarDecl(Id("a"),IntType),VarDecl(Id("b"),IntType),VarDecl(Id("c"),IntType)),IntType,Block(List(),List(If(BinaryOp("<",Id("a"),Id("b")),Block(List(),List(If(BinaryOp("<",Id("a"),Id("c")),Return(Some(Id("a"))),Some(Return(Some(Id("c"))))))),Some(If(BinaryOp("<",Id("b"),Id("c")),Return(Some(Id("b"))),Some(Return(Some(Id("c"))))))))))))
    assert(checkAst(input,expected,300))
  }

}


