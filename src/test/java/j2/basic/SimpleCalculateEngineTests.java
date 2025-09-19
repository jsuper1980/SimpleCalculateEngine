package j2.basic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * SimpleCalculateEngine 全面测试用例
 * 覆盖所有功能：基础运算、数学函数、单元格管理、依赖管理、错误处理等
 */
public class SimpleCalculateEngineTests {

  private SimpleCalculateEngine engine;

  @BeforeEach
  public void setUp() {
    engine = new SimpleCalculateEngine();
  }

  @AfterEach
  public void tearDown() {
    if (engine != null) {
      engine.shutdown();
    }
  }

  // ==================== 基础数学运算测试 ====================

  @Test
  @DisplayName("测试基础加减运算")
  public void testBasicAdditionSubtraction() {
    engine.setCellValue("A1", "=10+20");
    System.out.println("10+20 = " + engine.getCellValue("A1"));
    assertEquals(new BigDecimal("30"), engine.getCellValueNumber("A1"));

    engine.setCellValue("A2", "=50-30");
    System.out.println("50-30 = " + engine.getCellValue("A2"));
    assertEquals(new BigDecimal("20"), engine.getCellValueNumber("A2"));

    engine.setCellValue("A3", "=10+20-5.1");
    System.out.println("10+20-5.1 = " + engine.getCellValue("A3"));
    assertEquals("24.9", engine.getCellValue("A3"));

    // 测试负数
    engine.setCellValue("A4", "=-10+5");
    System.out.println("-10+5 = " + engine.getCellValue("A4"));
    assertEquals(new BigDecimal("-5"), engine.getCellValueNumber("A4"));
  }

  @Test
  @DisplayName("测试基础乘除运算")
  public void testBasicMultiplicationDivision() {
    engine.setCellValue("B1", "=10*20");
    System.out.println("10*20 = " + engine.getCellValue("B1"));
    assertEquals("200", engine.getCellValue("B1"));

    engine.setCellValue("B2", "=100/4");
    System.out.println("100/4 = " + engine.getCellValueNumber("B2"));
    assertEquals("25", engine.getCellValue("B2"));

    engine.setCellValue("B3", "=10*2/4");
    System.out.println("10*2/4 = " + engine.getCellValue("B3"));
    assertEquals("5", engine.getCellValue("B3"));
  }

  @Test
  @DisplayName("测试整数除和余数除运算")
  public void testIntegerDivisionAndModulo() {
    // 测试整数除法 \\
    engine.setCellValue("B4", "=17\\5");
    System.out.println("17\\5 = " + engine.getCellValue("B4"));
    assertEquals("3", engine.getCellValue("B4")); // 17除以5的整数部分是3

    engine.setCellValue("B5", "=100\\7");
    System.out.println("100\\7 = " + engine.getCellValue("B5"));
    assertEquals("14", engine.getCellValue("B5")); // 100除以7的整数部分是14

    // 测试余数运算 %
    engine.setCellValue("B6", "=17%5");
    System.out.println("17%5 = " + engine.getCellValue("B6"));
    assertEquals("2", engine.getCellValue("B6")); // 17除以5的余数是2

    engine.setCellValue("B7", "=100%7");
    System.out.println("100%7 = " + engine.getCellValue("B7"));
    assertEquals("2", engine.getCellValue("B7")); // 100除以7的余数是2

    // 测试负数的整数除法和余数
    engine.setCellValue("B8", "=-17\\5");
    System.out.println("-17\\5 = " + engine.getCellValue("B8"));
    assertEquals("-4", engine.getCellValue("B8")); // -17除以5向下取整是-4

    engine.setCellValue("B9", "=-17%5");
    System.out.println("-17%5 = " + engine.getCellValue("B9"));
    assertEquals("3", engine.getCellValue("B9")); // -17除以5的余数是3（数学模运算）

    // 测试小数的整数除法
    engine.setCellValue("B10", "=17.8\\5.2");
    System.out.println("17.8\\5.2 = " + engine.getCellValue("B10"));
    assertEquals("3", engine.getCellValue("B10")); // 17.8除以5.2向下取整是3

    // 测试除零错误
    assertThrows(RuntimeException.class, () -> {
      engine.setCellValue("B11", "=10\\0");
      String result = engine.getCellValue("B11");
      if (result.equals("#ERROR#")) {
        throw new RuntimeException("除零错误");
      }
    }, "整数除零应该抛出异常");

    assertThrows(RuntimeException.class, () -> {
      engine.setCellValue("B12", "=10%0");
      String result = engine.getCellValue("B12");
      if (result.equals("#ERROR#")) {
        throw new RuntimeException("除零错误");
      }
    }, "余数除零应该抛出异常");
  }

