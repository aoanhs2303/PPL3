import org.scalatest.FunSuite
import mc.checker._
import mc.utils._

/**
  * Created by nhphung on 4/29/17.
  * Modified by Do Thanh Phat - TNMT
  */
class CheckerSuite extends FunSuite with TestChecker {

  /****************REDECLARATION VARIABLE****************/

  test("1. Redeclaration variable in global scope") {
    val input =
      """
        float a,b;
        boolean b;
      """
    val expected = "Redeclared Variable: b"
    assert(checkCkr(input,expected,401))
  }
  test("2. Redeclaration variable in function block") {
    val input =
      """
        int main(int a){
          int a;
          return a;
        }
      """
    val expected = "Redeclared Variable: a"
    assert(checkCkr(input,expected,402))
  }
  test("3. Redeclaration variable with exist function") {
    val input =
      """
        void foo () {
        }
        int foo;
      """
    val expected = "Redeclared Variable: foo"
    assert(checkCkr(input,expected,403))
  }
  test("4. Redeclaration function with another function") {
    val input =
      """
        boolean goo;
        float[] foo() {
          return 1.2;
        }
        int foo;
        """
    val expected = "Redeclared Variable: foo"
    assert(checkCkr(input,expected,404))
  }
  test("5. Not redeclare variable in nest block of function") {
    val input =
      """
        int foo(int a,int b, int c){
          int m,n;
          {int m;}
          return 1;
        } """
    val expected = ""
    assert(checkCkr(input,expected,405))
  }
  test("6. Redeclaration variable in parameter's function") {
    val input =
      """
        void foo(int a){
          int a;
          return;
        } """
    val expected = "Redeclared Variable: a"
    assert(checkCkr(input,expected,406))
  }
  test("7. Redeclaration variable with Build In function") {
    val input =
      """
        int getInt;
        """
    val expected = "Redeclared Variable: getInt" 
    assert(checkCkr(input,expected,407))
  }
  test("8. Valid declare") {
    val input =
      """
        int arr[3];
        float[] todo(){
          float arr[5];
          if(false){
            boolean arr;
          }
          return arr;
        }"""
    val expected = ""
    assert(checkCkr(input,expected,408))
  }

  /****************REDECLARATION PARAMETER*****************************/

  test("9. Redeclaration parameter in the same type") {
    val input =
      """
        void foo (int a, boolean b, boolean b[]) {
        } 
      """
    val expected = "Redeclared Parameter: b"
    assert(checkCkr(input,expected,409))
  }
  test("10. Reclaration parameter 2") {
    val input =
      """
        int a;
        void foo(int a) {
          // something 
        }
        int goo(int a, float a) {
          return 1;
        }
      """
    val expected = "Redeclared Parameter: a"
    assert(checkCkr(input,expected,410))
  }
  test("11. Valid declare function") {
    val input =
      """
        void foo(int a, float b, boolean c) {
          // something
        }
      """
    val expected = ""
    assert(checkCkr(input,expected,411))
  }

  /****************REDECLARATION FUNCTION****************/

  test("12. Redeclaration simple function") {
    val input =
      """
        void foo(int a, int c){
          return;
        }
        float foo(float b, float d){
          return 1.2;
        }
      """
    val expected = "Redeclared Function: foo"
    assert(checkCkr(input,expected,412))
  }
  test("13. Redeclaration function with a variable same name") {
    val input =
      """
        int foo;
        string foo(string a[], string b){
           return b;
         }
      """
    val expected = "Redeclared Function: foo"
    assert(checkCkr(input,expected,413))
  }
  test("14. Redeclaration function with Build In function: getFloat") {
    val input =
      """
        boolean[] getFloat(int mn, int c){
          boolean b[5];
          return b;
        }
      """
    val expected = "Redeclared Function: getFloat"
    assert(checkCkr(input,expected,414))
  }

  test("15. Redeclaration function with Build In function: putBool") {
    val input =
      """
      int putBool(int a[]){
        a[2] = 2;
        return a[2];
      }
      """
    val expected = "Redeclared Function: putBool"
    assert(checkCkr(input,expected,415))
  }
  test("16. Redeclaration function with Build In function: putStringLn") {
    val input =
      """
        void putStringLn(string str){
          return str;
        }
      """
    val expected = "Redeclared Function: putStringLn"
    assert(checkCkr(input,expected,416))
  }

