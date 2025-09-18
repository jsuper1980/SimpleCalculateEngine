# 简易计算引擎 (SimpleCalculateEngine)

一个轻量级模拟 Excel 单元格联动的计算引擎，支持单元格引用、公式计算、数学函数和 Java 类调用。

## 🚀 特性

### 核心功能

- **依赖管理**: 自动处理单元格间的依赖关系，支持联动计算
- **公式计算**: 支持复杂的数学表达式和单元格引用
- **单元格管理**: 支持灵活的单元格命名（支持中文、希腊字母、下划线等）
- **并发安全**: 使用读写锁保证多线程环境下的数据一致性
- **循环检测**: 自动检测并防止循环引用
- **高精度计算**: 使用 BigDecimal 进行数值计算，避免浮点数精度问题
- **智能格式化**: 自动去除计算结果中无意义的尾随零（如 1.0000 显示为 1）

### 数学运算符

- **基础运算**: `+`, `-`, `*`, `/`
- **幂运算**: `^` (支持任意实数幂)
- **括号**: `()` (支持嵌套)

### 内置数学函数

#### 基础数学函数

- `sqrt(x)` - 平方根
- `abs(x)` - 绝对值
- `ceil(x)` - 向上取整
- `floor(x)` - 向下取整
- `round(x)` - 四舍五入
- `round(x, digits)` - 保留指定小数位数

#### 三角函数

- `sin(x)`, `cos(x)`, `tan(x)` - 基础三角函数
- `asin(x)`, `acos(x)`, `atan(x)` - 反三角函数

#### 双曲函数

- `sinh(x)`, `cosh(x)`, `tanh(x)` - 双曲函数

#### 对数函数

- `log(x)` - 自然对数
- `log10(x)` - 常用对数
- `exp(x)` - 指数函数

#### 多参数函数

- `pow(base, exponent)` - 幂运算
- `min(x1, x2, ...)` - 最小值
- `max(x1, x2, ...)` - 最大值
- `avg(x1, x2, ...)` - 平均值

#### Java 类调用

- `jcall(className, methodName, param1, param2, ...)` - 调用 Java 静态方法

## 📖 使用指南

### 基本用法

## 📖 快速开始

```java
// 1. 创建引擎实例
SimpleCalculateEngine engine = new SimpleCalculateEngine();

// 2. 设置单元格值
engine.setCellValue("A1", 10);
engine.setCellValue("A2", 20);

// 3. 设置公式
engine.setCellValue("A3", "=A1+A2");

// 4. 获取计算结果
String result = engine.getCellValue("A3"); // "30"
BigDecimal numResult = engine.getCellValueNumber("A3"); // 30

// 5. 高精度计算示例
engine.setCellValue("B1", "=0.1+0.2");
System.out.println(engine.getCellValue("B1")); // "0.3"

engine.setCellValue("B2", "=10/2");
System.out.println(engine.getCellValue("B2")); // "5"

// 6. 关闭引擎（释放线程池资源）
engine.shutdown();
```

### 单元格命名规则

支持灵活的单元格命名：

```java
// ✅ 支持的命名方式
engine.setCellValue("A1", 100);           // 传统Excel风格
engine.setCellValue("π", Math.PI);        // 希腊字母
engine.setCellValue("半径", 10);           // 中文
engine.setCellValue("_temp", 42);         // 下划线开头
engine.setCellValue("value_α", 100);      // 混合命名

// ❌ 不支持的命名方式
// engine.setCellValue("123abc", 100);    // 不能以数字开头
// engine.setCellValue("sqrt", 100);      // 不能使用内置函数名
```

### 数学表达式示例

```java
// 基础运算
engine.setCellValue("B1", "=10+20*3");        // 70
engine.setCellValue("B2", "=(10+20)*3");      // 90
engine.setCellValue("B3", "=2^3");            // 8 (幂运算)

// 数学函数
engine.setCellValue("C1", "=sqrt(25)");       // 5.0
engine.setCellValue("C2", "=sin(π/2)");       // 1.0
engine.setCellValue("C3", "=log(exp(2))");    // 2.0

// 多参数函数
engine.setCellValue("D1", "=max(10,20,30)");  // 30
engine.setCellValue("D2", "=avg(10,20,30)");  // 20.0
engine.setCellValue("D3", "=pow(2,10)");      // 1024.0
```

### 复杂公式示例

```java
// 圆的面积和周长计算
engine.setCellValue("π", Math.PI);
engine.setCellValue("半径", 10);
engine.setCellValue("面积", "=π*半径^2");      // 314.159...
engine.setCellValue("周长", "=2*π*半径");      // 62.831...

// 嵌套函数
engine.setCellValue("E1", "=sqrt(abs(-36))"); // 6.0
engine.setCellValue("E2", "=round(π*半径^2, 2)"); // 314.16
```

### Java 类调用示例