  @Test
  @DisplayName("测试幂运算")
  public void testPowerOperation() {
    engine.setCellValue("C1", "=2^3");
    System.out.println("2^3 = " + engine.getCellValue("C1"));
    assertEquals("8", engine.getCellValue("C1"));

    engine.setCellValue("C2", "=3^2");
    System.out.println("3^2 = " + engine.getCellValue("C2"));
    assertEquals("9", engine.getCellValue("C2"));

    engine.setCellValue("C3", "=2^3^2"); // 右结合：2^(3^2) = 2^9 = 512
    System.out.println("2^3^2 = " + engine.getCellValue("C3"));
    assertEquals("512", engine.getCellValue("C3"));
  }

  @Test
  @DisplayName("测试括号运算")
  public void testParentheses() {
    engine.setCellValue("D1", "=(10+20)*3");
    System.out.println("(10+20)*3 = " + engine.getCellValue("D1"));
    assertEquals("90", engine.getCellValue("D1"));

    engine.setCellValue("D2", "=10+(20*3)");
    System.out.println("10+(20*3) = " + engine.getCellValue("D2"));
    assertEquals("70", engine.getCellValue("D2"));

    engine.setCellValue("D3", "=((10+5)*2)^2");
    System.out.println("((10+5)*2)^2 = " + engine.getCellValue("D3"));
    assertEquals("900", engine.getCellValue("D3"));
  }

  @Test
  @DisplayName("测试运算符优先级")
  public void testOperatorPrecedence() {
    engine.setCellValue("E1", "=10+20*3");
    System.out.println("10+20*3 = " + engine.getCellValue("E1"));
    assertEquals("70", engine.getCellValue("E1")); // 先乘后加

    engine.setCellValue("E2", "=2^3*4");
    System.out.println("2^3*4 = " + engine.getCellValue("E2"));
    assertEquals("32", engine.getCellValue("E2")); // 先幂后乘

    engine.setCellValue("E3", "=2+3*4^2");
    System.out.println("2+3*4^2 = " + engine.getCellValue("E3"));
    assertEquals("50", engine.getCellValue("E3")); // 2+3*16=50
  }

  // ==================== 单元格管理测试 ====================

  @Test
  @DisplayName("测试单元格基本操作")
  public void testBasicCellOperations() {
    // 设置数值
    engine.setCellValue("F1", 100);
    System.out.println("F1 = " + engine.getCellValue("F1"));
    assertEquals("100", engine.getCellValue("F1"));

    // 设置字符串
    engine.setCellValue("F2", "Hello");
    System.out.println("F2 = " + engine.getCellValue("F2"));
    assertEquals("Hello", engine.getCellValue("F2"));

    // 设置公式
    engine.setCellValue("F3", "=F1+50");
    System.out.println("F3 = F1+50 = " + engine.getCellValue("F3"));
    assertEquals(BigDecimal.valueOf(150), engine.getCellValueNumber("F3"));
  }

  @Test
  @DisplayName("测试单元格引用")
  public void testCellReferences() {
    engine.setCellValue("G1", 10);
    engine.setCellValue("G2", 20);
    engine.setCellValue("G3", "=G1+G2");

    System.out.println("G1 = " + engine.getCellValue("G1"));
    System.out.println("G2 = " + engine.getCellValue("G2"));
    System.out.println("G3 = G1+G2 = " + engine.getCellValue("G3"));
    assertEquals("30", engine.getCellValue("G3"));

    // 修改被引用的单元格，检查联动更新
    engine.setCellValue("G1", 15);
    System.out.println("G3 = " + engine.getCellValue("G3"));
    assertEquals("35", engine.getCellValue("G3"));
  }

