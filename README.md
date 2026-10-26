# Papaya Injection (PPI)

**A hub for your selectors — declarative, interface-based locators for Playwright and Selenium.**

Papaya Injection gives your project a single, structured place to keep every selector: described as methods on an `interface`, and optionally externalized into a separate properties-style file by key. At runtime, the library builds a dynamic proxy that implements your interface and resolves each method to a real `Locator` (Playwright) or `WebElement` (Selenium) — lazily, exactly when and where you actually call it.

## The idea

Instead of scattering selectors across page classes, Papaya Injection is one more way to organize the web elements/locators in your project — as a central hub you can structure by page, analyze, and update in one place, whether that's directly in an interface or in a mapped file.

A few things worth knowing:

- **Lazy by design.** Nothing is resolved until you actually call the method — you declare your selectors once and use them wherever and whenever you need them in your test flow.
- **The driver/page is supplied per call, not upfront.** Where a typical setup needs a driver to instantiate a page before you can touch any element on it, the `WebElementSupplier` here accepts the driver (or Playwright `Page`) each time an element is resolved. That means the same declared element can be resolved against different drivers/pages on different calls, without needing to construct a page object per driver first.
- **Interfaces over classes is mostly a style choice.** There's no hard technical requirement that this has to be an interface rather than a class — it's a deliberate, declarative style, in the spirit of something like JPA-spring. If you prefer that shape for organizing selectors, this library leans into it.
- **Two ways to define selectors.** Inline on the method via annotations, or externalized in a mapped properties-style file and referenced by key — your choice, per project or even per interface.
- Using interface is avoiding to use excessive code, extending and instantiating, especially when using Spring dependencies.

> This library is still growing — more features are on the way.

## Modules

| Module | What it's for |
|---|---|
| `ppi-playwright-java` | Playwright `Locator` support (plain Java). |
| `ppi-playwright-spring` | Playwright support with Spring auto-configuration. |
| `ppi-selenium-java` | Selenium `WebElement` support (plain Java). |
| `ppi-selenium-spring` | Selenium support with Spring auto-configuration. |

You only need to depend on the module(s) that match your stack — e.g. just `ppi-playwright-spring` if you're on Playwright + Spring.

## Installation

Add whichever module(s) you need. For example, Playwright with Spring:

```groovy
implementation("io.github.hhrzc:ppi-playwright-spring:0.3.1")
```

Or plain Selenium, no Spring:

```groovy
implementation("io.github.hhrzc:ppi-selenium-java:0.3.1")
```

Maven:

```xml
<dependency>
    <groupId>io.github.hhrzc</groupId>
    <artifactId>ppi-playwright-spring</artifactId>
    <version>0.3.1</version>
</dependency>
```

> Note: Playwright/Selenium and Spring themselves are `compileOnly` dependencies — add your own version of `com.microsoft.playwright:playwright`, `org.seleniumhq.selenium:selenium-java`, and/or `org.springframework:spring-context` alongside this library.

## Core Features & Usage Examples 

A simple definition: Define selector directly in the `@Telemetry` annotation.
(Selector must be started with the `$` chair) 

```java
@Telemetry
public interface LoginPage extends PpiRepository{

    @Sensor("$//button[name='SignIn']")
    LocatorSupplier signInButton(); //WebElementSupplier for selenium-java/spring

}
```

Definition in the telemetry file using key mapping:

```java

@Telemetry("main_pages/login_page.telemetry")
public interface LoginPage {

    @Sensor("button.sign-in")
    LocatorSupplier signInButton();

}
```

login_page.telemetry file:

```bash
root-project/
└── src/
    └── (main || test)/
        └── resources/
            └── main_pages/
                └── login_page.telemetry
```

```property
button.sign-in=//button[name='SignIn']
```

Using:

```java
public class LoginPageAction{

    private LoginPage loginPage = WebElementIgniter.ignite(LoginPage.class);

    public void doLogin(){
        Locator signInButton = loginPage.signInButton().apply(page);
        ...
    }
}
```

Definition with `@Param` 

```java
@Telemetry
public interface LoginPage extends PpiRepository{

    @Sensor("$//button[@name='{name}']")
    LocatorSupplier buttonByName(@Param("name") String name);

}
```

Using:

```java
public class LoginPageAction{

    private LoginPage loginPage = WebElementIgniter.ignite(LoginPage.class);

    public void doLogin(){
        Locator signInButton = loginPage.buttonByName("SignIn").apply(page);
        ...
    }
}
```

Postfix using:

```java
@Telemetry
public interface LoginPage extends PpiRepository{

    @Sensor("$button[class*=[{btnName}]")
    LocatorSupplier buttonByNameWithPostfix(@Param("btnName") String classAttr, PostfixLocator postfix);

}
```


```java
public class LoginPageAction{

    private LoginPage loginPage = WebElementIgniter.ignite(LoginPage.class);

    public void doLogin(){
        PostfixLocator postfix = locator -> locator.nth(1);
        Locator signInButton = loginPage.buttonByNameWithPostfix("button", postfix);
        ...
    }
}
```

The locator in the result will be: `locator("button[class*=[{btnName}]").nth(1)`


WebElementSupplier implements searching using XPATH selector by default. But we can also switch to the CSS using BY definition as first parameter:

```java
public class LoginPageAction{

    private LoginPage loginPage = WebElementIgniter.ignite(LoginPage.class);

    public void doLogin(){
        PostfixLocator postfix = locator -> locator.nth(1);
        Locator signInButton = loginPage.button(BY.CSS);
        //also working with parameters: Locator signInButton = loginPage.button(BY.CSS, someParameter);
        ...
    }
}
```


## Quick start

### Selenium

1) add the dependency:

```groovy
implementation("io.github.hhrzc:ppi-selenium-java:0.3.1")
```

Maven:

```xml
<dependency>
    <groupId>io.github.hhrzc</groupId>
    <artifactId>ppi-selenium-java</artifactId>
    <version>0.3.1</version>
</dependency>
```

2) Add selenium dependency version you need:

```groovy
    implementation("org.seleniumhq.selenium:selenium-java:4.47.0")
```

3) Create interface and .telemetry file

The project structure:

```bash
root-project/
└── src/
    └── main/
        └── java/
          └── pages/
            └── HomePage.java
        └── resources/
            └── pages/
                └── home_page.telemetry
```

`HomePage` interface:

```java
@Telemetry("pages/home-page.telemetry")
public interface HomePage {
    @Sensor("products")
    WebElementSupplier productsMenu();
}
```

home-page.telemetry:

```property
products=//div[@class='shop-menu pull-right']//li/a[@href='/products']
```

Test example:

```java
public class HomePageTest {

    private HomePage homePage;

    public HomePageTest(){
        homePage = WebElementIgniter.ignite(HomePage.class); // <- for plain Java usage we need initialize through ignite() method instead @Autowired annotation in spring
    }

    @Test
    public void demoTest() {
        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().window().maximize();

            driver.get("https://automationexercise.com");

            WebElement productsElement = homePage.productsMenu().apply(driver); // <-- there we are receiving WebElement
            productsElement.click();
            
        } finally {
            driver.quit();
        }
    }
}
```

## Supplier metadata: element name and selector

Every `LocatorSupplier` and `WebElementSupplier` knows where it came from. You can read
the declared method name and a readable description of its selector, without a browser
and without resolving the element.

Playwright:

```java
LocatorSupplier supplier = loginPage.buttonByNameWithPostfix("baz", locator -> locator.locator("//xpath"));

supplier.getVariableName();   // "buttonByNameWithPostfix"
supplier.getSelectorValue();  // "button[class*='baz'] -> locator("//xpath")"
```

Selenium:

```java
WebElementSupplier supplier = loginPage.withPostfix(e -> e.findElement(By.xpath("//div[@class='second']")));

supplier.getVariableName();   // "withPostfix"
supplier.getSelectorValue();  // "By.xpath: //div[@class='first'], By.xpath: //div[@class='second']"
```

Name overriding:
```java
@Sensor(value = "$button[class*=[{btnName}]", name = "Sign In button")
LocatorSupplier buttonByNameWithPostfix(@Param("btnName") String classAttr, PostfixLocator postfix);
```

```java
buttonByNameWithPostfix.getVariableName();   // "Sign In button"
```

This is useful for logs, report step names, and clearer failure messages.

The selector value is a **description** for humans, not a selector you can pass to
`findElement` or `page.locator()` — it may combine CSS and XPath parts.

Postfix lambdas are described automatically: the library records the calls the lambda
makes, so `locator -> locator.nth(1)` becomes `nth(1)` in Playwright, and
`e -> e.findElement(By.xpath(...))` becomes `By.xpath: ...` in Selenium.
This works for simple call chains. If the postfix contains logic that depends on results
(for example `count()` or `isDisplayed()`), it is described as `custom`.

## Stages (different selectors per environment)

A `.telemetry` file can hold selectors for several environments. Prefix a key with `%<stage>.`:

```properties
okBtn=button.SignIn
%dev.okBtn=button.SignIn.dev
%prod.okBtn=button.SignIn.prod
```

Stages are **off** by default: only keys without `%` are used. To turn them on, tell Papaya the
name of **your** variable that holds the stage, once, before the first `ignite()`:

```java
PpiOptions.setStageFrom("my.stage");
```

Then choose the stage when you run the tests:

```bash
./gradlew test -Dmy.stage=prod     # or environment variable MY_STAGE=prod
```

Rules:
- A key without a `%<stage>.` version uses its base value.
- An unknown stage (for example `qa` with no `%qa.` keys) uses base values.
- The stage is read at `ignite()`. Set it before the first `ignite()` and don't change it during the run.
- `setStageFrom()` can't be changed after the first `ignite()`.
  for example in a `static {}` block. A `@ConfigTelemetry(stageFrom = ...)` attribute is planned for 0.4.0.

### Stages in Spring

In Spring, name the property in `@ConfigTelemetry` instead of calling `PpiOptions`:

```java
@Configuration
@ConfigTelemetry(value = "com.example.pages", stageFrom = "my.stage")
public class PapayaConfig {
}
```

The value is read from the Spring `Environment`, so any Spring source works, with Spring's usual priority:

```properties
# application.properties  (or application-prod.properties for the "prod" profile)
my.stage=prod
```

`-Dmy.stage=prod` and environment variable `MY_STAGE=prod` work too.
Without `stageFrom`, or with an empty value, only base selectors are used.

## License

MIT — see [LICENSE](./LICENSE).

## Source

[github.com/hhrzc/papaya-injection](https://github.com/hhrzc/papaya-injection)