  test("17. Redeclaration function with same type and same argument") {
    val input =
      """
        void foo(string a){
          a;
        }
        void foo(string a){
          return;
        }
      """
    val expected = "Redeclared Function: foo"
    assert(checkCkr(input,expected,417))
  }
  test("18. Redeclaration function with different signature") {
    val input =
      """
        int[] foo(int a){
          return a;
        }
        void foo(string a){
          return a;
        }
      """
    val expected = "Redeclared Function: foo"
    assert(checkCkr(input,expected,418))
  }
  

  /****************UNDECLARATION IDENTIFIER*****************************/

  test("19. Undeclaration simple identifier") {
    val input =
      """
        int foo(int a){
          c;
        } """
    val expected = "Undeclared Identifier: c"
    assert(checkCkr(input,expected,419))
  }
  test("20. Undeclaration identifier in binary op") {
    val input =
      """
        int foo(int x, int z){
          x + y;
          return 1;
        }
      """
    val expected = "Undeclared Identifier: y"
    assert(checkCkr(input,expected,420))
  }
  test("21. Undeclaration identifier in unary op") {
    val input =
      """
        boolean[] ret_my_type(int n){
          !a;
        }
      """
    val expected = "Undeclared Identifier: a"
    assert(checkCkr(input,expected,421))
  }
  test("22. Undeclaration identifier in do while statement") {
    val input =
      """
        void check_do_while(){
        if(a==1) return;
        }
      """
    val expected = "Undeclared Identifier: a"
    assert(checkCkr(input,expected,422))
  }
  test("23. Undeclaration identifier in for statement") {
    val input =
      """
        void foo(){
          int a;
          for(a; a < b; a = a + 1){
            //something
          }
        }
      """
    val expected = "Undeclared Identifier: b"
    assert(checkCkr(input,expected,423))
  }
  test("24. Undeclaration identifier in IF statement") {
    val input =
      """
        void foo(){
          int a;
          if(a < b){
            return a;
          }
        }
      """
    val expected = "Undeclared Identifier: b"
    assert(checkCkr(input,expected,424))
  }
  test("25. Undeclaration identifier in DO-WHILE statement") {
    val input =
      """
        void foo(){
          int a;
          do{
            a = 2;
            a = a * b;
          } while(true);
        }
      """
    val expected = "Undeclared Identifier: b"
    assert(checkCkr(input,expected,425))
  }
  test("26. Undeclaration identifier in RETURN statement") {
    val input =
      """
        void foo(){
          return x;
        }
      """
    val expected = "Undeclared Identifier: x"
    assert(checkCkr(input,expected,426))
  }


  /****************UNDECLARATION FUNCTION*****************************/

  test("27. Undeclaration function in call expression") {
    val input =
      """
        void test(){
          foo();
        }
      """
    val expected = "Undeclared Function: foo"
    assert(checkCkr(input,expected,427))
  }
  test("28. Undeclaration function in expression") {
    val input =
      """
        void foo(int a, int b) {
          a = b + goo();
        }
      """
    val expected = "Undeclared Function: goo"
    assert(checkCkr(input,expected,428))
  }
  test("29. Undeclaration function nest scope") {
    val input =
      """
        void foo() {
          {
            {
              goo(1, 2);
            }
          }
        }
      """
    val expected = "Undeclared Function: goo"
    assert(checkCkr(input,expected,429))
  }

  test("30. Undeclaration function in if statement") {
    val input =
      """
        int foo(){
          if(true) {
            goo();
          } else {
            foo();
          }
        }
      """
    val expected = "Undeclared Function: goo"
    assert(checkCkr(input,expected,430))
  }
  test("31. Undeclaration function in for statement") {
    val input =
      """
        int foo(){
          int i;
          for(i; i < 10; i = i + 1){
            goo();
          }
        }
      """
    val expected = "Undeclared Function: goo"
    assert(checkCkr(input,expected,431))
  }
  test("32. Undeclaration function in do-while statement") {
    val input =
      """
        int foo(){
          do{
            goo();
          } while(true);
        }
      """
    val expected = "Undeclared Function: goo"
    assert(checkCkr(input,expected,432))
  }
  test("33. Valid declared function 1") {
    val input =
      """
        float goo() {
          return 1.2;
        }
        int foo(){
          do{
            goo();
          } while(true);
          return 1;
        }
      """
    val expected = ""
    assert(checkCkr(input,expected,433))
  }