  @Test
  @DisplayName("测试复杂单元格引用")
  public void testComplexCellReferences() {
    engine.setCellValue("H1", 5);
    engine.setCellValue("H2", 10);
    engine.setCellValue("H3", "=H1*H2");
    engine.setCellValue("H4", "=H3+H1");
    engine.setCellValue("H5", "=(H1+H2)*H3");

    System.out.println("H1 = " + engine.getCellValue("H1"));
    System.out.println("H2 = " + engine.getCellValue("H2"));
    System.out.println("H3 = H1*H2 = " + engine.getCellValue("H3"));
    System.out.println("H4 = H3+H1 = " + engine.getCellValue("H4"));
    System.out.println("H5 = (H1+H2)*H3 = " + engine.getCellValue("H5"));

    assertEquals(new BigDecimal("50"), engine.getCellValueNumber("H3")); // 5*10
    assertEquals(new BigDecimal("55"), engine.getCellValueNumber("H4")); // 50+5
    assertEquals(new BigDecimal("750"), engine.getCellValueNumber("H5")); // (5+10)*50
  }

  // ==================== 数学函数测试 ====================

  @Test
  @DisplayName("测试基础数学函数")
  public void testBasicMathFunctions() {
    engine.setCellValue("I1", "=sqrt(25)");
    System.out.println("sqrt(25) = " + engine.getCellValue("I1"));
    assertEquals("5", engine.getCellValue("I1"));

    engine.setCellValue("I2", "=abs(-10)");
    System.out.println("abs(-10) = " + engine.getCellValue("I2"));
    assertEquals("10", engine.getCellValue("I2"));

    engine.setCellValue("I3", "=ceil(4.3)");
    System.out.println("ceil(4.3) = " + engine.getCellValue("I3"));
    assertEquals("5", engine.getCellValue("I3"));

    engine.setCellValue("I4", "=floor(4.7)");
    System.out.println("floor(4.7) = " + engine.getCellValue("I4"));
    assertEquals("4", engine.getCellValue("I4"));

    engine.setCellValue("I5", "=round(4.6)");
    System.out.println("round(4.6) = " + engine.getCellValue("I5"));
    assertEquals("5", engine.getCellValue("I5"));
  }

  @Test
  @DisplayName("测试三角函数")
  public void testTrigonometricFunctions() {
    // 设置π值
    engine.setCellValue("π", Math.PI);
    System.out.println("π = " + engine.getCellValue("π"));

    engine.setCellValue("J1", "=sin(π/2)");
    System.out.println("sin(π/2) = " + engine.getCellValue("J1") + ", java sin(π/2) = " + Math.sin(Math.PI / 2));
    assertEquals(1.0, Double.parseDouble(engine.getCellValue("J1")));
    engine.setCellValue("J1_1", "=sin(90*π/180)");
    System.out.println("sin(90°) = " + engine.getCellValue("J1_1") + ", java sin(90°) = " + Math.sin(90 * Math.PI / 180));
    assertEquals(1.0, Double.parseDouble(engine.getCellValue("J1_1")));

    engine.setCellValue("J2", "=cos(0)");
    System.out.println("cos(0) = " + engine.getCellValue("J2") + ", java cos(0) = " + Math.cos(0));
    assertEquals("1", engine.getCellValue("J2"));

    engine.setCellValue("J3", "=tan(π/4)");
    System.out.println("tan(π/4) = " + engine.getCellValue("J3") + ", java tan(π/4) = " + Math.tan(Math.PI / 4));
    assertEquals(1.0, Double.parseDouble(engine.getCellValue("J3")), 1e-10);

    engine.setCellValue("J4", "=asin(1)");
    System.out.println("asin(1) = " + engine.getCellValue("J4") + ", java asin(1) = " + Math.asin(1));
    assertEquals(Math.PI / 2, Double.parseDouble(engine.getCellValue("J4")), 1e-10);
  }

  @Test
  @DisplayName("测试对数函数")
  public void testLogarithmicFunctions() {
    engine.setCellValue("K1", "=log(exp(2))");
    System.out.println("log(exp(2)) = " + engine.getCellValue("K1") + ", java log(exp(2)) = " + Math.log(Math.exp(2)));
    assertEquals(2.0, Double.parseDouble(engine.getCellValue("K1")), 1e-10);

    engine.setCellValue("K2", "=log10(100)");
    System.out.println("log10(100) = " + engine.getCellValue("K2") + ", java log10(100) = " + Math.log10(100));
    assertEquals("2", engine.getCellValue("K2"));

    engine.setCellValue("K3", "=exp(0)");
    System.out.println("exp(0) = " + engine.getCellValue("K3") + ", java exp(0) = " + Math.exp(0));
    assertEquals("1", engine.getCellValue("K3"));
  }