```java
// 调用Math类的静态方法
engine.setCellValue("F1", "=jcall('java.lang.Math', 'random')");
engine.setCellValue("F2", "=jcall('java.lang.Math', 'max', 10, 20)");

// 调用String类的静态方法
engine.setCellValue("F3", "=jcall('java.lang.String', 'valueOf', 123)");
```

### 依赖关系和联动计算

```java
// 设置依赖链
engine.setCellValue("X1", 10);
engine.setCellValue("X2", "=X1*2");      // X2 = 20
engine.setCellValue("X3", "=X2+X1");     // X3 = 30

// 更新X1会自动触发X2和X3的重新计算
engine.setCellValue("X1", 20);
// 现在: X2 = 40, X3 = 60
```

## 🔧 高级特性

### 多线程安全

引擎使用读写锁机制，支持多线程并发访问：

```java
ExecutorService executor = Executors.newFixedThreadPool(4);

for (int i = 0; i < 4; i++) {
    final int threadId = i;
    executor.submit(() -> {
        engine.setCellValue("Thread" + threadId, threadId * 100);
        engine.setCellValue("Result" + threadId, "=Thread" + threadId + "*2");
    });
}
```

### 🔧 高级特性

### 高精度计算

引擎使用 **BigDecimal** 进行所有数值计算，彻底解决浮点数精度问题：

```java
// 传统 double 计算的问题
double result1 = 0.1 + 0.2;  // 0.30000000000000004
double result2 = 1.0 - 0.9;  // 0.09999999999999998

// SimpleCalculateEngine 的高精度计算
engine.setCellValue("A1", "=0.1+0.2");  // "0.3"
engine.setCellValue("A2", "=1.0-0.9");  // "0.1"
engine.setCellValue("A3", "=0.1*3");    // "0.3"
```

### 智能格式化

自动优化显示格式，去除无意义的尾随零：

```java
engine.setCellValue("B1", "=10/2");     // "5"
engine.setCellValue("B2", "=1.0000");   // "1"
engine.setCellValue("B3", "=10/3");     // "3.3333333333" (保留必要的小数位)
```

### 错误处理

- 计算错误的单元格值会显示为 `"#ERROR#"`
- 支持除零检测、参数范围检查等
- 循环引用会抛出运行时异常

### 性能优化

- **拓扑排序**: 确保依赖单元格按正确顺序计算
- **并行计算**: 同层级的独立单元格可并行计算
- **线程池**: 使用固定大小的线程池处理计算任务

## 📋 API 参考

### 主要方法

#### 设置单元格值

```java
void setCellValue(String cellId, String content)
void setCellValue(String cellId, Number value)
void setCellValue(String cellId, int value)
void setCellValue(String cellId, long value)
void setCellValue(String cellId, float value)
void setCellValue(String cellId, double value)
```

#### 获取单元格值

```java
String getCellValue(String cellId)           // 获取字符串结果
BigDecimal getCellValueNumber(String cellId) // 获取数值结果
```

#### 资源管理

```java
void shutdown()  // 关闭线程池，释放资源
```

### Cell 类

内嵌的单元格类，包含以下属性：

- `id`: 单元格标识
- `content`: 单元格内容（原始公式或值）
- `calculatedValue`: 计算结果
- `dependencies`: 依赖的其他单元格集合

## 🛠️ 技术实现

### 核心组件

1. **单元格存储**: 使用 `ConcurrentHashMap` 存储单元格数据
2. **依赖管理**: 维护正向和反向依赖关系图
3. **表达式解析**: 支持复杂数学表达式的递归解析
4. **函数处理**: 模块化的函数计算系统
5. **并发控制**: 读写锁保证数据一致性
6. **高精度计算**: 基于 BigDecimal 的数值计算引擎
7. **智能格式化**: 自动优化数值显示格式

### 计算流程

1. **公式解析**: 提取单元格引用和函数调用
2. **依赖建立**: 更新依赖关系图
3. **拓扑排序**: 确定计算顺序
4. **并行计算**: 按层级并行执行计算
5. **结果更新**: 更新单元格计算结果

## 📝 注意事项

1. **资源管理**: 使用完毕后请调用 `shutdown()` 方法释放线程池资源
2. **单元格命名**: 避免使用内置函数名作为单元格 ID
3. **循环引用**: 系统会自动检测并抛出异常
4. **数值精度**: 内部使用 **BigDecimal** 进行高精度计算，完全避免浮点数精度问题
5. **Java 调用**: `jcall` 函数只能调用公共静态方法
6. **格式化**: 计算结果会自动去除无意义的尾随零，提供更友好的显示格式

## 🔍 示例项目

项目包含完整的演示代码，展示了引擎的各种功能：

```bash
# 运行演示
mvn test
```

## 📄 许可证

本项目基于 Java 17 构建。

本项目采用 Apache-2.0 许可证开源，您可以在遵守许可证条款的前提下自由使用、修改和分发本项目的代码。

---

**作者**: j²  
**版本**: 1.0  
**日期**: 2025-09-17