  /****************TYPE MISMATCH IN STATEMENT****************/

  test("34. Type mismatch in statement: Valid statement") {
    val input =
      """
        int i;
        float foo() {
          int a;
          float b;
          boolean c;
          if (c) a; else b;
          for(a; c; a) {
            b = a;
          }
          do{
            b = a * b;
          } while (c);
          return b;
        }
      """
    val expected = ""
    assert(checkCkr(input,expected,434))        
  }

  test("35. Type mismatch in statement: type of if condition must be boolean") {
    val input =
      """
        void foo(int a,int b){
          if(a) a;
        }
      """
    val expected = "Type Mismatch In Statement: "+If(Id("a"),Id("a"),None).toString  
    assert(checkCkr(input,expected,435))        
  }
  test("36. Type mismatch in statement: type of exp2 of FOR must be boolean") {
    val input =
      """
        void foo(int a,int b){
          for (a; a + b; b) {
            a;
          }
        }
      """
    val expected = "Type Mismatch In Statement: "+For(Id("a"),BinaryOp("+",Id("a"),Id("b")),Id("b"),Block(List(),List(Id("a")))).toString
    assert(checkCkr(input,expected,436))
  }
  test("37. Type mismatch in statement: type of exp1 and exp 3 in FOR stmt must be integer") {
    val input =
      """
        int foo(){
          for(1; true; 1.2){
            //something
          }
          return 2;
        }
      """
    val expected = "Type Mismatch In Statement: "+For(IntLiteral(1),BooleanLiteral(true),FloatLiteral(1.2f),Block(List(),List())).toString
    assert(checkCkr(input,expected,437))
  }
  test("38. Type mismatch in statement: type of do while condition must be boolean") {
    val input =
      """
        boolean check;
        float[] foo(boolean a, int b[]){
          do{
            //something
          }
          while 1; //Error line
          return 1.2;
        }
      """
    val expected = "Type Mismatch In Statement: "+ Dowhile(List(Block(List(),List())),IntLiteral(1)).toString
    assert(checkCkr(input,expected,438))
  }
  test("39. Type of do while condition is boolean - no error") {
    val input =
      """
        boolean check;
        float[] foo(boolean check, int a[]){
          float b[2];
          do
            a[2]=a[2]+1;
          while (true);
          return b;
        }
      """
    val expected = ""
    assert(checkCkr(input,expected,339))
  }
  
  /***********RETURN***********/

  test("40. Type mismatch in statement: return type not suitable") {
    val input =
      """
        int main(){
        	return 0;
        }
        int foo(int a){
          return 1.2;
        }
      """

    val expected = "Type Mismatch In Statement: "+ Return(Some(FloatLiteral(1.2f))).toString

    assert(checkCkr(input,expected,440))
  }