  @Test
  @DisplayName("测试多参数函数")
  public void testMultiParameterFunctions() {
    engine.setCellValue("L1", "=pow(2,10)");
    System.out.println("pow(2,10) = " + engine.getCellValue("L1"));
    assertEquals("1024", engine.getCellValue("L1"));

    engine.setCellValue("L2", "=min(10,20,5,30)");
    System.out.println("min(10,20,5,30) = " + engine.getCellValue("L2"));
    assertEquals("5", engine.getCellValue("L2"));

    engine.setCellValue("L3", "=max(10,20,5,30)");
    System.out.println("max(10,20,5,30) = " + engine.getCellValue("L3"));
    assertEquals("30", engine.getCellValue("L3"));

    engine.setCellValue("L4", "=avg(10,20,30)");
    System.out.println("avg(10,20,30) = " + engine.getCellValue("L4"));
    assertEquals("20", engine.getCellValue("L4"));

    engine.setCellValue("L5", "=round(3.14159,2)");
    System.out.println("round(3.14159,2) = " + engine.getCellValue("L5"));
    assertEquals("3.14", engine.getCellValue("L5"));
  }

  // ==================== 依赖管理测试 ====================

  @Test
  @DisplayName("测试依赖链更新")
  public void testDependencyChainUpdate() {
    engine.setCellValue("M1", 10);
    engine.setCellValue("M2", "=M1*2");
    engine.setCellValue("M3", "=M2+5");
    engine.setCellValue("M4", "=M3^2");

    System.out.println("M1 = " + engine.getCellValue("M1"));
    System.out.println("M2 = M1*2 = " + engine.getCellValue("M2"));
    System.out.println("M3 = M2+5 = " + engine.getCellValue("M3"));
    System.out.println("M4 = M3^2 = " + engine.getCellValue("M4"));

    assertEquals("20", engine.getCellValue("M2"));
    assertEquals("25", engine.getCellValue("M3"));
    assertEquals("625", engine.getCellValue("M4"));

    // 修改根节点，检查整个依赖链更新
    engine.setCellValue("M1", 5);

    System.out.println("修改 M1 为 5 后：");
    System.out.println("M1 = " + engine.getCellValue("M1"));
    System.out.println("M2 = M1*2 = " + engine.getCellValue("M2"));
    System.out.println("M3 = M2+5 = " + engine.getCellValue("M3"));
    System.out.println("M4 = M3^2 = " + engine.getCellValue("M4"));

    assertEquals("10", engine.getCellValue("M2"));
    assertEquals("15", engine.getCellValue("M3"));
    assertEquals("225", engine.getCellValue("M4"));
  }

  @Test
  @DisplayName("测试复杂依赖网络")
  public void testComplexDependencyNetwork() {
    engine.setCellValue("N1", 10);
    engine.setCellValue("N2", 3);
    engine.setCellValue("N3", "=N1+N2");
    engine.setCellValue("N4", "=N1*N2");
    engine.setCellValue("N5", "=N3+N4");
    engine.setCellValue("N6", "=N3*N4");

    System.out.println("N1 = " + engine.getCellValue("N1"));
    System.out.println("N2 = " + engine.getCellValue("N2"));
    System.out.println("N3 = N1+N2 = " + engine.getCellValue("N3"));
    System.out.println("N4 = N1*N2 = " + engine.getCellValue("N4"));
    System.out.println("N5 = N3+N4 = " + engine.getCellValue("N5"));
    System.out.println("N6 = N3*N4 = " + engine.getCellValue("N6"));

    assertEquals("13", engine.getCellValue("N3")); // 10+3
    assertEquals("30", engine.getCellValue("N4")); // 10*3
    assertEquals("43", engine.getCellValue("N5")); // 13+30
    assertEquals("390", engine.getCellValue("N6")); // 13*30

    // 修改一个根节点
    engine.setCellValue("N1", 5);

    System.out.println("修改 N1 为 5 后：");
    System.out.println("N3 = N1+N2 = " + engine.getCellValue("N3"));
    System.out.println("N4 = N1*N2 = " + engine.getCellValue("N4"));
    System.out.println("N5 = N3+N4 = " + engine.getCellValue("N5"));
    System.out.println("N6 = N3*N4 = " + engine.getCellValue("N6"));

    assertEquals("5", engine.getCellValue("N1"));
    assertEquals("8", engine.getCellValue("N3"));
    assertEquals("15", engine.getCellValue("N4"));
    assertEquals("23", engine.getCellValue("N5"));
    assertEquals("120", engine.getCellValue("N6"));
  }

