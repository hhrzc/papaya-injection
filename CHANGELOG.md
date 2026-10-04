## 0.2.0
- Added `getVariableName()` and `getSelectorValue()` to `LocatorSupplier` and `WebElementSupplier`
- Playwright postfix lambdas are described automatically in `getSelectorValue()`
- Added `PostfixWebElement.withSelectorValue(...)` for describing Selenium postfixes
- Added `@Sensor(name = "...")` for overriding the variable name of a locator or element