  test("41. Type mismatch in statement: return type not suitable in IF stmt") {
    val input =
      """
        int foo(int a, int b){
          if (a < b) {
            return a;
          } else {
            return true;
          }
        }
      """

    val expected = "Type Mismatch In Statement: "+ Return(Some(BooleanLiteral(true))).toString
    assert(checkCkr(input,expected,441))
  }
  test("42. Type mismatch in statement: return type not suitable 2") {
    val input =
      """
        boolean main(){
          return 2;
        }
        float foo(int a){
        	a = a-(a-1);
        	return a+2.3;
        }
      """
    val expected = "Type Mismatch In Statement: "+ Return(Some(IntLiteral(2))).toString
    assert(checkCkr(input,expected,442))
  }
  test("43. Function return type if float can return integer type") {
    val input =
      """
         float foo(int a) {
           a = 2;
           return a;
         }
      """
    val expected = ""
    assert(checkCkr(input,expected,443))
  }
  test("44. Return is string - no error") {
    val input =
      """
        string concat(string substring,int start, int end){
          if(start>end)
            return "";
          else
          {
            return substring;
          }
        }
      """
    val expected = ""
    assert(checkCkr(input,expected,444))
  }
  test("45. Type mismatch in statement: Void function but return something") {
    val input =
      """
        void foo(){
          int a;
          a= 3;
          return a;
        }
      """
    val expected = "Type Mismatch In Statement: "+ Return(Some(Id("a"))).toString
    assert(checkCkr(input,expected,445))
  }
  test("46. Type mismatch in statement: Return void type in void function still error") {
    val input =
      """
        void foo(){
          return foo();
        }
      """
    val expected = "Type Mismatch In Statement: "+ Return(Some(CallExpr(Id("foo"),List()))).toString
    assert(checkCkr(input,expected,446))
  }
  test("47. Type mismatch in statement: Not error in void when not return") {
    val input =
      """
        string a;
        void foo(int a, boolean c[]){
          a = 2;
          c[1] = true;
        }
      """
    val expected = ""
    assert(checkCkr(input,expected,447))
  }
  test("48. Type mismatch in statement: Not error in void when return;") {
    val input =
      """
        string a;
        void check(int a, string b[], boolean c[]){
          return;
        }
      """
    val expected = ""
    assert(checkCkr(input,expected,448))
  }
  test("49. Type Mismatch Return Statement: ArrayPointerType FloatType Exp ArrayPointerType IntType") {
    val input = 
      """
        float[] main(int a[]){ return a;}
      """
    val expected = "Type Mismatch In Statement: "+Return(Some(Id("a"))).toString
    assert(checkCkr(input,expected,449))
  }
  test("50. Type Mismatch Return Statement: Return type of not suitable int->float") {
    val input =
      """ 
        int[] foo (int a) {
          return 1.2;
        }
      """
    val expected = "Type Mismatch In Statement: "+Return(Some(FloatLiteral(1.2f))).toString
    assert(checkCkr(input,expected,450))
  }

  // /****************TYPE MISMATCH IN EXPRESSION*****************************/

  test("51. Valid type Match In BinaryOp(+ - * /) Expression") {
    val input = """
    int a;
    float b;
    int main(){
      a + 2;
      b - 2;
      a * b;
      a / b;
      return 1;
    }"""
    val expected = ""
    assert(checkCkr(input,expected,451))
  }  
  test("52. Invalid type Match In BinaryOp + Expression") {
    val input = """
    int a;
    int main(){
      a + 2;
      a + 1.2;
      3 + a;
      a + "txt";
      return 1;
    }"""
    val expected = "Type Mismatch In Expression: "+BinaryOp("+",Id("a"),StringLiteral("txt")).toString
    assert(checkCkr(input,expected,452))
  }
  test("53. Invalid type Match In BinaryOp - Expression") {
    val input = """
    int[] main(float a){
      a - 1.2;
      2 - a;
      a - false;
      return 1;
    }"""
    val expected = "Type Mismatch In Expression: "+BinaryOp("-",Id("a"),BooleanLiteral(false)).toString
    assert(checkCkr(input,expected,453))
  }
  test("54. Invalid type Match In BinaryOp * / Expression") {
    val input = """
    int[] main(float a, int b){
      a * b;
      b / a;
      a / "txt";
      return 1;
    }"""
    val expected = "Type Mismatch In Expression: "+BinaryOp("/",Id("a"),StringLiteral("txt")).toString
    assert(checkCkr(input,expected,454))
  }

  //BinaryOp(<><=>=)
  test("55. Type Match In BinaryOp > | >= | < | <= Expression") {
    val input = """
    void foo(int a, int b){
      a > b;
      a > 1;
      a >= 20;
      b < 2;
      b <= 100;
    }"""
    val expected = ""
    assert(checkCkr(input,expected,455))
  }