  // ==================== 错误处理测试 ====================

  @Test
  @DisplayName("测试除零错误")
  public void testDivisionByZeroError() {
    engine.setCellValue("O1", "=10/0");
    System.out.println("O1 = 10/0 = " + engine.getCellValue("O1"));
    assertEquals("#ERROR#", engine.getCellValue("O1"));

    engine.setCellValue("O2", 0);
    engine.setCellValue("O3", "=10/O2");
    System.out.println("O2 = " + engine.getCellValue("O2"));
    System.out.println("O3 = 10/O2 = " + engine.getCellValue("O3"));
    assertEquals("#ERROR#", engine.getCellValue("O3"));
  }

  @Test
  @DisplayName("测试循环引用检测")
  public void testCircularReferenceDetection() {
    try {
      engine.setCellValue("P1", "=P2+1");
      engine.setCellValue("P2", "=P1+1");
    } catch (RuntimeException e) {
      System.out.println("异常信息：" + e.getMessage());
    }

    assertThrows(RuntimeException.class, () -> {
      engine.setCellValue("P1", "=P2+1");
      engine.setCellValue("P2", "=P1+1");
    });

    assertThrows(RuntimeException.class, () -> {
      engine.setCellValue("P3", "=P4+1");
      engine.setCellValue("P4", "=P5+1");
      engine.setCellValue("P5", "=P3+1");
    });
  }

  @Test
  @DisplayName("测试函数参数错误")
  public void testFunctionParameterErrors() {
    // sqrt负数
    engine.setCellValue("Q1", "=sqrt(-1)");
    System.out.println("Q1 = sqrt(-1) = " + engine.getCellValue("Q1"));
    assertEquals(SimpleCalculateEngine.ERROR, engine.getCellValue("Q1"));

    // asin超出范围
    engine.setCellValue("Q2", "=asin(2)");
    System.out.println("Q2 = asin(2) = " + engine.getCellValue("Q2"));
    assertEquals(SimpleCalculateEngine.ERROR, engine.getCellValue("Q2"));

    // log非正数
    engine.setCellValue("Q3", "=log(-1)");
    System.out.println("Q3 = log(-1) = " + engine.getCellValue("Q3"));
    assertEquals(SimpleCalculateEngine.ERROR, engine.getCellValue("Q3"));

    // 不存在的函数 - 注意：可能被解析为单元格引用
    engine.setCellValue("Q4", "=invalidFunc(1)");
    System.out.println("Q4 = invalidFunc(1) = " + engine.getCellValue("Q4"));
    assertEquals(SimpleCalculateEngine.ERROR, engine.getCellValue("Q4"));
  }

  @Test
  @DisplayName("测试语法错误")
  public void testSyntaxErrors() {
    // 括号不匹配
    engine.setCellValue("R1", "=(10+20");
    System.out.println("R1 = (10+20 = " + engine.getCellValue("R1"));
    assertEquals(SimpleCalculateEngine.ERROR, engine.getCellValue("R1"));

    // 无效表达式 - 使用更明确的错误语法
    engine.setCellValue("R2", "=10*/20");
    System.out.println("R2 = 10*/20 = " + engine.getCellValue("R2"));
    assertEquals(SimpleCalculateEngine.ERROR, engine.getCellValue("R2"));

    // 空表达式
    engine.setCellValue("R3", "=");
    System.out.println("R3 = = " + engine.getCellValue("R3"));
    assertEquals(SimpleCalculateEngine.ERROR, engine.getCellValue("R3"));
  }

  // ==================== Java调用测试 ====================

