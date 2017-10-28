import org.scalatest.FunSuite
import mc.checker._
import mc.utils._

/**
  * Created by nhphung on 4/29/17.
  */
class CheckerSuite extends FunSuite with TestChecker {
    // test("Undeclared Function") {
    //   val input = "void main () {writeIntLn(3);}"
    //   val expected = "Undeclared Function: writeIntLn"
    //   assert(checkCkr(input,expected,401))
    // }
  // test("Type Mismatch In Expression: getInt") {
  //   val input = "void main () {getInt(3);}"
  //   val expected = "Type Mismatch In Expression: "+CallExpr(Id("getInt"),List(IntLiteral(3))).toString
  //   assert(checkCkr(input,expected,402))
  // }
  // test("Type Mismatch In Expression: putIntLn") {

  //   val input = "void main () {putIntLn();}"
  //   val expected = "Type Mismatch In Expression: "+CallExpr(Id("putIntLn"),List()).toString
  //   assert(checkCkr(input,expected,403))
  // }
  // test("Check with AST") {

  //   val input = Program(List(
  //     FuncDecl(Id("main"),List(),VoidType,
  //       Block(List(),
  //         List(CallExpr(Id("putIntLn"),List()))))))
  //   val expected = "Type Mismatch In Expression: "+CallExpr(Id("putIntLn"),List()).toString
  //   assert(checkAst(input,expected,404))
  // }
  // test("Check with AST putIntLn with float") {

  //   val input = Program(List(
  //     FuncDecl(Id("main"),List(),VoidType,
  //       Block(List(),
  //         List(CallExpr(Id("putIntLn"),List(FloatLiteral(1.2f))))))))
  //   val expected = "Type Mismatch In Expression: "+CallExpr(Id("putIntLn"),List(FloatLiteral(1.2f))).toString
  //   assert(checkAst(input,expected,405))

  // }


  // test("khai bao 1 bien") {
  //   val input = "int a;"
  //   val expected = List(VarDecl(Id("a"),IntType)).toString
  //   assert(checkCkr(input,expected,401))
  // }


  // test("khai bao 2 bien") {
  //   val input = "int a; float b[3];"
  //   val expected = List(VarDecl(Id("a"),IntType), VarDecl(Id("b"),ArrayType(IntLiteral(3),FloatType))).toString
  //   assert(checkCkr(input,expected,402))
  // }



  // test("khai bao 1 ham") {
  //   val input = "void main() {}"
  //   val expected = List().toString
  //   assert(checkCkr(input,expected,403))
  // }



  // test("khai bao ham va bien") {
  //   val input = "int a; void main() {}"
  //   val expected = List(VarDecl(Id("a"),IntType)).toString 
  //   assert(checkCkr(input,expected,405))
  // }




  test("Truong hop khong bi trung bien") {
    val input = "int a; int b; float c;"
    val expected = ""
    assert(checkCkr(input,expected,403))
  }
  test("Bi trung bien int a") {

    val input = "int a; int b; float a;"
    val expected = "Redeclared Variable: a"
    assert(checkCkr(input,expected,404))
  }
  test("Trung bien int a kieu array") {
    val input = "int a[2]; int b; int a;"
    val expected = "Redeclared Variable: a"
    assert(checkCkr(input,expected,405))
  }
  test("Trung bien int a, o giua co ham") {
    val input = "int a[2]; int foo(int a) {} int a;"
    val expected = "Redeclared Variable: a"
    assert(checkCkr(input,expected,406))
  }
  test("Bi trung bien int a thu 2") {
    val input = "int m; int a; int b; float a;"
    val expected = "Redeclared Variable: a"
    assert(checkCkr(input,expected,404))
  }
  test("Bien trung ham") {
    val input = """
              int a;
              float a(int b) {} 
    """
    val expected = "Redeclared Function: a"
    assert(checkCkr(input,expected,404))
  }
  test("Ham trung bien") {
    val input = """      
              float a(int b) {} 
              int a;
    """
    val expected = "Redeclared Variable: a"
    assert(checkCkr(input,expected,404))  
  }
  
  test("Lap lai tham so") {
    val input = """      
              float foo(int a, float a) {} 
    """
    val expected = "Redeclared Parameter: a"
    assert(checkCkr(input,expected,404))  
  }

  test("Lap lai khai bao trong block") {
    val input = """      
              float foo() {
                float b;
                int r;
                boolean r;  
              } 
    """
    val expected = "Redeclared Variable: r"
    assert(checkCkr(input,expected,404))  
 }

  test("Lap lai khai bao trong block co tham so") {
    val input = """      
              float foo(int b) {
                float b;
                int r;
              } 
    """
    val expected = "Redeclared Variable: b"
    assert(checkCkr(input,expected,404))  
  }

  test("Khong loi: khai bao bien") {
    val input = """
              int a;      
              float foo(int b) {
                float a;
                int r;
              } 
    """
    val expected = ""
    assert(checkCkr(input,expected,404))  
  }

  test("Unary: khai bao bien") {
    val input = """
              int a;      
              float foo(int b) {
                !c;
              } 
    """
    val expected = "Undeclared Identifier: c"
    assert(checkCkr(input,expected,404))  
  }

  test("Chua khai bao bien") {
    val input = """
              float c;
              int foo(int j, float c, boolean f) {
                j;
                a[1];
                
              } 
    """
    val expected = "Undeclared Identifier: a"
    assert(checkCkr(input,expected,404))  
  }

  test("Chua khai bao ham") {
    val input = """
              float c;
              float goo() {}

              int foo(int j, float c, boolean f) {
                j;
                goo();
                soo();
              } 
    """
    val expected = "Undeclared Function: soo"
    assert(checkCkr(input,expected,404))  
  }


  // test("Chua khai bao bien 1") {
  //   val input = """
  //             float c;
  //             int foo(int j, float c) {
  //               goo();
                
  //             } 
  //   """
  //   val expected = "Undeclared Function: goo"
  //   assert(checkCkr(input,expected,405))  
  // }
}