  test("56. Type Match In BinaryOp >= Expression") {
    val input = """
    int a;
    int main(){
      a>=1.2;
      return 1;
    }"""
    val expected = ""
    assert(checkCkr(input,expected,456))
  }
  test("57. Type Match In BinaryOp >= Expression") {
    val input = """
    int a;
    int main(){
      a>=1.2;
      return 1;
    }"""
    val expected = ""
    assert(checkCkr(input,expected,457))
  }
  test("58. Type Mismatch In Expression: > >= <= < can match with String") {
    val input = """
    int a;
    int main(){
      a > "txt"; // familiar with > >= <= <
    }"""
    val expected = "Type Mismatch In Expression: "+BinaryOp(">",Id("a"),StringLiteral("txt")).toString
    assert(checkCkr(input,expected,458))
  }
  test("59. Type Mismatch In Expression: > >= <= < can match with Boolean") {
    val input = """
    int a;
    int main(){
      a <= true; // familiar with > >= <= <
    }"""
    val expected = "Type Mismatch In Expression: "+BinaryOp("<=",Id("a"),BooleanLiteral(true)).toString
    assert(checkCkr(input,expected,459))
  }
  
  // ASSIGN =
  test("60. Valid type Match In BinaryOp(=) Expression") {
    val input = """
      int foo(int a, float b, boolean boo, string str) {
        a = 2;
        b = 1.2;
        b = a;
        str = "Tran Nhu Luc - AoaAnhs2303";
        boo = true = false;
        return 0;
      }
    """
    val expected = ""
    assert(checkCkr(input,expected,460))
  }
  test("61. Type Mismatch In Expression: = CAN NOT match between int -> float") {
    val input = """
      int a;
      int main(){
        a = 1.2;
        return 1;
      }
    """
    val expected = "Type Mismatch In Expression: "+BinaryOp("=",Id("a"),FloatLiteral(1.2f)).toString
    assert(checkCkr(input,expected,461))
  }
  test("62. Type Mismatch In Expression: = CAN match between float -> int") {
    val input = """
      float a;
      int main(){
        a = 10;
        return 1;
      }
    """
    val expected = ""
    assert(checkCkr(input,expected,462))
  }
  test("63. Type Mismatch In Expression: = CAN match between int -> string") {
    val input = """
      float a;
      int main(){
        a = "hello";
      }
    """
    val expected = "Type Mismatch In Expression: "+BinaryOp("=",Id("a"),StringLiteral("hello")).toString
    assert(checkCkr(input,expected,463))
  }
  test("64. Type Mismatch In Expression: BinaryOp(=) Expression Variable ArrayType And Variable ArrayType") {
    val input = """
    int a[3],b[3];
    int main(){
      a=b;
      return 1;
    }"""
    val expected = "Type Mismatch In Expression: "+BinaryOp("=",Id("a"),Id("b")).toString
    assert(checkCkr(input,expected,464))
  }
  test("65. Type Mismatch In Expression: = CAN match between ArrayPointerType(float) -> boolean") {
    val input = """
      float a[10];
      int main(){
        a[1] = true;
      }
    """
    val expected = "Type Mismatch In Expression: "+BinaryOp("=",ArrayCell(Id("a"),IntLiteral(1)),BooleanLiteral(true)).toString
    assert(checkCkr(input,expected,465))
  }
  //Modulo %
  test("66. Valid type Match In BinaryOp(%) Expression") {
    val input = """
      int foo(int a, int b) {
        a = a % b;
        a % 3;
        return b;
      }
    """
    val expected = ""
    assert(checkCkr(input,expected,466))
  }
  test("67. Type Mismatch In Expression: % CAN match between int and float") {
    val input = """
      float a;
      int main(int b){
        10 % b;
        b % a; //Error
      }
    """
    val expected = "Type Mismatch In Expression: "+BinaryOp("%",Id("b"),Id("a")).toString
    assert(checkCkr(input,expected,467))
  }
  test("68. Type Mismatch In Expression: % CAN match between float and boolean,...") {
    val input = """
      float a;
      int main(int b){
        b % 2;
        b % true;
      }
    """
    val expected = "Type Mismatch In Expression: "+BinaryOp("%",Id("b"),BooleanLiteral(true)).toString
    assert(checkCkr(input,expected,468))
  }
  // Expresionn || &&
  test("69. Valid type Match In BinaryOp(|| &&) Expression") {
    val input = """
      void foo(boolean a, boolean b) {
        a && b;
        a && true;
        false && a;
        
        a || b;
        a || true;
        false || a;
        false || true;

       (a && b) || a;
       (a || b) && true;   
      }
    """
    val expected = ""
    assert(checkCkr(input,expected,469))
  }
  test("70. Type Mismatch In Expression: && or || CAN'T match all type expect Boolean vs Boolean 1") {
    val input = """
      boolean a;
      int main(int b){
        a && 1000;
      }
    """
    val expected = "Type Mismatch In Expression: "+BinaryOp("&&",Id("a"),IntLiteral(1000)).toString
    assert(checkCkr(input,expected,470))
  }
  test("71. Type Mismatch In Expression: && or || CAN'T match all type expect Boolean vs Boolean 2") {
    val input = """
      boolean a;
      int main(int b){
        (a && true) && "true";
      }
    """
    val expected = "Type Mismatch In Expression: "+BinaryOp("&&",BinaryOp("&&",Id("a"),BooleanLiteral(true)),StringLiteral("true")).toString
    assert(checkCkr(input,expected,471))
  }
  test("72. Type Mismatch In Expression: && or || CAN'T match all type expect Boolean vs Boolean 3") {
    val input = """
      boolean a;
      int main(int b){
        (1.2 || a) && true;
      }
    """
    val expected = "Type Mismatch In Expression: "+BinaryOp("||",FloatLiteral(1.2f),Id("a")).toString
    assert(checkCkr(input,expected,472))
  }