  @Test
  @DisplayName("测试jcall函数调用Java静态方法")
  public void testJavaCallFunction() {
    // 调用Math.max
    engine.setCellValue("S1", "=jcall(\"java.lang.Math\",\"max\",10,20)");
    System.out.println("S1 = jcall(\"java.lang.Math\",\"max\",10,20) = " + engine.getCellValue("S1"));
    assertEquals("20", engine.getCellValue("S1"));

    // 调用Math.min
    engine.setCellValue("S2", "=jcall(\"java.lang.Math\",\"min\",10,20)");
    System.out.println("S2 = jcall(\"java.lang.Math\",\"min\",10,20) = " + engine.getCellValue("S2"));
    assertEquals("10", engine.getCellValue("S2"));

    // 调用Math.abs
    engine.setCellValue("S3", "=jcall(\"java.lang.Math\",\"abs\",-15)");
    System.out.println("S3 = jcall(\"java.lang.Math\",\"abs\",-15) = " + engine.getCellValue("S3"));
    assertEquals("15", engine.getCellValue("S3"));

    // 调用String.valueOf - 返回数值类型
    engine.setCellValue("S4", "=jcall(\"java.lang.Integer\",\"valueOf\",123)");
    System.out.println("S4 = jcall(\"java.lang.Integer\",\"valueOf\",123) = " + engine.getCellValue("S4"));
    assertEquals("123", engine.getCellValue("S4"));

    engine.setCellValue("S5", "=jcall(\"j2.basic.MathUtils\",\"factorial\",5)");
    System.out.println("S5 = jcall(\"j2.basic.MathUtils\",\"factorial\",5) = " + engine.getCellValue("S5"));
    assertEquals("120", engine.getCellValue("S5"));
  }

  @Test
  @DisplayName("测试jcall错误处理")
  public void testJavaCallErrors() {
    // 不存在的类
    engine.setCellValue("T1", "=jcall(\"com.nonexistent.Class\",\"method\",1)");
    System.out.println("T1 = jcall(\"com.nonexistent.Class\",\"method\",1) = " + engine.getCellValue("T1"));
    assertEquals(SimpleCalculateEngine.ERROR, engine.getCellValue("T1"));

    // 不存在的方法
    engine.setCellValue("T2", "=jcall(\"java.lang.Math\",\"invalidMethod\",1)");
    System.out.println("T2 = jcall(\"java.lang.Math\",\"invalidMethod\",1) = " + engine.getCellValue("T2"));
    assertEquals(SimpleCalculateEngine.ERROR, engine.getCellValue("T2"));
  }

  // ==================== 单元格命名测试 ====================

  @Test
  @DisplayName("测试多样化单元格命名")
  public void testDiverseCellNaming() {
    // 传统Excel风格
    engine.setCellValue("A1", 100);
    System.out.println("A1 = " + engine.getCellValue("A1"));
    assertEquals("100", engine.getCellValue("A1"));

    // 希腊字母
    engine.setCellValue("α", Math.PI);
    engine.setCellValue("β", "=α*2");
    System.out.println("α = " + engine.getCellValue("α"));
    System.out.println("β = α*2 = " + engine.getCellValue("β"));
    assertEquals(Math.PI * 2, Double.parseDouble(engine.getCellValue("β")), 1e-10);

    // 中文
    engine.setCellValue("半径", 10);
    engine.setCellValue("面积", "=α*半径^2");
    System.out.println("半径 = " + engine.getCellValue("半径"));
    System.out.println("面积 = α*半径^2 = " + engine.getCellValue("面积"));
    assertEquals(Math.PI * 100, Double.parseDouble(engine.getCellValue("面积")), 1e-10);

    // 下划线
    engine.setCellValue("_temp", 42);
    engine.setCellValue("_result", "=_temp*2");
    System.out.println("_temp = " + engine.getCellValue("_temp"));
    System.out.println("_result = _temp*2 = " + engine.getCellValue("_result"));
    assertEquals("84", engine.getCellValue("_result"));

    // 混合命名
    engine.setCellValue("value_α", 100);
    engine.setCellValue("计算_β", "=value_α+50");
    System.out.println("value_α = " + engine.getCellValue("value_α"));
    System.out.println("计算_β = value_α+50 = " + engine.getCellValue("计算_β"));
    assertEquals("150", engine.getCellValue("计算_β"));
  }

  // ==================== 并发安全测试 ====================

