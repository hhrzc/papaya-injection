## 0.3.1

### Added
- Spring: `@ConfigTelemetry(stageFrom = "my.stage")` reads the stage from the Spring Environment
  (`application.properties`, profile files, `-D`, env).
- 
### Fixed
- Selenium: suppliers created from the same page no longer share the `By` type.
  An XPath supplier could be resolved as CSS when another method was called before `apply(driver)`.
- Selenium: postfixes written inline (`e -> e.findElement(By.xpath(...))`) are now described in
  `getSelectorValue()`. Before, only a `By` created outside the lambda was found.
- Selenium: `PostfixWebElement.defineSelectorValue(...)` text is now used in `getSelectorValue()`.
- Selenium: the "@Param annotation missing" error now reports the real parameter index (was always 0).
- An interface with an empty `@Telemetry` no longer loads the classpath folder listing as selectors.
- Missing `@Telemetry` on an ignited interface gives a clear error instead of a `NullPointerException`.

### Changed
- **Stage configuration is now code-only and off by default.** Turn stages on with
  `PpiOptions.setStageFrom("my.stage")` before the first `ignite()`, then set the stage with
  `-Dmy.stage=prod` or env `MY_STAGE=prod`.
- `setStageFrom()` called after the first `ignite()` with a different name now always fails
  with a clear message.
- The root `papaya-injection` artifact is no longer published (it contained no classes).
  Depend on `ppi-playwright-*` / `ppi-selenium-*` as before.

### Removed
- `-Dppi.stage.from` / env `PPI_STAGE_FROM` and `ppi.stage.from` in `ppi.properties`:
  use `PpiOptions.setStageFrom(...)` instead.
- The default stage variable `pi.stage`: without `setStageFrom(...)`, stages are off.
- `PpiOptions.dropStageFrom()` and the constants `STAGE_FROM_PROPERTY`, `DEFAULT_STAGE_FROM`, `CONFIG_FILE`.

## 0.3.0
- Added stage-specific selectors in `.telemetry` files (`%dev.key=...`, `%prod.key=...`).

## 0.2.0
- Added `getVariableName()` and `getSelectorValue()` to `LocatorSupplier` and `WebElementSupplier`
- Playwright postfix lambdas are described automatically in `getSelectorValue()`
- Added `PostfixWebElement.defineSelectorValue(...)` for describing Selenium postfixes
- Added `@Sensor(name = "...")` for overriding the variable name of a locator or element