  //UnaryOp(-!)
  test("73. Valid type Match In UnaryOp(! -) Expression") {
    val input = """
      void foo(int a, float b, boolean c) {
        a = 10;
        -a;
        -b;
        -100;
        -1.2;

        !c;
        !true;
        !false;      
      }
    """
    val expected = ""
    assert(checkCkr(input,expected,473))
  }

  test("74. Type Mismatch In Expression: - CAN'T match with BooleanType") {
    val input = """
      boolean a;
      int main(int b){
        -199;
        -a;
      }
    """
    val expected = "Type Mismatch In Expression: "+UnaryOp("-",Id("a")).toString
    assert(checkCkr(input,expected,474))
  }

  test("75. Type Mismatch In Expression: - CAN'T match with StringType") {
    val input = """
      boolean a;
      int main(int b){
        -"hello";
      }
    """
    val expected = "Type Mismatch In Expression: "+UnaryOp("-",StringLiteral("hello")).toString
    assert(checkCkr(input,expected,475))
  }

  test("76. Type Mismatch In Expression: ! CAN'T match with StringType") {
    val input = """
      int main(int b){
        !false;
        !"hello";
      }
    """
    val expected = "Type Mismatch In Expression: "+UnaryOp("!",StringLiteral("hello")).toString
    assert(checkCkr(input,expected,476))
  }
  test("77. Type Mismatch In Expression: ! CAN'T match with IntType or FloatType") {
    val input = """
      int main(int b){
        b = 10;
        !b;
        return b;
      }
    """
    val expected = "Type Mismatch In Expression: "+UnaryOp("!",Id("b")).toString
    assert(checkCkr(input,expected,477))
  }

  //ArrayCell
  test("78. Valid type Match ArrayCell") {
    val input = """
      int a[10];
      int foo(int b[], int c[]) {
        a[1 + 2];
        a[b[1]];
        c[b[a[2]]];
        a[b[2] + c[2]];
        a[b[2] - c[2]];

        return a[3];
      }
    """
    val expected = ""
    assert(checkCkr(input,expected,478))
  }
  
  test("79. Type Mismatch ArrayCell a[1]") {
    val input = """
    int a;
    int main(){
      a[1];
      return 1;
    }"""
    val expected = "Type Mismatch In Expression: "+ArrayCell(Id("a"),IntLiteral(1)).toString
    assert(checkCkr(input,expected,479))
  }
  test("80. Type Mismatch In Expression: ArrayCell CAN'T match with FloatType") {
    val input = """
      int main(int b[]){
        b[1.2] = 2;
      }
    """
    val expected = "Type Mismatch In Expression: "+ArrayCell(Id("b"),FloatLiteral(1.2f)).toString
    assert(checkCkr(input,expected,480))
  }
  test("81. Type Mismatch In Expression: ArrayCell CAN'T match with BooleanType") {
    val input = """
      int main(int b[]){
        boolean c;
        c = true;
        b[c] = 2;
      }
    """
    val expected = "Type Mismatch In Expression: "+ArrayCell(Id("b"),Id("c")).toString
    assert(checkCkr(input,expected,481))
  }
  test("82. Type Mismatch In Expression: getInt") {
    val input = "void main () {getInt(3);}"
    val expected = "Type Mismatch In Expression: " + CallExpr(Id("getInt"),List(IntLiteral(3))).toString
    assert(checkCkr(input,expected,482))
  }