  @Test
  @DisplayName("测试并发读写安全")
  public void testConcurrentSafety() throws InterruptedException {
    final int threadCount = 10;
    final int operationsPerThread = 100;
    final CountDownLatch latch = new CountDownLatch(threadCount);
    final ExecutorService executor = Executors.newFixedThreadPool(threadCount);

    // 初始化一些单元格
    for (int i = 0; i < threadCount; i++) {
      engine.setCellValue("U" + i, i * 10);
    }

    // 并发读写测试
    for (int i = 0; i < threadCount; i++) {
      final int threadId = i;
      executor.submit(() -> {
        try {
          for (int j = 0; j < operationsPerThread; j++) {
            // 读操作
            String value = engine.getCellValue("U" + threadId);
            assertNotNull(value);

            // 写操作
            engine.setCellValue("V" + threadId + "_" + j, "=U" + threadId + "+" + j);

            // 验证结果
            String result = engine.getCellValue("V" + threadId + "_" + j);
            assertNotNull(result);
            assertNotEquals("#ERROR#", result);
          }
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();
    executor.shutdown();

    // 验证所有操作都成功完成
    for (int i = 0; i < threadCount; i++) {
      for (int j = 0; j < operationsPerThread; j++) {
        String cellId = "V" + i + "_" + j;
        String value = engine.getCellValue(cellId);
        assertNotNull(value);
        assertNotEquals("#ERROR#", value);

        // 验证计算结果正确性
        double expected = i * 10 + j;
        assertEquals(expected, Double.parseDouble(value), 1e-10);
      }
    }
  }

  // ==================== 综合功能测试 ====================

  @Test
  @DisplayName("综合功能测试 - 复杂计算场景")
  public void testComplexCalculationScenario() {
    // 模拟一个复杂的计算场景：计算圆的面积和周长
    engine.setCellValue("π", Math.PI);
    engine.setCellValue("半径", 5);

    // 计算面积：π * r²
    engine.setCellValue("面积", "=π*pow(半径,2)");

    // 计算周长：2 * π * r
    engine.setCellValue("周长", "=2*π*半径");

    // 计算面积与周长的比值
    engine.setCellValue("比值", "=面积/周长");

    // 使用数学函数处理结果
    engine.setCellValue("面积_四舍五入", "=round(面积,2)");
    engine.setCellValue("周长_向上取整", "=ceil(周长)");

    // 验证结果
    double expectedArea = Math.PI * 25; // π * 5²
    double expectedCircumference = 2 * Math.PI * 5; // 2 * π * 5
    double expectedRatio = expectedArea / expectedCircumference; // 2.5

    assertEquals(expectedArea, Double.parseDouble(engine.getCellValue("面积")), 1e-10);
    assertEquals(expectedCircumference, Double.parseDouble(engine.getCellValue("周长")), 1e-10);
    assertEquals(expectedRatio, Double.parseDouble(engine.getCellValue("比值")), 1e-10);
    assertEquals("78.54", engine.getCellValue("面积_四舍五入"));
    assertEquals("32", engine.getCellValue("周长_向上取整"));
  }

  @Test
  @DisplayName("测试BigDecimal精度改进")
  public void testBigDecimalPrecisionImprovement() {
    // 测试浮点精度问题的改进
    engine.setCellValue("P1", "=0.1+0.2");
    System.out.println("0.1+0.2 = " + engine.getCellValue("P1"));
    assertEquals(new BigDecimal("0.3"), engine.getCellValueNumber("P1"));

    engine.setCellValue("P2", "=1.0-0.9");
    System.out.println("1.0-0.9 = " + engine.getCellValue("P2"));
    assertEquals(new BigDecimal("0.1"), engine.getCellValueNumber("P2"));

    engine.setCellValue("P3", "=0.1*3");
    System.out.println("0.1*3 = " + engine.getCellValue("P3"));
    assertEquals(new BigDecimal("0.3"), engine.getCellValueNumber("P3"));

    // 测试高精度计算
    engine.setCellValue("P4", "=1/3*3");
    System.out.println("1/3*3 = " + engine.getCellValue("P4"));
    // 由于除法可能产生无限小数，这里检查结果是否接近1
    BigDecimal result = engine.getCellValueNumber("P4");
    assertTrue(result.subtract(BigDecimal.ONE).abs().compareTo(new BigDecimal("0.0001")) < 0);
  }

  @Test
  @DisplayName("测试引擎资源管理")
  public void testEngineResourceManagement() {
    // 测试引擎可以正常关闭
    SimpleCalculateEngine testEngine = new SimpleCalculateEngine();
    testEngine.setCellValue("test", "=1+1");
    assertEquals("2", testEngine.getCellValue("test"));

    // 关闭引擎
    assertDoesNotThrow(() -> testEngine.shutdown());

    // 关闭后仍可以进行基本操作（但不推荐）
    // 这里只是测试不会抛出异常
    assertDoesNotThrow(() -> testEngine.getCellValue("test"));
  }
}
