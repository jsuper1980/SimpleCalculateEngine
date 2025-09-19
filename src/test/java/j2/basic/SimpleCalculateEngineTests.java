package j2.basic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    engine.set("A1", "=10+20");
    System.out.println("10+20 = " + engine.get("A1"));
    assertEquals(new BigDecimal("30"), engine.getNumber("A1"));

    engine.set("A2", "=50-30");
    System.out.println("50-30 = " + engine.get("A2"));
    assertEquals(new BigDecimal("20"), engine.getNumber("A2"));

    engine.set("A3", "=10+20-5.1");
    System.out.println("10+20-5.1 = " + engine.get("A3"));
    assertEquals("24.9", engine.get("A3"));

    // 测试负数
    engine.set("A4", "=-10+5");
    System.out.println("-10+5 = " + engine.get("A4"));
    assertEquals(new BigDecimal("-5"), engine.getNumber("A4"));
  }

  @Test
  @DisplayName("测试基础乘除运算")
  public void testBasicMultiplicationDivision() {
    engine.set("B1", "=10*20");
    System.out.println("10*20 = " + engine.get("B1"));
    assertEquals("200", engine.get("B1"));

    engine.set("B2", "=100/4");
    System.out.println("100/4 = " + engine.getNumber("B2"));
    assertEquals("25", engine.get("B2"));

    engine.set("B3", "=10*2/4");
    System.out.println("10*2/4 = " + engine.get("B3"));
    assertEquals("5", engine.get("B3"));
  }

  @Test
  @DisplayName("测试整数除和余数除运算")
  public void testIntegerDivisionAndModulo() {
    // 测试整数除法 \\
    engine.set("B4", "=17\\5");
    System.out.println("17\\5 = " + engine.get("B4"));
    assertEquals("3", engine.get("B4")); // 17除以5的整数部分是3

    engine.set("B5", "=100\\7");
    System.out.println("100\\7 = " + engine.get("B5"));
    assertEquals("14", engine.get("B5")); // 100除以7的整数部分是14

    // 测试余数运算 %
    engine.set("B6", "=17%5");
    System.out.println("17%5 = " + engine.get("B6"));
    assertEquals("2", engine.get("B6")); // 17除以5的余数是2

    engine.set("B7", "=100%7");
    System.out.println("100%7 = " + engine.get("B7"));
    assertEquals("2", engine.get("B7")); // 100除以7的余数是2

    // 测试负数的整数除法和余数
    engine.set("B8", "=-17\\5");
    System.out.println("-17\\5 = " + engine.get("B8"));
    assertEquals("-4", engine.get("B8")); // -17除以5向下取整是-4

    engine.set("B9", "=-17%5");
    System.out.println("-17%5 = " + engine.get("B9"));
    assertEquals("3", engine.get("B9")); // -17除以5的余数是3（数学模运算）

    // 测试小数的整数除法
    engine.set("B10", "=17.8\\5.2");
    System.out.println("17.8\\5.2 = " + engine.get("B10"));
    assertEquals("3", engine.get("B10")); // 17.8除以5.2向下取整是3

    // 测试除零错误
    assertThrows(RuntimeException.class, () -> {
      engine.set("B11", "=10\\0");
      String result = engine.get("B11");
      if (result.equals("#ERROR#")) {
        throw new RuntimeException("除零错误");
      }
    }, "整数除零应该抛出异常");

    assertThrows(RuntimeException.class, () -> {
      engine.set("B12", "=10%0");
      String result = engine.get("B12");
      if (result.equals("#ERROR#")) {
        throw new RuntimeException("除零错误");
      }
    }, "余数除零应该抛出异常");
  }

  @Test
  @DisplayName("测试幂运算")
  public void testPowerOperation() {
    engine.set("C1", "=2^3");
    System.out.println("2^3 = " + engine.get("C1"));
    assertEquals("8", engine.get("C1"));

    engine.set("C2", "=3^2");
    System.out.println("3^2 = " + engine.get("C2"));
    assertEquals("9", engine.get("C2"));

    engine.set("C3", "=2^3^2"); // 右结合：2^(3^2) = 2^9 = 512
    System.out.println("2^3^2 = " + engine.get("C3"));
    assertEquals("512", engine.get("C3"));
  }

  @Test
  @DisplayName("测试括号运算")
  public void testParentheses() {
    engine.set("D1", "=(10+20)*3");
    System.out.println("(10+20)*3 = " + engine.get("D1"));
    assertEquals("90", engine.get("D1"));

    engine.set("D2", "=10+(20*3)");
    System.out.println("10+(20*3) = " + engine.get("D2"));
    assertEquals("70", engine.get("D2"));

    engine.set("D3", "=((10+5)*2)^2");
    System.out.println("((10+5)*2)^2 = " + engine.get("D3"));
    assertEquals("900", engine.get("D3"));
  }

  @Test
  @DisplayName("测试运算符优先级")
  public void testOperatorPrecedence() {
    engine.set("E1", "=10+20*3");
    System.out.println("10+20*3 = " + engine.get("E1"));
    assertEquals("70", engine.get("E1")); // 先乘后加

    engine.set("E2", "=2^3*4");
    System.out.println("2^3*4 = " + engine.get("E2"));
    assertEquals("32", engine.get("E2")); // 先幂后乘

    engine.set("E3", "=2+3*4^2");
    System.out.println("2+3*4^2 = " + engine.get("E3"));
    assertEquals("50", engine.get("E3")); // 2+3*16=50
  }

  // ==================== 单元格管理测试 ====================

  @Test
  @DisplayName("测试单元格基本操作")
  public void testBasicCellOperations() {
    // 设置数值
    engine.set("F1", 100);
    System.out.println("F1 = " + engine.get("F1"));
    assertEquals("100", engine.get("F1"));

    // 设置字符串
    engine.set("F2", "Hello");
    System.out.println("F2 = " + engine.get("F2"));
    assertEquals("Hello", engine.get("F2"));

    // 设置公式
    engine.set("F3", "=F1+50");
    System.out.println("F3 = F1+50 = " + engine.get("F3"));
    assertEquals(BigDecimal.valueOf(150), engine.getNumber("F3"));
  }

  @Test
  @DisplayName("测试单元格引用")
  public void testCellReferences() {
    engine.set("G1", 10);
    engine.set("G2", 20);
    engine.set("G3", "=G1+G2");

    System.out.println("G1 = " + engine.get("G1"));
    System.out.println("G2 = " + engine.get("G2"));
    System.out.println("G3 = G1+G2 = " + engine.get("G3"));
    assertEquals("30", engine.get("G3"));

    // 修改被引用的单元格，检查联动更新
    engine.set("G1", 15);
    System.out.println("G3 = " + engine.get("G3"));
    assertEquals("35", engine.get("G3"));
  }

  @Test
  @DisplayName("测试复杂单元格引用")
  public void testComplexCellReferences() {
    engine.set("H1", 5);
    engine.set("H2", 10);
    engine.set("H3", "=H1*H2");
    engine.set("H4", "=H3+H1");
    engine.set("H5", "=(H1+H2)*H3");

    System.out.println("H1 = " + engine.get("H1"));
    System.out.println("H2 = " + engine.get("H2"));
    System.out.println("H3 = H1*H2 = " + engine.get("H3"));
    System.out.println("H4 = H3+H1 = " + engine.get("H4"));
    System.out.println("H5 = (H1+H2)*H3 = " + engine.get("H5"));

    assertEquals(new BigDecimal("50"), engine.getNumber("H3")); // 5*10
    assertEquals(new BigDecimal("55"), engine.getNumber("H4")); // 50+5
    assertEquals(new BigDecimal("750"), engine.getNumber("H5")); // (5+10)*50
  }

  // ==================== 数学函数测试 ====================

  @Test
  @DisplayName("测试基础数学函数")
  public void testBasicMathFunctions() {
    engine.set("I1", "=sqrt(25)");
    System.out.println("sqrt(25) = " + engine.get("I1"));
    assertEquals("5", engine.get("I1"));

    engine.set("I2", "=abs(-10)");
    System.out.println("abs(-10) = " + engine.get("I2"));
    assertEquals("10", engine.get("I2"));

    engine.set("I3", "=ceil(4.3)");
    System.out.println("ceil(4.3) = " + engine.get("I3"));
    assertEquals("5", engine.get("I3"));

    engine.set("I4", "=floor(4.7)");
    System.out.println("floor(4.7) = " + engine.get("I4"));
    assertEquals("4", engine.get("I4"));

    engine.set("I5", "=round(4.6)");
    System.out.println("round(4.6) = " + engine.get("I5"));
    assertEquals("5", engine.get("I5"));
  }

  @Test
  @DisplayName("测试三角函数")
  public void testTrigonometricFunctions() {
    // 设置π值
    engine.set("π", Math.PI);
    System.out.println("π = " + engine.get("π"));

    engine.set("J1", "=sin(π/2)");
    System.out.println("sin(π/2) = " + engine.get("J1") + ", java sin(π/2) = " + Math.sin(Math.PI / 2));
    assertEquals(1.0, Double.parseDouble(engine.get("J1")));
    engine.set("J1_1", "=sin(90*π/180)");
    System.out.println("sin(90°) = " + engine.get("J1_1") + ", java sin(90°) = " + Math.sin(90 * Math.PI / 180));
    assertEquals(1.0, Double.parseDouble(engine.get("J1_1")));

    engine.set("J2", "=cos(0)");
    System.out.println("cos(0) = " + engine.get("J2") + ", java cos(0) = " + Math.cos(0));
    assertEquals("1", engine.get("J2"));

    engine.set("J3", "=tan(π/4)");
    System.out.println("tan(π/4) = " + engine.get("J3") + ", java tan(π/4) = " + Math.tan(Math.PI / 4));
    assertEquals(1.0, Double.parseDouble(engine.get("J3")), 1e-10);

    engine.set("J4", "=asin(1)");
    System.out.println("asin(1) = " + engine.get("J4") + ", java asin(1) = " + Math.asin(1));
    assertEquals(Math.PI / 2, Double.parseDouble(engine.get("J4")), 1e-10);
  }

  @Test
  @DisplayName("测试对数函数")
  public void testLogarithmicFunctions() {
    engine.set("K1", "=log(exp(2))");
    System.out.println("log(exp(2)) = " + engine.get("K1") + ", java log(exp(2)) = " + Math.log(Math.exp(2)));
    assertEquals(2.0, Double.parseDouble(engine.get("K1")), 1e-10);

    engine.set("K2", "=log10(100)");
    System.out.println("log10(100) = " + engine.get("K2") + ", java log10(100) = " + Math.log10(100));
    assertEquals("2", engine.get("K2"));

    engine.set("K3", "=exp(0)");
    System.out.println("exp(0) = " + engine.get("K3") + ", java exp(0) = " + Math.exp(0));
    assertEquals("1", engine.get("K3"));
  }

  @Test
  @DisplayName("测试多参数函数")
  public void testMultiParameterFunctions() {
    engine.set("L1", "=pow(2,10)");
    System.out.println("pow(2,10) = " + engine.get("L1"));
    assertEquals("1024", engine.get("L1"));

    engine.set("L2", "=min(10,20,5,30)");
    System.out.println("min(10,20,5,30) = " + engine.get("L2"));
    assertEquals("5", engine.get("L2"));

    engine.set("L3", "=max(10,20,5,30)");
    System.out.println("max(10,20,5,30) = " + engine.get("L3"));
    assertEquals("30", engine.get("L3"));

    engine.set("L4", "=avg(10,20,30)");
    System.out.println("avg(10,20,30) = " + engine.get("L4"));
    assertEquals("20", engine.get("L4"));

    engine.set("L5", "=round(3.14159,2)");
    System.out.println("round(3.14159,2) = " + engine.get("L5"));
    assertEquals("3.14", engine.get("L5"));
  }

  // ==================== 依赖管理测试 ====================

  @Test
  @DisplayName("测试依赖链更新")
  public void testDependencyChainUpdate() {
    engine.set("M1", 10);
    engine.set("M2", "=M1*2");
    engine.set("M3", "=M2+5");
    engine.set("M4", "=M3^2");

    System.out.println("M1 = " + engine.get("M1"));
    System.out.println("M2 = M1*2 = " + engine.get("M2"));
    System.out.println("M3 = M2+5 = " + engine.get("M3"));
    System.out.println("M4 = M3^2 = " + engine.get("M4"));

    assertEquals("20", engine.get("M2"));
    assertEquals("25", engine.get("M3"));
    assertEquals("625", engine.get("M4"));

    // 修改根节点，检查整个依赖链更新
    engine.set("M1", 5);

    System.out.println("修改 M1 为 5 后：");
    System.out.println("M1 = " + engine.get("M1"));
    System.out.println("M2 = M1*2 = " + engine.get("M2"));
    System.out.println("M3 = M2+5 = " + engine.get("M3"));
    System.out.println("M4 = M3^2 = " + engine.get("M4"));

    assertEquals("10", engine.get("M2"));
    assertEquals("15", engine.get("M3"));
    assertEquals("225", engine.get("M4"));
  }

  @Test
  @DisplayName("测试复杂依赖网络")
  public void testComplexDependencyNetwork() {
    engine.set("N1", 10);
    engine.set("N2", 3);
    engine.set("N3", "=N1+N2");
    engine.set("N4", "=N1*N2");
    engine.set("N5", "=N3+N4");
    engine.set("N6", "=N3*N4");

    System.out.println("N1 = " + engine.get("N1"));
    System.out.println("N2 = " + engine.get("N2"));
    System.out.println("N3 = N1+N2 = " + engine.get("N3"));
    System.out.println("N4 = N1*N2 = " + engine.get("N4"));
    System.out.println("N5 = N3+N4 = " + engine.get("N5"));
    System.out.println("N6 = N3*N4 = " + engine.get("N6"));

    assertEquals("13", engine.get("N3")); // 10+3
    assertEquals("30", engine.get("N4")); // 10*3
    assertEquals("43", engine.get("N5")); // 13+30
    assertEquals("390", engine.get("N6")); // 13*30

    // 修改一个根节点
    engine.set("N1", 5);

    System.out.println("修改 N1 为 5 后：");
    System.out.println("N3 = N1+N2 = " + engine.get("N3"));
    System.out.println("N4 = N1*N2 = " + engine.get("N4"));
    System.out.println("N5 = N3+N4 = " + engine.get("N5"));
    System.out.println("N6 = N3*N4 = " + engine.get("N6"));

    assertEquals("5", engine.get("N1"));
    assertEquals("8", engine.get("N3"));
    assertEquals("15", engine.get("N4"));
    assertEquals("23", engine.get("N5"));
    assertEquals("120", engine.get("N6"));
  }

  // ==================== 错误处理测试 ====================

  @Test
  @DisplayName("测试除零错误")
  public void testDivisionByZeroError() {
    engine.set("O1", "=10/0");
    System.out.println("O1 = 10/0 = " + engine.get("O1"));
    assertEquals("#ERROR#", engine.get("O1"));

    engine.set("O2", 0);
    engine.set("O3", "=10/O2");
    System.out.println("O2 = " + engine.get("O2"));
    System.out.println("O3 = 10/O2 = " + engine.get("O3"));
    assertEquals("#ERROR#", engine.get("O3"));
  }

  @Test
  @DisplayName("测试循环引用检测")
  public void testCircularReferenceDetection() {
    try {
      engine.set("P1", "=P2+1");
      engine.set("P2", "=P1+1");
    } catch (RuntimeException e) {
      System.out.println("异常信息：" + e.getMessage());
    }

    assertThrows(RuntimeException.class, () -> {
      engine.set("P1", "=P2+1");
      engine.set("P2", "=P1+1");
    });

    assertThrows(RuntimeException.class, () -> {
      engine.set("P3", "=P4+1");
      engine.set("P4", "=P5+1");
      engine.set("P5", "=P3+1");
    });
  }

  @Test
  @DisplayName("测试函数参数错误")
  public void testFunctionParameterErrors() {
    // sqrt负数
    engine.set("Q1", "=sqrt(-1)");
    System.out.println("Q1 = sqrt(-1) = " + engine.get("Q1"));
    assertEquals(SimpleCalculateEngine.ERROR, engine.get("Q1"));

    // asin超出范围
    engine.set("Q2", "=asin(2)");
    System.out.println("Q2 = asin(2) = " + engine.get("Q2"));
    assertEquals(SimpleCalculateEngine.ERROR, engine.get("Q2"));

    // log非正数
    engine.set("Q3", "=log(-1)");
    System.out.println("Q3 = log(-1) = " + engine.get("Q3"));
    assertEquals(SimpleCalculateEngine.ERROR, engine.get("Q3"));

    // 不存在的函数 - 注意：可能被解析为单元格引用
    engine.set("Q4", "=invalidFunc(1)");
    System.out.println("Q4 = invalidFunc(1) = " + engine.get("Q4"));
    assertEquals(SimpleCalculateEngine.ERROR, engine.get("Q4"));
  }

  @Test
  @DisplayName("测试语法错误")
  public void testSyntaxErrors() {
    // 括号不匹配
    engine.set("R1", "=(10+20");
    System.out.println("R1 = (10+20 = " + engine.get("R1"));
    assertEquals(SimpleCalculateEngine.ERROR, engine.get("R1"));

    // 无效表达式 - 使用更明确的错误语法
    engine.set("R2", "=10*/20");
    System.out.println("R2 = 10*/20 = " + engine.get("R2"));
    assertEquals(SimpleCalculateEngine.ERROR, engine.get("R2"));

    // 空表达式
    engine.set("R3", "=");
    System.out.println("R3 = = " + engine.get("R3"));
    assertEquals(SimpleCalculateEngine.ERROR, engine.get("R3"));
  }

  // ==================== Java调用测试 ====================

  @Test
  @DisplayName("测试jcall函数调用Java静态方法")
  public void testJavaCallFunction() {
    // 调用Math.max
    engine.set("S1", "=jcall(\"java.lang.Math\",\"max\",10,20)");
    System.out.println("S1 = jcall(\"java.lang.Math\",\"max\",10,20) = " + engine.get("S1"));
    assertEquals("20", engine.get("S1"));

    // 调用Math.min
    engine.set("S2", "=jcall(\"java.lang.Math\",\"min\",10,20)");
    System.out.println("S2 = jcall(\"java.lang.Math\",\"min\",10,20) = " + engine.get("S2"));
    assertEquals("10", engine.get("S2"));

    // 调用Math.abs
    engine.set("S3", "=jcall(\"java.lang.Math\",\"abs\",-15)");
    System.out.println("S3 = jcall(\"java.lang.Math\",\"abs\",-15) = " + engine.get("S3"));
    assertEquals("15", engine.get("S3"));

    // 调用String.valueOf - 返回数值类型
    engine.set("S4", "=jcall(\"java.lang.Integer\",\"valueOf\",123)");
    System.out.println("S4 = jcall(\"java.lang.Integer\",\"valueOf\",123) = " + engine.get("S4"));
    assertEquals("123", engine.get("S4"));

    engine.set("S5", "=jcall(\"j2.basic.MathUtils\",\"factorial\",5)");
    System.out.println("S5 = jcall(\"j2.basic.MathUtils\",\"factorial\",5) = " + engine.get("S5"));
    assertEquals("120", engine.get("S5"));
  }

  @Test
  @DisplayName("测试jcall错误处理")
  public void testJavaCallErrors() {
    // 不存在的类
    engine.set("T1", "=jcall(\"com.nonexistent.Class\",\"method\",1)");
    System.out.println("T1 = jcall(\"com.nonexistent.Class\",\"method\",1) = " + engine.get("T1"));
    assertEquals(SimpleCalculateEngine.ERROR, engine.get("T1"));

    // 不存在的方法
    engine.set("T2", "=jcall(\"java.lang.Math\",\"invalidMethod\",1)");
    System.out.println("T2 = jcall(\"java.lang.Math\",\"invalidMethod\",1) = " + engine.get("T2"));
    assertEquals(SimpleCalculateEngine.ERROR, engine.get("T2"));
  }

  // ==================== 单元格命名测试 ====================

  @Test
  @DisplayName("测试多样化单元格命名")
  public void testDiverseCellNaming() {
    // 传统Excel风格
    engine.set("A1", 100);
    System.out.println("A1 = " + engine.get("A1"));
    assertEquals("100", engine.get("A1"));

    // 希腊字母
    engine.set("α", Math.PI);
    engine.set("β", "=α*2");
    System.out.println("α = " + engine.get("α"));
    System.out.println("β = α*2 = " + engine.get("β"));
    assertEquals(Math.PI * 2, Double.parseDouble(engine.get("β")), 1e-10);

    // 中文
    engine.set("半径", 10);
    engine.set("面积", "=α*半径^2");
    System.out.println("半径 = " + engine.get("半径"));
    System.out.println("面积 = α*半径^2 = " + engine.get("面积"));
    assertEquals(Math.PI * 100, Double.parseDouble(engine.get("面积")), 1e-10);

    // 下划线
    engine.set("_temp", 42);
    engine.set("_result", "=_temp*2");
    System.out.println("_temp = " + engine.get("_temp"));
    System.out.println("_result = _temp*2 = " + engine.get("_result"));
    assertEquals("84", engine.get("_result"));

    // 混合命名
    engine.set("value_α", 100);
    engine.set("计算_β", "=value_α+50");
    System.out.println("value_α = " + engine.get("value_α"));
    System.out.println("计算_β = value_α+50 = " + engine.get("计算_β"));
    assertEquals("150", engine.get("计算_β"));
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
      engine.set("U" + i, i * 10);
    }

    // 并发读写测试
    for (int i = 0; i < threadCount; i++) {
      final int threadId = i;
      executor.submit(() -> {
        try {
          for (int j = 0; j < operationsPerThread; j++) {
            // 读操作
            String value = engine.get("U" + threadId);
            assertNotNull(value);

            // 写操作
            engine.set("V" + threadId + "_" + j, "=U" + threadId + "+" + j);

            // 验证结果
            String result = engine.get("V" + threadId + "_" + j);
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
        String value = engine.get(cellId);
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
    engine.set("π", Math.PI);
    engine.set("半径", 5);

    // 计算面积：π * r²
    engine.set("面积", "=π*pow(半径,2)");

    // 计算周长：2 * π * r
    engine.set("周长", "=2*π*半径");

    // 计算面积与周长的比值
    engine.set("比值", "=面积/周长");

    // 使用数学函数处理结果
    engine.set("面积_四舍五入", "=round(面积,2)");
    engine.set("周长_向上取整", "=ceil(周长)");

    // 验证结果
    double expectedArea = Math.PI * 25; // π * 5²
    double expectedCircumference = 2 * Math.PI * 5; // 2 * π * 5
    double expectedRatio = expectedArea / expectedCircumference; // 2.5

    assertEquals(expectedArea, Double.parseDouble(engine.get("面积")), 1e-10);
    assertEquals(expectedCircumference, Double.parseDouble(engine.get("周长")), 1e-10);
    assertEquals(expectedRatio, Double.parseDouble(engine.get("比值")), 1e-10);
    assertEquals("78.54", engine.get("面积_四舍五入"));
    assertEquals("32", engine.get("周长_向上取整"));
  }

  @Test
  @DisplayName("测试BigDecimal精度改进")
  public void testBigDecimalPrecisionImprovement() {
    // 测试浮点精度问题的改进
    engine.set("P1", "=0.1+0.2");
    System.out.println("0.1+0.2 = " + engine.get("P1"));
    assertEquals(new BigDecimal("0.3"), engine.getNumber("P1"));

    engine.set("P2", "=1.0-0.9");
    System.out.println("1.0-0.9 = " + engine.get("P2"));
    assertEquals(new BigDecimal("0.1"), engine.getNumber("P2"));

    engine.set("P3", "=0.1*3");
    System.out.println("0.1*3 = " + engine.get("P3"));
    assertEquals(new BigDecimal("0.3"), engine.getNumber("P3"));

    // 测试高精度计算
    engine.set("P4", "=1/3*3");
    System.out.println("1/3*3 = " + engine.get("P4"));
    // 由于除法可能产生无限小数，这里检查结果是否接近1
    BigDecimal result = engine.getNumber("P4");
    assertTrue(result.subtract(BigDecimal.ONE).abs().compareTo(new BigDecimal("0.0001")) < 0);
  }

  @Test
  @DisplayName("测试引擎资源管理")
  public void testEngineResourceManagement() {
    // 测试引擎可以正常关闭
    SimpleCalculateEngine testEngine = new SimpleCalculateEngine();
    testEngine.set("test", "=1+1");
    assertEquals("2", testEngine.get("test"));

    // 关闭引擎
    assertDoesNotThrow(() -> testEngine.shutdown());

    // 关闭后仍可以进行基本操作（但不推荐）
    // 这里只是测试不会抛出异常
    assertDoesNotThrow(() -> testEngine.get("test"));
  }

  // ==================== 性能测试用例 ====================

  @Test
  @DisplayName("性能测试 - 大量计算和复杂公式")
  public void testPerformance() {
    System.out.println("\n========== 性能测试开始 ==========");

    // 测试1: 大量简单计算的性能
    long startTime = System.nanoTime();
    for (int i = 1; i <= 1000; i++) {
      String cellName = "PERF_" + i;
      engine.set(cellName, "=" + i + "*2+" + (i + 1));
      engine.get(cellName); // 触发计算
    }
    long endTime = System.nanoTime();
    long duration1 = (endTime - startTime) / 1_000_000; // 转换为毫秒
    System.out.println("大量简单计算(1000个): " + duration1 + "ms");
    assertTrue(duration1 < 5000, "大量简单计算应在5秒内完成，实际用时: " + duration1 + "ms");

    // 测试2: 复杂数学函数计算性能
    startTime = System.nanoTime();
    engine.set("COMPLEX1", "=sin(cos(tan(sqrt(abs(-100)))))");
    engine.set("COMPLEX2", "=log(exp(pow(2,3)))");
    engine.set("COMPLEX3", "=sqrt(pow(sin(PI/4),2)+pow(cos(PI/4),2))");
    engine.set("COMPLEX4", "=max(min(100,200),min(300,400))");
    engine.set("COMPLEX5", "=round(PI*pow(5,2),2)");

    // 触发所有复杂计算
    for (int i = 1; i <= 5; i++) {
      engine.get("COMPLEX" + i);
    }
    endTime = System.nanoTime();
    long duration2 = (endTime - startTime) / 1_000_000;
    System.out.println("复杂数学函数计算: " + duration2 + "ms");
    assertTrue(duration2 < 1000, "复杂数学函数计算应在1秒内完成，实际用时: " + duration2 + "ms");

    // 测试3: 依赖链计算性能
    startTime = System.nanoTime();
    engine.set("CHAIN_1", "=1");
    for (int i = 2; i <= 100; i++) {
      engine.set("CHAIN_" + i, "=CHAIN_" + (i - 1) + "+" + i);
    }
    // 触发最终计算，这会导致整个依赖链的计算
    String finalResult = engine.get("CHAIN_100");
    endTime = System.nanoTime();
    long duration3 = (endTime - startTime) / 1_000_000;
    System.out.println("依赖链计算(100层): " + duration3 + "ms，最终结果: " + finalResult);
    assertTrue(duration3 < 2000, "依赖链计算应在2秒内完成，实际用时: " + duration3 + "ms");
    assertEquals("5050", finalResult); // 1+2+3+...+100 = 5050

    // 测试4: 并发性能测试
    startTime = System.nanoTime();
    ExecutorService executor = Executors.newFixedThreadPool(10);
    CountDownLatch latch = new CountDownLatch(100);

    for (int i = 0; i < 100; i++) {
      final int index = i;
      executor.submit(() -> {
        try {
          String cellName = "CONCURRENT_" + index;
          engine.set(cellName, "=sqrt(" + (index + 1) + ")*PI");
          engine.get(cellName); // 触发计算
        } finally {
          latch.countDown();
        }
      });
    }

    try {
      latch.await(); // 等待所有任务完成
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    executor.shutdown();

    endTime = System.nanoTime();
    long duration4 = (endTime - startTime) / 1_000_000;
    System.out.println("并发计算(100个任务，10线程): " + duration4 + "ms");
    assertTrue(duration4 < 3000, "并发计算应在3秒内完成，实际用时: " + duration4 + "ms");

    // 测试5: 内存使用和垃圾回收影响
    Runtime runtime = Runtime.getRuntime();
    long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

    startTime = System.nanoTime();
    // 创建大量临时计算
    for (int i = 0; i < 500; i++) {
      String tempCell = "TEMP_" + i;
      engine.set(tempCell, "=pow(" + i + ",2)+sqrt(" + i + ")+log(" + (i + 1) + ")");
      engine.get(tempCell);

      // 每100次清理一些单元格
      if (i % 100 == 99) {
        for (int j = i - 50; j < i; j++) {
          engine.set("TEMP_" + j, ""); // 清理单元格
        }
      }
    }
    endTime = System.nanoTime();
    long duration5 = (endTime - startTime) / 1_000_000;

    long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
    long memoryUsed = (memoryAfter - memoryBefore) / 1024 / 1024; // 转换为MB

    System.out.println("内存管理测试: " + duration5 + "ms，内存使用: " + memoryUsed + "MB");
    assertTrue(duration5 < 4000, "内存管理测试应在4秒内完成，实际用时: " + duration5 + "ms");
    assertTrue(memoryUsed < 100, "内存使用应控制在100MB以内，实际使用: " + memoryUsed + "MB");

    // 输出性能测试总结
    long totalDuration = duration1 + duration2 + duration3 + duration4 + duration5;
    System.out.println("========== 性能测试总结 ==========");
    System.out.println("总耗时: " + totalDuration + "ms");
    System.out.println("平均每项测试: " + (totalDuration / 5) + "ms");
    System.out.println("性能测试通过！");
    System.out.println("================================");

    // 整体性能断言
    assertTrue(totalDuration < 15000, "整体性能测试应在15秒内完成，实际用时: " + totalDuration + "ms");
  }

  // ==================== 单元格删除测试 ====================

  @Test
  @DisplayName("测试删除单元格定义")
  public void testDeleteCellDefinition() {
    // 创建一些测试单元格
    engine.set("DEL_A", 10);
    engine.set("DEL_B", 20);
    engine.set("DEL_C", "=DEL_A+DEL_B");
    engine.set("DEL_D", "=DEL_C*2");

    // 验证初始状态
    assertEquals("10", engine.get("DEL_A"));
    assertEquals("20", engine.get("DEL_B"));
    assertEquals("30", engine.get("DEL_C"));
    assertEquals("60", engine.get("DEL_D"));
    assertTrue(engine.exist("DEL_A"));
    assertTrue(engine.exist("DEL_C"));

    System.out.println("删除前:");
    System.out.println("DEL_A = " + engine.get("DEL_A"));
    System.out.println("DEL_B = " + engine.get("DEL_B"));
    System.out.println("DEL_C = DEL_A+DEL_B = " + engine.get("DEL_C"));
    System.out.println("DEL_D = DEL_C*2 = " + engine.get("DEL_D"));

    // 删除DEL_A
    engine.del("DEL_A");

    System.out.println("删除DEL_A后:");
    System.out.println("DEL_A存在? " + engine.exist("DEL_A"));
    System.out.println("DEL_A = " + engine.get("DEL_A"));
    System.out.println("DEL_C = DEL_A+DEL_B = " + engine.get("DEL_C"));
    System.out.println("DEL_D = DEL_C*2 = " + engine.get("DEL_D"));

    // 验证删除结果
    assertFalse(engine.exist("DEL_A"));
    assertNull(engine.get("DEL_A"));

    // DEL_C现在应该计算为0+20=20（因为DEL_A被删除，引用视为0）
    assertEquals("20", engine.get("DEL_C"));
    assertEquals("40", engine.get("DEL_D")); // DEL_C*2 = 20*2 = 40

    // 删除DEL_C
    engine.del("DEL_C");

    System.out.println("删除DEL_C后:");
    System.out.println("DEL_C存在? " + engine.exist("DEL_C"));
    System.out.println("DEL_C = " + engine.get("DEL_C"));
    System.out.println("DEL_D = DEL_C*2 = " + engine.get("DEL_D"));

    // 验证删除结果
    assertFalse(engine.exist("DEL_C"));
    assertNull(engine.get("DEL_C"));

    // DEL_D现在应该计算为0*2=0（因为DEL_C被删除，引用视为0）
    assertEquals("0", engine.get("DEL_D"));
  }

  @Test
  @DisplayName("测试删除不存在的单元格")
  public void testDeleteNonExistentCell() {
    // 删除不存在的单元格应该不抛出异常（幂等操作）
    assertDoesNotThrow(() -> engine.del("NON_EXISTENT"));
    assertFalse(engine.exist("NON_EXISTENT"));
    assertNull(engine.get("NON_EXISTENT"));
  }

  @Test
  @DisplayName("测试删除单元格的参数验证")
  public void testDeleteCellParameterValidation() {
    // 测试null参数
    assertThrows(IllegalArgumentException.class, () -> engine.del(null));

    // 测试空字符串参数
    assertThrows(IllegalArgumentException.class, () -> engine.del(""));

    // 测试空白字符串参数
    assertThrows(IllegalArgumentException.class, () -> engine.del("   "));
  }

  @Test
  @DisplayName("测试删除单元格对复杂依赖网络的影响")
  public void testDeleteCellComplexDependencies() {
    // 创建复杂的依赖网络
    engine.set("NET_A", 5);
    engine.set("NET_B", 10);
    engine.set("NET_C", "=NET_A+NET_B");
    engine.set("NET_D", "=NET_A*NET_B");
    engine.set("NET_E", "=NET_C+NET_D");
    engine.set("NET_F", "=NET_C*NET_D");
    engine.set("NET_G", "=NET_E+NET_F");

    // 验证初始状态
    assertEquals("5", engine.get("NET_A"));
    assertEquals("10", engine.get("NET_B"));
    assertEquals("15", engine.get("NET_C")); // 5+10
    assertEquals("50", engine.get("NET_D")); // 5*10
    assertEquals("65", engine.get("NET_E")); // 15+50
    assertEquals("750", engine.get("NET_F")); // 15*50
    assertEquals("815", engine.get("NET_G")); // 65+750

    System.out.println("删除NET_A前的复杂依赖网络:");
    System.out.println("NET_A = " + engine.get("NET_A"));
    System.out.println("NET_C = NET_A+NET_B = " + engine.get("NET_C"));
    System.out.println("NET_D = NET_A*NET_B = " + engine.get("NET_D"));
    System.out.println("NET_E = NET_C+NET_D = " + engine.get("NET_E"));
    System.out.println("NET_F = NET_C*NET_D = " + engine.get("NET_F"));
    System.out.println("NET_G = NET_E+NET_F = " + engine.get("NET_G"));

    // 删除NET_A，这应该影响NET_C, NET_D, NET_E, NET_F, NET_G
    engine.del("NET_A");

    System.out.println("删除NET_A后的复杂依赖网络:");
    System.out.println("NET_A存在? " + engine.exist("NET_A"));
    System.out.println("NET_C = NET_A+NET_B = " + engine.get("NET_C"));
    System.out.println("NET_D = NET_A*NET_B = " + engine.get("NET_D"));
    System.out.println("NET_E = NET_C+NET_D = " + engine.get("NET_E"));
    System.out.println("NET_F = NET_C*NET_D = " + engine.get("NET_F"));
    System.out.println("NET_G = NET_E+NET_F = " + engine.get("NET_G"));

    // 验证删除后的计算结果
    assertFalse(engine.exist("NET_A"));
    assertNull(engine.get("NET_A"));
    assertEquals("10", engine.get("NET_C")); // 0+10 (NET_A被删除，视为0)
    assertEquals("0", engine.get("NET_D")); // 0*10
    assertEquals("10", engine.get("NET_E")); // 10+0
    assertEquals("0", engine.get("NET_F")); // 10*0
    assertEquals("10", engine.get("NET_G")); // 10+0
  }

  @Test
  @DisplayName("测试cellExists方法")
  public void testCellExists() {
    // 测试不存在的单元格
    assertFalse(engine.exist("NOT_EXISTS"));
    assertFalse(engine.exist(null));
    assertFalse(engine.exist(""));
    assertFalse(engine.exist("   "));

    // 创建单元格
    engine.set("EXISTS_TEST", 42);
    assertTrue(engine.exist("EXISTS_TEST"));

    // 删除单元格
    engine.del("EXISTS_TEST");
    assertFalse(engine.exist("EXISTS_TEST"));
  }
}