  //CallExpression
  test("83. Type Mismatch In Expression: CallExpr not equal size of argument") {
    val input = """
      int foo(int a, float b) {
        foo();
      }
      
    """
    val expected = "Type Mismatch In Expression: " + CallExpr(Id("foo"),List()).toString
    assert(checkCkr(input,expected,483))
  }
  test("84. Type Mismatch In Expression: CallExpr not same type each other") {
    val input = """
      void foo(int a, float b) {
        foo(10, true);
      }
    """
    val expected = "Type Mismatch In Expression: " + CallExpr(Id("foo"),List(IntLiteral(10),BooleanLiteral(true))).toString
    assert(checkCkr(input,expected,484))
  }
  test("85. Valid CallExpr") {
    val input = """
      void foo(int a, float b) {
        foo(1, 1.2);
      }
      int goo(boolean b, string c, float f) {
        goo(true, "hello", 1.2);
        return 2;
      }
    """
    val expected = ""
    assert(checkCkr(input,expected,485))
  }

  /****************FUNCTION NOT RETURN*****************************/

  test("86. Function not return: function not return in scope") {
    val input = """
      int foo() {
        int a[10];
        a[1] = 2;
      }
    """
    val expected = "Function Not Return: foo"
    assert(checkCkr(input,expected,486))
  }
  test("87. Function not return: function not return in then path") {
    val input =
      """
        int foo(int a,float b){
          if(a>1){
            return 2;
          } else {
            a = 2;
          }
        }
      """
    val expected = "Function Not Return: foo"
    assert(checkCkr(input,expected,487))
  }
  test("88. Function not return: function not return in else path") {
    val input =
      """
        int foo(int a,float b){
          if(a>1){
            //something
          } else {
            return 2;
          }
        }
      """
    val expected = "Function Not Return: foo"
    assert(checkCkr(input,expected,488))
  }
  test("89. Function return: IF stmt return in all path") {
    val input = """
      int foo(int a,float b){
          if(a>1){
            return 2;
          } else {
            a = 2;
            return a;
          }
        }
    """
    val expected = ""
    assert(checkCkr(input,expected,489))
  }

  /****************BREAK/CONTINUE NOT IN LOOP*****************************/

  test("90. Break not in loop: break outside for statement") {
    val input =
      """
        void foo() {
          int a;
          break;
        }
      """
    val expected = "Break Not In Loop"
    assert(checkCkr(input,expected,490))
  }
  test("91. Break not in loop: break outside do while statement") {
    val input =
      """
           void foo(){
                do{
                  2;
                } while (true);
                break;
           }"""
    val expected = "Break Not In Loop"
    assert(checkCkr(input,expected,491))
  }
  test("92. Continue not in loop: continue outsite for loop") {
    val input =
      """
      void main() {
        int i;
        for (i; i < 10; i = i * 2) {
          i = i + 1;
        }          
        continue;  
      }
    """
    val expected = "Continue Not In Loop"
    assert(checkCkr(input,expected,492))
  }
  test("93. Break, Continue in for loop, Dowhile - no error") {
    val input =
      """
        int a[5], b[7];
        void foo() {
          do{
            a[1] = b[3];
            break;
          } while(true);
          for(a[1]; a[1] < b[3]; a[1] = a[1] * a[2]) {
            a[1] = 2;
            continue;
          }
        }
      """
    val expected = ""
    assert(checkCkr(input,expected,493))
  }
  
  test("94. Continue in for loop, Dowhile - no error") {
    val input =
      """
        int foo(){
            boolean a[2];
            do do{for(1;true;2) {continue;}}while false; while true;
            return 2;
        }
      """
    val expected = ""
    assert(checkCkr(input,expected,494))
  }

  // /****************UNREACHABLE STATEMENT*****************************/

  // test("Unreachable statement: unreachable statement after return in if statement") {
  //   val input = " boolean boo(int a[]){do{if(true) return true; else return false;} 1=a[2]; while true; a[1]=a[1]+1;} "
  //   val expected = "Unreachable Statement: "+BinaryOp("=",IntLiteral(1),ArrayCell(Id("a"),IntLiteral(2))).toString
  //   assert(checkCkr(input,expected,494))
  // }
  // test("Unreachable statement: No unreachable statement after break - no error") {
  //   val input =
  //         """
  //     int m;
  //     float n;
  //     float result;
  //     float mul_operation(int a,int b){
  //       do
  //       if(a!=0&&b!=0){
  //         break;
  //         }
  //       else
  //         return -1;
  //         while true;
  //         a=1;
  //         return 2.4;
  //     }

  //   """
  //   val expected = ""
  //   assert(checkCkr(input,expected,495))
  // }
  // test("Unreachable statement: unreachable statement after continue") {
  //   val input =
  //         """
  //     int static_void_main(){
  //       int a,b;
  //       float result;
  //       if(a>b)
  //         a;
  //       else return a;
  //       do
  //         continue;
  //         if(a>1) return a; //Unreachable line
  //         else return b;
  //       while (true);
  //       return 1;
  //     }

  //   """
  //   val expected = "Unreachable Statement: "+If(BinaryOp(">",Id("a"),IntLiteral(1)),Return(Some(Id("a"))),Some(Return(Some(Id("b"))))).toString
  //   assert(checkCkr(input,expected,496))
  // }
  // test("Unreachable statement: unreachable statement after do while contain return") {
  //   val input =
  //           """
  //       void main(){
  //         int a,b,c;
  //         int index[10];
  //         for(a=1;a<10;a=a+1){
  //           main();
  //         }
  //         do{
  //           main();
  //           if(a>b)
  //             return;
  //           else main();
  //           return;
  //         }
  //         while(a==10);
  //         a=a+b;  //Unreachable line
  //       }
  //     """
  //   val expected = "Unreachable Statement: "+BinaryOp("=",Id("a"),BinaryOp("+",Id("a"),Id("b"))).toString
  //   assert(checkCkr(input,expected,497))
  // }
  // test("Unreachable statement: unreachable statement if else statement contain return") {
  //   val input =
  //               """
  //           float n;
  //           int checkOddNumber(int n){
  //             if(n%2==0)
  //               return n;
  //             else
  //             return checkOddNumber(n+1);
  //             n=1%n;  //Unreachable line
  //           }
  //         """
  //   val expected = "Unreachable Statement: "+BinaryOp("=",Id("n"),BinaryOp("%",IntLiteral(1),Id("n"))).toString
  //   assert(checkCkr(input,expected,498))
  // }
  // test("Unreachable statement: unreachable statement after block return") {
  //   val input =
  //           """
  //       float n;
  //       void main(){
  //         int a,b,c;
  //         int index[10];
  //         for(a=1;a<10;a=a+1){
  //           main();
  //         }
  //         {return;}
  //         do{ //Unreachable line
  //           main();
  //           if(a>b)
  //             return;
  //           else main();
  //         }
  //         while(a==10);
  //       }
  //     """
  //   val expected = "Unreachable Statement: "+Dowhile(List(Block(List(),List(CallExpr(Id("main"),List()),If(BinaryOp(">",Id("a"),Id("b")),Return(None),Some(CallExpr(Id("main"),List())))))),BinaryOp("==",Id("a"),IntLiteral(10))).toString
  //   assert(checkCkr(input,expected,499))
  // }
  // test("Unreachable statement: unreachable statement after return expression") {
  //   val input =
  //           """
  //       float n;
  //       int checkOddNumber(int n){
  //         if(n%2==0)
  //           n=1;
  //         else
  //         {}
  //         return checkOddNumber(n+1);
  //         n=n*n*n;    //Unreachable line
  //       }
  //     """
  //   val expected = "Unreachable Statement: "+ BinaryOp("=",Id("n"),BinaryOp("*",BinaryOp("*",Id("n"),Id("n")),Id("n"))).toString
  //   assert(checkCkr(input,expected,500))
  // }





}